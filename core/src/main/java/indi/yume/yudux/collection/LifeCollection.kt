package indi.yume.yudux.collection

import indi.yume.yudux.Log
import indi.yume.yudux.functions.*
import indi.yume.yudux.store.Action
import indi.yume.yudux.store.Store
import indi.yume.yudux.store.SubStore
import io.reactivex.Single


/**
 * Created by yume on 17-4-28.
 */
val TAG = "Context"

val VOID = Any()

abstract class BaseDependAction<Key, State, Data> : Action<ContextCollection<Key>, State, Data> {

    constructor()

    constructor(groupId: String) : super(groupId)

    override fun effect(collection: ContextCollection<Key>, oldState: State): Single<Data> {
        val result = collection.getDepends(depends())
        return when(result) {
            is Right -> dependEffect(result.right, oldState)
            is Fail -> Single.error(DependsNotReadyException(result.fail.joinToString(separator = ",")))
        }
    }

    abstract fun depends(): Set<Key>

    fun dependsNotReady(depends: Set<Key>) {}

    abstract fun dependEffect(realworld: RealWorld<Key>, oldState: State): Single<Data>
}

class DependActionImpl<Key, State, Data>: BaseDependAction<Key, State, Data> {
    val depends: Set<Key>
    val effect: (RealWorld<Key>, State) -> Single<Data>
    val reducer: (Data, State) -> State

    constructor(
            groupId: String,
            depends: Array<out Key>,
            effect: (RealWorld<Key>, State) -> Single<Data>,
            reducer: (Data, State) -> State) : super(groupId) {
        this.depends = depends.toSet()
        this.effect = effect
        this.reducer = reducer
    }

    constructor(
            depends: Array<out Key>,
            effect: (RealWorld<Key>, State) -> Single<Data>,
            reducer: (Data, State) -> State) : super() {
        this.depends = depends.toSet()
        this.effect = effect
        this.reducer = reducer
    }

    override fun depends(): Set<Key> = depends

    override fun dependEffect(realworld: RealWorld<Key>, oldState: State): Single<Data> =
            effect(realworld, oldState)

    override fun reduce(data: Data, oldState: State): State =
            reducer(data, oldState)
}

class RenderAction<Real, State> : Action<Real, State, Unit>() {

    override fun effect(realworld: Real, oldState: State): Single<Unit> = Single.just(Unit)

    override fun reduce(unit: Unit, oldState: State): State = oldState
}

class RealWorld<Key>(val context: Map<Key, Any>) {

    fun <T> getItem(key: Key): T = context[key] as T

    class Builder<K> internal constructor(var context: Map<K, Any> = emptyMap<K, Any>()) {

        fun withItem(key: K, contextItem: Any): Builder<*> {
            context += key to contextItem
            return this
        }

        fun build(): RealWorld<K> = RealWorld(context)
    }

    companion object {
        @JvmStatic
        fun <K> empty(): RealWorld<K> = RealWorld(emptyMap())
        @JvmStatic
        fun <K> builder(): Builder<K> = Builder()
        @JvmStatic
        fun <K> newBuilder(copy: RealWorld<K>): Builder<K> = Builder<K>(copy.context)
    }
}

data class Ready<Key, T>(val key: Key, val context: T)

data class Module<Key, T>(
    val depends: Set<Key>,
    val notReadyDepends: Set<Key> = HashSet(depends),
    val provideKey: Key,

    val initializer: (RealWorld<Key>) -> T?,
    val finalizer: Effect1<T>,

    val isInit: Boolean = false,
    val context: T? = null
) {

    fun depends(): Set<Key> = depends

    fun init(dependContext: RealWorld<Key>): Module<Key, T> {
        if (isInit)
            return this

        val con = initializer(dependContext)
        return if(con == null) this else copy(isInit = true, context = con)
    }

    fun destroy(): Module<Key, T> {
        if (!isInit)
            return this

        finalizer.f(context!!)
        return copy(isInit = false, context = null)
    }

    fun ready(key: Key): Module<Key, T> {
        if (!notReadyDepends.contains(key))
            return this

        return copy(notReadyDepends = notReadyDepends - key)
    }

    fun destroy(key: Key): Module<Key, T> {
        if (!depends.contains(key))
            return this

        return copy(notReadyDepends = notReadyDepends + key)
    }

    val isReady: Result<Set<Key>, Set<Key>>
        get() {
            if (notReadyDepends.isEmpty())
                return Result.right(depends)

            return Result.fail(notReadyDepends)
        }
}

class ContextCollection<Key>(private var providerList: Map<Key, Module<Key, *>> = emptyMap()) {
    private var contextMap: Map<Key, Any> = emptyMap()

    @Synchronized internal fun setProviders(providerList: Map<Key, Module<Key, *>>) {
        this.providerList = providerList
    }

    @Synchronized fun getDepends(depends: Set<Key>): Result<RealWorld<Key>, Set<Key>> =
            getDepends(contextMap, depends)

    @Synchronized fun ready(ready: Ready<Key, *>) {
        if(contextMap.containsKey(ready.key))
            return

        val readyMap: Map<Key, Any> = mapOf(ready.key to ready.context!!)
        var readyData: ReadyData<Key> = ReadyData(contextMap, readyMap, providerList)
        do {
            readyData = ready(readyData)
        } while (!readyData.readyMap.isEmpty())

        contextMap = readyData.contextMap
        providerList = readyData.providerList
    }

    @Synchronized fun destroy(key: Key) {
        var newContextMap: Map<Key, Any> = contextMap
        var newProviderMap: Map<Key, Module<Key, *>> = emptyMap()

        newContextMap -= key

        for ((contextKey, value) in providerList) {
            var newModule = value.destroy(key)

            if (newModule.isInit && (contextKey === key || contextKey == key))
                newModule = newModule.destroy()

            newProviderMap += contextKey to newModule
        }

        contextMap = newContextMap
        providerList = newProviderMap
    }

    @Synchronized fun destroyAll() {
        val newContextMap: Map<Key, Any> = contextMap
        for (key in newContextMap.keys)
            destroy(key)
    }

    companion object {
        fun <Key> getDepends(contextMap: Map<Key, Any>, depends: Set<Key>): Result<RealWorld<Key>, Set<Key>> {
            val builder = RealWorld.builder<Key>()
            var notReady: Set<Key> = emptySet()
            for (key in depends) {
                val context = contextMap[key]
                if (context != null) {
                    if (notReady.isEmpty())
                        builder.withItem(key, context)
                } else {
                    notReady += key
                }
            }

            if (notReady.isEmpty())
                return Result.right(builder.build())
            return Result.fail(notReady)
        }

        private fun <Key> ready(readyData: ReadyData<Key>): ReadyData<Key> {
            val allDependsMap: Map<Key, Any> = readyData.contextMap + readyData.readyMap

            var newReadyMap: Map<Key, Any> = emptyMap()
            var newProviderMap: Map<Key, Module<Key, *>> = emptyMap()

            for ((key, value) in readyData.providerList) {
                var newModule = value
                for((k, _) in readyData.readyMap)
                    newModule = newModule.ready(k)

                val result = newModule.isReady
                if (result is Right && !newModule.isInit) {
                    newModule = newModule.init(getDepends(allDependsMap, result.right).right!!)
                    newReadyMap += key to newModule.context!!
                }

                newProviderMap += key to newModule
            }
            return ReadyData(allDependsMap, newReadyMap, newProviderMap)
        }
    }
}

internal data class ReadyData<Key> (
        val contextMap: Map<Key, Any>,
        val readyMap: Map<Key, Any>,
        val providerList: Map<Key, Module<Key, *>>
)

class DependsStore<Key, State>(
        mainStore: Store<State>,
        providerList: Map<Key, Module<Key, *>> = emptyMap())
    : SubStore<ContextCollection<Key>, State>(mainStore, ContextCollection(providerList)) {

    @SafeVarargs
    fun dispatchTransaction(vararg actions: BaseDependAction<Key, State, *>) =
            dispatchTransaction(null, *actions)

    fun dispatchWithDepends(action: BaseDependAction<Key, State, *>) {
        super.dispatch(action)
    }

    fun forceRender() = dispatch(RenderAction<ContextCollection<Key>, State>())

    fun subscribe(depends: Set<Key>, subscriber: BiSubscriber<State, RealWorld<Key>>): Subscription {
        if (depends.isEmpty())
            return mainStore.subscribe(object: Subscriber<State> {
                override fun onStateChange(state: State) =
                        subscriber.onStateChange(state, RealWorld.empty())
            })

        return mainStore.subscribe(object : Subscriber<State> {
            override fun onStateChange(state: State) {
                val result = realWorld.getDepends(depends)
                when(result) {
                    is Right -> subscriber.onStateChange(state, result.right)
                    is Fail -> Log.d(TAG,
                            "Can not subscribe, depends not ready: ${result.fail.joinToString(separator = ",")}")
                }
            }
        })
    }

    fun subscribe(depends: Array<out Key>, subscriber: BiSubscriber<State, RealWorld<Key>>): Subscription {
        return subscribe(subscriber, *depends)
    }

    fun subscribe(subscriber: BiSubscriber<State, RealWorld<Key>>, vararg depends: Key): Subscription {
        return subscribe(depends.toSet(), subscriber)
    }

    fun ready(ready: Ready<Key, *>) = realWorld.ready(ready)

    fun ready(key: Key, value: Any?) = value?.apply { ready(Ready(key, value)) }

    fun destroy(key: Key) = realWorld.destroy(key)

    fun destroyAll() = realWorld.destroyAll()

    fun getState(): State = mainStore.getState()

    class Builder<Key, State> internal constructor(mainStore: Store<State>) {
        internal val store: DependsStore<Key, State> = DependsStore(mainStore)
        internal val providerList: MutableMap<Key, Module<Key, *>> = HashMap()

        fun <T> withItem(key: Key,
                         depends: Array<out Key>,
                         initializer: (RealWorld<Key>, DependsStore<Key, State>) -> T?): Builder<Key, State> {
            return withItem<T>(key, initializer, emptyEffect1(), *depends)
        }

        fun <T> withItem(key: Key,
                         depends: Array<out Key>,
                         initializer: (RealWorld<Key>, DependsStore<Key, State>) -> T?,
                         finalizer: Effect1<T>): Builder<Key, State> {
            return withItem<T>(key, initializer, finalizer, *depends)
        }

        fun <T> withItem(key: Key,
                         initializer: (RealWorld<Key>, DependsStore<Key, State>) -> T?,
                         finalizer: Effect1<T>,
                         vararg depends: Key): Builder<Key, State> {
            providerList.put(key, Module(provideKey = key,
                    depends = depends.toSet(),
                    initializer = { realWorld -> initializer(realWorld, store) },
                    finalizer = finalizer))
            return this
        }

        fun <T> withItem(key: Key,
                         initializer: BiSubscriber<RealWorld<Key>, DependsStore<Key, State>>): Builder<Key, State> {
            return withItem<T>(key, initializer, emptyEffect1())
        }

        fun <T> withItem(key: Key,
                         initializer: BiSubscriber<RealWorld<Key>, DependsStore<Key, State>>,
                         finalizer: Effect1<T>): Builder<Key, State> {
            return withItem<T>(key,
                    { real, store ->
                        initializer.onStateChange(real, store)
                        real.getItem(key)
                    },
                    finalizer)
        }

        fun build(): DependsStore<Key, State> {
            store.realWorld.setProviders(providerList)
            return store
        }
    }

    companion object {
        @JvmStatic
        fun <Key, State> builder(mainStore: Store<State>): Builder<Key, State> = Builder(mainStore)

        internal fun <T> emptyEffect1(): Effect1<T> = object : Effect1<T> {
            override fun f(a: T) = Unit
        }
    }
}
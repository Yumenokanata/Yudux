package indi.yume.yudux.collection

import indi.yume.yudux.functions.*
import indi.yume.yudux.functions.Result.Companion.left
import indi.yume.yudux.functions.Result.Companion.right
import indi.yume.yudux.store.*
import io.reactivex.Single


/**
 * Created by yume on 17-4-28.
 */
val TAG = "Context"

val VOID = Any()

abstract class BaseDependAction<R : MultiReal, State, Data>(val provider: ContextProvider<R>,
                                                            groupId: String = GROUP_ID_DEFAULT)
    : Action<State, Data>(groupId) {

    override fun effect(oldState: State): Single<Data> {
        val result = provider.get()
        return when(result) {
            is Right -> dependEffect(result.right, oldState)
            is Left -> {
                dependsNotReady(result.left)
                Single.error(DependsNotReadyException(result.left))
            }
        }
    }

    fun dependsNotReady(depends: List<String>) {}

    abstract fun dependEffect(realworld: R, oldState: State): Single<Data>
}

class DependActionImpl<R : MultiReal, State, Data>: BaseDependAction<R, State, Data> {
    val effect: (R, State) -> Single<Data>
    val reducer: (Data, State) -> State

    constructor(
            groupId: String?,
            depends: ContextProvider<R>,
            effect: (R, State) -> Single<Data>,
            reducer: (Data, State) -> State) : super(depends, groupId ?: GROUP_ID_DEFAULT) {
        this.effect = effect
        this.reducer = reducer
    }

    constructor(
            depends: ContextProvider<R>,
            effect: (R, State) -> Single<Data>,
            reducer: (Data, State) -> State) : super(depends) {
        this.effect = effect
        this.reducer = reducer
    }

    override fun dependEffect(realworld: R, oldState: State): Single<Data> =
            effect(realworld, oldState)

    override fun reduce(data: Data, oldState: State): State =
            reducer(data, oldState)
}

data class LazyAction<R : MultiReal, D : Depends<R>, State, Data> (
        val depends: D,
        val effect: (R, State) -> Single<Data>,
        val reducer: (Data, State) -> State,
        val groupId: String? = null)

data class Ready<Key, T>(val key: Key, val context: T)

data class Module<Key, T>(
        val depends: Set<Key>,
        val notReadyDepends: Set<Key> = HashSet(depends),
        val extraDepends: ContextProvider<MultiReal>?,
        val provideKey: Key,

        val initializer: (MultiReal) -> T?,
        val finalizer: Effect1<T>,

        val isInit: Boolean = false,
        val context: T? = null
) {

    fun depends(): Set<Key> = depends

    fun init(dependContext: RealWorld<Key>): Module<Key, T> {
        if (isInit)
            return this

        val extra = extraDepends?.get()
        if(extra?.isLeft ?: false)
            return this

        val con = initializer(if(extra == null) dependContext else extra.right!!.append(dependContext))
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

    val isReady: Result<Set<Key>, List<String>>
        get() {
            if (notReadyDepends.isEmpty()) {
                val extra = extraDepends?.get()
                if(extra != null && extra is Left)
                    return Result.left(extra.left)

                return Result.right(depends)
            }

            return Result.left(notReadyDepends.map { it.toString() })
        }
}

class ContextCollection<Key>(private var providerList: Map<Key, Module<Key, *>> = emptyMap()) {
    private var contextMap: Map<Key, Any> = emptyMap()

    @Synchronized internal fun setProviders(providerList: Map<Key, Module<Key, *>>) {
        this.providerList = providerList
    }

    @Synchronized fun getDepends(depends: Set<Key>): Result<RealWorld<Key>, Set<Key>> {
        var readyData: ReadyData<Key> = ReadyData(contextMap, emptyMap(), providerList)
        do {
            readyData = ready(readyData)
        } while (!readyData.readyMap.isEmpty())

        return getDepends(contextMap, depends)
    }

    @Synchronized fun check(depends: List<Any>): List<Any> =
            depends.filter { (it as? Key)?.let { k -> !contextMap.containsKey(k) } ?: true }

    fun ready(key: Key, context: Any?) = if(context != null) ready(Ready(key, context)) else Unit

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
    class Builder<Key> {
        internal val collection: ContextCollection<Key> = ContextCollection()
        internal val providerList: MutableMap<Key, Module<Key, *>> = HashMap()

        fun <T> withItem(key: Key,
                         depends: Array<out Key>,
                         initializer: (RealWorld<Key>, ContextCollection<Key>) -> T?): Builder<Key> {
            return withItem<T>(key, { real, coll -> initializer(real as RealWorld<Key>, coll) }, emptyEffect1(), null, *depends)
        }

        fun <T> withItem(key: Key,
                         depends: Array<out Key>,
                         initializer: (RealWorld<Key>, ContextCollection<Key>) -> T?,
                         finalizer: Effect1<T>): Builder<Key> {
            return withItem<T>(key, { real, coll -> initializer(real as RealWorld<Key>, coll) }, finalizer, null, *depends)
        }

        fun <K1, T> withItem(key: Key,
                             depends: Array<out Key>,
                             extraDepends: ContextProvider1<K1>,
                             initializer: (BiReal<K1, Key>, ContextCollection<Key>) -> T?): Builder<Key> {
            return withItem<T>(key, { real, coll -> initializer(real as BiReal<K1, Key>, coll) }, emptyEffect1(), extraDepends, *depends)
        }

        fun <K1, T> withItem(key: Key,
                             depends: Array<out Key>,
                             extraDepends: ContextProvider1<K1>,
                             initializer: (BiReal<K1, Key>, ContextCollection<Key>) -> T?,
                             finalizer: Effect1<T>): Builder<Key> {
            return withItem<T>(key, { real, coll -> initializer(real as BiReal<K1, Key>, coll) }, finalizer, extraDepends, *depends)
        }

        fun <K1, K2, T> withItem(key: Key,
                             depends: Array<out Key>,
                             extraDepends: ContextProvider2<K1, K2>,
                             initializer: (Real3<K1, K2, Key>, ContextCollection<Key>) -> T?): Builder<Key> {
            return withItem<T>(key, { real, coll -> initializer(real as Real3<K1, K2, Key>, coll) }, emptyEffect1(), extraDepends, *depends)
        }

        fun <K1, K2, T> withItem(key: Key,
                             depends: Array<out Key>,
                             extraDepends: ContextProvider2<K1, K2>,
                             initializer: (Real3<K1, K2, Key>, ContextCollection<Key>) -> T?,
                             finalizer: Effect1<T>): Builder<Key> {
            return withItem<T>(key, { real, coll -> initializer(real as Real3<K1, K2, Key>, coll) }, finalizer, extraDepends, *depends)
        }

        fun <T> withItem(key: Key,
                         initializer: (MultiReal, ContextCollection<Key>) -> T?,
                         finalizer: Effect1<T> = emptyEffect1(),
                         extraDepends: ContextProvider<MultiReal>? = null,
                         vararg depends: Key): Builder<Key> {
            providerList.put(key, Module(provideKey = key,
                    depends = depends.toSet(),
                    extraDepends = extraDepends,
                    initializer = { realWorld -> initializer(realWorld, collection) },
                    finalizer = finalizer))
            return this
        }

        fun <T> withItem(key: Key,
                         initializer: BiSubscriber<RealWorld<Key>, ContextCollection<Key>>): Builder<Key> {
            return withItem<T>(key, initializer, emptyEffect1())
        }

        fun <T> withItem(key: Key,
                         initializer: BiSubscriber<RealWorld<Key>, ContextCollection<Key>>,
                         finalizer: Effect1<T>): Builder<Key> {
            return withItem<T>(key,
                    { real, collection ->
                        initializer.onStateChange(real as RealWorld<Key>, collection)
                        real.getItem(key as Any)
                    },
                    finalizer)
        }

        fun build(): ContextCollection<Key> {
            collection.providerList = providerList
            return collection
        }
    }

    companion object {
        @JvmStatic
        fun <Key> builder(): Builder<Key> = Builder()

        internal fun <T> emptyEffect1(): Effect1<T> = object : Effect1<T> {
            override fun f(a: T) = Unit
        }

        internal fun <Key> getDepends(contextMap: Map<Key, Any>, depends: Set<Key>): Result<RealWorld<Key>, Set<Key>> {
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
            return Result.left(notReady)
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



//class Depends1Store<Key, State>(
//        mainStore: Store<State>,
//        providerList: Map<Key, Module<Key, *>> = emptyMap())
//    : SubStore<ContextCollection<Key>, State>(mainStore, ContextCollection(providerList)) {
//
//    @SafeVarargs
//    fun dispatchTransaction(vararg actions: BaseDependAction<Key, State, *>) =
//            dispatchTransaction(null, *actions)
//
//    fun dispatchWithDepends(action: BaseDependAction<Key, State, *>) {
//        super.dispatch(action)
//    }
//
//    fun forceRender() = dispatch(RenderAction<ContextCollection<Key>, State>())
//
//    fun subscribe(depends: Set<Key>, subscriber: BiSubscriber<State, RealWorld<Key>>): Subscription {
//        if (depends.isEmpty())
//            return mainStore.subscribe(object: Subscriber<State> {
//                override fun onStateChange(state: State) =
//                        subscriber.onStateChange(state, RealWorld.empty())
//            })
//
//        return mainStore.subscribe(object : Subscriber<State> {
//            override fun onStateChange(state: State) {
//                val result = realWorld.getDepends(depends)
//                when(result) {
//                    is Right -> subscriber.onStateChange(state, result.right)
//                    is Left -> Log.d(TAG,
//                            "Can not subscribe, depends not ready: ${result.left.joinToString(separator = ",")}")
//                }
//            }
//        })
//    }
//
//    fun subscribe(depends: Array<out Key>, subscriber: BiSubscriber<State, RealWorld<Key>>): Subscription {
//        return subscribe(subscriber, *depends)
//    }
//
//    fun subscribe(subscriber: BiSubscriber<State, RealWorld<Key>>, vararg depends: Key): Subscription {
//        return subscribe(depends.toSet(), subscriber)
//    }
//
//    fun ready(ready: Ready<Key, *>) = realWorld.ready(ready)
//
//    fun ready(key: Key, value: Any?) = value?.apply { ready(Ready(key, value)) }
//
//    fun destroy(key: Key) = realWorld.destroy(key)
//
//    fun destroyAll() = realWorld.destroyAll()
//
//    fun getState(): State = mainStore.getState()
//
//}
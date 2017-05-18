package indi.yume.yudux.collection

import indi.yume.yudux.functions.Result
import indi.yume.yudux.functions.Result.Companion.mapLeft
import indi.yume.yudux.functions.Result.Companion.map2
import indi.yume.yudux.functions.Result.Companion.right
import java.lang.UnsupportedOperationException

/**
 * Created by yume on 17-5-17.
 */

sealed class ContextProvider<out Right : MultiReal> {
    abstract fun get(): Result<Right, List<String>>

    abstract fun check(): Boolean
}

object ContextProviderEmpty : ContextProvider<EmptyReal>() {
    override fun get(): Result<EmptyReal, List<String>> = right(EmptyReal)

    override fun check(): Boolean = true
}

class ContextProvider1<Key>(val realWorld: ContextCollection<Key>,
                            val depends: Set<Key>)
    : ContextProvider<RealWorld<Key>>() {
    override fun get(): Result<RealWorld<Key>, List<String>> =
            if (depends.isEmpty())
                Result.right(RealWorld.empty())
            else
                realWorld.getDepends(depends).mapLeft { s -> s.map { it.toString() } }

    override fun check(): Boolean = realWorld.check(depends.map { it as Any }).isEmpty()
}

class ContextProvider2<Key1, Key2>(val provider1: ContextProvider1<Key1>,
                                   val provider2: ContextProvider1<Key2>)
    : ContextProvider<BiReal<Key1, Key2>>() {
    override fun get(): Result<BiReal<Key1, Key2>, List<String>> =
            provider1.get().map2(provider2.get(),
                    { real1, real2 -> BiReal(real1, real2) },
                    { set1, set2 -> (set1 ?: emptyList()) + (set2 ?: emptyList()) })

    override fun check(): Boolean = provider1.check() && provider2.check()
}

class ContextProvider3<Key1, Key2, Key3>(val provider1: ContextProvider1<Key1>,
                                         val provider2: ContextProvider1<Key2>,
                                         val provider3: ContextProvider1<Key3>)
    : ContextProvider<Real3<Key1, Key2, Key3>>() {
    override fun get(): Result<Real3<Key1, Key2, Key3>, List<String>> =
            provider1.get().map2(provider2.get(),
                    { real1, real2 -> BiReal(real1, real2) },
                    { set1, set2 -> (set1 ?: emptyList()) to (set2 ?: emptyList()) })
                    .map2(provider3.get(),
                            { bireal, real3 -> Real3(bireal.realWorld1, bireal.realWorld2, real3) },
                            { pair, set3 -> (pair?.first ?: emptyList()) + (pair?.second ?: emptyList()) + ((set3 ?: emptyList())) })

    override fun check(): Boolean = provider1.check() && provider2.check() && provider3.check()
}

sealed class MultiReal {
    abstract fun <Key> append(real: RealWorld<Key>): MultiReal

    fun <T> getItem(key: Any): T = checkAndGet(key)!!

    internal abstract fun <T> checkAndGet(key: Any): T?
}

object EmptyReal : MultiReal() {
    override fun <Key> append(real: RealWorld<Key>): MultiReal = EmptyReal

    override fun <T> checkAndGet(key: Any): T? = null
}

class RealWorld<Key>(val context: Map<Key, Any>) : MultiReal() {

    fun <T> get(key: Key): T = context[key] as T

    override fun <T> checkAndGet(key: Any): T? = (key as? Key)?.let { context[it] }?.let { c -> c as? T } ?: null

    override fun <K1> append(real: RealWorld<K1>): MultiReal = BiReal<Key, K1>(this, real)


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

data class BiReal<Key1, Key2>(val realWorld1: RealWorld<Key1>, val realWorld2: RealWorld<Key2>) : MultiReal() {
    override fun <T> checkAndGet(key: Any): T? =
            realWorld1.checkAndGet(key) ?: realWorld2.checkAndGet(key)

    override fun <Key3> append(real: RealWorld<Key3>): MultiReal = Real3<Key1, Key2, Key3>(realWorld1, realWorld2, real)

    fun <T> getRepo1(key: Key1): T = realWorld1.get(key)

    fun <T> getRepo2(key: Key2): T = realWorld2.get(key)
}

data class Real3<Key1, Key2, Key3>(val realWorld1: RealWorld<Key1>,
                                   val realWorld2: RealWorld<Key2>,
                                   val realWorld3: RealWorld<Key3>) : MultiReal() {
    override fun <T> checkAndGet(key: Any): T? =
            realWorld1.checkAndGet(key) ?: realWorld2.checkAndGet(key) ?: realWorld3.checkAndGet(key)

    override fun <Key> append(real: RealWorld<Key>): MultiReal = throw UnsupportedOperationException()

    fun <T> getRepo1(key: Key1): T = realWorld1.get(key)

    fun <T> getRepo2(key: Key2): T = realWorld2.get(key)

    fun <T> getRepo3(key: Key3): T = realWorld3.get(key)
}

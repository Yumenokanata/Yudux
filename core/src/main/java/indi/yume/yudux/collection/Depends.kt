package indi.yume.yudux.collection

import indi.yume.yudux.collection.DSL.extra

/**
 * Created by yume on 17-5-18.
 */

sealed class Depends<R : MultiReal> {
    abstract fun makeProvider(vararg repos: ContextCollection<*>): ContextProvider<R>
}

data class SingleDepends<Key>(val depend: Set<Key>) : Depends<RealWorld<Key>>() {
    override fun makeProvider(vararg repos: ContextCollection<*>) =
            ContextProvider1<Key>(repos[0] as ContextCollection<Key>, depend)
}

data class BiDepends<Key1, Key2>(val depend1: Set<Key1>, val depend2: Set<Key2>) : Depends<BiReal<Key1, Key2>>() {
    override fun makeProvider(vararg repos: ContextCollection<*>): ContextProvider<BiReal<Key1, Key2>> =
            ContextProvider2<Key1, Key2>(
                    ContextProvider1(repos[0] as ContextCollection<Key1>, depend1),
                    ContextProvider1(repos[1] as ContextCollection<Key2>, depend2))
}

data class Depends3<Key1, Key2, Key3>(val depend1: Set<Key1>,
                                      val depend2: Set<Key2>,
                                      val depend3: Set<Key3>) : Depends<Real3<Key1, Key2, Key3>>() {
    override fun makeProvider(vararg repos: ContextCollection<*>): ContextProvider<Real3<Key1, Key2, Key3>> =
            ContextProvider3(
                    ContextProvider1(repos[0] as ContextCollection<Key1>, depend1),
                    ContextProvider1(repos[1] as ContextCollection<Key2>, depend2),
                    ContextProvider1(repos[2] as ContextCollection<Key3>, depend3))
}
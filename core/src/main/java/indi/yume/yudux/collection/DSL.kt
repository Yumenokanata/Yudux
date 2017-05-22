package indi.yume.yudux.collection

import indi.yume.yudux.Log
import indi.yume.yudux.functions.*
import indi.yume.yudux.store.Store
import io.reactivex.Observable

/**
 * Created by yume on 17-5-17.
 */

object DSL {
    @JvmStatic
    fun <R : MultiReal, State> subscribe(mainStore: Store<State>,
                                         depends: ContextProvider<R>,
                                         subscriber: BiSubscriber<State, MultiReal>): Subscription {
        return mainStore.subscribe(object : Subscriber<State> {
            override fun onStateChange(state: State) {
                val result = depends.get()
                when(result) {
                    is Right -> subscriber.onStateChange(state, result.right)
                    is Left -> Log.d(TAG,
                            "Can not subscribe, depends not ready: ${result.left.joinToString(separator = ",")}")
                }
            }
        })
    }

    @JvmStatic
    fun <R : MultiReal, State> subscribeUntilChanged(mainStore: Store<State>,
                                                     depends: ContextProvider<R>,
                                                     subscriber: BiSubscriber<State, MultiReal>): Subscription =
            subscribeUntilChanged(mainStore, depends, { it }, subscriber)

    @JvmStatic
    fun <R : MultiReal, State, T> subscribeUntilChanged(mainStore: Store<State>,
                                                        depends: ContextProvider<R>,
                                                        mapper: (State) -> T,
                                                        subscriber: BiSubscriber<T, MultiReal>): Subscription {
        return mainStore.subscribe(object : Subscriber<State> {
            var lastState: T? = null
            var hasValue = false

            override fun onStateChange(state: State) {
                val newS = mapper(state)
                if(hasValue) {
                    val oldS = lastState
                    if(oldS == newS) return
                } else {
                    lastState = newS
                    hasValue = true
                }

                val result = depends.get()
                when(result) {
                    is Right -> subscriber.onStateChange(newS, result.right)
                    is Left -> Log.d(TAG,
                            "Can not subscribe, depends not ready: ${result.left.joinToString(separator = ",")}")
                }
            }
        })
    }

    @JvmStatic
    fun <R : MultiReal, State, T> subscribeUntilChanged(mainStore: Store<State>,
                                                        depends: ContextProvider<R>,
                                                        compare: (State, State) -> Boolean,
                                                        mapper: (State) -> T,
                                                        subscriber: BiSubscriber<T, MultiReal>): Subscription {
        return mainStore.subscribe(object : Subscriber<State> {
            var lastState: State? = null
            var hasValue = false

            override fun onStateChange(state: State) {
                val oldS = lastState
                if(hasValue && oldS != null) {
                    if(compare(oldS, state)) return
                } else {
                    lastState = state
                    hasValue = true
                }
                val newData = mapper(state)

                val result = depends.get()
                when(result) {
                    is Right -> subscriber.onStateChange(newData, result.right)
                    is Left -> Log.d(TAG,
                            "Can not subscribe, depends not ready: ${result.left.joinToString(separator = ",")}")
                }
            }
        })
    }

    @JvmStatic
    fun <R : MultiReal, State> subscribe(mainStore: Store<State>,
                                         depends: ContextProvider<R>): Observable<Pair<State, MultiReal>> {
        return mainStore.subscribe()
                .flatMap { state ->
                    val result = depends.get()
                    when(result) {
                        is Right -> Observable.just(state to result.right)
//                        is Left -> Observable.error(DependsNotReadyException(result.left))
                        is Left -> {
                            Log.d(TAG,
                                    "Can not subscribe, depends not ready: ${result.left.joinToString(separator = ",")}")
                            Observable.empty()
                        }
                    }
                }
    }

    @JvmStatic
    fun <Key> extra(depends: Array<out Key>): SingleDepends<Key> =
            SingleDepends(depends.toSet())

    @JvmStatic
    fun <Key1, Key2> extra(depends1: Array<out Key1>,
                           depends2: Array<out Key2>): BiDepends<Key1, Key2> =
            BiDepends(depends1.toSet(), depends2.toSet())

    @JvmStatic
    fun <Key> extra(realWorld: ContextCollection<Key>, depends: Array<out Key>): ContextProvider1<Key> =
            ContextProvider1(realWorld, depends.toSet())

    @JvmStatic
    fun <Key1, Key2> extra(realWorld1: ContextCollection<Key1>, depends1: Array<out Key1>,
                           realWorld2: ContextCollection<Key2>, depends2: Array<out Key2>): ContextProvider2<Key1, Key2> =
            ContextProvider2(extra(realWorld1, depends1), extra(realWorld2, depends2))
}
package indi.yume.yudux

import indi.yume.yudux.collection.DependActionImpl
import indi.yume.yudux.collection.Ready
import indi.yume.yudux.collection.RealWorld
import io.reactivex.Single

/**
 * Created by yume on 17-4-28.
 */

object DSL {
    @JvmStatic
    fun <Key, State, Data> action(groupId: String,
                                  depends: Array<out Key>,
                                  effect: (RealWorld<Key>, State) -> Single<Data>,
                                  reducer: (Data, State) -> State): DependActionImpl<Key, State, Data> =
            DependActionImpl(groupId, depends, effect, reducer)

    @JvmStatic
    fun <Key, State, Data> action(depends: Array<out Key>,
                                  effect: (RealWorld<Key>, State) -> Single<Data>,
                                  reducer: (Data, State) -> State): DependActionImpl<Key, State, Data> =
            DependActionImpl(depends, effect, reducer)

    @JvmStatic
    fun <Key, State, SubState, Data> action(depends: Array<out Key>,
                                            selector: (State) -> SubState,
                                            setter: (State, SubState) -> State,
                                            effect: (RealWorld<Key>, SubState) -> Single<Data>,
                                            reducer: (Data, SubState) -> SubState): DependActionImpl<Key, State, Data> =
            DependActionImpl(depends,
                    { real, state ->
                        effect(real, selector(state))
                    },
                    { data, state ->
                        setter(state, reducer(data, selector(state)))
                    })

    @JvmStatic
    fun <T> plus(collection: Collection<T>, element: T): List<T> = collection + element

    @JvmStatic
    fun <T> plus(collection: Collection<T>, elements: Iterable<T>): List<T> = collection + elements

    @JvmStatic
    fun <Key> depends(vararg depends: Key): Array<out Key> = depends

    @JvmStatic
    fun <Key, T> ready(key: Key, value: T): Ready<Key, T> = Ready(key, value)
}
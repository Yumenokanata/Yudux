package indi.yume.yudux

import indi.yume.yudux.DSL.emptyEffect
import indi.yume.yudux.DSL.emptyReducer
import indi.yume.yudux.collection.*
import indi.yume.yudux.functions.BiConsumer
import indi.yume.yudux.store.*
import io.reactivex.Single

/**
 * Created by yume on 17-4-28.
 */

object DSL {
    @JvmStatic
    fun <R : MultiReal, D : Depends<R>, State, Data> action(groupId: String,
                                                            depends: D,
                                                            effect: (R, State) -> Single<Data>,
                                                            reducer: (Data, State) -> State): LazyAction<R, D, State, Data> =
            LazyAction(depends, effect, reducer, groupId)

    @JvmStatic
    fun <R : MultiReal, D : Depends<R>, State, Data> action(
            depends: D,
            effect: (R, State) -> Single<Data>,
            reducer: (Data, State) -> State): LazyAction<R, D, State, Data> = LazyAction(depends, effect, reducer)

    @JvmStatic
    fun <State, Data> action(effect: (State) -> Single<Data>,
                             reducer: (Data, State) -> State): Action<State, Data> =
            StoreActionDefault(effect, reducer)

    @JvmStatic
    fun <State, Data> action(groupId: String,
                             effect: (State) -> Single<Data>,
                             reducer: (Data, State) -> State): StoreAction<State> =
            StoreActionDefault(effect, reducer, groupId)

    @JvmStatic
    fun <R : MultiReal, D : Depends<R>, State> effect(depends: D,
                                                      effect: BiConsumer<R, State>): LazyAction<R, D, State, indi.yume.yudux.functions.Unit> =
            LazyAction(depends,
                    { real, oldState ->
                        effect.accept(real, oldState)
                        Single.just(indi.yume.yudux.functions.Unit.unit())
                    }, emptyReducer())

    @JvmStatic
    fun <R : MultiReal, D : Depends<R>, State, SubState, Data> action(
            depends: D,
            selector: (State) -> SubState,
            setter: (State, SubState) -> State,
            effect: (R, SubState) -> Single<Data>,
            reducer: (Data, SubState) -> SubState): LazyAction<R, D, State, Data> =
            LazyAction(depends,
                    { real, state -> effect(real, selector(state)) },
                    { data, state -> setter(state, reducer(data, selector(state))) })

    @JvmStatic
    fun <Key, State, Data> create(
            repo: ContextCollection<Key>,
            lazyAction: LazyAction<RealWorld<Key>, SingleDepends<Key>, State, Data>): DependActionImpl<RealWorld<Key>, State, Data> =
            create(null, repo, lazyAction)

    @JvmStatic
    fun <Key, State, Data> create(
            groupId: String?,
            repo: ContextCollection<Key>,
            lazyAction: LazyAction<RealWorld<Key>, SingleDepends<Key>, State, Data>): DependActionImpl<RealWorld<Key>, State, Data> =
        DependActionImpl(
                groupId ?: lazyAction.groupId,
                lazyAction.depends.makeProvider(repo),
                lazyAction.effect,
                lazyAction.reducer)

    @JvmStatic
    fun <K1, K2, State, Data> create(
            repo1: ContextCollection<K1>,
            repo2: ContextCollection<K2>,
            lazyAction: LazyAction<BiReal<K1, K2>, BiDepends<K1, K2>, State, Data>): DependActionImpl<BiReal<K1, K2>, State, Data> =
            create(null, repo1, repo2, lazyAction)

    @JvmStatic
    fun <K1, K2, State, Data> create(
            groupId: String?,
            repo1: ContextCollection<K1>,
            repo2: ContextCollection<K2>,
            lazyAction: LazyAction<BiReal<K1, K2>, BiDepends<K1, K2>, State, Data>): DependActionImpl<BiReal<K1, K2>, State, Data> =
            DependActionImpl(
                    groupId ?: lazyAction.groupId,
                    lazyAction.depends.makeProvider(repo1, repo2),
                    lazyAction.effect,
                    lazyAction.reducer)

    @JvmStatic
    fun <K1, K2, K3, State, Data> create(
            repo1: ContextCollection<K1>,
            repo2: ContextCollection<K2>,
            repo3: ContextCollection<K3>,
            lazyAction: LazyAction<Real3<K1, K2, K3>, Depends3<K1, K2, K3>, State, Data>): DependActionImpl<Real3<K1, K2, K3>, State, Data> =
            create(null, repo1, repo2, repo3, lazyAction)

    @JvmStatic
    fun <K1, K2, K3, State, Data> create(
            groupId: String?,
            repo1: ContextCollection<K1>,
            repo2: ContextCollection<K2>,
            repo3: ContextCollection<K3>,
            lazyAction: LazyAction<Real3<K1, K2, K3>, Depends3<K1, K2, K3>, State, Data>): DependActionImpl<Real3<K1, K2, K3>, State, Data> =
            DependActionImpl(
                    groupId ?: lazyAction.groupId,
                    lazyAction.depends.makeProvider(repo1, repo2, repo3),
                    lazyAction.effect,
                    lazyAction.reducer)

    @JvmStatic
    fun <State> forceRender(store: Store<State>): Unit = store.dispatch(renderAction())

    @JvmStatic
    fun <State, Data> mainAction(data: Data,
                                 reducer: (Data, State) -> State): Action<State, Data> =
            StoreActionDefault<State, Data>({ Single.just(data) }, reducer)

    @JvmStatic
    fun <State> emptyEffect(): (State) -> Single<Unit> = { Single.just(Unit) }

    @JvmStatic
    fun <Data, State> emptyReducer(): (Data, State) -> State = { _, s -> s }

    @JvmStatic
    fun <T> plus(collection: Collection<T>, element: T): List<T> = collection + element

    @JvmStatic
    fun <T> plus(collection: Collection<T>, elements: Iterable<T>): List<T> = collection + elements

    @JvmStatic
    fun <Key> depends(vararg depends: Key): Array<out Key> = depends

    @JvmStatic
    fun <Key> lazy(vararg depends: Key): SingleDepends<Key> = SingleDepends(depends.toSet())

    @JvmStatic
    fun <K1, K2> lazy(depends1: Array<out K1>, depends2: Array<out K2>): BiDepends<K1, K2> =
            BiDepends(depends1.toSet(), depends2.toSet())

    @JvmStatic
    fun <K1, K2, K3> lazy(depends1: Array<out K1>,
                          depends2: Array<out K2>,
                          depends3: Array<out K3>): Depends3<K1, K2, K3> =
            Depends3(depends1.toSet(), depends2.toSet(), depends3.toSet())

    @JvmStatic
    fun <Key, T> ready(key: Key, value: T): Ready<Key, T> = Ready(key, value)
}

fun <State> renderAction() : StoreActionDefault<State, Unit> = StoreActionDefault(emptyEffect(), emptyReducer())
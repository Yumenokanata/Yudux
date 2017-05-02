package indi.yume.yudux.store

import indi.yume.yudux.functions.Subscriber
import indi.yume.yudux.functions.Subscription
import io.reactivex.Single

/**
 * Created by yume on 17-4-28.
 */

abstract class Action<Real, State, Data> {
    val groupId: String

    constructor(groupId: String) {
        this.groupId = groupId
    }

    constructor() {
        this.groupId = GROUP_ID_DEFAULT
    }

    fun doReduce(data: Any, oldState: Any): State {
        return reduce(data as Data, oldState as State)
    }

    abstract fun effect(realworld: Real, oldState: State): Single<Data>

    abstract fun reduce(data: Data, oldState: State): State
}


open class SubStore<Real, State>(val mainStore: Store<State>, val realWorld: Real) {

    @SafeVarargs
    fun dispatchTransaction(vararg actions: Action<Real, State, *>) {
        dispatchTransaction(null, *actions)
    }

    @SafeVarargs
    fun dispatchTransaction(groupId: String?, vararg actions: Action<Real, State, *>) {
        var list = emptyList<StoreAction<State>>()
        for (item in actions)
            list += StoreActionImpl(realWorld, item)
        mainStore.dispatch(TransactionAction<State>(groupId, list))
    }

    fun subscribe(subscriber: Subscriber<State>): Subscription {
        return mainStore.subscribe(subscriber)
    }

    fun <Data> dispatch(action: Action<Real, State, Data>) {
        mainStore.dispatch(StoreActionImpl<Real, State, Data>(realWorld, action))
    }
}

class StoreActionImpl<Real, State, Data>(val realworld: Real,
                                         val action: Action<Real, State, Data>) : StoreAction<State> {

    override fun groupId(): String {
        return action.groupId
    }

    override fun effect(oldState: State): Single<Any> {
        return action.effect(realworld, oldState).map<Any> { d -> d }
    }

    override fun reduce(data: Any, oldState: State): State {
        return action.reduce(data as Data, oldState)
    }

    override fun makeReduce(data: Any): Reducer<State> {
        return { s -> reduce(data, s) }
    }
}
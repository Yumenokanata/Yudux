package indi.yume.yudux.store

import android.os.Handler
import android.os.Looper
import indi.yume.yudux.Log
import indi.yume.yudux.functions.Subscriber
import indi.yume.yudux.functions.Subscription
import io.reactivex.BackpressureStrategy
import io.reactivex.Single
import io.reactivex.internal.schedulers.NewThreadScheduler
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.function.BiFunction

/**
 * Created by yume on 17-4-27.
 */

val GROUP_ID_DEFAULT = "default"

class TransactionAction<State> : StoreAction<State> {
    private val groupId: String
    val list: List<StoreAction<State>>

    constructor(groupId: String?, list: List<StoreAction<State>>) {
        this.groupId = groupId ?: GROUP_ID_DEFAULT
        this.list = list
    }

    constructor(list: List<StoreAction<State>>) {
        this.groupId = GROUP_ID_DEFAULT
        this.list = list
    }

    override fun groupId(): String {
        return groupId
    }

    override fun effect(oldState: State): Single<Any> {
        throw UnsupportedOperationException("TransactionAction can not be real effect")
    }

    override fun reduce(o: Any, oldState: State): State {
        throw UnsupportedOperationException("TransactionAction can not be real reduce")
    }

    override fun makeReduce(data: Any): Reducer<State> {
        return { s -> reduce(data, s) }
    }
}

typealias Reducer<S> = (S) -> S

interface StoreAction<S> {
    fun groupId(): String

    fun effect(oldState: S): Single<Any>

    fun reduce(data: Any, oldState: S): S

    fun makeReduce(data: Any): Reducer<S>
}

class Store<State> {

    val eventSubject: Subject<StoreAction<State>> = PublishSubject.create()

    var subscribers: List<Subscriber<State>>  = emptyList()

    var currentState: State

    val UIHandler: Handler by lazy { Handler(Looper.getMainLooper()) }

    constructor(initState: State) {
        currentState = initState;
        eventSubject.toFlowable(BackpressureStrategy.BUFFER)
                .groupBy { act -> act.groupId() }
                .flatMap { group ->
                    group.observeOn(NewThreadScheduler())
                            .scan<StateData<State>>(StateData(getState(), getState()))
                            { stateData, action ->
                                try {
                                    var actionName = action::class.java.simpleName
                                    if (action is StoreActionImpl<*, State, *>)
                                        actionName = action.action::class.java.simpleName
                                    Log.d(TAG, "group: " + group.getKey() + " | start invoke action: " + actionName);

                                    handAction(stateData, action);
                                } catch (e: Exception) {
                                    Log.e(TAG, "Deal event error: ", e);
                                    stateData;
                                }
                            }
                }
                .subscribe({ },
                        { t -> Log.e(TAG, "Deal event error: ", t) },
                        { Log.d(TAG, "Loading Store event looper has dead.") })
    }

    private fun handAction(stateData: StateData<State>, action: StoreAction<State>): StateData<State> {
        if(action is TransactionAction) {
            var stateDataTemp = stateData;
            val list: List<StoreAction<State>>  = action.list;
            for(aItem in list)
                stateDataTemp = handAction(stateDataTemp, aItem);
            return stateDataTemp;
        }

        val data = action
                .effect(stateData.newState)
                .blockingGet();

        setState(action.makeReduce(data));

        return StateData(stateData.newState, getState());
    }

    private fun setState(reducer: Reducer<State>) {
        var state: State? = null;
        synchronized (this) {
            currentState = reducer(getState())
            state = currentState
        }

        renderUi(state!!)
    }

    private fun renderUi(state: State) {
        if(Looper.myLooper() != Looper.getMainLooper()) {
            UIHandler.post { renderUi(state) }
            return
        }

        for (i in 0..subscribers.size - 1)
            subscribers.get(i).onStateChange(state);
    }

    fun getState(): State = synchronized(this) { currentState }

    fun dispatch(action: StoreAction<State>) =
            eventSubject.onNext(action)

    fun subscribe(subscriber: Subscriber<State>): Subscription {
        subscribers += subscriber
        return object: Subscription {
            override fun unsubscribe() {
                subscribers -= subscriber
            }
        }
    }

    fun unsubscribe() = eventSubject.onComplete()

    companion object {
        val TAG = Store::class.java.simpleName
    }
}

data class StateData<State>(val oldState: State, val newState: State)
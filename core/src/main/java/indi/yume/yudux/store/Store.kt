package indi.yume.yudux.store

import android.os.Handler
import android.os.Looper
import indi.yume.yudux.DSL.emptyEffect
import indi.yume.yudux.DSL.emptyReducer
import indi.yume.yudux.Log
import indi.yume.yudux.functions.Subscriber
import indi.yume.yudux.functions.Subscription
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
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

    override fun anyEffect(oldState: State): Single<Any> {
        throw UnsupportedOperationException("TransactionAction can not be real effect")
    }

    override fun anyReduce(o: Any, oldState: State): State {
        throw UnsupportedOperationException("TransactionAction can not be real reduce")
    }

    override fun makeReduce(data: Any): Reducer<State> {
        return { s -> anyReduce(data, s) }
    }
}

typealias Reducer<S> = (S) -> S

fun <S> Reducer<S>.bind(reducer: Reducer<S>): Reducer<S> = { reducer(this(it)) }

interface StoreAction<S> {
    fun groupId(): String

    fun anyEffect(oldState: S): Single<Any>

    fun anyReduce(data: Any, oldState: S): S

    fun makeReduce(data: Any): Reducer<S>
}

abstract class Action<State, Data>(val groupId: String = GROUP_ID_DEFAULT) : StoreAction<State> {

    override fun groupId(): String = groupId

    final override fun anyEffect(oldState: State): Single<Any> =
            effect(oldState).map { it as Any }

    final override fun anyReduce(data: Any, oldState: State): State =
            reduce(data as Data, oldState)

    final override fun makeReduce(data: Any): Reducer<State> =
            { s -> anyReduce(data, s) }

    abstract fun effect(oldState: State): Single<Data>

    abstract fun reduce(data: Data, oldState: State): State
}

class StoreActionDefault<State, Data>(val effector: (State) -> Single<Data>,
                                      val reducer: (Data, State) -> State,
                                      groupId: String = GROUP_ID_DEFAULT) : Action<State, Data>(groupId){
    override fun effect(oldState: State): Single<Data> = effector(oldState)

    override fun reduce(data: Data, oldState: State): State = reducer(data, oldState)
}

class Store<State> {

    val eventSubject: Subject<StoreAction<State>> = PublishSubject.create()

    var subscribers: List<Subscriber<State>>  = emptyList()

    private var currentState: State

    val UIHandler: Handler by lazy { Handler(Looper.getMainLooper()) }

    constructor(initState: State) {
        currentState = initState;
        eventSubject.toFlowable(BackpressureStrategy.BUFFER)
                .groupBy { act -> act.groupId() }
                .flatMap { group ->
                    group.observeOn(NewThreadScheduler())
                            .scan<State>(getState())
                            { oldState, action ->
                                val state = getState()
                                try {
                                    val actionName = action::class.java.simpleName
                                    Log.d(TAG, "group: " + group.getKey() + " | start invoke action: " + actionName);

                                    setState(handAction(state, action))
                                    getState()
                                } catch (e: Exception) {
                                    Log.e(TAG, "Deal event error: ", e)
                                    state
                                }
                            }
                }
                .subscribe({ },
                        { t -> Log.e(TAG, "Deal event error: ", t) },
                        { Log.d(TAG, "Loading Store event looper has dead.") })
    }

    private fun handAction(stateData: State, action: StoreAction<State>): Reducer<State> {
        if(action is TransactionAction) {
            var stateDataTemp = stateData
            var reducer: Reducer<State> = { it }
            val list: List<StoreAction<State>>  = action.list
            for(aItem in list) {
                val subReducer = handAction(stateDataTemp, aItem)
                stateDataTemp = subReducer(stateDataTemp)
                reducer = reducer.bind(subReducer)
            }
            return reducer
        }

        val data = action
                .anyEffect(stateData)
                .blockingGet()

        return action.makeReduce(data)
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

    @SafeVarargs
    fun dispatchTransaction(vararg actions: StoreAction<State>) =
            dispatchTransaction(null, *actions)

    @SafeVarargs
    fun dispatchTransaction(groupId: String?, vararg actions: StoreAction<State>) =
            dispatch(TransactionAction<State>(groupId, actions.toList()))

    fun subscribe(subscriber: Subscriber<State>): Subscription {
        subscribers += subscriber
        return object: Subscription {
            override fun unsubscribe() {
                subscribers -= subscriber
            }
        }
    }

    fun subscribe(): Observable<State> {
        val subject = PublishSubject.create<State>()
        val subscription = subscribe(Subscriber<State> { state -> subject.onNext(state) })
        subject.doOnDispose { subscription.unsubscribe() }

        return subject
    }

    fun <T> subscribeUntilChange(mapper: (State) -> T): Observable<T> =
            subscribe().map(mapper).distinctUntilChanged()

    fun unsubscribe() = eventSubject.onComplete()

    companion object {
        val TAG = Store::class.java.simpleName
    }
}

data class StateData<State>(val oldState: State, val newState: State)
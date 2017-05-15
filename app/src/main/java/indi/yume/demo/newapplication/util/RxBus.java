package indi.yume.demo.newapplication.util;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by yume on 15/11/5.
 */
public class RxBus {
    private Subject<Object> bus = PublishSubject.create().toSerialized();

    private static RxBus rxBus;
    private RxBus(){}

    //http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html
    public static synchronized RxBus getInstance(){
        if(rxBus == null)
            rxBus = new RxBus();
        return rxBus;
    }

    public void send(Object event){
        bus.onNext(event);
    }

    public <T> Observable<T> toObservable(Class<T> clazz){
        return bus
                .filter(clazz::isInstance)
                .map(o -> (T)o)
                .doOnError(LogUtil::e)
                .observeOn(AndroidSchedulers.mainThread());
    }
}

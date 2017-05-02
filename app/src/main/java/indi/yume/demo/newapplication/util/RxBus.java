//package indi.yume.demo.newapplication.util;
//
//import rx.Observable;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.subjects.PublishSubject;
//import rx.subjects.SerializedSubject;
//import rx.subjects.Subject;
//
///**
// * Created by yume on 15/11/5.
// */
//public class RxBus {
//    private Subject<Object, Object> bus = new SerializedSubject<>(PublishSubject.create());
//
//    private static RxBus rxBus;
//    private RxBus(){}
//
//    //http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html
//    public static synchronized RxBus getInstance(){
//        if(rxBus == null)
//            rxBus = new RxBus();
//        return rxBus;
//    }
//
//    public void send(Object event){
//        bus.onNext(event);
//    }
//
//    public <T> Observable<T> toObservable(Class<T> clazz){
//        return bus.asObservable()
//                .filter(clazz::isInstance)
//                .map(o -> (T)o)
//                .doOnError(LogUtil::e)
//                .observeOn(AndroidSchedulers.mainThread());
//    }
//}

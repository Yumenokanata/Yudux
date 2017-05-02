package indi.yume.demo.newapplication;

import org.junit.Test;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        BehaviorSubject<Object> lifecycleSubject = BehaviorSubject.create();
        Observable<Object> shareObs = lifecycleSubject.share();
        Completable completable = shareObs.doOnNext(o -> System.out.println("doOnNext: " + o))
                .ofType(String.class)
                .flatMapCompletable(s -> {
                    System.out.println("flatMapCompletable: " + s);
                    return Completable.complete();
                });
        Disposable disposable = completable.doOnComplete(() -> System.out.println("Completable complete"))
                .doOnSubscribe(dis -> System.out.println("doOnSubscribe"))
                .doOnDispose(() -> System.out.println("Completable doOnDispose"))
                .subscribe(() -> System.out.println("Completable subscribe complete"));

        lifecycleSubject.onNext("event1");
        lifecycleSubject.onNext("event2");

        System.out.printf("lifecycleSubject: hasObservers=%s\ndisposable: %s",
                String.valueOf(lifecycleSubject.hasObservers()),
                disposable.isDisposed());
    }
}
//package indi.yume.demo.newapplication.util;
//
//import android.content.Context;
//import android.content.DialogInterface;
//import android.support.v7.app.AppCompatActivity;
//import android.text.Html;
//
//import com.annimon.stream.Stream;
//import com.google.common.collect.ImmutableList;
//import com.google.common.collect.ObjectArrays;
//
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import indi.yume.tools.avocado.model.tuple.Tuple2;
//import io.reactivex.Observable;
//import io.reactivex.ObservableTransformer;
//import io.reactivex.functions.Consumer;
//import lombok.Data;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.UtilityClass;
//
//import static indi.yume.demo.newapplication.util.DialogUtil.CALLBACK_TYPE_OK;
//
///**
// * Created by yume on 16-7-21.
// *
// * Recommend using {@link DealErrorUtil#dealErrorRetry(Context)}.
// */
//@UtilityClass
//public class DealErrorUtil {
//    private static final Class[] unCheckError = new Class[]{
//            NullPointerException.class
//    };
//
//    /**
//     * Just show error message, don't do anything else.
//     */
//    public static <T> ObservableTransformer<T, T> dealError() {
//        return obs -> obs.doOnError(DealErrorUtil::sendError);
//    }
//
//    /**
//     * Show error message, and will be retry when offline and click "retry" button.
//     *
//     * Recommended use.
//     */
//    public static <T> ObservableTransformer<T, T> dealErrorRetry(Context context) {
//        return obs -> obs
//                .onErrorResumeNext(t -> {
//                    if(ApiUtil.isOfflineError(t))
//                        return dealNetErrorRetryObservable(context, t)
//                                .flatMap(e -> {
//                                    if(e.getButton() == DialogUtil.CALLBACK_TYPE_RETRY)
//                                        return Observable.<T>error(new RetryThrowable(t));
//                                    return Observable.<T>error(t);
//                                });
//                    return Observable.<T>error(t)
//                            .doOnError(DealErrorUtil::sendError);
//                })
//                .retry((count, error) -> error instanceof RetryThrowable);
//    }
//
//    /**
//     * Show error message.
//     * Don't do anything else for Observable.
//     *
//     * @param doOnClickRetry action when click retry button.
//     */
//    public static <T> ObservableTransformer<T, T> dealErrorRetry(
//            Context context,
//            Consumer<DialogInterface> doOnClickRetry) {
//        return obs -> obs
//                .onErrorResumeNext(t -> {
//                    if(ApiUtil.isOfflineError(t))
//                        return dealNetErrorRetryObservable(context, t)
//                                .flatMap(e -> {
//                                    if(e.getButton() == DialogUtil.CALLBACK_TYPE_RETRY)
//                                        doOnClickRetry.accept(e.getDialog());
//                                    return Observable.<T>error(t);
//                                });
//                    return Observable.<T>error(t)
//                            .doOnError(DealErrorUtil::sendError);
//                });
//    }
//
//    /**
//     * Just show error message dialog.
//     */
//    public static void dealNetError(Context context,
//                                    Throwable t) {
//        Tuple2<String, String> msg = ApiUtil.errorMessage(context, t);
//
//        DialogUtil.showMessageDialog(
//                context,
//                Html.fromHtml(msg.getData1()),
//                Html.fromHtml(msg.getData2()))
//                .show();
//    }
//
//    /**
//     * Show retry error message dialog. Return DialogEvent of button witch be clicked.
//     */
//    public static Observable<DialogUtil.DialogEvent> dealNetErrorRetryObservable(
//            Context context,
//            Throwable t) {
//        Tuple2<String, String> msg = ApiUtil.errorMessage(context, t);
//
//        return DialogUtil.showMessageDialogObs(context,
//                msg.getData1(),
//                msg.getData2(),
//                DialogUtil.CALLBACK_TYPE_CANCEL,
//                DialogUtil.CALLBACK_TYPE_RETRY);
//    }
//
//    /**
//     * Show error message dialog, dialog has ok button.
//     * Return DialogEvent of ok button when be clicked.
//     */
//    public static Observable<DialogInterface> dealNetErrorObservable(Context context,
//                                                                     Throwable t) {
//        Tuple2<String, String> msg = ApiUtil.errorMessage(context, t);
//
//        return DialogUtil.showMessageDialogObs(context, msg.getData1(), msg.getData2(), CALLBACK_TYPE_OK)
//                .map(DialogUtil.DialogEvent::getDialog);
//    }
//
//    /**
//     * Show error message dialog, dialog has ok button. Don't do anything else.
//     *
//     * @param doOnClickOk action when click ok button.
//     */
//    public static <T> ObservableTransformer<T, T> dealError(Context context,
//                                                             Consumer<DialogInterface> doOnClickOk) {
//        return obs -> obs.onErrorResumeNext(t -> dealNetErrorObservable(context, t)
//                .doOnNext(doOnClickOk)
//                .flatMap(d -> Observable.<T>error(t)));
//    }
//
//    /**
//     * Show error message dialog, dialog has cancel and retry button.
//     * Don't do anything else for Observable.
//     *
//     * @param doOnClickRetry action when click retry button.
//     */
//    public static <T> ObservableTransformer<T, T> retryError(
//            Context context,
//            Consumer<DialogInterface> doOnClickRetry) {
//        return obs -> obs.onErrorResumeNext(t -> dealNetErrorRetryObservable(context, t)
//                .flatMap(e -> {
//                    if(e.getButton() == DialogUtil.CALLBACK_TYPE_RETRY)
//                        doOnClickRetry.call(e.getDialog());
//                    return Observable.<T>error(t);
//                }));
//    }
//
//    /**
//     * Show error message dialog, dialog has cancel and retry button.
//     * Will be retry Observable when click retry button.
//     */
//    public static <T> ObservableTransformer<T, T> retryError(Context context) {
//        return obs -> obs.onErrorResumeNext(t -> dealNetErrorRetryObservable(context, t)
//                .flatMap(e -> {
//                    if(e.getButton() == DialogUtil.CALLBACK_TYPE_RETRY)
//                        return Observable.<T>error(new RetryThrowable(t));
//                    return Observable.<T>error(t);
//                })
//        ).retry((count, error) -> error instanceof RetryThrowable);
//    }
//
//    @RequiredArgsConstructor
//    private class RetryThrowable extends Throwable {
//        private final Throwable error;
//    }
//
//    public static Consumer<Throwable> doOnError() {
//        return DealErrorUtil::sendError;
//    }
//
//    public static void sendError(Throwable t) {
//        RxBus.getInstance().send(new ErrorEvent(t));
//    }
//
//    @SafeVarargs
//    public static DealError bindDealErrorWithDefault(AppCompatActivity activity, Consumer<ErrorEvent>... actL) {
//        return new DealError(RxBus.getInstance().toObservable(ErrorEvent.class).throttleFirst(500, TimeUnit.MILLISECONDS),
//                ObjectArrays.concat(actL,
//                        e -> dealThrowable(activity, e)));
//    }
//
//    @SafeVarargs
//    public static DealError bindDealError(Consumer<ErrorEvent>... actL) {
//        return new DealError(
//                RxBus.getInstance()
//                        .toObservable(ErrorEvent.class)
//                        .throttleFirst(500, TimeUnit.MILLISECONDS),
//                actL);
//    }
//
//    private static void dealThrowable(Context context, ErrorEvent e) {
//        for(Class clazz : unCheckError)
//            if(clazz.isInstance(e.getThrowable()))
//                return;
//
//        dealNetError(context, e.getThrowable());
//    }
//
//    public static class DealError {
//        private final List<Subscription> subList;
//
//        public DealError(Observable<ErrorEvent> observable, Consumer<ErrorEvent>... actL) {
//            subList = ImmutableList.copyOf(Stream.of(actL)
//                    .map(a -> observable
//                            .subscribe(a,
//                            LogUtil::e))
//                    .iterator());
//        }
//
//        public void unbind() {
//            Stream.of(subList).filterNot(Subscription::isUnsubscribed).forEach(Subscription::unsubscribe);
//        }
//    }
//}
//
//@Data
//class ErrorEvent {
//    private final Throwable throwable;
//}

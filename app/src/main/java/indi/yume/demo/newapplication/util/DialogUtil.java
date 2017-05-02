//package indi.yume.demo.newapplication.util;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.graphics.drawable.ColorDrawable;
//import android.support.annotation.IntDef;
//import android.support.annotation.LayoutRes;
//import android.support.annotation.StringRes;
//import android.support.v7.app.AlertDialog;
//import android.view.Window;
//import android.view.WindowManager;
//
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//
//import indi.yume.demo.newapplication.R;
//import lombok.Data;
//import rx.Observable;
//import rx.functions.Consumer;
//
///**
// * Created by yume on 16-11-15.
// */
//public class DialogUtil {
//
//    public static final int CALLBACK_TYPE_CANCEL = 0x100;
//    public static final int CALLBACK_TYPE_OK     = 0x010;
//    public static final int CALLBACK_TYPE_RETRY  = 0x001;
//
//    @IntDef({CALLBACK_TYPE_CANCEL, CALLBACK_TYPE_OK, CALLBACK_TYPE_RETRY})
//    @Retention(RetentionPolicy.SOURCE)
//    public @interface CallbackType{}
//
//    public static Dialog showProgressDialog(Context context) {
//        Dialog dialog = createFullDialog(context,
//                R.layout.loading_layout,
//                true);
//        dialog.show();
//        return dialog;
//    }
//
//    public static Dialog createFullDialog(Context context,
//                                          @LayoutRes int layout) {
//        return createFullDialog(context, layout, false);
//    }
//
//    public static Dialog createFullDialog(Context context,
//                                          @LayoutRes int layout,
//                                          boolean clearBackground) {
//        final Dialog dialog = new Dialog(context);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        Window window = dialog.getWindow();
//        if(window != null) {
//            if(clearBackground)
//                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        }
//        dialog.setContentView(layout);
//        dialog.setCancelable(false);
//
//        return dialog;
//    }
//
//    public static Dialog showMessageDialog(Context context,
//                                           @StringRes int messageRes) {
//        return showMessageDialog(context, null, context.getString(messageRes));
//    }
//
//    public static Dialog showMessageDialog(Context context,
//                                           @StringRes int titleRes,
//                                           @StringRes int messageRes) {
//        return showMessageDialog(context,
//                context.getString(titleRes),
//                context.getString(messageRes));
//    }
//
//    public static Dialog showMessageDialog(Context context,
//                                           @StringRes int titleRes,
//                                           CharSequence message) {
//        return showMessageDialog(context, context.getString(titleRes), message);
//    }
//
//    public static Dialog showMessageDialog(Context context,
//                                           CharSequence message) {
//        return showMessageDialog(context, null, message);
//    }
//
//    public static Dialog showMessageDialog(Context context,
//                                           CharSequence title,
//                                           CharSequence message) {
//        return new AlertDialog.Builder(context)
//                .setTitle(title)
//                .setMessage(message)
//                .setPositiveButton(R.string.dialog_button_ok_en, null)
//                .show();
//    }
//
//    public static Observable<DialogEvent> showMessageDialogObs(Context context,
//                                                               CharSequence title,
//                                                               CharSequence message,
//                                                               @CallbackType int... buttons) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context)
//                .setTitle(title)
//                .setMessage(message);
//
//        return Observable.<DialogEvent>create(sub -> {
//            configureBtn(builder,
//                    sub::onNext,
//                    buttons);
//            builder.setOnDismissListener(dialog -> sub.onCompleted());
//            builder.show();
//        });
//    }
//
//    private static void configureBtn(AlertDialog.Builder builder,
//                                     Consumer<DialogEvent> callback,
//                                     int[] buttons) {
//        if(buttons.length == 0) {
//            builder.setPositiveButton(R.string.dialog_button_ok_en, null);
//            return;
//        }
//
//        for (int btn : buttons) {
//            if(checkFlag(btn, CALLBACK_TYPE_CANCEL))
//                builder.setNegativeButton(R.string.dialog_button_cancel,
//                        (dialog, which) -> callback.call(new DialogEvent(dialog, CALLBACK_TYPE_CANCEL)));
//            else if(checkFlag(btn, CALLBACK_TYPE_OK))
//                builder.setPositiveButton(R.string.dialog_button_ok_en,
//                        (dialog, which) -> {
//                            callback.call(new DialogEvent(dialog, CALLBACK_TYPE_OK));
//                            dialog.dismiss();
//                        });
//            else if(checkFlag(btn, CALLBACK_TYPE_RETRY))
//                builder.setPositiveButton(R.string.dialog_button_retry,
//                        (dialog, which) -> callback.call(new DialogEvent(dialog, CALLBACK_TYPE_RETRY)));
//        }
//    }
//
//    private static boolean checkFlag(int flag, int target) {
//        return (flag & target) != 0;
//    }
//
//    @Data
//    public static class DialogEvent {
//        private final DialogInterface dialog;
//        @CallbackType
//        private final int button;
//    }
//}

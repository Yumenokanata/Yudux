package indi.yume.demo.newapplication.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;

import com.annimon.stream.function.Consumer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.atomic.AtomicInteger;

import indi.yume.demo.newapplication.R;
import io.reactivex.Single;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import lombok.Data;

/**
 * Created by yume on 16-11-15.
 */
public class DialogUtil {

    public static final int CALLBACK_TYPE_CANCEL   = 0x1;
    public static final int CALLBACK_TYPE_OK       = 0x1 << 1;
    public static final int CALLBACK_TYPE_RETRY    = 0x1 << 2;
    public static final int CALLBACK_TYPE_DISMISS  = 0x1 << 3;

    @IntDef({CALLBACK_TYPE_CANCEL, CALLBACK_TYPE_OK, CALLBACK_TYPE_RETRY, CALLBACK_TYPE_DISMISS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CallbackType{}

    private static Dialog progressDialog;
    private static final AtomicInteger dialogCount = new AtomicInteger(0);

    public static Dialog showProgressDialogNow(Context context) {
        Dialog dialog = createFullDialog(context,
                R.layout.loading_layout,
                true);
        dialog.show();
        return dialog;
    }

    public static  <T> SingleTransformer<T, T> composeNetProgressDialog(Context context) {
        return obs -> obs
                .doOnSubscribe(disposable -> showProgressDialog(context))
                .doOnError(t -> DialogUtil.hideProgressDialog())
                .doOnSuccess(disposable -> DialogUtil.hideProgressDialog());
    }

    public static void showProgressDialog(final Context context) {
        if (progressDialog != null)
            return;
        AndroidSchedulers.mainThread().createWorker().schedule(() -> {
            if (progressDialog == null && dialogCount.get() == 0) {
                progressDialog = DialogUtil.showProgressDialogNow(context);
            }
            dialogCount.incrementAndGet();
        });
    }

    public static void hideProgressDialog() {
        AndroidSchedulers.mainThread().createWorker().schedule(() -> {
            dialogCount.decrementAndGet();
            if (progressDialog != null && dialogCount.get() == 0) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        });
    }

    public static Dialog createFullDialog(Context context,
                                          @LayoutRes int layout) {
        return createFullDialog(context, layout, false);
    }

    public static Dialog createFullDialog(Context context,
                                          @LayoutRes int layout,
                                          boolean clearBackground) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        if(window != null) {
            if(clearBackground)
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        dialog.setContentView(layout);
        dialog.setCancelable(false);

        return dialog;
    }

    public static Dialog showMessageDialog(Context context,
                                           @StringRes int messageRes) {
        return showMessageDialog(context, null, context.getString(messageRes));
    }

    public static Dialog showMessageDialog(Context context,
                                           @StringRes int titleRes,
                                           @StringRes int messageRes) {
        return showMessageDialog(context,
                context.getString(titleRes),
                context.getString(messageRes));
    }

    public static Dialog showMessageDialog(Context context,
                                           @StringRes int titleRes,
                                           CharSequence message) {
        return showMessageDialog(context, context.getString(titleRes), message);
    }

    public static Dialog showMessageDialog(Context context,
                                           CharSequence message) {
        return showMessageDialog(context, null, message);
    }

    public static Dialog showMessageDialog(Context context,
                                           CharSequence title,
                                           CharSequence message) {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_button_ok_en, null)
                .show();
    }

    public static Single<DialogEvent> showMessageDialogObs(Context context,
                                                          CharSequence title,
                                                          CharSequence message,
                                                          @CallbackType int... buttons) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message);

        return Single.<DialogEvent>create(emitter -> {
            configureBtn(builder,
                    emitter::onSuccess,
                    buttons);
            builder.setOnCancelListener(dialog ->
                    emitter.onSuccess(new DialogEvent(dialog, CALLBACK_TYPE_DISMISS)));
            builder.setOnDismissListener(dialog -> {
                if(!emitter.isDisposed())
                    emitter.onError(new RuntimeException("Not emit any dialog event util dialog dismiss"));
            });
            builder.show();
        });
    }

    private static void configureBtn(AlertDialog.Builder builder,
                                     Consumer<DialogEvent> callback,
                                     int[] buttons) {
        if(buttons.length == 0) {
            builder.setPositiveButton(R.string.dialog_button_ok_en, null);
            return;
        }

        for (int btn : buttons) {
            if(checkFlag(btn, CALLBACK_TYPE_CANCEL))
                builder.setNegativeButton(R.string.dialog_button_cancel,
                        (dialog, which) -> callback.accept(new DialogEvent(dialog, CALLBACK_TYPE_CANCEL)));
            else if(checkFlag(btn, CALLBACK_TYPE_OK))
                builder.setPositiveButton(R.string.dialog_button_ok_en,
                        (dialog, which) -> {
                            callback.accept(new DialogEvent(dialog, CALLBACK_TYPE_OK));
                            dialog.dismiss();
                        });
            else if(checkFlag(btn, CALLBACK_TYPE_RETRY))
                builder.setPositiveButton(R.string.dialog_button_retry_en,
                        (dialog, which) -> callback.accept(new DialogEvent(dialog, CALLBACK_TYPE_RETRY)));
        }
    }

    private static boolean checkFlag(int flag, int target) {
        return (flag & target) != 0;
    }

    @Data
    public static class DialogEvent {
        private final DialogInterface dialog;
        @CallbackType
        private final int button;
    }
}

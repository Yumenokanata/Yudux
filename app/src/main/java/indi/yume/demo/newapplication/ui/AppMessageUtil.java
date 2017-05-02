package indi.yume.demo.newapplication.ui;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

import indi.yume.demo.newapplication.R;
import indi.yume.demo.newapplication.manager.api.base.NetErrorException;
import indi.yume.demo.newapplication.model.api.CommonModel;
//import indi.yume.demo.newapplication.util.DialogUtil;
import indi.yume.demo.newapplication.util.LogUtil;
import indi.yume.demo.newapplication.util.StringUtil;
import okhttp3.Response;
//import rx.Observable;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.functions.Action0;

/**
 * Created by yume on 15/9/16.
 */
public class AppMessageUtil {
//    private Context context;
//    private transient Dialog progressDialog;
//    private String errorTitle = "エラーが発生しました";
//    private final AtomicInteger dialogCount = new AtomicInteger(0);
//
//    public AppMessageUtil(Context context) {
//        this.context = context;
//    }
//
//    public void dealResultError(Response response) {
//        String result = null;
//
//        result = StringUtil.inputStream2String(response.body().byteStream());
//
//        JSONObject jsonObject = null;
//        try {
//            jsonObject = new JSONObject(result);
//        } catch (JSONException e) {
//            LogUtil.e(e);
//            return;
//        }
//        try {
//            String title = jsonObject.getString("title");
//            String msg = jsonObject.getString("message");
//            showDialog(title, msg);
//        } catch (JSONException e) {
//            LogUtil.e(e);
//        }
//    }
//
//    public void dealResultError(CommonModel errorModel) {
//        showDialog(errorTitle, errorModel.getMessage());
//    }
//
//    public void showDialog(String msg) {
//        showDialog(errorTitle, msg);
//    }

//    public void showDialog(String title, String msg) {
//        showDialog(title, msg, null);
//    }
//
//    public void showDialog(@StringRes int title, @StringRes int msg) {
//        showDialog(title == 0 ? null : context.getString(title),
//                msg == 0 ? null : context.getString(msg),
//                null);
//    }

//    public void showDialog(String title, String msg, Action0 doOnDismiss) {
//        Observable.just("")
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(s -> showCustomDialog(title, msg, doOnDismiss).show(),
//                        LogUtil::e);
//    }

//    public void showMessage(String msg) {
//        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
//    }

//    public <T> Observable.Transformer<T, T> showNetProgressDialog(String title) {
//        return obs -> obs
//                .doOnSubscribe(() -> showProgressDialog(title))
//                .doOnError(t -> this.hideProgressDialog())
//                .doOnCompleted(this::hideProgressDialog);
//    }
//
//    public void showProgressDialog(@NonNull String title) {
//        if (progressDialog != null)
//            return;
//        Observable.just("")
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        s -> {
//                            if (progressDialog == null && dialogCount.get() == 0) {
//                                progressDialog = DialogUtil.showProgressDialog(context);
//                            }
//                            dialogCount.incrementAndGet();
//                        },
//                        LogUtil::e
//                );
//    }
//
//    public void hideProgressDialog() {
//        Observable.just("")
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(s -> {
//                            dialogCount.decrementAndGet();
//                            if (progressDialog != null && dialogCount.get() == 0) {
//                                progressDialog.dismiss();
//                                progressDialog = null;
//                            }
//                        },
//                        LogUtil::e
//                );
//    }
//
//    public void dealThrowable(Throwable t) {
//        LogUtil.e(t);
//        if (t instanceof NetErrorException)
//            dealResultError(((NetErrorException) t).getErrorModel());
//        else
//            showDialog(errorTitle, t.getMessage());
//    }
//
//    public Dialog showCustomDialog(String title, Action0 doOnDismiss) {
//        return this.showCustomDialog(title, null, true, doOnDismiss);
//    }
//
//    public Dialog showCustomDialog(String title, String message, Action0 doOnDismiss) {
//        return this.showCustomDialog(title, message, true, doOnDismiss);
//    }
//
//    private Dialog showCustomDialog(String title, String message, boolean showOkButton, Action0 doOnDismiss) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context)
//                .setTitle(title)
//                .setMessage(message)
//                .setNegativeButton(R.string.dialog_button_cancel, null);
//
//        if (showOkButton)
//            builder.setPositiveButton(R.string.dialog_button_ok, null);
//
//        builder.setOnDismissListener(dialog -> {
//            if (doOnDismiss != null)
//                doOnDismiss.call();
//        });
//        return builder.create();
//    }
//
//    public void showOkDialog(String title, String message, boolean show) {
//        if (show)
//            new AlertDialog.Builder(context)
//                    .setTitle(TextUtils.isEmpty(title) ? null : title)
//                    .setMessage(TextUtils.isEmpty(message) ? null : message)
//                    .setPositiveButton(R.string.dialog_button_ok, null)
//                    .show();
//    }
}

package indi.yume.demo.newapplication.ui.fragment.other;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import indi.yume.demo.newapplication.R;
import indi.yume.demo.newapplication.databinding.E0101ChangePasswordDialogBinding;
import indi.yume.demo.newapplication.databinding.OtherFragmentBinding;
import indi.yume.demo.newapplication.model.api.UserModel;
import indi.yume.demo.newapplication.model.sharedpref.SharedPrefModel;
import indi.yume.demo.newapplication.ui.AppState;

import indi.yume.demo.newapplication.ui.activity.login.LoginActivity;
import indi.yume.demo.newapplication.ui.fragment.other.OtherFragment.OtherKey;
import indi.yume.demo.newapplication.ui.presenter.OtherPresenter;
import indi.yume.demo.newapplication.util.LogUtil;
import indi.yume.yudux.collection.BaseDependAction;
import indi.yume.yudux.collection.ContextCollection;
import indi.yume.yudux.collection.LazyAction;
import indi.yume.yudux.collection.RealWorld;
import indi.yume.yudux.collection.SingleDepends;
import indi.yume.yudux.functions.Unit;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import lombok.RequiredArgsConstructor;

import static indi.yume.demo.newapplication.ui.AppStore.MainKey.Shared;
import static indi.yume.demo.newapplication.ui.AppStore.mainRepo;
import static indi.yume.demo.newapplication.ui.AppStore.mainStore;
import static indi.yume.demo.newapplication.ui.fragment.other.OtherFragment.OtherKey.*;
import static indi.yume.demo.newapplication.util.RxUtil.postUI;
import static indi.yume.yudux.DSL.*;

/**
 * Created by DaggerGenerator on 2017-05-24.
 */
@RequiredArgsConstructor
public class OtherHandler {
    private final ContextCollection<OtherKey> repo;

    public void logout() {
        mainStore.dispatch(create(mainRepo, action(lazy(Shared),
                (real, state) -> {
                    SharedPrefModel sharedPrefModel = real.get(Shared);
                    sharedPrefModel.setToken("");

                    return Single.just(Unit.unit());
                },
                (unit, s) -> s.withUserModel(null))));
        mainStore.dispatch(create(repo, effect(lazy(CONTEXT),
                (real, state) -> {
                    Activity activity = real.get(CONTEXT);
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                    activity.finish();
                })));
    }

    public void modifyPassword() {
        mainStore.dispatch(create(repo, modifyPasswordAction));
    }

    public void about() {
        mainStore.dispatch(create(repo, effect(lazy(CONTEXT),
                (real, state) -> {
                    Context context = real.get(CONTEXT);
                    postUI(() -> Toast.makeText(context, R.string.userinfo_about_detail, Toast.LENGTH_LONG).show());
                })));
    }

    private static final LazyAction<RealWorld<OtherKey>, SingleDepends<OtherKey>, AppState, UserModel> modifyPasswordAction =
            action(lazy(CONTEXT, PRESENTER),
                    (real, state) -> {
                        Context context = real.get(CONTEXT);
                        OtherPresenter presenter = real.get(PRESENTER);
                        String name = state.getUserModel().getName();

                        return Single.<UserModel>create(emmit -> {
                            E0101ChangePasswordDialogBinding binding = E0101ChangePasswordDialogBinding.inflate(LayoutInflater.from(context));
                            AlertDialog alertDialog = new AlertDialog.Builder(context)
                                    .setView(binding.getRoot())
                                    .setCancelable(false)
                                    .setNegativeButton(R.string.dialog_button_cancel, null)
                                    .setPositiveButton(R.string.dialog_button_ok, (d, i) -> {
                                        String oldPassword = binding.oldPasswordEdittext.getText().toString();
                                        String newPassword = binding.newPasswordEdittext.getText().toString();
                                        presenter.modifyUserInfo(name, oldPassword, newPassword)
                                                .subscribe(model -> {
                                                    Toast.makeText(context, "修改成功", Toast.LENGTH_LONG).show();
                                                    emmit.onSuccess(model);
                                                }, e -> {
                                                    LogUtil.e(e);
                                                    Toast.makeText(context, "修改失败", Toast.LENGTH_LONG).show();
                                                    emmit.onError(e);
                                                });
                                    })
                                    .setOnDismissListener(dialog ->
                                            emmit.onError(new RuntimeException("not select anything at Modify dialog")))
                                    .create();
                            alertDialog.show();
                        }).subscribeOn(AndroidSchedulers.mainThread());
                    },
                    (user, state) -> state.withUserModel(user));
}
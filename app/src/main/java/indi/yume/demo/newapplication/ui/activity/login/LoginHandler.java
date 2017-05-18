package indi.yume.demo.newapplication.ui.activity.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import indi.yume.demo.newapplication.databinding.LoginActivityBinding;
import indi.yume.demo.newapplication.model.api.UserModel;
import indi.yume.demo.newapplication.ui.activity.MainBaseActivity;
import indi.yume.demo.newapplication.ui.activity.login.LoginActivity.LoginKey;
import indi.yume.demo.newapplication.ui.AppState;
import indi.yume.demo.newapplication.ui.presenter.LoginPresenter;
import indi.yume.demo.newapplication.util.DealErrorUtil;
import indi.yume.demo.newapplication.util.DialogUtil;
import indi.yume.yudux.collection.BaseDependAction;
import indi.yume.yudux.collection.ContextCollection;
import indi.yume.yudux.collection.LazyAction;
import indi.yume.yudux.collection.RealWorld;
import indi.yume.yudux.collection.SingleDepends;
import io.reactivex.Single;
import lombok.RequiredArgsConstructor;

import static indi.yume.demo.newapplication.ui.AppStore.mainStore;
import static indi.yume.demo.newapplication.ui.activity.login.LoginActivity.LoginKey.*;
import static indi.yume.yudux.collection.DSL.*;
import static indi.yume.yudux.DSL.*;

/**
 * Created by yume on 17-5-2.
 */
@RequiredArgsConstructor
public class LoginHandler {
    private final ContextCollection<LoginKey> repo;

    public void initViewData() {
        mainStore.dispatch(create(repo, INIT_ACTION));
    }

    public void clickLogin(View view) {
        mainStore.dispatch(create(repo, LOGIN_ACTION));
    }

    public static final LazyAction<RealWorld<LoginKey>, SingleDepends<LoginKey>, AppState, UserModel> INIT_ACTION =
            action(lazy(PRESENTER, ACTIVITY),
                    (real, state) -> {
                        AppCompatActivity activity = real.getItem(ACTIVITY);
                        LoginPresenter presenter = real.getItem(PRESENTER);

                        if(!presenter.hasLogin())
                            return Single.error(new RuntimeException("Has not login"));

                        return presenter.getUserInfo(presenter.getToken())
                                .compose(DialogUtil.composeNetProgressDialog(activity))
                                .compose(DealErrorUtil.dealErrorRetry(activity))
                                .doOnSuccess(m -> {
                                    presenter.setToken(m.getToken());
                                    onLoginOver(activity);
                                });
                    },
                    (user, oldState) -> oldState.withUserModel(user));

    public static final LazyAction<RealWorld<LoginKey>, SingleDepends<LoginKey>, AppState, UserModel> LOGIN_ACTION =
            action(lazy(PRESENTER, BINDER, ACTIVITY),
                    (real, state) -> {
                        AppCompatActivity activity = real.getItem(ACTIVITY);
                        LoginPresenter presenter = real.getItem(PRESENTER);
                        LoginActivityBinding binding = real.getItem(BINDER);

                        return presenter.login(binding.usernameEditText.getText().toString(),
                                binding.userpassEditText.getText().toString())
                                .compose(DialogUtil.composeNetProgressDialog(activity))
                                .compose(DealErrorUtil.dealErrorRetry(activity))
                                .doOnSuccess(m -> {
                                    presenter.setToken(m.getToken());
                                    onLoginOver(activity);
                                });
                    },
                    (user, oldState) -> oldState.withUserModel(user));

    private static void onLoginOver(AppCompatActivity activity) {
        activity.startActivity(new Intent(activity, MainBaseActivity.class));
        activity.finish();
    }
}

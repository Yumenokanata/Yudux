package indi.yume.demo.newapplication.ui.activity.login;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import indi.yume.demo.newapplication.R;
import indi.yume.demo.newapplication.databinding.LoginActivityBinding;
import indi.yume.demo.newapplication.ui.AppComponent;
import indi.yume.demo.newapplication.ui.MainApplication;
import indi.yume.demo.newapplication.ui.activity.base.BaseActivity;
import indi.yume.demo.newapplication.ui.component.DaggerLoginComponent;
import indi.yume.demo.newapplication.ui.component.LoginComponent;
import indi.yume.demo.newapplication.ui.AppState;
import indi.yume.demo.newapplication.ui.module.LoginModule;
import indi.yume.demo.newapplication.ui.presenter.LoginPresenter;
import indi.yume.yudux.collection.ContextCollection;
import indi.yume.yudux.functions.Unit;

import static indi.yume.demo.newapplication.ui.AppStore.mainStore;
import static indi.yume.demo.newapplication.ui.activity.login.LoginActivity.LoginKey.*;
import static indi.yume.yudux.collection.DSL.*;
import static indi.yume.yudux.DSL.*;

/**
 * Created by DaggerGenerator on 2017/05/02.
 */
public class LoginActivity extends BaseActivity {
    enum LoginKey {
        APP_COMPONENT,
        ACTIVITY,
        PRESENTER,
        BINDER,
        HANDLER,
        NONE   // use for init when all ready
    }

    private final ContextCollection<LoginKey> repo =
            ContextCollection.<LoginKey>builder()
                    .withItem(APP_COMPONENT,
                            depends(ACTIVITY),
                            (real, store) ->
                                MainApplication.getInstance(real.getItem(ACTIVITY)).getAppComponent()
                            )
                    .withItem(BINDER,
                            depends(ACTIVITY, HANDLER),
                            (real, store) -> {
                                LoginActivityBinding binding = DataBindingUtil.setContentView(real.getItem(ACTIVITY), R.layout.login_activity);
                                binding.setHanlder(real.getItem(HANDLER));
                                return binding;
                            })
                    .withItem(HANDLER,
                            depends(),
                            (real, collection) -> new LoginHandler(collection))
                    .withItem(PRESENTER,
                            depends(APP_COMPONENT),
                            (real, store) -> {
                                LoginComponent loginComponent = DaggerLoginComponent.builder()
                                        .appComponent(real.getItem(APP_COMPONENT))
                                        .loginModule(new LoginModule(this))
                                        .build();
                                LoginPresenter presenter = loginComponent.getPresenter();
                                loginComponent.injectPresenter(presenter);

                                return presenter;
                            })
                    .withItem(NONE,
                            depends(HANDLER, PRESENTER, BINDER),
                            (real, store) -> {
                                real.<LoginHandler>getItem(HANDLER).initViewData();
                                return Unit.unit();
                            })
                    .build();

    @Override
    public void onCreate(Bundle savedInstanceState){
        repo.ready(ACTIVITY, this);
        super.onCreate(savedInstanceState);
        forceRender(mainStore);
    }

    @Override
    protected void onDestroy() {
        repo.destroyAll();
        super.onDestroy();
    }

    @Override
    public void inject(AppComponent appComponent){

    }
}
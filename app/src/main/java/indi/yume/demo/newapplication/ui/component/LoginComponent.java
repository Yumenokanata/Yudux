package indi.yume.demo.newapplication.ui.component;

import dagger.Component;

import indi.yume.demo.newapplication.di.ActivityScope;
import indi.yume.demo.newapplication.ui.AppComponent;
import indi.yume.demo.newapplication.ui.activity.login.LoginActivity;
import indi.yume.demo.newapplication.ui.module.LoginModule;
import indi.yume.demo.newapplication.ui.presenter.LoginPresenter;

/**
 * Created by DaggerGenerator on 2017/05/02.
 */
@Component(modules = LoginModule.class,
        dependencies = AppComponent.class)
@ActivityScope
public interface LoginComponent{

    public LoginPresenter getPresenter();

    public void injectPresenter(LoginPresenter presenter);

}
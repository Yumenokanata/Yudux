package indi.yume.demo.newapplication.ui.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

import indi.yume.demo.newapplication.di.ActivityScope;
import indi.yume.demo.newapplication.ui.activity.login.LoginActivity;
import indi.yume.demo.newapplication.ui.presenter.LoginPresenter;

/**
 * Created by DaggerGenerator on 2017/05/02.
 */
@Module
public class LoginModule extends BaseModule{
    LoginActivity activity;

    public LoginModule(LoginActivity activity){
        this.activity = activity;
    }

    @ActivityScope
    @Provides
    public LoginPresenter providePresenter(){
        return new LoginPresenter();
    }

    @Override
    public Context provideActivity(){
        return activity;
    }

}
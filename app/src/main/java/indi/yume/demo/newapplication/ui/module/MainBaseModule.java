package indi.yume.demo.newapplication.ui.module;

import android.content.Context;

import indi.yume.demo.newapplication.di.ActivityScope;
import indi.yume.demo.newapplication.ui.activity.MainBaseActivity;
import indi.yume.demo.newapplication.ui.presenter.MainBasePresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by DaggerGenerator on 2017/02/14.
 */
@Module
public class MainBaseModule extends BaseModule{
    MainBaseActivity activity;

    public MainBaseModule(MainBaseActivity activity){
        this.activity = activity;
    }

    @ActivityScope
    @Provides
    public MainBasePresenter providePresenter(){
        return new MainBasePresenter();
    }

    @Override
    public Context provideActivity(){
        return activity;
    }

}
package indi.yume.demo.newapplication.ui.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

import indi.yume.demo.newapplication.di.ActivityScope;
import indi.yume.demo.newapplication.ui.fragment.keep.KeepFragment;
import indi.yume.demo.newapplication.ui.presenter.KeepPresenter;

/**
 * Created by DaggerGenerator on 2017/05/16.
 */
@Module
public class KeepModule extends BaseModule{
    KeepFragment fragment;

    public KeepModule(KeepFragment fragment){
        this.fragment = fragment;
    }

    @ActivityScope
    @Provides
    public KeepPresenter providePresenter(){
        return new KeepPresenter();
    }

    @Override
    public Context provideActivity(){
        return fragment.getContext();
    }

}
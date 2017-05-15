package indi.yume.demo.newapplication.ui.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

import indi.yume.demo.newapplication.di.ActivityScope;
import indi.yume.demo.newapplication.ui.fragment.home.HomeFragment;
import indi.yume.demo.newapplication.ui.presenter.HomePresenter;

/**
 * Created by DaggerGenerator on 2017/05/02.
 */
@Module
public class HomeModule extends BaseModule{
    HomeFragment fragment;

    public HomeModule(HomeFragment fragment){
        this.fragment = fragment;
    }

    @ActivityScope
    @Provides
    public HomePresenter providePresenter(){
        return new HomePresenter();
    }

    @Override
    public Context provideActivity(){
        return fragment.getContext();
    }

}
package indi.yume.demo.newapplication.ui.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

import indi.yume.demo.newapplication.di.ActivityScope;
import indi.yume.demo.newapplication.ui.fragment.mypage.MyPageFragment;
import indi.yume.demo.newapplication.ui.presenter.MyPagePresenter;

/**
 * Created by DaggerGenerator on 2017/05/22.
 */
@Module
public class MyPageModule extends BaseModule{
    MyPageFragment fragment;

    public MyPageModule(MyPageFragment fragment){
        this.fragment = fragment;
    }

    @ActivityScope
    @Provides
    public MyPagePresenter providePresenter(){
        return new MyPagePresenter();
    }

    @Override
    public Context provideActivity(){
        return fragment.getContext();
    }

}
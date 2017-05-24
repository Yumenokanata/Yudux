package indi.yume.demo.newapplication.ui.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

import indi.yume.demo.newapplication.di.ActivityScope;
import indi.yume.demo.newapplication.ui.fragment.other.OtherFragment;
import indi.yume.demo.newapplication.ui.presenter.OtherPresenter;

/**
 * Created by DaggerGenerator on 2017/05/24.
 */
@Module
public class OtherModule extends BaseModule{
    OtherFragment fragment;

    public OtherModule(OtherFragment fragment){
        this.fragment = fragment;
    }

    @ActivityScope
    @Provides
    public OtherPresenter providePresenter(){
        return new OtherPresenter();
    }

    @Override
    public Context provideActivity(){
        return fragment.getContext();
    }

}
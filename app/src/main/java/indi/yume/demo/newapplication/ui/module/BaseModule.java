package indi.yume.demo.newapplication.ui.module;

import android.content.Context;

import indi.yume.demo.newapplication.di.ActivityScope;
import indi.yume.demo.newapplication.ui.AppMessageUtil;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yume on 15/9/16.
 */
@Module
public abstract class BaseModule {
    @ActivityScope
    @Provides
    AppMessageUtil getAppMessageUtil(){
        return new AppMessageUtil();
    }

    abstract Context provideActivity();
}

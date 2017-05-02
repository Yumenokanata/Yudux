package indi.yume.demo.newapplication.ui;

import android.content.Context;

import indi.yume.demo.newapplication.manager.api.base.ApiModule;
import indi.yume.demo.newapplication.manager.sharedpref.SharedPrefModule;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;

/**
 * Created by yume on 15/9/14.
 */
@Singleton
@Component(modules = {ApiModule.class, SharedPrefModule.class, MainAppModule.class})
public interface AppComponent {
    @Named("AppContext")
    Context getContext();

    OkHttpClient getOkHttpClient();
}

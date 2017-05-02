package indi.yume.demo.newapplication.ui;

import android.content.Context;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yume on 15/9/14.
 */
@Module
public class MainAppModule {
    private Context context;

    public MainAppModule(Context context){
        this.context = context;
    }

    @Singleton
    @Provides
    @Named("AppContext")
    Context provideContext(){
        return context;
    }
}

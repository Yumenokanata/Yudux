package indi.yume.demo.newapplication.manager.sharedpref;

import android.content.Context;

import indi.yume.demo.newapplication.model.sharedpref.SharedPrefModel;
import indi.yume.demo.newapplication.util.Constants;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import indi.yume.tools.autosharedpref.AutoSharedPref;

/**
 * Created by yume on 16/11/15.
 */
@Module
public class SharedPrefModule {
    @Singleton
    @Provides
    SharedPrefModel provideSharedPrefModel(@Named("AppContext") Context context) {
        return AutoSharedPref.newModel(context, SharedPrefModel.class, Constants.SHARED_PREF_FILE_NAME);
    }
}

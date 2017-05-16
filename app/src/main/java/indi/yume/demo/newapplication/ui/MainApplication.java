package indi.yume.demo.newapplication.ui;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

//import indi.yume.tools.fragmentmanager.ThrottleUtil;
import indi.yume.yudux.store.Store;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by yume on 16-4-16.
 */
public class MainApplication extends Application {
    protected AppComponent appComponent;
    //has homeJobDialog already shown.
    @Setter
    @Getter
    private boolean myNotificationShown = false;
    @Getter
    private static final Store<AppState> mainStore = new Store<>(AppState.empty());

    public static MainApplication getInstance(Context context) {
        return (MainApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        inject();

//        ThrottleUtil.setThrottleTime(300);
    }

    protected void inject() {
        appComponent = DaggerAppComponent.builder()
                .mainAppModule(new MainAppModule(this.getApplicationContext()))
                .build();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}

package indi.yume.demo.newapplication.ui;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

//import indi.yume.tools.fragmentmanager.ThrottleUtil;
import indi.yume.demo.newapplication.component.cart.CartActions;
import indi.yume.yudux.store.Store;
import lombok.Getter;
import lombok.Setter;

import static indi.yume.demo.newapplication.component.keep.Actions.refreshKeep;
import static indi.yume.demo.newapplication.ui.AppStore.initMainRepo;

/**
 * Created by yume on 16-4-16.
 */
public class MainApplication extends Application {
    protected AppComponent appComponent;
    //has homeJobDialog already shown.
    @Setter
    @Getter
    private boolean myNotificationShown = false;

    public static MainApplication getInstance(Context context) {
        return (MainApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        inject();
        initMainRepo(appComponent);
        refreshKeep();
        CartActions.init();

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

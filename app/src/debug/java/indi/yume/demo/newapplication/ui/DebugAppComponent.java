package indi.yume.demo.newapplication.ui;

import javax.inject.Singleton;

import dagger.Component;
import indi.yume.demo.newapplication.manager.api.DebugApiModule;
import indi.yume.demo.newapplication.manager.sharedpref.SharedPrefModule;

/**
 * Created by yume on 15/12/15.
 */
@Singleton
@Component(modules = {DebugApiModule.class, SharedPrefModule.class, MainAppModule.class})
public interface DebugAppComponent extends AppComponent {

}

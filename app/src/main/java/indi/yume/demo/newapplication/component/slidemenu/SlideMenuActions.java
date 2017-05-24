package indi.yume.demo.newapplication.component.slidemenu;

import indi.yume.demo.newapplication.ui.activity.MainBaseActivity;
import indi.yume.demo.newapplication.util.SlideMenuScreensTag;
import lombok.experimental.UtilityClass;

import static indi.yume.demo.newapplication.ui.AppStore.MainKey.MainActivity;
import static indi.yume.demo.newapplication.ui.AppStore.mainRepo;
import static indi.yume.demo.newapplication.ui.AppStore.mainStore;
import static indi.yume.yudux.collection.DSL.*;
import static indi.yume.yudux.DSL.*;

/**
 * Created by yume on 17-5-24.
 */
@UtilityClass
public class SlideMenuActions {
    public static void init() {
        subscribeUntilChanged(mainStore,
                extra(mainRepo, depends(MainActivity)),
                s -> s.getCurrentTag(),
                (real, tag) -> real.<MainBaseActivity>get(MainActivity).switchToStack(tag));
        subscribeUntilChanged(mainStore,
                extra(mainRepo, depends(MainActivity)),
                s -> s.isEnableDrawer(),
                (real, enable) -> real.<MainBaseActivity>get(MainActivity).setEnableDrawer(enable));
    }

    public static void switchTag(SlideMenuScreensTag tag) {
        mainStore.dispatch(reduce(s -> s.withCurrentTag(tag)));
    }

    public static void enableDrawer(boolean enable) {
        mainStore.dispatch(reduce(s -> s.withEnableDrawer(enable)));
    }

    public static void register(MainBaseActivity activity) {
        mainRepo.ready(MainActivity, activity);
    }

    public static void destroy() {
        mainRepo.destroy(MainActivity);
    }
}

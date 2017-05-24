package indi.yume.demo.newapplication.ui;

import indi.yume.yudux.collection.ContextCollection;
import indi.yume.yudux.store.Store;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import static indi.yume.demo.newapplication.ui.AppStore.MainKey.*;

/**
 * Created by yume on 17-4-24.
 */
@UtilityClass
public class AppStore {
    public static final Store<AppState> mainStore = new Store<>(AppState.empty());

    public enum MainKey {
        Shared,
        AppContext,
        /**
         * {@link indi.yume.demo.newapplication.component.slidemenu.SlideMenuActions}
         */
        MainActivity
    }

    public static final ContextCollection<MainKey> mainRepo = ContextCollection.<MainKey>builder().build();

    public static void initMainRepo(AppComponent appComponent) {
        mainRepo.ready(Shared, appComponent.getSharedPrefModel());
        mainRepo.ready(AppContext, appComponent.getContext());
    }
}

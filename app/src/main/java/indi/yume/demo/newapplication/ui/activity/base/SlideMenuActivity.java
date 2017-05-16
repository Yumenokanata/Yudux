package indi.yume.demo.newapplication.ui.activity.base;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import indi.yume.demo.newapplication.R;
import indi.yume.demo.newapplication.util.SlideMenuScreensTag;
import indi.yume.demo.newapplication.widget.slidemenu.SlideMenuView;
import indi.yume.tools.fragmentmanager.BaseManagerFragment;

import static indi.yume.demo.newapplication.util.SlideMenuScreensTag.HOME;

/**
 * Created by yume on 16/1/18.
 * <p>
 * Base class for activities that has slide menu.
 * subclass must have a SlideMenuView(id = slide_menu_view) and a DrawerLayout(id = drawer_layout) at layout xml file.
 */
public abstract class SlideMenuActivity extends BaseFragmentActivity implements SlideMenuView.ProviderSlideMenu {
    @BindView(R.id.slide_menu_view)
    SlideMenuView slideMenuView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Override
    public Map<String, Class<? extends BaseManagerFragment>> baseFragmentWithTag() {
        Map<String, Class<? extends BaseManagerFragment>> map = new HashMap<>();
        for(SlideMenuScreensTag tag : SlideMenuScreensTag.values())
            map.put(tag.getTag(), tag.getFragmentClazz());

        return map;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        slideMenuView.setListener(view -> drawerLayout.closeDrawers());
        switchToStackByTag(HOME.getTag());
    }

    public void setEnableDrawer(boolean enable) {
        if (enable)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        else
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void switchToStack(SlideMenuScreensTag tag){
        slideMenuView.switchToStack(tag);
    }

    @Override
    public void closeSlideMenu() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void openSlideMenu() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void toggleSlidingMenu() {
        if (drawerLayout.isDrawerOpen(drawerLayout))
            closeSlideMenu();
        else
            openSlideMenu();
    }
}

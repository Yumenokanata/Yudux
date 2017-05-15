package indi.yume.demo.newapplication.ui.activity;

import android.app.Notification;
import android.os.Bundle;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import indi.yume.demo.newapplication.R;
import indi.yume.demo.newapplication.ui.AppComponent;
import indi.yume.demo.newapplication.ui.activity.base.BaseFragmentActivity;
import indi.yume.demo.newapplication.ui.activity.base.SlideMenuActivity;
import indi.yume.demo.newapplication.ui.component.DaggerMainBaseComponent;
import indi.yume.demo.newapplication.ui.component.MainBaseComponent;
import indi.yume.demo.newapplication.ui.module.MainBaseModule;
import indi.yume.demo.newapplication.ui.presenter.MainBasePresenter;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import indi.yume.demo.newapplication.util.SlideMenuScreensTag;
import indi.yume.tools.fragmentmanager.BaseManagerFragment;

/**
 * Created by DaggerGenerator on 2016/11/15.
 */
public class MainBaseActivity extends SlideMenuActivity {
    private Long exitTime = 0l;

    @Inject
    MainBasePresenter presenter;

    @Override
    protected int provideFragmentId() {
        return R.id.fragment_layout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
//
//        setIsShowAnimWhenFinish(false);
//        setDefaultFragmentAnim(R.anim.activity_open_enter,
//                R.anim.activity_close_exit,
//                R.anim.stay_anim);
        switchToStackByTag(SlideMenuScreensTag.HOME.getTag());
    }

    @Override
    public void inject(AppComponent appComponent) {
        MainBaseComponent component = DaggerMainBaseComponent.builder()
                .appComponent(appComponent)
                .mainBaseModule(new MainBaseModule(this))
                .build();
        component.injectFragment(this);
        component.injectPresenter(presenter);
    }

    @Override
    public boolean onBackPressed(int currentStackSize) {
        if (currentStackSize == 1)
            if ((System.currentTimeMillis() - exitTime) > 2000) {
//                Toast.makeText(this, R.string.exit_info, Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
                return true;
            }
        return false;
    }
}
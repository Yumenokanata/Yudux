package indi.yume.demo.newapplication.ui.fragment.base;

import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;

import javax.inject.Inject;

import butterknife.BindView;
//import indi.yume.tools.fragmentmanager.OnShowMode;
import indi.yume.demo.newapplication.widget.CustomToolbar;

import static indi.yume.demo.newapplication.widget.CustomToolbar.TOOLBAR_MODE_MENU;

/**
 * Created by yume on 16-6-23.
 */

public abstract class BaseToolbarFragment extends BaseFragment {

//    @BindView(R.id.toolbar)
    @Nullable
    protected CustomToolbar toolbar;

    @MenuRes
    public int provideOptionMenuRes() {
        return -1;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void doForToolbar(CustomToolbar toolbar) {
        toolbar.initListener();
        toolbar.setDoOnClickBackButton(this::finish);
//        toolbar.setDoOnClickMenuButton(() -> ((SlideMenuActivity) getActivity()).openSlideMenu());
        toolbar.setDoOnClickCloseButton(this::finish);

    }

    protected void setToolbarMode(@CustomToolbar.ToolbarMode int mode) {
        if (toolbar == null)
            return;

        toolbar.setMode(mode);
    }

    protected void setToolbarTitle(@StringRes int titleRes) {
        if (toolbar == null)
            return;

        toolbar.setTitle(titleRes);
    }

    protected void setToolbarTitle(String title) {
        if (toolbar == null)
            return;

        toolbar.setTitle(title);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        final int optionMenuRes = provideOptionMenuRes();
        if (optionMenuRes != -1) {
            inflater.inflate(optionMenuRes, menu);
            onOptionsMenuCreated(menu);
        }
    }

    protected void onOptionsMenuCreated(Menu menu) {

    }


//    @Override
//    protected void onShow(int callMode) {
//        super.onShow(callMode);

//        if (toolbar != null)
//            if (toolbar.getMode() == TOOLBAR_MODE_MENU)
//                enableDrawer(true);
//            else
//                enableDrawer(false);

//        if (toolbar != null && isTopOfStack() && callMode != OnShowMode.ON_CREATE_AFTER_ANIM) {
//            if (getActivity() != null)
//                ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//
//            if(provideOptionMenuRes() != -1)
//                setHasOptionsMenu(true);
//            else
//                setHasOptionsMenu(false);
//
//            doForToolbar(toolbar);
//        }
//    }
}

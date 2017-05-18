package indi.yume.demo.newapplication.ui.fragment.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import indi.yume.demo.newapplication.R;
import indi.yume.demo.newapplication.ui.activity.base.SlideMenuActivity;
import indi.yume.demo.newapplication.widget.CustomToolbar;
import indi.yume.tools.fragmentmanager.anno.OnHideMode;
import indi.yume.tools.fragmentmanager.anno.OnShowMode;
import indi.yume.yudux.collection.ContextCollection;

import static indi.yume.demo.newapplication.ui.AppStore.mainStore;
import static indi.yume.demo.newapplication.ui.fragment.base.BaseToolbarFragment.BaseToolKey.*;
import static indi.yume.demo.newapplication.widget.CustomToolbar.TOOLBAR_MODE_MENU;
import static indi.yume.yudux.collection.DSL.*;
import static indi.yume.yudux.DSL.*;

/**
 * Created by yume on 16-6-23.
 */

public abstract class BaseToolbarFragment extends BaseFragment {
    enum BaseToolKey {
        ACTIVITY,
        TOOLBAR,
        VIEW
    }

    protected final ContextCollection<BaseToolKey> baseRepo =
            ContextCollection.<BaseToolKey>builder()
                    .withItem(TOOLBAR,
                            depends(ACTIVITY, VIEW),
                            (real, store) -> {
                                View view = real.getItem(VIEW);
                                CustomToolbar toolbar = (CustomToolbar) view.findViewById(R.id.toolbar);
                                if(toolbar == null)
                                    return null;
                                real.<AppCompatActivity>getItem(ACTIVITY).setSupportActionBar(toolbar);

                                if (toolbar.getMode() == TOOLBAR_MODE_MENU)
                                    enableDrawer(true);
                                else
                                    enableDrawer(false);

                                if(provideOptionMenuRes() != -1)
                                    setHasOptionsMenu(true);
                                else
                                    setHasOptionsMenu(false);

                                doForToolbar(toolbar);

                                return toolbar;
                            })
                    .build();

    @MenuRes
    public int provideOptionMenuRes() {
        return -1;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        baseRepo.ready(VIEW, view);
        super.onViewCreated(view, savedInstanceState);
    }

    protected void doForToolbar(CustomToolbar toolbar) {
        toolbar.initListener();
        toolbar.setDoOnClickBackButton(this::finish);
        toolbar.setDoOnClickMenuButton(() -> ((SlideMenuActivity) getActivity()).openSlideMenu());
        toolbar.setDoOnClickCloseButton(this::finish);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        baseRepo.ready(ACTIVITY, getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        baseRepo.destroy(ACTIVITY);
    }

    protected void setToolbarMode(@CustomToolbar.ToolbarMode int mode) {
        mainStore.dispatch(create(baseRepo, effect(lazy(TOOLBAR),
                (real, oldState) -> real.<CustomToolbar>getItem(TOOLBAR).setMode(mode))));
    }

    protected void setToolbarTitle(@StringRes int titleRes) {
        mainStore.dispatch(create(baseRepo, effect(lazy(TOOLBAR),
                (real, oldState) -> real.<CustomToolbar>getItem(TOOLBAR).setTitle(titleRes))));
    }

    protected void setToolbarTitle(String title) {
        mainStore.dispatch(create(baseRepo, effect(lazy(TOOLBAR),
                (real, oldState) -> real.<CustomToolbar>getItem(TOOLBAR).setTitle(title))));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        final int optionMenuRes = provideOptionMenuRes();
        if (optionMenuRes != -1) {
            inflater.inflate(optionMenuRes, menu);
            onOptionsMenuCreated(menu);
        }
    }

    protected void onOptionsMenuCreated(Menu menu) {}

    @Override
    public void onHide(OnHideMode mode) {
        super.onHide(mode);
        baseRepo.destroy(TOOLBAR);
        baseRepo.destroy(VIEW);
    }

    @Override
    public void onShow(OnShowMode mode) {
        super.onShow(mode);

        baseRepo.ready(VIEW, getView());
    }
}

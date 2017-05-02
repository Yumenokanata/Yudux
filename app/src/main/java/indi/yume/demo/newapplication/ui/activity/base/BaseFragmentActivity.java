package indi.yume.demo.newapplication.ui.activity.base;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.CallSuper;

import butterknife.ButterKnife;
import indi.yume.tools.fragmentmanager.BaseFragmentManagerActivity;
import indi.yume.demo.newapplication.ui.AppComponent;
import indi.yume.demo.newapplication.ui.MainApplication;
//import indi.yume.demo.newapplication.util.DealErrorUtil;

/**
 * Created by yume on 15/9/22.
 */
public abstract class BaseFragmentActivity extends BaseFragmentManagerActivity {

//    private DealErrorUtil.DealError dealErrorBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        AppComponent appComponent = MainApplication.getInstance(this).getAppComponent();
        inject(appComponent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        injectOnStart(MainApplication.getInstance(this).getAppComponent());
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }

    protected abstract void inject(AppComponent appComponent);

    protected void injectOnStart(AppComponent appComponent){

    }

    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();

//        if(dealErrorBinder == null)
//            dealErrorBinder = DealErrorUtil.bindDealErrorWithDefault(this);
    }

    @Override
    @CallSuper
    protected void onPause() {
        super.onPause();
//        if(dealErrorBinder != null) {
//            dealErrorBinder.unbind();
//            dealErrorBinder = null;
//        }
    }
}

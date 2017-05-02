package indi.yume.demo.newapplication.ui.activity.base;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import indi.yume.demo.newapplication.ui.AppComponent;
import indi.yume.demo.newapplication.ui.MainApplication;
//import indi.yume.demo.newapplication.util.DealErrorUtil;

/**
 * Created by yume on 16-7-11.
 */

public abstract class BaseActivity extends AppCompatActivity {
//    private DealErrorUtil.DealError dealErrorBinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject(MainApplication.getInstance(this).getAppComponent());
    }

    protected abstract void inject(AppComponent appComponent);

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

package indi.yume.demo.newapplication.ui.fragment.base;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import indi.yume.demo.newapplication.ui.AppComponent;
import indi.yume.demo.newapplication.ui.AppMessageUtil;
import indi.yume.demo.newapplication.ui.MainApplication;

import java.util.Hashtable;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
//import indi.yume.tools.avocado.util.DisplayUtil;
import indi.yume.tools.fragmentmanager.BaseManagerFragment;
//import indi.yume.tools.fragmentmanager.OnHideMode;
//import rx.Observable;
//import rx.Subscription;
//import rx.subscriptions.CompositeSubscription;

/**
 * Created by yume on 15/9/14.
 */
public abstract class BaseFragment extends BaseManagerFragment {
    protected final String TAG = getClass().getSimpleName();

    private Unbinder unbinder;

    @Inject
    public AppMessageUtil appMessageUtil;

//    private CompositeSubscription allSubscription = new CompositeSubscription();
//    private Hashtable<String, Subscription> mapSubscription = new Hashtable<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject(MainApplication.getInstance(getContext()).getAppComponent());
    }

    public void setUnbinder(Unbinder unbinder) {
        if(this.unbinder != null) {
            throw new RuntimeException("You bind View more than once time.");
        }
        this.unbinder = unbinder;
    }

    protected void bind(View view) {
        setUnbinder(ButterKnife.bind(this, view));
    }

//    protected void removeSubscription(Subscription subscription) {
//        if(subscription != null)
//            allSubscription.remove(subscription);
//    }
//
//    protected void addSubscription(Subscription subscription) {
//        allSubscription.add(subscription);
//    }
//
//    protected void unSubscription(String tag) {
//        if(tag != null && mapSubscription.contains(tag)) {
//            Subscription sub = mapSubscription.get(tag);
//            sub.unsubscribe();
//            mapSubscription.remove(tag);
//        }
//    }
//
//    protected void addSubscription(String tag, Subscription subscription) {
//        if(tag != null && subscription != null && !mapSubscription.contains(tag))
//            mapSubscription.put(tag, subscription);
//    }
//
//    protected void unsubscribeAllAndReset() {
//        allSubscription.unsubscribe();
//        allSubscription = new CompositeSubscription();
//
//        for(Subscription sub : mapSubscription.values())
//            sub.unsubscribe();
//        mapSubscription.clear();
//    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if(unbinder == null) {
            bind(view);
        }
        super.onViewCreated(view, savedInstanceState);

        releaseFocus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }

//        allSubscription.unsubscribe();
    }

    @Override
    public void preBackResultData() {
        super.preBackResultData();
//        DisplayUtil.hideSoftKeyBoard(getActivity());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        if (hidden) {
//            DisplayUtil.hideSoftKeyBoard(getActivity());
//        }
    }

    protected abstract void inject(AppComponent appComponent);

    protected void releaseFocus() {
        View view = getView();
        if(view != null) {
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.requestFocus();
        }
    }

    protected void setOnTouchReleaseFocus() {
        View view = getView();
        if(view != null) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    view.setFocusable(true);
                    view.setFocusableInTouchMode(true);
                    view.requestFocus();
                    return false;
                }
            });
        }
    }

//    protected <T> Observable.Transformer<T, T> showNetProgressDialog(int title){
//        return obs -> obs
//                .doOnSubscribe(() -> appMessageUtil.showProgressDialog(getResources().getString(title)))
//                .doOnError(t -> appMessageUtil.hideProgressDialog())
//                .doOnCompleted(appMessageUtil::hideProgressDialog);
//    }
//
//    protected Bundle getResultBundle() {
//        Bundle bundle = getResultData();
//        if(bundle == null)
//            bundle = new Bundle();
//        setResult(1, bundle);
//
//        return bundle;
//    }
}

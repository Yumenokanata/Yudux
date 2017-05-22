package indi.yume.demo.newapplication.ui.fragment.mypage;

import android.content.Context;

import indi.yume.demo.newapplication.databinding.MyPageFragmentBinding;
import indi.yume.demo.newapplication.ui.AppState;

import indi.yume.demo.newapplication.ui.fragment.mypage.MyPageFragment.MyPageKey;
import indi.yume.demo.newapplication.ui.presenter.MyPagePresenter;
import indi.yume.demo.newapplication.util.DealErrorUtil;
import indi.yume.demo.newapplication.util.DialogUtil;
import indi.yume.yudux.collection.BaseDependAction;
import indi.yume.yudux.collection.ContextCollection;
import indi.yume.yudux.collection.LazyAction;
import indi.yume.yudux.collection.RealWorld;
import indi.yume.yudux.collection.SingleDepends;
import io.reactivex.Single;
import lombok.RequiredArgsConstructor;

import static indi.yume.demo.newapplication.ui.AppStore.mainStore;
import static indi.yume.demo.newapplication.ui.fragment.mypage.MyPageFragment.MyPageKey.*;
import static indi.yume.yudux.DSL.*;

/**
 * Created by DaggerGenerator on 2017-05-22.
 */
@RequiredArgsConstructor
public class MyPageHandler {
    private final ContextCollection<MyPageKey> repo;

    public void init() {
        mainStore.dispatch(create(repo, initAction));
    }

    private final static LazyAction<RealWorld<MyPageKey>, SingleDepends<MyPageKey>, AppState, MyPageState> initAction =
            action(lazy(CONTEXT, PRESENTER),
                    (real, state) -> {
                        Context context = real.get(CONTEXT);
                        MyPagePresenter presenter = real.get(PRESENTER);

                        return Single.zip(presenter.getChargeHistory(state.getUserModel().getToken()),
                                presenter.getPayHistory(state.getUserModel().getToken()),
                                (change, pay) -> new MyPageState(pay.getHistory(), change.getChargeHistory()))
                                .compose(DialogUtil.composeNetProgressDialog(context))
                                .compose(DealErrorUtil.dealErrorRetry(context));
                    },
                    (data, state) -> state.withMyPageState(data));
}
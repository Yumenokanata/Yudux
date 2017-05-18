package indi.yume.demo.newapplication.ui.fragment.home;

import android.content.Context;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.Collections;
import java.util.List;

import indi.yume.demo.newapplication.model.api.GoodsModel;
import indi.yume.demo.newapplication.model.api.PayHistoryModel;
import indi.yume.demo.newapplication.ui.AppState;
import indi.yume.demo.newapplication.ui.AppStore;
import indi.yume.demo.newapplication.ui.fragment.home.HomeFragment.HomeKey;
import indi.yume.demo.newapplication.ui.presenter.HomePresenter;
import indi.yume.demo.newapplication.util.DealErrorUtil;
import indi.yume.demo.newapplication.util.DialogUtil;
import indi.yume.yudux.collection.BaseDependAction;
import indi.yume.yudux.collection.ContextCollection;
import indi.yume.yudux.collection.LazyAction;
import indi.yume.yudux.collection.RealWorld;
import indi.yume.yudux.collection.SingleDepends;
import io.reactivex.Single;
import lombok.RequiredArgsConstructor;

import static indi.yume.yudux.DSL.*;
import static indi.yume.yudux.collection.DSL.*;
import static indi.yume.demo.newapplication.ui.fragment.home.HomeFragment.HomeKey.*;

/**
 * Created by yume on 17-5-2.
 */
@RequiredArgsConstructor
public class HomeHandler {
    private final ContextCollection<HomeKey> repo;

    public void initData() {
        AppStore.mainStore.dispatch(create(repo, INIT_ACTION));
    }

    private static final LazyAction<RealWorld<HomeKey>, SingleDepends<HomeKey>, AppState, HomeState> INIT_ACTION =
            action(lazy(CONTEXT, PRESENTER),
                    (real, oldState) -> {
                        Context context = real.getItem(CONTEXT);
                        HomePresenter presenter = real.getItem(PRESENTER);
                        return Single.zip(presenter.getAllGoods(),
                                presenter.getPayHistory(oldState.getUserModel().getToken()),
                                (list, history) -> new HomeState(list.size(),
                                        oldState.getUserModel().getMoney(),
                                        getLastPayTime(history),
                                        getPayHistoryList(history),
                                        Stream.of(list).skip(list.size() - 5).collect(Collectors.toList())))
                                .compose(DialogUtil.composeNetProgressDialog(context))
                                .compose(DealErrorUtil.dealErrorRetry(context));
                    },
                    (data, oldState) -> oldState.withHomeState(data));

    private static String getLastPayTime(PayHistoryModel model) {
        if(model.getHistory() == null || model.getHistory().isEmpty())
            return "";
        else
            return model.getHistory().get(model.getHistory().size() - 1).getPayTime();
    }

    private static List<GoodsModel> getPayHistoryList(PayHistoryModel model) {
        if(model.getHistory() == null || model.getHistory().isEmpty())
            return Collections.emptyList();
        else
            return Stream.of(model.getHistory().get(model.getHistory().size() - 1).getPayGoods())
                    .map(pay -> pay.getGoodsData())
                    .collect(Collectors.toList());
    }
}

package indi.yume.demo.newapplication.ui.fragment.goods;

import android.app.AlertDialog;
import android.content.Context;

import indi.yume.demo.newapplication.ui.fragment.AppState;
import indi.yume.demo.newapplication.ui.fragment.goods.GoodsFragment.GoodsFragmentKey;
import indi.yume.demo.newapplication.ui.fragment.goods.LoadMoreState.PageData;
import indi.yume.demo.newapplication.ui.fragment.goods.TestData.ItemModel;
import indi.yume.yudux.collection.BaseDependAction;
import indi.yume.yudux.collection.DependsStore;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import lombok.RequiredArgsConstructor;

import static indi.yume.demo.newapplication.ui.fragment.goods.GoodsFragment.GoodsFragmentKey.CONTEXT;
import static indi.yume.demo.newapplication.ui.fragment.goods.GoodsFragment.GoodsFragmentKey.PRESENTER;
import static indi.yume.yudux.DSL.*;

/**
 * Created by yume on 17-4-28.
 */
@RequiredArgsConstructor
public class GoodsViewModel {
    private final DependsStore<GoodsFragmentKey, AppState> store;

    public void refreshData() {
        store.dispatch(REFRESH_ACTION);
    }

    public void loadMore() {
        store.dispatch(LOAD_MORE_ACTION);
    }

    public void clickTitle() {
        store.dispatch(CHANGE_TITLE_ACTION);
    }

    private final Function1<AppState, LoadMoreState<ItemModel>> loadStateSelector = state -> state.getGoodsState().getLoadMoreState();

    private final Function2<AppState, LoadMoreState<ItemModel>, AppState> loadStateSetter =
            (state, loadState) -> state.withGoodsState(state.getGoodsState().withLoadMoreState(loadState));

    private final BaseDependAction<GoodsFragmentKey, AppState, PageData<ItemModel>> REFRESH_ACTION =
            action(depends(PRESENTER),
                    loadStateSelector,
                    loadStateSetter,
                    (real, loadState) -> {
                        GoodsPresenter presenter = real.getItem(PRESENTER);
                        return presenter.getData(0)
                                .map(data -> PageData.<ItemModel>builder()
                                        .pageNum(0)
                                        .data(data)
                                        .isLastData(false)
                                        .hasMoreData(true)
                                        .build());
                    },
                    ((data, loadState) -> loadState.withFirstPageData(data)));

    private final BaseDependAction<GoodsFragmentKey, AppState, PageData<ItemModel>> LOAD_MORE_ACTION =
            action(depends(PRESENTER),
                    loadStateSelector,
                    loadStateSetter,
                    (real, loadState) -> {
                        GoodsPresenter presenter = real.getItem(PRESENTER);
                        int next = loadState.getPageNumber() + 1;
                        return presenter.getData(next)
                                .map(data -> PageData.<ItemModel>builder()
                                        .pageNum(next)
                                        .data(data)
                                        .isLastData(next == 3)
                                        .hasMoreData(next < 3)
                                        .build());
                    },
                    ((data, loadState) -> {
                        if(data.getPageNum() == loadState.getPageNumber() + 1)
                            return loadState.withMorePageData(data);
                        return loadState;
                    }));

    private final BaseDependAction<GoodsFragmentKey, AppState, Boolean> CHANGE_TITLE_ACTION =
            action("TitleChange",
                    depends(CONTEXT),
                    (real, state) -> {
                        Context context = real.getItem(CONTEXT);

                        return Single.<Boolean>create(source -> new AlertDialog.Builder(context)
                                .setTitle("Change Title")
                                .setMessage("Ok: Ok Title\nCancel: Fail Title")
                                .setPositiveButton("Cancel", (dialog, which) -> source.onSuccess(false))
                                .setNegativeButton("OK", (dialog, which) -> source.onSuccess(true))
                                .setOnCancelListener(dialog -> source.onSuccess(true))
                                .create().show())

                                .subscribeOn(AndroidSchedulers.mainThread());
                    },
                    ((data, state) -> state.withGoodsState(state.getGoodsState().withTitle(data ? "Ok Title" : "Fail Title"))));
}

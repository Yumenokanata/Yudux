package indi.yume.demo.newapplication.ui.fragment.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import indi.yume.demo.newapplication.component.shopsearch.Handler;
import indi.yume.demo.newapplication.databinding.SearchFragmentBinding;
import indi.yume.demo.newapplication.model.api.GoodsModel;
import indi.yume.demo.newapplication.ui.fragment.AppState;
import indi.yume.demo.newapplication.ui.presenter.SearchPresenter;
import indi.yume.yudux.collection.BaseDependAction;
import indi.yume.yudux.collection.DependsStore;
import io.reactivex.Single;
import lombok.RequiredArgsConstructor;

import static indi.yume.demo.newapplication.ui.fragment.search.SearchFragment.SearchKey.BINDER;
import static indi.yume.demo.newapplication.ui.fragment.search.SearchFragment.SearchKey.PRESENTER;
import static indi.yume.yudux.DSL.*;

/**
 * Created by yume on 17-5-15.
 */
@RequiredArgsConstructor
public class SearchHandler implements Handler {
    private final DependsStore<SearchFragment.SearchKey, AppState> store;

    public void refreshData() {
        store.dispatchWithDepends(REFRESH_ACTION);
    }

    @Override
    public void onClickSearch() {
        store.dispatchWithDepends(SEARCH_ACTION);
    }

    private final BaseDependAction<SearchFragment.SearchKey, AppState, List<GoodsModel>> REFRESH_ACTION =
        action(depends(PRESENTER),
                (real, loadState) -> {
                    SearchPresenter presenter = real.getItem(PRESENTER);
                    return presenter.getAllGoods()
                            .map(l -> {
                                ArrayList<GoodsModel> list = new ArrayList<>(l);
                                Collections.reverse(list);
                                return list;
                            });
                },
                ((data, state) -> state.withSearchState(state.getSearchState().withLoading(false).withResult(data))));

    private final BaseDependAction<SearchFragment.SearchKey, AppState, String> SEARCH_ACTION =
            action(depends(BINDER),
                    (real, loadState) -> {
                        SearchFragmentBinding binding = real.getItem(BINDER);
                        return Single.just(binding.searchFrameLayout.searchEdittext.getText().toString());
                    },
                    ((data, state) -> state.withSearchState(state.getSearchState().withKeyWord(data))));

}

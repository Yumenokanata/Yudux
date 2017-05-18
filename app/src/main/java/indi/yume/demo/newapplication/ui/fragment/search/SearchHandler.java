package indi.yume.demo.newapplication.ui.fragment.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import indi.yume.demo.newapplication.component.shopsearch.Handler;
import indi.yume.demo.newapplication.databinding.SearchFragmentBinding;
import indi.yume.demo.newapplication.model.api.GoodsModel;
import indi.yume.demo.newapplication.ui.AppState;
import indi.yume.demo.newapplication.ui.fragment.search.SearchFragment.SearchKey;
import indi.yume.demo.newapplication.ui.presenter.SearchPresenter;
import indi.yume.yudux.collection.ContextCollection;
import indi.yume.yudux.collection.LazyAction;
import indi.yume.yudux.collection.RealWorld;
import indi.yume.yudux.collection.SingleDepends;
import io.reactivex.Single;
import lombok.RequiredArgsConstructor;

import static indi.yume.demo.newapplication.ui.AppStore.mainStore;
import static indi.yume.demo.newapplication.ui.fragment.search.SearchFragment.SearchKey.BINDER;
import static indi.yume.demo.newapplication.ui.fragment.search.SearchFragment.SearchKey.PRESENTER;
import static indi.yume.yudux.collection.DSL.*;
import static indi.yume.yudux.DSL.*;

/**
 * Created by yume on 17-5-15.
 */
@RequiredArgsConstructor
public class SearchHandler implements Handler {
    private final ContextCollection<SearchKey> repo;

    public void refreshData() {
        mainStore.dispatch(create(repo, REFRESH_ACTION));
    }

    @Override
    public void onClickSearch() {
        mainStore.dispatch(create(repo, SEARCH_ACTION));
    }

    private static final LazyAction<RealWorld<SearchKey>, SingleDepends<SearchKey>, AppState, List<GoodsModel>> REFRESH_ACTION =
        action(lazy(PRESENTER),
                (real, loadState) -> {
                    SearchPresenter presenter = real.getItem(PRESENTER);
                    return presenter.getAllGoods()
                            .map(l -> {
                                ArrayList<GoodsModel> list = new ArrayList<>(l);
                                Collections.reverse(list);
                                return list;
                            });
                },
                (data, state) -> state.withSearchState(state.getSearchState().withLoading(false).withResult(data)));

    private static final LazyAction<RealWorld<SearchKey>, SingleDepends<SearchKey>, AppState, String> SEARCH_ACTION =
            action(lazy(BINDER),
                    (real, loadState) -> {
                        SearchFragmentBinding binding = real.getItem(BINDER);
                        return Single.just(binding.searchFrameLayout.searchEdittext.getText().toString());
                    },
                    (data, state) -> state.withSearchState(state.getSearchState().withKeyWord(data)));

}

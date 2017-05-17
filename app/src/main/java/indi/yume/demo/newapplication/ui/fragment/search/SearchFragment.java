package indi.yume.demo.newapplication.ui.fragment.search;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.annimon.stream.ComparatorCompat;
import com.annimon.stream.Stream;

import java.util.Arrays;

import indi.yume.demo.newapplication.BR;
import indi.yume.demo.newapplication.R;
import indi.yume.demo.newapplication.component.keep.Actions;
import indi.yume.demo.newapplication.databinding.SearchFragmentBinding;
import indi.yume.demo.newapplication.functions.Receiver;
import indi.yume.demo.newapplication.model.api.GoodsModel;
import indi.yume.demo.newapplication.ui.AppComponent;
import indi.yume.demo.newapplication.ui.MainApplication;
import indi.yume.demo.newapplication.ui.component.DaggerSearchComponent;
import indi.yume.demo.newapplication.ui.component.SearchComponent;
import indi.yume.demo.newapplication.ui.AppState;
import indi.yume.demo.newapplication.ui.fragment.base.BaseToolbarFragment;
import indi.yume.demo.newapplication.ui.module.SearchModule;
import indi.yume.demo.newapplication.ui.presenter.SearchPresenter;
import indi.yume.tools.dsladapter2.RendererAdapter;
import indi.yume.yudux.collection.BaseDependAction;
import indi.yume.yudux.collection.DependsStore;
import indi.yume.yudux.functions.Unit;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static indi.yume.demo.databinding.DataBindingDsl.dataBindingRepositoryPresenterOf;
import static indi.yume.demo.newapplication.ui.fragment.search.SearchFragment.SearchKey.*;
import static indi.yume.demo.newapplication.ui.fragment.search.SearchState.*;
import static indi.yume.tools.dsladapter2.renderer.RenderDsl.*;
import static indi.yume.yudux.DSL.action;
import static indi.yume.yudux.DSL.depends;
import static indi.yume.yudux.DSL.effect;

/**
 * Created by DaggerGenerator on 2017/05/15.
 */
public class SearchFragment extends BaseToolbarFragment{
    enum SearchKey {
        CONTEXT,
        VIEW,
        BINDER,
        PRESENTER,
        HANDLER,
        RESULT_ADAPTER,
        MENU,
        INIT
    }

    private final DependsStore<SearchKey, AppState> store =
            DependsStore.<SearchKey, AppState>builder(MainApplication.getMainStore())
                    .withItem(HANDLER,
                            depends(),
                            (real, store) -> new SearchHandler(store))
                    .withItem(MENU,
                            depends(MENU),
                            (real, store) -> {
                                MenuItem menuItem = real.getItem(MENU);
                                return menuItem;
                            })
                    .withItem(BINDER,
                            depends(VIEW, HANDLER, RESULT_ADAPTER),
                            (real, store) -> {
                                SearchFragmentBinding binding = SearchFragmentBinding.bind(real.getItem(VIEW));
                                SearchHandler handler = real.getItem(HANDLER);
                                binding.setHandler(handler);
                                binding.setSearchHandler(handler);

                                RendererAdapter adapter = real.getItem(RESULT_ADAPTER);
                                RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                                adapter.setLayoutManager(layoutManager);
                                binding.shopRecyclerview.setAdapter(adapter);
                                binding.shopRecyclerview.setLayoutManager(layoutManager);

                                binding.swipeRefreshLayout.setOnRefreshListener(() -> store.dispatchWithDepends(
                                        effect(depends(HANDLER), (r, s) -> r.<SearchHandler>getItem(HANDLER).refreshData())));

                                binding.searchFrameLayout.searchEdittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                    @Override
                                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                                            store.dispatchWithDepends(
                                                    effect(depends(HANDLER), (r, s) -> r.<SearchHandler>getItem(HANDLER).onClickSearch()));
                                        }
                                        return false;
                                    }
                                });
                                binding.searchFrameLayout.searchEdittext.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                                    }

                                    @Override
                                    public void afterTextChanged(Editable editable) {
                                        if (TextUtils.isEmpty(editable)) {
                                            store.dispatchWithDepends(
                                                    effect(depends(HANDLER), (r, s) -> r.<SearchHandler>getItem(HANDLER).onClickSearch()));

                                        }
                                    }
                                });

                                return binding;
                            })
                    .withItem(PRESENTER,
                            depends(CONTEXT),
                            (real, store) -> {
                                AppComponent appComponent = MainApplication.getInstance(real.getItem(CONTEXT)).getAppComponent();
                                SearchComponent searchComponent = DaggerSearchComponent.builder()
                                        .appComponent(appComponent)
                                        .searchModule(new SearchModule(this))
                                        .build();
                                SearchPresenter presenter = searchComponent.getPresenter();
                                searchComponent.injectPresenter(presenter);

                                return presenter;
                            })
                    .withItem(RESULT_ADAPTER,
                            depends(),
                            (real, store) -> new RendererAdapter<>(store.getState().getSearchState(),
                                    suppiler ->
                                            list(GoodsModel.class)
                                                    .item(dataBindingRepositoryPresenterOf(GoodsModel.class)
                                                            .layout(R.layout.recycler_shop)
                                                            .staticItemId(BR.model)
                                                            .itemId(BR.showBtn, m -> suppiler.get().getSelectedItem() == m.getBarCode())
                                                            .handler(BR.clickMain,
                                                                    (Receiver<GoodsModel>) (model) -> {
                                                                        if (suppiler.get().getSelectedItem() == model.getBarCode())
                                                                            store.dispatchWithDepends(selectItem(""));
                                                                        else
                                                                            store.dispatchWithDepends(selectItem(model.getBarCode()));
                                                                    })
                                                            .handler(BR.clickBuy,
                                                                    (Receiver<GoodsModel>) (model) -> {
                                                                    })
                                                            .handler(BR.clickKeep,
                                                                    (Receiver<GoodsModel>) (model) -> {
                                                                        Actions.toggleKeep(model);
                                                                    })
                                                            .forItem())
                                                    .forCollection(s -> {
                                                        Stream<GoodsModel> stream = Stream.of(s.getResult())
                                                                .filter(m -> m.getCount() > 0 && (s.getKeyWord() == null || m.getName().contains(s.getKeyWord())));

                                                        switch (s.getSort()) {
                                                            case SORT_NAME:
                                                                return stream.sorted(
                                                                        ComparatorCompat.<GoodsModel, String>comparing(m -> m.getName()).reversed())
                                                                        .toList();
                                                            case SORT_COUNT:
                                                                return stream.sorted(
                                                                        ComparatorCompat.<GoodsModel>comparingDouble(m -> m.getCount()).reversed())
                                                                        .toList();
                                                            case SORT_PRICE:
                                                                return stream.sorted(
                                                                        ComparatorCompat.<GoodsModel>comparingDouble(m -> m.getSalePrice()).reversed())
                                                                        .toList();
                                                            case SORT_DEFAULT:
                                                            default:
                                                                return stream.toList();
                                                        }
                                                    })))
                    .withItem(INIT,
                            depends(HANDLER, PRESENTER, BINDER),
                            (real, store) -> {
                                real.<SearchHandler>getItem(HANDLER).refreshData();
                                return Unit.unit();
                            })
                    .build();

    {
        store.subscribe(depends(BINDER),
                (state, real) -> {
                    SearchFragmentBinding binding = real.getItem(BINDER);
                    binding.setModel(state.getSearchState());
                    if(!state.getSearchState().isLoading())
                        binding.swipeRefreshLayout.setRefreshing(false);
                });
        store.subscribe(depends(MENU),
                (state, real) -> real.<MenuItem>getItem(MENU)
                        .setEnabled(!state.getSearchState().isLoading() && !state.getSearchState().getResult().isEmpty()));
        store.subscribe(depends(RESULT_ADAPTER),
                (state, real) -> real.<RendererAdapter>getItem(RESULT_ADAPTER).update(state.getSearchState()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.search_fragment, container, false);
        store.ready(VIEW, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        store.ready(CONTEXT, context);
        super.onAttach(context);
    }

    @Override
    public int provideOptionMenuRes() {
        return R.menu.search_result_fragment;
    }

    @Override
    protected void onOptionsMenuCreated(Menu menu) {
        MenuItem sortMenuItem = menu.findItem(R.id.menu_sort);
        store.ready(MENU, sortMenuItem);
        store.forceRender();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort:
                store.dispatchWithDepends(CLICK_SORT_MENU);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        store.destroy(CONTEXT);
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        store.destroyAll();
        super.onDestroy();
    }

    @Override
    public void inject(AppComponent appComponent){

    }

    private static final BaseDependAction<SearchKey, AppState, Integer> CLICK_SORT_MENU =
        action("menu",
                depends(CONTEXT, MENU, VIEW),
                (real, oldState) -> {
                    if(oldState.getSearchState().isLoading())
                        return Single.just(-1);

                    return Single.<Integer>create(emmit -> {
                        ViewGroup mainView = real.getItem(VIEW);
                        Context context = real.getItem(CONTEXT);
                        View view = LayoutInflater.from(context).inflate(R.layout.sort_option_layout, mainView, false);
                        ListView list = (ListView) view.findViewById(R.id.list_view);
                        list.setAdapter(new ArrayAdapter<>(context, R.layout.search_sort_menu_item,
                                context.getResources().getStringArray(R.array.search_sort_items)));
                        list.setItemChecked(Arrays.binarySearch(SORT_OPTIONS, oldState.getSearchState().getSort()), true);

                        PopupWindow window = new PopupWindow(view, MATCH_PARENT, MATCH_PARENT);
                        window.setFocusable(true);
                        window.setOutsideTouchable(true);
                        window.setBackgroundDrawable(new ColorDrawable());
                        window.showAtLocation(mainView, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);

                        window.setOnDismissListener(() -> {
                            if(!emmit.isDisposed()) emmit.onSuccess(-1);
                        });
                        list.setOnItemClickListener((lv, v, p, i) -> {
                            emmit.onSuccess(SORT_OPTIONS[p]);
                            window.dismiss();
                        });
                        view.setOnClickListener(v -> window.dismiss());
                    })
                            .subscribeOn(AndroidSchedulers.mainThread());
                },
                ((sort, oldState) -> sort == -1 ? oldState
                        : oldState.withSearchState(oldState.getSearchState().withSort(sort))));

    private static final BaseDependAction<SearchKey, AppState, String> selectItem(String barCode) {
        return action(depends(),
                (real, oldState) -> Single.just(barCode),
                ((selected, oldState) -> oldState.withSearchState(oldState.getSearchState().withSelectedItem(selected))));
    }
}
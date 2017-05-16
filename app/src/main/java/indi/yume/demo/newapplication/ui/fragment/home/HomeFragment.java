package indi.yume.demo.newapplication.ui.fragment.home;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.Collections;

import indi.yume.demo.newapplication.BR;
import indi.yume.demo.newapplication.R;
import indi.yume.demo.newapplication.databinding.HomeFragmentBinding;
import indi.yume.demo.newapplication.functions.Receiver;
import indi.yume.demo.newapplication.model.api.GoodsModel;
import indi.yume.demo.newapplication.ui.AppComponent;
import indi.yume.demo.newapplication.ui.MainApplication;
import indi.yume.demo.newapplication.ui.component.DaggerHomeComponent;
import indi.yume.demo.newapplication.ui.component.HomeComponent;
import indi.yume.demo.newapplication.ui.AppState;
import indi.yume.demo.newapplication.ui.fragment.base.BaseToolbarFragment;
import indi.yume.demo.newapplication.ui.module.HomeModule;
import indi.yume.demo.newapplication.ui.presenter.HomePresenter;
import indi.yume.tools.dsladapter2.RendererAdapter;
import indi.yume.yudux.collection.BaseDependAction;
import indi.yume.yudux.collection.DependsStore;
import indi.yume.yudux.functions.Unit;
import io.reactivex.Single;

import static indi.yume.demo.databinding.DataBindingDsl.dataBindingRepositoryPresenterOf;
import static indi.yume.demo.newapplication.ui.fragment.home.HomeFragment.HomeKey.*;
import static indi.yume.tools.dsladapter2.renderer.RenderDsl.*;
import static indi.yume.yudux.DSL.*;

/**
 * Created by DaggerGenerator on 2017/05/02.
 */
public class HomeFragment extends BaseToolbarFragment{
    enum HomeKey {
        CONTEXT,
        VIEW,
        BINDER,
        PRESENTER,
        HANDLER,
        LAST_ADAPTER,
        NEW_ADAPTER,
        INIT
    }

    private final DependsStore<HomeKey, AppState> store =
            DependsStore.<HomeKey, AppState>builder(MainApplication.getMainStore())
                    .withItem(HANDLER,
                            depends(),
                            (real, store) -> new HomeHandler(store))
                    .withItem(BINDER,
                            depends(VIEW, HANDLER, CONTEXT, LAST_ADAPTER, NEW_ADAPTER),
                            (real, store) -> {
                                HomeFragmentBinding binding = HomeFragmentBinding.bind(real.getItem(VIEW));
                                binding.setHandler(real.getItem(HANDLER));

                                Context context = real.getItem(CONTEXT);
                                RendererAdapter adapter = real.getItem(LAST_ADAPTER);
                                RecyclerView.LayoutManager lastLayoutManager = new LinearLayoutManager(context);
                                adapter.setLayoutManager(lastLayoutManager);
                                binding.lastPayListView.setAdapter(adapter);
                                binding.lastPayListView.setLayoutManager(lastLayoutManager);
                                binding.lastPayListView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(context).build());

                                RendererAdapter newAdapter = real.getItem(NEW_ADAPTER);
                                RecyclerView.LayoutManager newLayoutManager = new LinearLayoutManager(context);
                                newAdapter.setLayoutManager(newLayoutManager);
                                binding.newArrivalListView.setAdapter(newAdapter);
                                binding.newArrivalListView.setLayoutManager(newLayoutManager);
                                binding.newArrivalListView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(context).build());

                                return binding;
                            })
                    .withItem(PRESENTER,
                            depends(CONTEXT),
                            (real, store) -> {
                                AppComponent appComponent = MainApplication.getInstance(real.getItem(CONTEXT)).getAppComponent();
                                HomeComponent homeComponent = DaggerHomeComponent.builder()
                                        .appComponent(appComponent)
                                        .homeModule(new HomeModule(this))
                                        .build();
                                HomePresenter presenter = homeComponent.getPresenter();
                                homeComponent.injectPresenter(presenter);

                                return presenter;
                            })
                    .withItem(LAST_ADAPTER,
                            depends(),
                            (real, store) -> new RendererAdapter<>(store.getState().getHomeState(),
                                    suppiler ->
                                            list(GoodsModel.class)
                                                    .item(dataBindingRepositoryPresenterOf(GoodsModel.class)
                                                            .layout(R.layout.home_history_item)
                                                            .staticItemId(BR.model)
                                                            .itemId(BR.showBtn, m -> suppiler.get().getSelectedItemAtLast() == m.getBarCode())
                                                            .handler(BR.clickMain,
                                                                    (Receiver<GoodsModel>) (model) -> {
                                                                        if (suppiler.get().getSelectedItemAtLast() == model.getBarCode())
                                                                            store.dispatchWithDepends(selectItemAtLast(""));
                                                                        else
                                                                            store.dispatchWithDepends(selectItemAtLast(model.getBarCode()));
                                                                    })
                                                            .handler(BR.clickBuy,
                                                                    (Receiver<GoodsModel>) (model) -> {
                                                                    })
                                                            .handler(BR.clickKeep,
                                                                    (Receiver<GoodsModel>) (model) -> {
                                                                    })
                                                            .forItem())
                                                    .forCollection(s -> s.getLastPay() == null ?
                                                            Collections.<GoodsModel>emptyList() : s.getLastPay())))
                    .withItem(NEW_ADAPTER,
                            depends(),
                            (real, store) -> new RendererAdapter<>(store.getState().getHomeState(),
                                    suppiler ->
                                            list(GoodsModel.class)
                                                    .item(dataBindingRepositoryPresenterOf(GoodsModel.class)
                                                            .layout(R.layout.home_history_item)
                                                            .staticItemId(BR.model)
                                                            .itemId(BR.showBtn, m -> suppiler.get().getSelectedItemAtNew() == m.getBarCode())
                                                            .handler(BR.clickMain,
                                                                    (Receiver<GoodsModel>) (model) -> {
                                                                        if (suppiler.get().getSelectedItemAtNew() == model.getBarCode())
                                                                            store.dispatchWithDepends(selectItemAtNew(""));
                                                                        else
                                                                            store.dispatchWithDepends(selectItemAtNew(model.getBarCode()));
                                                                    })
                                                            .handler(BR.clickBuy,
                                                                    (Receiver<GoodsModel>) (model) -> {
                                                                    })
                                                            .handler(BR.clickKeep,
                                                                    (Receiver<GoodsModel>) (model) -> {
                                                                    })
                                                            .forItem())
                                                    .forCollection(s -> s.getNewArrival() == null ?
                                                            Collections.<GoodsModel>emptyList() : s.getNewArrival()))
                    )
                    .withItem(INIT,
                            depends(HANDLER, PRESENTER, BINDER),
                            (real, store) -> {
                                real.<HomeHandler>getItem(HANDLER).initData();
                                return Unit.unit();
                            })
                    .build();

    {
        store.subscribe(depends(BINDER),
                (state, real) -> real.<HomeFragmentBinding>getItem(BINDER).setState(state.getHomeState()));
        store.subscribe(depends(LAST_ADAPTER),
                (state, real) -> real.<RendererAdapter>getItem(LAST_ADAPTER).update(state.getHomeState()));
        store.subscribe(depends(NEW_ADAPTER),
                (state, real) -> real.<RendererAdapter>getItem(NEW_ADAPTER).update(state.getHomeState()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        store.ready(VIEW, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        store.ready(CONTEXT, context);
        super.onAttach(context);
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

    private final BaseDependAction<HomeKey, AppState, String> selectItemAtLast(String barCode) {
        return action(depends(),
                (real, oldState) -> Single.just(barCode),
                ((selected, oldState) -> oldState.withHomeState(oldState.getHomeState().withSelectAtLast(selected))));
    }

    private final BaseDependAction<HomeKey, AppState, String> selectItemAtNew(String barCode) {
        return action(depends(),
                (real, oldState) -> Single.just(barCode),
                ((selected, oldState) -> oldState.withHomeState(oldState.getHomeState().withSelectAtNew(selected))));
    }
}
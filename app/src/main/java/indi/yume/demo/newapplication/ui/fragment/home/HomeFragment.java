package indi.yume.demo.newapplication.ui.fragment.home;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.Stream;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.Collections;

import indi.yume.demo.newapplication.BR;
import indi.yume.demo.newapplication.R;
import indi.yume.demo.newapplication.component.cart.CartActions;
import indi.yume.demo.newapplication.component.keep.Actions;
import indi.yume.demo.newapplication.databinding.HomeFragmentBinding;
import indi.yume.demo.newapplication.functions.Receiver;
import indi.yume.demo.newapplication.model.api.GoodsModel;
import indi.yume.demo.newapplication.ui.AppComponent;
import indi.yume.demo.newapplication.ui.AppStore;
import indi.yume.demo.newapplication.ui.MainApplication;
import indi.yume.demo.newapplication.ui.component.DaggerHomeComponent;
import indi.yume.demo.newapplication.ui.component.HomeComponent;
import indi.yume.demo.newapplication.ui.AppState;
import indi.yume.demo.newapplication.ui.fragment.base.BaseToolbarFragment;
import indi.yume.demo.newapplication.ui.module.HomeModule;
import indi.yume.demo.newapplication.ui.presenter.HomePresenter;
import indi.yume.tools.dsladapter2.RendererAdapter;
import indi.yume.yudux.collection.BaseDependAction;
import indi.yume.yudux.collection.ContextCollection;
import indi.yume.yudux.collection.RealWorld;
import indi.yume.yudux.functions.Unit;
import indi.yume.yudux.store.Action;
import io.reactivex.Single;

import static indi.yume.demo.databinding.DataBindingDsl.dataBindingRepositoryPresenterOf;
import static indi.yume.demo.newapplication.ui.AppStore.mainStore;
import static indi.yume.demo.newapplication.ui.fragment.home.HomeFragment.HomeKey.*;
import static indi.yume.tools.dsladapter2.renderer.RenderDsl.*;
import static indi.yume.yudux.collection.DSL.*;
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

    private final ContextCollection<HomeKey> repo =
            ContextCollection.<HomeKey>builder()
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
                            (real, collection) -> new RendererAdapter<>(mainStore.getState().getHomeState(),
                                    suppiler ->
                                            list(GoodsModel.class)
                                                    .item(dataBindingRepositoryPresenterOf(GoodsModel.class)
                                                            .layout(R.layout.home_history_item)
                                                            .staticItemId(BR.model)
                                                            .itemId(BR.hasKeep, m -> Stream.of(mainStore.getState().getKeepState().getKeepList())
                                                                    .anyMatch(g -> TextUtils.equals(g.getBarCode(), m.getBarCode())))
                                                            .itemId(BR.showBtn, m -> suppiler.get().getSelectedItemAtLast() == m.getBarCode())
                                                            .handler(BR.clickMain,
                                                                    (Receiver<GoodsModel>) (model) -> {
                                                                        if (suppiler.get().getSelectedItemAtLast() == model.getBarCode())
                                                                            mainStore.dispatch(selectItemAtLast(""));
                                                                        else
                                                                            mainStore.dispatch(selectItemAtLast(model.getBarCode()));
                                                                    })
                                                            .handler(BR.clickBuy,
                                                                    (Receiver<GoodsModel>) (model) -> {
                                                                        CartActions.toggle(model);
                                                                        mainStore.dispatch(selectItemAtLast(""));
                                                                    })
                                                            .handler(BR.clickKeep,
                                                                    (Receiver<GoodsModel>) (model) -> {
                                                                        Actions.toggleKeep(model);
                                                                        mainStore.dispatch(selectItemAtLast(""));
                                                                    })
                                                            .forItem())
                                                    .forCollection(s -> s.getLastPay() == null ?
                                                            Collections.<GoodsModel>emptyList() : s.getLastPay())))
                    .withItem(NEW_ADAPTER,
                            depends(),
                            (real, store) -> new RendererAdapter<>(mainStore.getState().getHomeState(),
                                    suppiler ->
                                            list(GoodsModel.class)
                                                    .item(dataBindingRepositoryPresenterOf(GoodsModel.class)
                                                            .layout(R.layout.home_history_item)
                                                            .staticItemId(BR.model)
                                                            .itemId(BR.hasKeep, m -> Stream.of(mainStore.getState().getKeepState().getKeepList())
                                                                    .anyMatch(g -> TextUtils.equals(g.getBarCode(), m.getBarCode())))
                                                            .itemId(BR.showBtn, m -> suppiler.get().getSelectedItemAtNew() == m.getBarCode())
                                                            .handler(BR.clickMain,
                                                                    (Receiver<GoodsModel>) (model) -> {
                                                                        if (suppiler.get().getSelectedItemAtNew() == model.getBarCode())
                                                                            mainStore.dispatch(selectItemAtNew(""));
                                                                        else
                                                                            mainStore.dispatch(selectItemAtNew(model.getBarCode()));
                                                                    })
                                                            .handler(BR.clickBuy,
                                                                    (Receiver<GoodsModel>) (model) -> {
                                                                        CartActions.toggle(model);
                                                                        mainStore.dispatch(selectItemAtLast(""));
                                                                    })
                                                            .handler(BR.clickKeep,
                                                                    (Receiver<GoodsModel>) (model) -> {
                                                                        Actions.toggleKeep(model);
                                                                        mainStore.dispatch(selectItemAtNew(""));
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
        subscribeUntilChanged(mainStore,
                extra(repo, depends(BINDER)),
                AppState::getHomeState,
                (state, real) -> real.<HomeFragmentBinding>getItem(BINDER).setState(state));
        subscribeUntilChanged(mainStore,
                extra(repo, depends(LAST_ADAPTER)),
                AppState::getHomeState,
                (state, real) -> real.<RendererAdapter>getItem(LAST_ADAPTER).update(state));
        subscribeUntilChanged(mainStore,
                extra(repo, depends(NEW_ADAPTER)),
                AppState::getHomeState,
                (state, real) -> real.<RendererAdapter>getItem(NEW_ADAPTER).update(state));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        repo.ready(VIEW, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        repo.ready(CONTEXT, context);
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        repo.destroy(CONTEXT);
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        repo.destroyAll();
        super.onDestroy();
    }

    @Override
    public void inject(AppComponent appComponent){

    }

    private final Action<AppState, String> selectItemAtLast(String barCode) {
        return action(oldState -> Single.just(barCode),
                ((selected, oldState) -> oldState.withHomeState(oldState.getHomeState().withSelectAtLast(selected))));
    }

    private final Action<AppState, String> selectItemAtNew(String barCode) {
        return action(oldState -> Single.just(barCode),
                ((selected, oldState) -> oldState.withHomeState(oldState.getHomeState().withSelectAtNew(selected))));
    }
}
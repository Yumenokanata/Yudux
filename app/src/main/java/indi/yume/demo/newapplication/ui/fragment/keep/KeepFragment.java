package indi.yume.demo.newapplication.ui.fragment.keep;

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
import indi.yume.demo.newapplication.databinding.KeepFragmentBinding;
import indi.yume.demo.newapplication.functions.Receiver;
import indi.yume.demo.newapplication.model.api.GoodsModel;
import indi.yume.demo.newapplication.ui.AppComponent;
import indi.yume.demo.newapplication.ui.MainApplication;
import indi.yume.demo.newapplication.ui.component.DaggerKeepComponent;
import indi.yume.demo.newapplication.ui.component.KeepComponent;
import indi.yume.demo.newapplication.ui.AppState;
import indi.yume.demo.newapplication.ui.fragment.base.BaseToolbarFragment;
import indi.yume.demo.newapplication.ui.module.KeepModule;
import indi.yume.demo.newapplication.ui.presenter.KeepPresenter;
import indi.yume.tools.dsladapter2.RendererAdapter;
import indi.yume.yudux.collection.BaseDependAction;
import indi.yume.yudux.collection.ContextCollection;
import indi.yume.yudux.store.Action;
import io.reactivex.Single;

import static indi.yume.demo.databinding.DataBindingDsl.*;
import static indi.yume.demo.newapplication.ui.AppStore.mainStore;
import static indi.yume.demo.newapplication.ui.fragment.keep.KeepFragment.KeepKey.*;
import static indi.yume.tools.dsladapter2.renderer.RenderDsl.*;
import static indi.yume.yudux.collection.DSL.*;
import static indi.yume.yudux.DSL.*;

/**
 * Created by DaggerGenerator on 2017-05-16.
 */
public class KeepFragment extends BaseToolbarFragment {
    enum KeepKey {
        CONTEXT,
        VIEW,
        BINDER,
        PRESENTER,
        HANDLER,
        ADAPTER
    }

    private final ContextCollection<KeepKey> repo =
            ContextCollection.<KeepKey>builder()
                    .withItem(HANDLER,
                            depends(),
                            (real, store) -> new KeepHandler(store))
                    .withItem(BINDER,
                            depends(CONTEXT, VIEW, HANDLER, ADAPTER),
                            (real, store) -> {
                                KeepFragmentBinding binding = KeepFragmentBinding.bind(real.getItem(VIEW));
                                binding.setHandler(real.getItem(HANDLER));

                                Context context = real.getItem(CONTEXT);
                                RendererAdapter adapter = real.getItem(ADAPTER);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                                adapter.setLayoutManager(layoutManager);
                                binding.keepListView.setAdapter(adapter);
                                binding.keepListView.setLayoutManager(layoutManager);
                                binding.keepListView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(context).build());

                                return binding;
                            })
                    .withItem(PRESENTER,
                            depends(CONTEXT),
                            (real, store) -> {
                                AppComponent appComponent = MainApplication.getInstance(real.getItem(CONTEXT)).getAppComponent();
                                KeepComponent keepComponent = DaggerKeepComponent.builder()
                                        .appComponent(appComponent)
                                        .keepModule(new KeepModule(this))
                                        .build();
                                KeepPresenter presenter = keepComponent.getPresenter();
                                keepComponent.injectPresenter(presenter);

                                return presenter;
                            })
                    .withItem(ADAPTER,
                            depends(),
                            (real, collection) -> new RendererAdapter<>(mainStore.getState().getKeepState(),
                                    suppiler ->
                                            list(GoodsModel.class)
                                                    .item(dataBindingRepositoryPresenterOf(GoodsModel.class)
                                                            .layout(R.layout.home_history_item)
                                                            .staticItemId(BR.model)
                                                            .itemId(BR.hasKeep, m -> Stream.of(mainStore.getState().getKeepState().getKeepList())
                                                                    .anyMatch(g -> TextUtils.equals(g.getBarCode(), m.getBarCode())))
                                                            .itemId(BR.showBtn, m -> suppiler.get().getSelectedItem() == m.getBarCode())
                                                            .handler(BR.clickMain,
                                                                    (Receiver<GoodsModel>) (model) -> {
                                                                        if (suppiler.get().getSelectedItem() == model.getBarCode())
                                                                            mainStore.dispatch(selectItem(""));
                                                                        else
                                                                            mainStore.dispatch(selectItem(model.getBarCode()));
                                                                    })
                                                            .handler(BR.clickBuy,
                                                                    (Receiver<GoodsModel>) (model) -> {
                                                                        CartActions.toggle(model);
                                                                        mainStore.dispatch(selectItem(""));
                                                                    })
                                                            .handler(BR.clickKeep,
                                                                    (Receiver<GoodsModel>) (model) -> {
                                                                        Actions.toggleKeep(model);
                                                                        mainStore.dispatch(selectItem(""));
                                                                    })
                                                            .forItem())
                                                    .forCollection(s -> s.getKeepList() == null ?
                                                            Collections.<GoodsModel>emptyList() : s.getKeepList())))
                    .build();

    {
        subscribeUntilChanged(
                mainStore,
                extra(repo, depends(BINDER)),
                s -> s.getKeepState(),
                (real, state) -> {
                    System.out.println(getClass().getSimpleName() + "| Render Keep: " + state);
                    real.<KeepFragmentBinding>getItem(BINDER).setModel(state);
                });
        subscribe(mainStore,
                extra(repo, depends(ADAPTER)),
                (real, state) -> real.<RendererAdapter>getItem(ADAPTER).update(state.getKeepState()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.keep_fragment, container, false);
        repo.ready(VIEW, view);
        forceRender(mainStore);
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

    private final Action<AppState, String> selectItem(String barCode) {
        return action(oldState -> Single.just(barCode),
                ((selected, oldState) -> oldState.withKeepState(oldState.getKeepState().withSelectedItem(selected))));
    }
}
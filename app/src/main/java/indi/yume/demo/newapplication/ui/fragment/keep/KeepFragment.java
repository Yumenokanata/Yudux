package indi.yume.demo.newapplication.ui.fragment.keep;

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
import indi.yume.yudux.collection.DependsStore;
import io.reactivex.Single;

import static indi.yume.demo.databinding.DataBindingDsl.*;
import static indi.yume.demo.newapplication.ui.fragment.keep.KeepFragment.KeepKey.*;
import static indi.yume.tools.dsladapter2.renderer.RenderDsl.*;
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

    private final DependsStore<KeepKey, AppState> store =
            DependsStore.<KeepKey, AppState>builder(MainApplication.getMainStore())
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
                            (real, store) -> new RendererAdapter<>(store.getState().getKeepState(),
                                    suppiler ->
                                            list(GoodsModel.class)
                                                    .item(dataBindingRepositoryPresenterOf(GoodsModel.class)
                                                            .layout(R.layout.home_history_item)
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
                                                    .forCollection(s -> s.getKeepList() == null ?
                                                            Collections.<GoodsModel>emptyList() : s.getKeepList())))
                    .build();

    {
        store.subscribe(depends(BINDER),
                (state, real) -> real.<KeepFragmentBinding>getItem(BINDER).setModel(state.getKeepState()));
        store.subscribe(depends(ADAPTER),
                (state, real) -> real.<RendererAdapter>getItem(ADAPTER).update(state.getKeepState()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.keep_fragment, container, false);
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

    private final BaseDependAction<KeepKey, AppState, String> selectItem(String barCode) {
        return action(depends(),
                (real, oldState) -> Single.just(barCode),
                ((selected, oldState) -> oldState.withKeepState(oldState.getKeepState().withSelectedItem(selected))));
    }
}
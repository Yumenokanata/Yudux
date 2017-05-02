package indi.yume.demo.newapplication.ui.fragment.goods;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import indi.yume.demo.newapplication.BR;
import indi.yume.demo.newapplication.R;
import indi.yume.demo.newapplication.databinding.GoodsFragmentBinding;
import indi.yume.demo.newapplication.ui.MainApplication;
import indi.yume.demo.newapplication.ui.fragment.AppState;
import indi.yume.demo.newapplication.ui.fragment.goods.TestData.ItemModel;
import indi.yume.tools.fragmentmanager.BaseManagerFragment;
import indi.yume.view.avocadoviews.dsladapter.RendererAdapter;
import indi.yume.view.avocadoviews.dsladapter.functions.Supplier;
import indi.yume.view.avocadoviews.loadinglayout.LoadMoreStatus;
import indi.yume.yudux.collection.DependsStore;
import kotlin.jvm.functions.Function1;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import static indi.yume.demo.newapplication.ui.fragment.goods.GoodsFragment.GoodsFragmentKey.*;
import static indi.yume.view.avocadoviews.adapterdatabinding.DataBindingRendererBuilder.dataBindingRepositoryPresenterOf;
import static indi.yume.view.avocadoviews.dsladapter.builder.RendererBuilder.layout;
import static indi.yume.yudux.DSL.depends;

/**
 * Created by yume on 17-4-28.
 */

public class GoodsFragment extends BaseManagerFragment {
    enum GoodsFragmentKey {
        CONTEXT,
        PRESENTER,
        VIEW,
        BINDER,
        VIEW_MODEL,
        ADAPTER
    }

    private final DependsStore<GoodsFragmentKey, AppState> store =
            DependsStore.<GoodsFragmentKey, AppState>builder(MainApplication.getMainStore())
                    .withItem(BINDER,
                            depends(VIEW, VIEW_MODEL),
                            (real, store) -> {
                                GoodsFragmentBinding binding = GoodsFragmentBinding.bind(real.getItem(VIEW));
                                binding.setViewModel(real.getItem(VIEW_MODEL));
                                return binding;
                            })
                    .withItem(ADAPTER,
                            depends(BINDER, CONTEXT),
                            (real, store) -> {
                                DataSupplier<LoadMoreState<ItemModel>> supplier = new DataSupplier<>(new LoadMoreState<>());
                                RendererAdapter adapter = RendererAdapter.repositoryAdapter()
                                        .addLayout(layout(R.layout.list_header))
                                        .add(() -> supplier.get().getData(),
                                                dataBindingRepositoryPresenterOf(ItemModel.class)
                                                        .layout(R.layout.item_layout)
                                                        .itemId(BR.model)
                                                        .stableIdForItem(ItemModel::getId)
                                                        .forList())
                                        .add(() -> supplier.get().isLoadingMore() ? "Loading" : "LoadOver",
                                                dataBindingRepositoryPresenterOf(String.class)
                                                        .layout(R.layout.list_footer)
                                                        .itemId(BR.text)
                                                        .forItem())
                                        .build();
                                adapter.setHasStableIds(true);

                                Context context = real.getItem(CONTEXT);
                                GoodsFragmentBinding binding = real.getItem(BINDER);
                                binding.recyclerView.setAdapter(adapter);
                                binding.recyclerView.setLayoutManager(new LinearLayoutManager(context));

                                return new AdapterData<>(adapter, supplier);
                            })
                    .build();

    {
        store.subscribe(depends(BINDER),
                (state, real) -> real.<GoodsFragmentBinding>getItem(BINDER).setModel(state.getGoodsState()));
        store.subscribe(depends(ADAPTER),
                (state, real) -> {
                    AdapterData<RendererAdapter, LoadMoreState<ItemModel>> adapterData = real.getItem(ADAPTER);
                    adapterData.supplier.setData(state.getGoodsState().getLoadMoreState());
                    adapterData.adapter.update();
                });
    }

    @AllArgsConstructor
    static class DataSupplier<T> implements Supplier<T> {
        @Setter
        private T data;

        @Override
        public T get() {
            return data;
        }
    }

    @Data
    static class AdapterData<A extends RendererAdapter, T> {
        private final A adapter;
        private final DataSupplier<T>supplier;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.goods_fragment, container, false);
        GoodsPresenter presenter = new GoodsPresenter();
        GoodsViewModel viewModel = new GoodsViewModel(store);
        store.ready(VIEW, view);
        store.ready(PRESENTER, presenter);
        store.ready(VIEW_MODEL, viewModel);
        store.forceRender();
        return view;
    }

    @Override
    public void onDestroy() {
        store.destroyAll();
        super.onDestroy();
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
}

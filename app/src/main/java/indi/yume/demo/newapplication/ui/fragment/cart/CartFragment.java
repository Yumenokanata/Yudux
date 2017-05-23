package indi.yume.demo.newapplication.ui.fragment.cart;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import indi.yume.demo.newapplication.BR;
import indi.yume.demo.newapplication.R;
import indi.yume.demo.newapplication.databinding.CartFragmentBinding;
import indi.yume.demo.newapplication.functions.Receiver;
import indi.yume.demo.newapplication.ui.AppComponent;
import indi.yume.demo.newapplication.ui.MainApplication;
import indi.yume.demo.newapplication.ui.component.DaggerCartComponent;
import indi.yume.demo.newapplication.ui.component.CartComponent;
import indi.yume.demo.newapplication.ui.fragment.base.BaseToolbarFragment;
import indi.yume.demo.newapplication.ui.fragment.cart.CartState.ItemData;
import indi.yume.demo.newapplication.ui.module.CartModule;
import indi.yume.demo.newapplication.ui.presenter.CartPresenter;
import indi.yume.tools.dsladapter2.RendererAdapter;
import indi.yume.yudux.collection.ContextCollection;

import static indi.yume.demo.newapplication.ui.AppStore.mainStore;
import static indi.yume.demo.databinding.DataBindingDsl.dataBindingRepositoryPresenterOf;
import static indi.yume.demo.newapplication.ui.fragment.cart.CartFragment.CartKey.*;
import static indi.yume.tools.dsladapter2.renderer.RenderDsl.*;
import static indi.yume.yudux.collection.DSL.*;
import static indi.yume.yudux.DSL.*;

/**
 * Created by DaggerGenerator on 2017-05-22.
 */
public class CartFragment extends BaseToolbarFragment {
    enum CartKey {
        CONTEXT,
        VIEW,
        BINDER,
        PRESENTER,
        ADAPTER,
        HANDLER
    }

    private final ContextCollection<CartKey> repo =
            ContextCollection.<CartKey>builder()
                    .withItem(HANDLER,
                            depends(),
                            (real, repo) -> new CartHandler(repo))
                    .withItem(BINDER,
                            depends(CONTEXT, VIEW, HANDLER, ADAPTER),
                            (real, repo) -> {
                                CartFragmentBinding binding = CartFragmentBinding.bind(real.getItem(VIEW));
                                binding.setHandler(real.getItem(HANDLER));

                                Context context = real.get(CONTEXT);
                                RendererAdapter adapter = real.get(ADAPTER);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                                adapter.setLayoutManager(layoutManager);
                                binding.cartListView.setLayoutManager(layoutManager);
                                binding.cartListView.setAdapter(adapter);

                                return binding;
                            })
                    .withItem(PRESENTER,
                            depends(CONTEXT),
                            (real, repo) -> {
                                AppComponent appComponent = MainApplication.getInstance(real.getItem(CONTEXT)).getAppComponent();
                                CartComponent cartComponent = DaggerCartComponent.builder()
                                        .appComponent(appComponent)
                                        .cartModule(new CartModule(this))
                                        .build();
                                CartPresenter presenter = cartComponent.getPresenter();
                                cartComponent.injectPresenter(presenter);

                                return presenter;
                            })
                    .withItem(ADAPTER,
                            depends(HANDLER),
                            (real, repo) -> {
                                CartHandler handler = real.get(HANDLER);

                                return new RendererAdapter<>(mainStore.getState().getCartState().getCart(),
                                        supplier -> list(ItemData.class)
                                                .item(dataBindingRepositoryPresenterOf(ItemData.class)
                                                        .layout(R.layout.b01_01_cart_recyclerview_item)
                                                        .staticItemId(BR.model)
                                                        .handler(BR.clickChecked, (Receiver<ItemData>) d -> handler.toggleItem(d))
                                                        .handler(BR.plusHandler, (Receiver<ItemData>) d -> handler.plusItemCount(d))
                                                        .handler(BR.minusHandler, (Receiver<ItemData>) d -> handler.minusItemCount(d))
                                                        .handler(BR.clickBack, (Receiver<ItemData>) d -> handler.deleteItem(d))
                                                        .forItem())
                                                .forList());
                            })
                    .build();

    {
        subscribeUntilChanged(
                mainStore,
                extra(repo, depends(BINDER)),
                s -> s.getCartState(),
                (state, real) -> real.<CartFragmentBinding>getItem(BINDER).setModel(state));
        subscribeUntilChanged(
                mainStore,
                extra(repo, depends(ADAPTER)),
                s -> s.getCartState(),
                (state, real) -> real.<RendererAdapter<List<ItemData>>>getItem(ADAPTER).update(state.getCart()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.cart_fragment, container, false);
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
}
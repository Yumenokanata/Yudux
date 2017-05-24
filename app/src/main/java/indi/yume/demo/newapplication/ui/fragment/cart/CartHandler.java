package indi.yume.demo.newapplication.ui.fragment.cart;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.annimon.stream.Stream;

import java.util.Collections;
import java.util.List;

import indi.yume.demo.newapplication.BR;
import indi.yume.demo.newapplication.R;
import indi.yume.demo.newapplication.databinding.B0101CartOutofstockDialogBinding;
import indi.yume.demo.newapplication.databinding.CartFragmentBinding;
import indi.yume.demo.newapplication.ui.AppState;

import indi.yume.demo.newapplication.ui.fragment.cart.CartFragment.CartKey;
import indi.yume.demo.newapplication.ui.fragment.cart.CartState.ItemData;
import indi.yume.demo.newapplication.ui.presenter.CartPresenter;
import indi.yume.demo.newapplication.util.DealErrorUtil;
import indi.yume.demo.newapplication.util.DialogUtil;
import indi.yume.tools.dsladapter2.RendererAdapter;
import indi.yume.yudux.collection.BaseDependAction;
import indi.yume.yudux.collection.ContextCollection;
import indi.yume.yudux.collection.LazyAction;
import indi.yume.yudux.collection.RealWorld;
import indi.yume.yudux.collection.SingleDepends;
import indi.yume.yudux.functions.Result;
import indi.yume.yudux.functions.Unit;
import indi.yume.yudux.store.Action;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import lombok.RequiredArgsConstructor;

import static indi.yume.demo.newapplication.model.api.ResultModel.STATUS_OK;
import static indi.yume.demo.newapplication.ui.AppStore.mainStore;
import static indi.yume.demo.newapplication.ui.fragment.cart.CartFragment.CartKey.*;
import static indi.yume.demo.newapplication.util.DialogUtil.CALLBACK_TYPE_CANCEL;
import static indi.yume.demo.newapplication.util.DialogUtil.CALLBACK_TYPE_OK;
import static indi.yume.demo.newapplication.util.DialogUtil.composeNetProgressDialog;
import static indi.yume.demo.newapplication.util.DialogUtil.showProgressDialog;
import static indi.yume.demo.databinding.DataBindingDsl.dataBindingRepositoryPresenterOf;
import static indi.yume.demo.newapplication.util.RxUtil.postUI;
import static indi.yume.tools.dsladapter2.renderer.RenderDsl.*;
import static indi.yume.yudux.DSL.*;
import static indi.yume.yudux.Utils.*;

/**
 * Created by DaggerGenerator on 2017-05-22.
 */
@RequiredArgsConstructor
public class CartHandler {
    private final ContextCollection<CartKey> repo;

    public void toggleAll() {
        mainStore.dispatch(toggleAll);
    }

    public void onClickPay() {
        if(!mainStore.getState().getCartState().isShownDialog())
            mainStore.dispatchTransaction("dialog",
                    create(repo, startPay),
                    create(repo, checkPayAction));
    }

    public void onClickClear() {
        mainStore.dispatch(clearAction);
    }

    public void toggleItem(ItemData item) {
        mainStore.dispatch(mainAction(item,
                (d, state) -> state.withCartState(state.getCartState().withCart(
                        replace(state.getCartState().getCart(), d, d.withSelected(!d.isSelected()))
                ))));
    }

    public void plusItemCount(ItemData item) {
        mainStore.dispatch(mainAction(item,
                (d, state) -> state.withCartState(state.getCartState().withCart(
                        replace(state.getCartState().getCart(), d, d.withCount(d.getCount() + 1))
                ))));
    }

    public void minusItemCount(ItemData item) {
        mainStore.dispatch(mainAction(item,
                (d, state) -> {
                    int count = Math.max(0, d.getCount() - 1);
                    return state.withCartState(state.getCartState().withCart(
                            replace(state.getCartState().getCart(), d, d.withCount(count))
                    ));
                }));
    }

    public void deleteItem(ItemData item) {
        mainStore.dispatch(create(repo, checkDeleteItem(item)));
    }

    private static LazyAction<RealWorld<CartKey>, SingleDepends<CartKey>, AppState, Boolean> checkDeleteItem(ItemData item) {
        return action(lazy(CONTEXT),
                (real, state) -> {
                    Context context = real.get(CONTEXT);

                    return DialogUtil.showMessageDialogObs(context,
                            "删除",
                            "删除商品： " + item.getModel().getName() + "?",
                            CALLBACK_TYPE_CANCEL,
                            CALLBACK_TYPE_OK)
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .map(e -> e.getButton() == CALLBACK_TYPE_OK);
                },
                (ok, state) -> {
                    if(!ok)
                        return state;
                    return state.withCartState(state.getCartState().withCart(
                            Stream.of(state.getCartState().getCart())
                                    .filter(m -> !m.getModel().getBarCode().equals(item.getModel().getBarCode()))
                                    .toList()
                    ));
                });
    }

    private static final Action<AppState, CartState> toggleAll =
            action(state -> {
                        CartState cartState = state.getCartState();
                        final boolean selected = DataBindUtil.selectedCount(cartState) != cartState.getCart().size();
                        return Observable.fromIterable(cartState.getCart())
                                .map(m -> m.withSelected(selected))
                                .toList()
                                .map(cartState::withCart);
                    },
                    (data, state) -> state.withCartState(data));

    private static final Action<AppState, CartState> clearAction =
            action(state -> Single.just(state.getCartState().withCart(Collections.emptyList())),
                    (data, state) -> state.withCartState(data));

    private static final LazyAction<RealWorld<CartKey>, SingleDepends<CartKey>, AppState, Unit> startPay =
            action(lazy(CONTEXT),
                    (real, state) -> {
                        Context context = real.get(CONTEXT);
                        if (DataBindUtil.selectedCount(state.getCartState()) <= 0) {
                            postUI(() -> Toast.makeText(context, "您还没有选择宝贝哦！", Toast.LENGTH_SHORT).show());
                            return Single.error(new RuntimeException("cart is empty"));
                        }

                        if(mainStore.getState().getCartState().isShownDialog()) {
                            return Single.error(new RuntimeException("has shown dialog"));
                        } else {
                            return Single.just(Unit.unit());
                        }
                    },
                    (data, state) -> state.withCartState(state.getCartState().withShownDialog(true)));

    private final LazyAction<RealWorld<CartKey>, SingleDepends<CartKey>, AppState, List<String>> checkPayAction =
            action(lazy(CONTEXT, PRESENTER),
                    (real, state) -> {
                        Context context = real.get(CONTEXT);
                        CartPresenter presenter = real.get(PRESENTER);

                        return Observable.fromIterable(state.getCartState().getCart())
                                .filter(m -> m.isSelected())
                                .flatMap(m -> presenter.checkGoodsStock(m.getModel())
                                        .map(item -> new ItemData(item, m.getCount(), m.isSelected()))
                                        .toObservable())
                                .toList()
                                .compose(composeNetProgressDialog(context))
                                .flatMap(list -> showDialogAction(context, list))
                                .flatMap(list -> payAction(context, presenter, list, state))
                                .doOnSuccess(l -> postUI(() -> Toast.makeText(context, "购买成功", Toast.LENGTH_LONG).show()));
                    },
                    (result, state) ->
                            state.withCartState(state.getCartState().withCart(
                                    Stream.of(state.getCartState().getCart()).filter(m -> !result.contains(m.getModel().getBarCode())).toList()
                            ).withShownDialog(false)));

    private static Single<List<ItemData>> showDialogAction(Context context, List<ItemData> list) {
        return showPayDialog(context, list)
                .flatMap(pay -> {
                    if(pay)
                        return Single.just(list);
                    else
                        return Single.just(Collections.<ItemData>emptyList());
                })
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    private static Single<List<String>> payAction(Context context, CartPresenter presenter,
                                                  List<ItemData> list, AppState state) {
        return presenter.payManyGoods(state.getUserModel().getToken(), list)
                .compose(composeNetProgressDialog(context))
                .compose(DealErrorUtil.dealErrorRetry(context))
                .map(result -> {
                    if(STATUS_OK.equals(result.getStatus()))
                        return Stream.of(list).map(m -> m.getModel().getBarCode()).toList();
                    else
                        return Collections.emptyList();
                });
    }

    private static Single<Boolean> showPayDialog(Context context, List<ItemData> list) {
        return Single.<Boolean>create(emmit -> {
            B0101CartOutofstockDialogBinding binding = B0101CartOutofstockDialogBinding.inflate(LayoutInflater.from(context));

            AlertDialog orderDialog = new AlertDialog.Builder(context)
                    .setView(binding.getRoot())
                    .create();
            Result<Pair<Float, Float>, Pair<Float, Float>> canPay = canPay(list);
            binding.setCanPay(canPay.isRight());
            binding.setOriginPrice(canPay.isRight() ? canPay.getRight().first : canPay.getLeft().first);
            binding.setPayPrice(canPay.isRight() ? canPay.getRight().second : canPay.getLeft().second);

            RendererAdapter adapter = new RendererAdapter<>(list,
                    supplier -> list(ItemData.class)
                            .item(dataBindingRepositoryPresenterOf(ItemData.class)
                                    .layout(R.layout.b01_01_cart_order_dialog_recyclerview_item)
                                    .staticItemId(BR.name, d -> d.getModel().getName())
                                    .staticItemId(BR.count, d -> d.getCount())
                                    .staticItemId(BR.enough, d -> d.getCount() <= d.getModel().getCount())
                                    .forItem())
                            .forList());
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            adapter.setLayoutManager(layoutManager);
            binding.orderDialogRecyclerview.setLayoutManager(layoutManager);
            binding.orderDialogRecyclerview.setAdapter(adapter);

            binding.setPayHandler(v -> {
                emmit.onSuccess(true);
                orderDialog.dismiss();
                return true;
            });
            binding.setReorderHandler(v -> {
                emmit.onSuccess(false);
                orderDialog.dismiss();
            });

            orderDialog.setOnDismissListener(dialog -> emmit.onSuccess(false));
            orderDialog.show();
        });
    }

    private static Result<Pair<Float, Float>, Pair<Float, Float>> canPay(List<ItemData> list) {
        float valuePrice = 0;
        float payPrice = 0;
        boolean isOutOfStock = false;

        for (ItemData model : list) {
            if (model.getCount() <= model.getModel().getCount()) {
                valuePrice = valuePrice + model.getCount() * model.getModel().getCostPrice();
                payPrice = payPrice + model.getCount() * model.getModel().getSalePrice();
            } else {
                isOutOfStock = true;
            }
        }

        if(!isOutOfStock)
            return Result.right(new Pair<>(valuePrice, payPrice));
        else
            return Result.left(new Pair<>(valuePrice, payPrice));
    }
}
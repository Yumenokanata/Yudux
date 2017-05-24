package indi.yume.demo.newapplication.component.cart;

import android.text.TextUtils;

import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import indi.yume.demo.newapplication.model.api.GoodsModel;
import indi.yume.demo.newapplication.model.sharedpref.SharedPrefModel;
import indi.yume.demo.newapplication.ui.AppState;
import indi.yume.demo.newapplication.ui.fragment.cart.CartState;
import indi.yume.yudux.store.Action;
import indi.yume.yudux.store.StoreAction;
import io.reactivex.Single;
import lombok.experimental.UtilityClass;

import static indi.yume.demo.newapplication.ui.AppStore.MainKey.Shared;
import static indi.yume.demo.newapplication.ui.AppStore.mainRepo;
import static indi.yume.demo.newapplication.ui.AppStore.mainStore;
import static indi.yume.yudux.DSL.*;
import static indi.yume.yudux.Utils.*;
import static indi.yume.yudux.collection.DSL.extra;
import static indi.yume.yudux.collection.DSL.subscribeUntilChanged;

/**
 * Created by yume on 17-5-23.
 */
@UtilityClass
public class CartActions {
    public static void init() {
        refreshKeep();
        subscribe();
    }

    public static void subscribe() {
        subscribeUntilChanged(mainStore,
                extra(mainRepo, depends(Shared)),
                s -> s.getCartState(),
                (real, state) -> real.<SharedPrefModel>getItem(Shared).setCartGoodsModelList(state.getCart()));
    }

    public static void refreshKeep() {
        mainStore.dispatch(create(mainRepo,
                action(lazy(Shared),
                        (real, state) -> {
                            SharedPrefModel shared = real.get(Shared);

                            return Single.just(state.getCartState()
                                    .withCart(shared.getCartGoodsModelList()));
                        },
                        (CartState cart, AppState s) -> s.withCartState(cart)
                )));
    }

    public static void putItem(GoodsModel model) {
        mainStore.dispatch(mainAction(model, (m, state) -> {
            if(state.getCartState().isShownDialog())
                return state;

            boolean hasItem = Stream.of(state.getCartState().getCart())
                    .anyMatch(i -> TextUtils.equals(i.getModel().getBarCode(), model.getBarCode()));
            List<CartState.ItemData> list = hasItem ? state.getCartState().getCart()
                    : plus(state.getCartState().getCart(), new CartState.ItemData(m, 1, false));

            return state.withCartState(state.getCartState().withCart(list));
        }));
    }

    public static void toggle(GoodsModel model) {
        mainStore.dispatch(toggleAction(model));
    }

    private static StoreAction<AppState> toggleAction(GoodsModel model) {
        return mainAction(model, (m, state) -> {
            if(state.getCartState().isShownDialog())
                return state;

            boolean hasItem = Stream.of(state.getCartState().getCart())
                    .anyMatch(i -> TextUtils.equals(i.getModel().getBarCode(), model.getBarCode()));
            List<CartState.ItemData> list = hasItem ? Stream.of(state.getCartState().getCart())
                    .filter(i -> TextUtils.equals(i.getModel().getBarCode(), model.getBarCode()))
                    .toList()
                    : plus(state.getCartState().getCart(), new CartState.ItemData(m, 1, false));

            return state.withCartState(state.getCartState().withCart(list));
        });
    }
}

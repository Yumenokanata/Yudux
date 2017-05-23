package indi.yume.demo.newapplication.ui.fragment.cart;

import java.util.Collections;
import java.util.List;

import indi.yume.demo.newapplication.model.api.GoodsModel;
import lombok.Data;
import lombok.experimental.Wither;

/**
 * Created by yume on 2017-05-22.
 */
@Data
public class CartState {
    @Wither
    private final boolean isShownDialog;
    @Wither
    private final List<ItemData> cart;

    public CartState(boolean isShownDialog, List<ItemData> cart) {
        this.isShownDialog = isShownDialog;
        this.cart = cart;
    }

    public CartState() {
        this(false, Collections.emptyList());
    }

    @Data
    public static class ItemData {
        private final GoodsModel model;
        @Wither
        private final int count;
        @Wither
        private final boolean selected;
    }
}
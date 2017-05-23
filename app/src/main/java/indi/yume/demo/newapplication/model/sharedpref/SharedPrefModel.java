package indi.yume.demo.newapplication.model.sharedpref;

import java.util.Collections;
import java.util.List;

import indi.yume.demo.newapplication.model.api.CartGoodsModel;
import indi.yume.demo.newapplication.model.api.GoodsModel;
import indi.yume.demo.newapplication.ui.fragment.cart.CartState;
import lombok.Data;

/**
 * Created by yume on 16-11-15.
 */
@Data
public class SharedPrefModel {
    private String token = "";

    private List<CartState.ItemData> cartGoodsModelList = Collections.emptyList();

    private List<GoodsModel> keepList = Collections.emptyList();
}

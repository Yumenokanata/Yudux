package indi.yume.demo.newapplication.model.sharedpref;

import java.util.List;

import indi.yume.demo.newapplication.model.api.CartGoodsModel;
import lombok.Data;

/**
 * Created by yume on 16-11-15.
 */
@Data
public class SharedPrefModel {
    private String token = "";
    private List<CartGoodsModel> cartGoodsModelList;
}

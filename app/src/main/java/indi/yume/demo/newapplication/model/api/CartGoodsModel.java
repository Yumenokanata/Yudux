package indi.yume.demo.newapplication.model.api;

import lombok.Data;

/**
 * Created by xiejinpeng on 16/5/25.
 */
@Data
public class CartGoodsModel {
    private GoodsModel goodsModel;
    private int quantity;
}

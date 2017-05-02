package indi.yume.demo.newapplication.model.api;

import java.util.List;

import lombok.Data;

/**
 * Created by sashiro on 16/6/8.
 */
@Data
public class PayTokenModel {
    private String payTime;
    private int staffId;
    private int payCount;
    private float spend;
    private String historyToken;
    private List<GoodsData> payGoods;

    @Data
    public static class GoodsData {
        private GoodsModel goodsData;
        private int payCount;
        private float spend;
    }
}

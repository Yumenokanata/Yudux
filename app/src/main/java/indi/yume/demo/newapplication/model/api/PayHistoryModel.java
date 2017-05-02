package indi.yume.demo.newapplication.model.api;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * Created by sashiro on 16/5/16.
 */
@Data
public class PayHistoryModel {
    private List<HistoryEntry> history;
    private float sumSpend;

    @Data
    public static class HistoryEntry implements Serializable {
        private String payTime;
        private int staffId;
        private int payCount;
        private float spend;
        private String historyToken;
        private List<GoodsData> payGoods;
    }

    @Data
    public static class GoodsData {
        private GoodsModel goodsData;
        private int payCount;
        private float spend;
    }
}

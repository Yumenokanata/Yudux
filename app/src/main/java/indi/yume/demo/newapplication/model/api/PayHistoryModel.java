package indi.yume.demo.newapplication.model.api;

import java.util.List;

import lombok.Data;

/**
 * Created by sashiro on 16/5/11.
 */
@Data
public class PayHistoryModel {

    private List<History> history;

    private double sumSpend;

    @Data
    public class History {
        private String payTime;

        private int staffId;

        private int payCount;

        private double spend;

        private String historyToken;

        private List<PayGoods> payGoods;
    }

    @Data
    public class PayGoods {
        private GoodsModel goodsData;

        private int payCount;

        private double spend;
    }
}


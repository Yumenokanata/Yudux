package indi.yume.demo.newapplication.model.api;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * Created by sashiro on 16/6/6.
 */
@Data
public class WarehouseModel {
    private List<HistoryEntry> history;
    private String startDate;
    private String endDate;
    private float sumSpend;

    @Data
    public static class HistoryEntry implements Serializable {
        String insertDate;//yyyy-mm-dd hh:mm
        List<GoodsData> entryList;
        int sumCount;
        String token;
        float sumSpend;
    }

    @Data
    public static class GoodsData {
        GoodsModel goodsData;
        String insertDate;
        int insertCount;
        float spend;
    }
}

package indi.yume.demo.newapplication.model.api;

import java.util.List;

import lombok.Data;

/**
 * Created by sashiro on 16/6/8.
 */
@Data
public class WareTokenModel {
    String insertDate;
    List<GoodsData> entryList;
    int sumCount;
    String token;

    @Data
    public static class GoodsData {
        GoodsModel goodsModel;
        String insertDate;
        int insertCount;
        float spend;
    }
}

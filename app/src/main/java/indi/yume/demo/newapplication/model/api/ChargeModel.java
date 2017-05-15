package indi.yume.demo.newapplication.model.api;

import java.util.List;

import lombok.Data;

/**
 * Created by sashiro on 16/5/12.
 */
@Data
public class ChargeModel {
    private List<ChargeEntry> chargeHistory;

    @Data
    public static class ChargeEntry {
        private float charge;
        private String time;
        private String chargeToken;
    }
}

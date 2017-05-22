package indi.yume.demo.newapplication.ui.fragment.mypage;

import java.util.Collections;
import java.util.List;

import indi.yume.demo.newapplication.model.api.ChargeModel;
import indi.yume.demo.newapplication.model.api.PayHistoryModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Wither;

/**
 * Created by yume on 2017-05-22.
 */
@Data
public class MyPageState {
    @Wither
    private final List<PayHistoryModel.History> history;
    @Wither
    private final List<ChargeModel.ChargeEntry> chargeHistory;

    public MyPageState() {
        this(Collections.emptyList(), Collections.emptyList());
    }

    public MyPageState(List<PayHistoryModel.History> history, List<ChargeModel.ChargeEntry> chargeHistory) {
        this.history = history;
        this.chargeHistory = chargeHistory;
    }
}
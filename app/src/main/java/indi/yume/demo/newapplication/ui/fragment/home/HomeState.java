package indi.yume.demo.newapplication.ui.fragment.home;

import android.support.annotation.Nullable;

import java.util.List;

import indi.yume.demo.newapplication.model.api.GoodsModel;
import indi.yume.demo.newapplication.model.api.PayHistoryModel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Wither;

/**
 * Created by yume on 17-5-2.
 */
@Data
@RequiredArgsConstructor
public class HomeState {
    @Wither
    private final int goodsCount;
    @Wither
    private final float money;
    @Wither
    private final String selectedItemAtNew;
    @Wither
    private final String selectedItemAtLast;
    @Wither
    private final String lastPayTime;
    @Wither
    @Nullable
    private final List<GoodsModel> lastPay;
    @Wither
    @Nullable
    private final List<GoodsModel> newArrival;

    public HomeState() {
        this.goodsCount = 0;
        this.money = 0;
        this.selectedItemAtNew = "";
        this.selectedItemAtLast = "";
        this.lastPayTime = "";
        this.lastPay = null;
        this.newArrival = null;
    }

    public HomeState(int goodsCount, float money, String lastPayTime, List<GoodsModel> lastPay, List<GoodsModel> newArrival) {
        this.goodsCount = goodsCount;
        this.money = money;
        this.selectedItemAtNew = "";
        this.selectedItemAtLast = "";
        this.lastPayTime = lastPayTime;
        this.lastPay = lastPay;
        this.newArrival = newArrival;
    }

    public HomeState withSelectAtLast(String barCode) {
        return withSelectedItemAtLast(barCode)
                .withSelectedItemAtNew(null);
    }

    public HomeState withSelectAtNew(String barCode) {
        return withSelectedItemAtLast(null)
                .withSelectedItemAtNew(barCode);
    }
}

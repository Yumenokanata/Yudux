package indi.yume.demo.newapplication.ui.fragment;

import com.google.common.collect.ImmutableMap;

import indi.yume.demo.newapplication.ui.fragment.goods.GoodsState;
import indi.yume.demo.newapplication.ui.fragment.login.LoginState;
import lombok.Data;
import lombok.experimental.Wither;

/**
 * Created by yume on 17-4-24.
 */
@Data
public class AppState {
    @Wither
    private final LoginState loginState;
    @Wither
    private final GoodsState goodsState;

    public static AppState empty() {
        return new AppState(new LoginState(), new GoodsState());
    }
}

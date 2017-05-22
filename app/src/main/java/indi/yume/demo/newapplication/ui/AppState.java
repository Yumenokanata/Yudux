package indi.yume.demo.newapplication.ui;

import android.support.annotation.Nullable;

import indi.yume.demo.newapplication.model.api.UserModel;
import indi.yume.demo.newapplication.ui.activity.login.LoginState;
import indi.yume.demo.newapplication.ui.fragment.home.HomeState;
import indi.yume.demo.newapplication.ui.fragment.keep.KeepState;
import indi.yume.demo.newapplication.ui.fragment.mypage.MyPageState;
import indi.yume.demo.newapplication.ui.fragment.search.SearchState;
import lombok.Data;
import lombok.experimental.Wither;

/**
 * Created by yume on 17-4-24.
 */
@Data
public class AppState {
    @Wither
    @Nullable
    private final UserModel userModel;

    @Wither
    private final LoginState loginState;
    @Wither
    private final HomeState homeState;
    @Wither
    private final SearchState searchState;
    @Wither
    private final KeepState keepState;
    @Wither
    private final MyPageState myPageState;

    public static AppState empty() {
        return new AppState(null, new LoginState(), new HomeState(), new SearchState(),
                new KeepState(), new MyPageState());
    }
}

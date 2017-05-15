package indi.yume.demo.newapplication.ui.fragment;

import android.support.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import indi.yume.demo.newapplication.model.api.UserModel;
import indi.yume.demo.newapplication.ui.activity.login.LoginState;
import indi.yume.demo.newapplication.ui.fragment.home.HomeState;
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

    public static AppState empty() {
        return new AppState(null, new LoginState(), new HomeState(), new SearchState());
    }
}

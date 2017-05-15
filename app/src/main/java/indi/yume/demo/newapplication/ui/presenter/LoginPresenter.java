package indi.yume.demo.newapplication.ui.presenter;

import android.text.TextUtils;

import javax.inject.Inject;

import indi.yume.demo.newapplication.manager.api.UserService;
import indi.yume.demo.newapplication.model.api.UserModel;
import indi.yume.demo.newapplication.model.sharedpref.SharedPrefModel;
import io.reactivex.Single;

import static indi.yume.demo.newapplication.util.RxUtil.switchThread;

/**
 * Created by DaggerGenerator on 2017/05/02.
 */
public class LoginPresenter extends BasePresenter{
    @Inject
    UserService userService;
    @Inject
    SharedPrefModel sharedPrefModel;

    public boolean hasLogin() {
        return !TextUtils.isEmpty(sharedPrefModel.getToken());
    }

    public void setToken(String token) {
        sharedPrefModel.setToken(token);
    }

    public String getToken() {
        return sharedPrefModel.getToken();
    }

    public Single<UserModel> login(String name, String password) {
        return userService.login(name, password);
    }

    public Single<UserModel> getUserInfo(String token) {
        return userService.getInfo(token);
    }
}
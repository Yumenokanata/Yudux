package indi.yume.demo.newapplication.ui.presenter;


import javax.inject.Inject;

import indi.yume.demo.newapplication.manager.api.UserService;
import indi.yume.demo.newapplication.model.api.UserModel;
import io.reactivex.Single;

import static indi.yume.demo.newapplication.util.RxUtil.switchThread;

/**
 * Created by DaggerGenerator on 2017/05/24.
 */
public class OtherPresenter extends BasePresenter {
    @Inject
    UserService userService;

    public Single<UserModel> updateUserInfo(String token) {
        return userService.getInfo(token)
                .compose(switchThread());
    }

    public Single<UserModel> modifyUserInfo(String name, String oldPassword, String newPassword) {
        return userService.modifyUserInfo(name, oldPassword, newPassword)
                .compose(switchThread());
    }
}
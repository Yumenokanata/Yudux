package indi.yume.demo.newapplication.manager.api;

import indi.yume.demo.newapplication.model.api.UserModel;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import io.reactivex.Single;

/**
 * Created by sashiro on 16/5/9.
 */
public interface UserService {

    /**
     * Api:  login
     * 概要:  用户登录
     *
     * @param name     (必須)   用户名
     * @param password (必须) 用户密码
     */

    @FormUrlEncoded
    @POST("api_login")
    public Single<UserModel> login(
            @Field("name") String name,
            @Field("password") String password
    );

    /**
     * Api:   userinfo
     * 概要:  用户信息查询
     *
     * @param token (必须)  登录token
     */
    @FormUrlEncoded
    @POST("api_userinfo")
    public Single<UserModel> getInfo(
            @Field("token") String token
    );

    /**
     * API: modifyUserInfo
     * 概要： 用户密码修改
     *
     * @param
     */
    @FormUrlEncoded
    @POST("api_modifyUserInfo")
    public Single<UserModel> modifyUserInfo(
            @Field("name") String name,
            @Field("password") String password,
            @Field("newpassword") String newPassword
    );

}

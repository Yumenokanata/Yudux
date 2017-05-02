package indi.yume.demo.newapplication.manager.api;

import indi.yume.demo.newapplication.model.api.StatusModel;
import indi.yume.demo.newapplication.model.api.UserModel;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by sashiro on 16/5/13.
 */
public interface UserService {
    /**
     * Api:  login
     * 概要:  用户登录
     *
     * @param name     (必須)   用户名
     * @param password (必須)   密码
     */
    @FormUrlEncoded
    @POST("api_login")
    public Single<UserModel> login(
            @Field("name") String name,
            @Field("password") String password
    );

    /**
     * Api:  alluserinfo
     * 概要:  获得全部用户列表
     *
     * @param adminToken (必須)   管理员token
     */
    @FormUrlEncoded
    @POST("api_alluserinfo")
    public Single<List<UserModel>> selectAllUsers(
            @Field("token") String adminToken
    );


    /**
     * Api:  userallinfo
     * 概要:  获得某个用户的全部信息
     *
     * @param adminToken (必須)   管理员token
     * @param staffId    (必須)    用户id
     */
    @FormUrlEncoded
    @POST("api_userallinfo")
    public Single<UserModel> selectUserById(
            @Field("token") String adminToken,
            @Field("staffId") Integer staffId
    );

    /**
     * Api:  deleteInfo
     * 概要:  删除该用户
     *
     * @param adminToken (必須)   管理员token
     * @param staffId    (必須)    用户id
     */
    @FormUrlEncoded
    @POST("api_deleteInfo")
    public Single<StatusModel> deleteUserById(
            @Field("token") String adminToken,
            @Field("staffId") int staffId
    );

    /**
     * Api:  register
     * 概要:  注册新用户
     *
     * @param adminToken (必須)   管理员token
     * @param name       (必須)    用户名
     * @param password   (必須)    密码
     * @param username   (必須)    姓名
     * @param staffId    (必須)    用户id
     * @param money      (必須)    余额
     * @param point      (必須)    点数
     */
    @FormUrlEncoded
    @POST("api_register")
    public Single<StatusModel> insertUser(
            @Field("token") String adminToken,
            @Field("name") String name,
            @Field("password") String password,
            @Field("username") String username,
            @Field("staffId") int staffId,
            @Field("money") float money,
            @Field("point") float point
    );

}

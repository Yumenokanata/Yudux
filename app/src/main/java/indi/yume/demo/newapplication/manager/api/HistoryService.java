package indi.yume.demo.newapplication.manager.api;

import indi.yume.demo.newapplication.model.api.PayHistoryModel;
import indi.yume.demo.newapplication.model.api.PayTokenModel;
import indi.yume.demo.newapplication.model.api.WareTokenModel;
import indi.yume.demo.newapplication.model.api.WarehouseModel;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by sashiro on 16/5/17.
 */
public interface HistoryService {
    /**
     * Api: getPayHistory
     * 概要: 查询出库记录
     *
     * @param adminToken 管理员token
     * @param token      用户token
     * @param start      开始日期
     * @param end        结束日期
     * @param offset     偏移量
     * @param limit      每页数量
     */
    @FormUrlEncoded
    @POST("api_getPayHistory")
    public Single<PayHistoryModel> getPayHistory(
            @Field("adminToken") String adminToken,
            @Field("token") String token,
            @Field("start") String start,
            @Field("end") String end,
            @Field("offset") long offset,
            @Field("limit") long limit
    );

    /**
     * Api: getWarehouseWarrant
     * 概要: 查询入库记录
     *
     * @param adminToken
     * @param start
     * @param end
     * @param offset
     * @param limit
     *
     */
    @FormUrlEncoded
    @POST("api_getWarehouseWarrant")
    public Single<WarehouseModel> getInHistory(
            @Field("adminToken") String adminToken,
            @Field("start") String start,
            @Field("end") String end,
            @Field("offset") Long offset,
            @Field("limit") Long limit
    );

    /**
     * Api: getWarehouseWarrantByToken
     * 概要: 根据token查询入库记录
     *
     * @param adminToken
     * @param warrantToken
     *
     */
    @FormUrlEncoded
    @POST("api_getWarehouseWarrantByToken")
    public Single<WareTokenModel> getInHistoryByToken(
            @Field("adminToken") String adminToken,
            @Field("warrantToken") String warrantToken
    );

    /**
     * Api: getPayHistoryByToken
     * 概要: 根据token查询出售记录
     *
     * @param adminToken
     * @param payToken
     *
     */
    @FormUrlEncoded
    @POST("api_getPayHistoryByToken")
    public Single<PayTokenModel> getOutHistoryByToken(
            @Field("adminToken") String adminToken,
            @Field("payToken") String payToken
    );
}

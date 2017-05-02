package indi.yume.demo.newapplication.manager.api;

import indi.yume.demo.newapplication.model.api.ChargeHistoryModel;
import indi.yume.demo.newapplication.model.api.StatusModel;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by sashiro on 16/5/16.
 */
public interface ChargeService {
    /**
     * Api:  chargemoney
     * 概要:  充钱
     *
     * @param adminToken (必須)   管理员toekn
     * @param staffId    (必須)   用户id
     * @param charge     (必須)   金额
     */
    @FormUrlEncoded
    @POST("api_chargemoney")
    public Single<StatusModel> chargeMoney(
            @Field("token") String adminToken,
            @Field("staffId") int staffId,
            @Field("charge") float charge
    );

    /**
     * Api:  chargepoint
     * 概要:  充点
     *
     * @param adminToken  (必須)   管理员toekn
     * @param staffId     (必須)   用户id
     * @param chargePoint (必須)   金额
     */
    @FormUrlEncoded
    @POST("api_chargepoint")
    public Single<StatusModel> chargePoint(
            @Field("token") String adminToken,
            @Field("staffId") int staffId,
            @Field("chargePoint") float chargePoint
    );

    /**
     * Api:  getChargeHistory
     * 概要:  获取指定用户充值记录
     *
     * @param adminToken 管理员token
     * @param token      用户token
     * @param start      开始日期
     * @param end        结束日期
     * @param offset     偏移量
     * @param limit      每页数量
     */
    @FormUrlEncoded
    @POST("api_getChargeHistory")
    public Single<ChargeHistoryModel> getChargeHistory(
            @Field("adminToken") String adminToken,
            @Field("token") String token,
            @Field("start") String start,
            @Field("end") String end,
            @Field("offset") long offset,
            @Field("limit") long limit
    );
}

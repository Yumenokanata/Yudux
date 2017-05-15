package indi.yume.demo.newapplication.manager.api;

import indi.yume.demo.newapplication.model.api.ChargeModel;
import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by sashiro on 16/5/12.
 */
public interface ChargeHistoryService {
    /**
     * Api: api_getPayHistory
     *
     *@param token (必須)   用户的token
     *@param start (必須)   起始时间
     *@param end (必須)     终止时间
     *@param offset (必须) 偏移量
     *@param limit (必须) 每一页显示数
     */
    @FormUrlEncoded
    @POST("api_getChargeHistory")
    public Single<ChargeModel> getPayHistory(
            @Field("token") String token,
            @Field("start") String start,
            @Field("end") String end,
            @Field("offset") long offset,
            @Field("limit") long limit
    );
}

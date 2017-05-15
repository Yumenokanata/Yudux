package indi.yume.demo.newapplication.manager.api;

import indi.yume.demo.newapplication.model.api.PayHistoryModel;
import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by sashiro on 16/5/11.
 */
public interface PayHistoryService {

    /**
     * Api: api_getPayHistory
     *
     *@param token (必須)   用户的token
     *@param start (可选)   起始时间
     *@param end (可选)     终止时间
     *@param offset (可选) 偏移量
     *@param limit (可选) 每一页显示数
     */
    @FormUrlEncoded
    @POST("api_getPayHistory")
    public Single<PayHistoryModel> getPayHistory(
            @Field("token") String token,
            @Field("start") String start,
            @Field("end") String end,
            @Field("offset") Long offset,
            @Field("limit") Long limit
    );
}

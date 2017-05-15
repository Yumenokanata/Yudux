package indi.yume.demo.newapplication.manager.api;

import indi.yume.demo.newapplication.model.api.CartGoodsModel;
import indi.yume.demo.newapplication.model.api.ResultModel;
import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by sashiro on 16/5/11.
 */
public interface PayGoodsService {

    /**
     * Api:  payGoods
     * 概要:  用户登录
     *
     * @param token (必須)   用户信息
     * @param barCode (必须) 商品条形码
     * @param count (必须) 购买数量
     */

    @FormUrlEncoded
    @POST("api_payGoods")
    public Single<CartGoodsModel> payGoods(
            @Field("token") String token,
            @Field("barCode") String barCode,
            @Field("amount") int count
    );

    @FormUrlEncoded
    @POST("api_payManyGoods")
    public Single<ResultModel> payManyGoods(
            @Field("token") String token,
            @Field("cart") String cart
    );
}

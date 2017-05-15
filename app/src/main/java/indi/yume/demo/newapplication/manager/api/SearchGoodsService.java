package indi.yume.demo.newapplication.manager.api;

import java.util.List;

import indi.yume.demo.newapplication.model.api.GoodsModel;
import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by yume on 16/1/22.
 *
 * @author xuemao.tang
 */
public interface SearchGoodsService {
    /**
     * Api:  findGoodsItem
     * 概要:  通过条形码查询商品数据
     *
     * @param barCode (必須)   商品的条形码
     */
    @FormUrlEncoded
    @POST("api_findGoodsItem")
    public Single<GoodsModel> findGoodsItem(
            @Field("barCode") String barCode
    );

    /**
     *
     */
    @POST("api_getAllGoods")
    public Single<List<GoodsModel>> getAllGoods();
}
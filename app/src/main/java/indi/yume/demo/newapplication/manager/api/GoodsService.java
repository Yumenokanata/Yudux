package indi.yume.demo.newapplication.manager.api;

import indi.yume.demo.newapplication.model.api.GoodsModel;
import indi.yume.demo.newapplication.model.api.StatusModel;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by yume on 16-4-17.
 */
public interface GoodsService {
    /**
     * Api:  insertGoods
     * 概要:  添加新商品
     *
     * @param barCode (必須)   商品的条形码
     */
    @FormUrlEncoded
    @POST("api_insertGoods")
    public Single<StatusModel> insertGoods(
            @Field("token") String adminToken,
            @Field("name") String name,
            @Field("barCode") String barCode,
            @Field("salePrice") Float salePrice,
            @Field("costPrice") Float costPrice,
            @Field("amount") Integer count,
            @Field("unit") String unit,
            @Field("packageNum") Integer packageNum,
            @Field("note") String note,
            @Field("className") String className
    );

    /**
     * Api:  saveGoods
     * 概要:  修改商品信息
     *
     * @param barCode (必須)   商品的条形码
     */
    @FormUrlEncoded
    @POST("api_saveGoods")
    public Single<StatusModel> saveGoods(
            @Field("token") String adminToken,
            @Field("name") String name,
            @Field("barCode") String barCode,
            @Field("salePrice") Float salePrice,
            @Field("costPrice") Float costPrice,
            @Field("amount") Integer count,
            @Field("unit") String unit,
            @Field("packageNum") Integer packageNum,
            @Field("note") String note,
            @Field("className") String className
    );

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
     * Api:  getAllGoods
     * 概要:  获取所有商品列表
     */
    @POST("api_getAllGoods")
    public Single<List<GoodsModel>> getAllGoods(
    );
}

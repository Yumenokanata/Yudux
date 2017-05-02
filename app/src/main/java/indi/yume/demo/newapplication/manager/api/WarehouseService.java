package indi.yume.demo.newapplication.manager.api;

import indi.yume.demo.newapplication.model.api.StatusModel;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by yume on 16-6-16.
 */

public interface WarehouseService {
    /**
     * Api:  warehouseWarrant
     * 概要:  批量入库
     *
     * @param adminToken     (必須)   登陆后获取的管理员token
     * @param warehouse      (必須)   入库的物品信息，格式：${条形码},${数量},${总花费};${条形码},${数量},${总花费};...
     */
    @FormUrlEncoded
    @POST("api_warehouseWarrant")
    public Single<StatusModel> warehouseWarrant(
            @Field("adminToken") String adminToken,
            @Field("warehouse") String warehouse
    );
}

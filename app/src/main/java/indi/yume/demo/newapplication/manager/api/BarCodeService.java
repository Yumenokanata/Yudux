package indi.yume.demo.newapplication.manager.api;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by yume on 17-2-16.
 */

public interface BarCodeService {
    /**
     * Api:  barCode
     * 概要:  商品条码查询
     *
     * @param barCode (必須)   barCode
     */
    @GET("http://search.anccnet.com/searchResult2.aspx")
    public Single<String> info(
            @Query("keyword") String barCode
    );
}

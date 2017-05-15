package indi.yume.demo.newapplication.manager.api.base;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import indi.yume.demo.newapplication.manager.api.ChargeHistoryService;
import indi.yume.demo.newapplication.manager.api.PayGoodsService;
import indi.yume.demo.newapplication.manager.api.PayHistoryService;
import indi.yume.demo.newapplication.manager.api.SearchGoodsService;
import indi.yume.demo.newapplication.manager.api.UserService;
import lombok.val;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by yume on 16/11/15.
 */
@Module
public class ApiModule {
    @Singleton
    @Provides
    public ApiClient provideAresRestAdapter(){
        val client = new ApiClient.Builder();

        okHttpClientSetting(client.getOkBuilder())
                .addInterceptor(new NetErrorInterceptor(client.getGson()))
                .readTimeout(50, TimeUnit.SECONDS);

        dealRestAdapter(client.getAdapterBuilder());

        return client.build();
    }

    protected Retrofit.Builder dealRestAdapter(Retrofit.Builder builder) {
        return builder;
    }

    @Singleton
    @Provides
    public OkHttpClient provideOkHttpClient(ApiClient client){
        return client.getOkHttpClient();
    }

    protected OkHttpClient.Builder okHttpClientSetting(OkHttpClient.Builder builder){
        return builder;
    }

    @Singleton
    @Provides
    public SearchGoodsService provideSearchService(ApiClient client) {
        return client.createService(SearchGoodsService.class);
    }

    @Singleton
    @Provides
    public UserService provideUserService(ApiClient client) {
        return client.createService(UserService.class);
    }

    @Singleton
    @Provides
    public PayGoodsService providePayGoodsService(ApiClient client) {
        return client.createService(PayGoodsService.class);
    }

    @Singleton
    @Provides
    public PayHistoryService providePayHistoryService(ApiClient client) {
        return client.createService(PayHistoryService.class);
    }

    @Singleton
    @Provides
    public ChargeHistoryService provideChargeHistoryService(ApiClient client) {
        return client.createService(ChargeHistoryService.class);
    }
}

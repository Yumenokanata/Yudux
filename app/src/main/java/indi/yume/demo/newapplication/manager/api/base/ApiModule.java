package indi.yume.demo.newapplication.manager.api.base;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import indi.yume.demo.newapplication.manager.api.BarCodeService;
import indi.yume.demo.newapplication.manager.api.ChargeService;
import indi.yume.demo.newapplication.manager.api.GoodsService;
import indi.yume.demo.newapplication.manager.api.HistoryService;
import indi.yume.demo.newapplication.manager.api.UserService;
import indi.yume.demo.newapplication.manager.api.WarehouseService;
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
    public GoodsService provideSearchService(Retrofit retrofit){
        return retrofit.create(GoodsService.class);
    }

    @Singleton
    @Provides
    public UserService provideUserService(Retrofit retrofit){
        return retrofit.create(UserService.class);
    }

    @Singleton
    @Provides
    public ChargeService provideChargeService(Retrofit retrofit){
        return retrofit.create(ChargeService.class);
    }

    @Singleton
    @Provides
    public HistoryService provideHistoryService(Retrofit retrofit) {
        return retrofit.create(HistoryService.class);
    }

    @Singleton
    @Provides
    public WarehouseService provideWarehouseService(Retrofit retrofit) {
        return retrofit.create(WarehouseService.class);
    }

    @Singleton
    @Provides
    public BarCodeService provideBarCodeService(Retrofit retrofit) {
        return retrofit.create(BarCodeService.class);
    }
}

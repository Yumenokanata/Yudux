package indi.yume.demo.newapplication.ui;

import android.content.Context;

import indi.yume.demo.newapplication.manager.api.ChargeHistoryService;
import indi.yume.demo.newapplication.manager.api.PayGoodsService;
import indi.yume.demo.newapplication.manager.api.PayHistoryService;
import indi.yume.demo.newapplication.manager.api.SearchGoodsService;
import indi.yume.demo.newapplication.manager.api.UserService;
import indi.yume.demo.newapplication.manager.api.base.ApiModule;
import indi.yume.demo.newapplication.manager.sharedpref.SharedPrefModule;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import indi.yume.demo.newapplication.model.sharedpref.SharedPrefModel;
import okhttp3.OkHttpClient;

/**
 * Created by yume on 15/9/14.
 */
@Singleton
@Component(modules = {ApiModule.class, SharedPrefModule.class, MainAppModule.class})
public interface AppComponent {
    @Named("AppContext")
    Context getContext();

    OkHttpClient getOkHttpClient();

    SharedPrefModel getSharedPrefModel();

    //API objects:

    SearchGoodsService getSearchGoodsService();

    UserService getUserService();

    PayGoodsService getPayGoodsService();

    PayHistoryService getPayHistoryService();

    ChargeHistoryService getChargeHistoryService();
}

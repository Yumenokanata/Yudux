package indi.yume.demo.newapplication.ui.presenter;


import java.util.List;

import javax.inject.Inject;

import indi.yume.demo.newapplication.manager.api.SearchGoodsService;
import indi.yume.demo.newapplication.model.api.GoodsModel;
import indi.yume.demo.newapplication.model.sharedpref.SharedPrefModel;
import io.reactivex.Single;

import static indi.yume.demo.newapplication.util.RxUtil.switchThread;

/**
 * Created by DaggerGenerator on 2017/05/15.
 */
public class SearchPresenter extends BasePresenter{
    @Inject
    SearchGoodsService searchGoodsService;
    @Inject
    SharedPrefModel sharedPrefModel;

    public Single<List<GoodsModel>> getAllGoods(){
        return searchGoodsService.getAllGoods()
                .compose(switchThread());
    }
}
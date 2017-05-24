package indi.yume.demo.newapplication.ui.presenter;


import javax.inject.Inject;

import indi.yume.demo.newapplication.manager.api.SearchGoodsService;
import indi.yume.demo.newapplication.model.api.GoodsModel;
import io.reactivex.Single;

import static indi.yume.demo.newapplication.util.RxUtil.switchThread;

/**
 * Created by DaggerGenerator on 2017/05/23.
 */
public class QrScanPresenter extends BasePresenter{
    @Inject
    SearchGoodsService searchGoodsService;

    public Single<GoodsModel> getGoodsInfoByBarCode(String barcode) {
        return searchGoodsService.findGoodsItem(barcode)
                .compose(switchThread());
    }
}
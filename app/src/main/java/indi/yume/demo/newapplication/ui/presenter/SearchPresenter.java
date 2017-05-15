package indi.yume.demo.newapplication.ui.presenter;


import java.util.List;

import javax.inject.Inject;

import indi.yume.demo.newapplication.manager.api.SearchGoodsService;
import indi.yume.demo.newapplication.model.api.CartGoodsModel;
import indi.yume.demo.newapplication.model.api.GoodsModel;
import indi.yume.demo.newapplication.model.sharedpref.SharedPrefModel;
import io.reactivex.Observable;
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
    public void saveGoodsToSharePref(GoodsModel goodsModel) {
        CartGoodsModel model = new CartGoodsModel();
        model.setQuantity(1);
        model.setGoodsModel(goodsModel);
        List<CartGoodsModel> list = sharedPrefModel.getCartGoodsModelList();

        //check if the goods already exist in the list in SharePref.
        boolean exist = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getGoodsModel().getName().equals(goodsModel.getName())) {
                list.get(i).setQuantity(list.get(i).getQuantity() + 1);
                exist = true;
                break;
            }
        }
        if (!exist)
            list.add(model);

        sharedPrefModel.setCartGoodsModelList(list);
    }
}
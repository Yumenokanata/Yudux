package indi.yume.demo.newapplication.ui.presenter;


import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import indi.yume.demo.newapplication.manager.api.PayGoodsService;
import indi.yume.demo.newapplication.manager.api.SearchGoodsService;
import indi.yume.demo.newapplication.manager.api.UserService;
import indi.yume.demo.newapplication.model.api.CartGoodsModel;
import indi.yume.demo.newapplication.model.api.GoodsModel;
import indi.yume.demo.newapplication.model.api.ResultModel;
import indi.yume.demo.newapplication.ui.fragment.cart.CartState;
import io.reactivex.Observable;
import io.reactivex.Single;
import lombok.Getter;

import static indi.yume.demo.newapplication.model.api.ResultModel.STATUS_OK;
import static indi.yume.demo.newapplication.util.RxUtil.switchThread;

/**
 * Created by DaggerGenerator on 2017/05/22.
 */
public class CartPresenter extends BasePresenter{
    @Inject
    PayGoodsService payGoodsService;
    @Inject
    SearchGoodsService searchGoodsService;

    public Single<GoodsModel> checkGoodsStock(GoodsModel model) {
        return searchGoodsService.findGoodsItem(model.getBarCode())
                .compose(switchThread());
    }

    public Single<ResultModel> payManyGoods(String token, List<CartState.ItemData> list) {
        String cart = Stream.of(list)
                .map(m -> m.getModel().getBarCode() + "," + m.getCount())
                .collect(Collectors.joining(";"));

//        return payGoodsService.payManyGoods(token, cart)
//                .compose(switchThread());
        return Single.just(cart)
                .map(s -> {
                    System.out.println("pay: " + cart);
                    ResultModel result = new ResultModel();
                    result.setStatus(STATUS_OK);
                    return result;
                })
                .compose(switchThread());
    }

    public class PayGoodsException extends RuntimeException {
        public PayGoodsException(ResultModel resultModel) {
            super(resultModel.toString());
            this.errorMsg = resultModel.getErrorMsg();
        }

        @Getter
        String errorMsg;
    }
}
package indi.yume.demo.newapplication.ui.presenter;


import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import indi.yume.demo.newapplication.manager.api.PayHistoryService;
import indi.yume.demo.newapplication.manager.api.SearchGoodsService;
import indi.yume.demo.newapplication.model.api.GoodsModel;
import indi.yume.demo.newapplication.model.api.PayHistoryModel;
import io.reactivex.Single;

/**
 * Created by DaggerGenerator on 2017/05/02.
 */
public class HomePresenter extends BasePresenter{
    @Inject
    SearchGoodsService searchGoodsService;
    @Inject
    PayHistoryService payHistoryService;

    public Single<PayHistoryModel> getPayHistory(String token) {
        return payHistoryService.getPayHistory(token, null, null, null, null);
    }

    public Single<List<GoodsModel>> getPayHistoryList(String token) {
        return getPayHistory(token)
                .map(m -> {
                            if(m.getHistory() == null || m.getHistory().isEmpty())
                                return Collections.emptyList();
                            else
                                return Stream.of(m.getHistory().get(m.getHistory().size() - 1).getPayGoods())
                                        .map(pay -> pay.getGoodsData())
                                        .collect(Collectors.toList());
                        }
                );
    }

    public Single<List<GoodsModel>> getAllGoods() {
        return searchGoodsService.getAllGoods();
    }
}
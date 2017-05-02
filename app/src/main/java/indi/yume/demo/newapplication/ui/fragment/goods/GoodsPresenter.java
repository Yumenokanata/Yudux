package indi.yume.demo.newapplication.ui.fragment.goods;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import indi.yume.demo.newapplication.ui.fragment.goods.TestData.ItemModel;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yume on 17-4-28.
 */

public class GoodsPresenter {
    private static final int eachPageCount = 4;

    private final Random random = new Random();

    public Single<List<ItemModel>> getData(int pageNum) {
        return Single.fromCallable(() -> genList(pageNum * eachPageCount, eachPageCount))
                .subscribeOn(Schedulers.io())
                .delay(random.nextInt(500), TimeUnit.MILLISECONDS);
    }

    ItemModel generaModel(int index) {
        return new ItemModel(
                index,
                "Title " + index,
                "This is Content " + index
        );
    }

    List<ItemModel> genList(int startIndex, int count) {
        List<ItemModel> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(generaModel(startIndex + i));
        }

        return list;
    }
}

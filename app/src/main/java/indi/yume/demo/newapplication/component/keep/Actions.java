package indi.yume.demo.newapplication.component.keep;

import com.annimon.stream.Stream;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import indi.yume.demo.newapplication.model.api.GoodsModel;
import indi.yume.demo.newapplication.ui.AppState;
import indi.yume.demo.newapplication.ui.MainApplication;
import indi.yume.yudux.store.StoreAction;
import io.reactivex.Single;
import kotlin.jvm.functions.Function1;
import lombok.experimental.UtilityClass;

import static indi.yume.yudux.DSL.*;

/**
 * Created by yume on 17-5-16.
 */
@UtilityClass
public class Actions {
    public static void keep(GoodsModel model) {
        MainApplication.getMainStore().dispatch(mainAction(
                model,
                (m, s) -> {
                    List<GoodsModel> newList = new ArrayList<>(s.getKeepState().getKeepList());
                    newList.add(m);

                    return s.withKeepState(s.getKeepState()
                            .withKeepCount(newList.size())
                            .withKeepList(newList));
                }
        ));
    }

    public static void disKeep(String barCode) {
        MainApplication.getMainStore().dispatch(mainAction(
                barCode,
                (code, s) -> {
                    List<GoodsModel> newList = Stream.of(s.getKeepState().getKeepList())
                            .filter(m -> m.getBarCode() != code)
                            .toList();

                    return s.withKeepState(s.getKeepState()
                            .withKeepCount(newList.size())
                            .withKeepList(newList));
                }
        ));
    }

    public static void toggleKeep(GoodsModel model) {
        MainApplication.getMainStore().dispatch(mainAction(
                model,
                (goods, s) -> {
                    List<GoodsModel> newList;
                    if(Stream.of(s.getKeepState().getKeepList()).anyMatch(m -> m.getBarCode() == goods.getBarCode())) {
                        newList = Stream.of(s.getKeepState().getKeepList())
                                .filter(m -> m.getBarCode() != goods.getBarCode())
                                .toList();
                    } else {
                        newList = new ArrayList<>(s.getKeepState().getKeepList());
                        newList.add(goods);
                    }

                    return s.withKeepState(s.getKeepState()
                            .withKeepCount(newList.size())
                            .withKeepList(newList));
                }
        ));
    }
}

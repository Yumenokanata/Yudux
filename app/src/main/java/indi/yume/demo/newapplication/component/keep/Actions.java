package indi.yume.demo.newapplication.component.keep;

import com.annimon.stream.Stream;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import indi.yume.demo.newapplication.model.api.GoodsModel;
import indi.yume.demo.newapplication.model.sharedpref.SharedPrefModel;
import indi.yume.demo.newapplication.ui.AppState;
import indi.yume.demo.newapplication.ui.AppStore;
import indi.yume.demo.newapplication.ui.AppStore.MainKey;
import indi.yume.demo.newapplication.ui.MainApplication;
import indi.yume.demo.newapplication.ui.fragment.keep.KeepState;
import indi.yume.yudux.collection.LazyAction;
import indi.yume.yudux.collection.RealWorld;
import indi.yume.yudux.collection.SingleDepends;
import indi.yume.yudux.store.StoreAction;
import io.reactivex.Single;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import lombok.experimental.UtilityClass;

import static indi.yume.demo.newapplication.ui.AppStore.mainRepo;
import static indi.yume.demo.newapplication.ui.AppStore.mainStore;
import static indi.yume.demo.newapplication.ui.AppStore.MainKey.*;
import static indi.yume.yudux.DSL.*;

/**
 * Created by yume on 17-5-16.
 */
@UtilityClass
public class Actions {
    public static void refreshKeep() {
        mainStore.dispatch(create(mainRepo,
                action(lazy(Shared),
                        (real, state) -> {
                            SharedPrefModel shared = real.get(Shared);
                            List<GoodsModel> newList = shared.getKeepList();

                            return Single.just(state.getKeepState()
                                    .withKeepCount(newList.size())
                                    .withKeepList(newList));
                        },
                        (KeepState keep, AppState s) -> s.withKeepState(keep)
                )));
    }

    public static void keep(GoodsModel model) {
        mainStore.dispatch(create(mainRepo,
                action(lazy(Shared),
                        (real, state) -> {
                            SharedPrefModel shared = real.get(Shared);
                            List<GoodsModel> newList = new ArrayList<>(state.getKeepState().getKeepList());
                            newList.add(model);
                            shared.setKeepList(newList);

                            return Single.just(state.getKeepState()
                                    .withKeepCount(newList.size())
                                    .withKeepList(newList));
                        },
                        (KeepState keep, AppState s) -> s.withKeepState(keep)
        )));
    }

    public static void disKeep(String barCode) {
        mainStore.dispatch(create(mainRepo,
                action(lazy(Shared),
                        (real, state) -> {
                            SharedPrefModel shared = real.get(Shared);
                            List<GoodsModel> newList = Stream.of(state.getKeepState().getKeepList())
                                    .filter(m -> m.getBarCode() != barCode)
                                    .toList();
                            shared.setKeepList(newList);

                            return Single.just(state.getKeepState()
                                    .withKeepCount(newList.size())
                                    .withKeepList(newList));
                        },
                        (KeepState keep, AppState s) -> s.withKeepState(keep)
                )));
    }

    public static void toggleKeep(GoodsModel model) {
        mainStore.dispatch(create(mainRepo,
                action(lazy(Shared),
                        (real, state) -> {
                            SharedPrefModel shared = real.get(Shared);
                            List<GoodsModel> newList;
                            if(Stream.of(state.getKeepState().getKeepList()).anyMatch(m -> m.getBarCode() == model.getBarCode())) {
                                newList = Stream.of(state.getKeepState().getKeepList())
                                        .filter(m -> m.getBarCode() != model.getBarCode())
                                        .toList();
                            } else {
                                newList = new ArrayList<>(state.getKeepState().getKeepList());
                                newList.add(model);
                            }
                            shared.setKeepList(newList);

                            return Single.just(state.getKeepState()
                                    .withKeepCount(newList.size())
                                    .withKeepList(newList));
                        },
                        (KeepState keep, AppState s) -> s.withKeepState(keep)
                )));
    }
}

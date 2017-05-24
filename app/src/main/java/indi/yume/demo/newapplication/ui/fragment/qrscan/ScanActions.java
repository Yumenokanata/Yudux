package indi.yume.demo.newapplication.ui.fragment.qrscan;

import android.content.Context;

import java.io.IOException;

import indi.yume.demo.newapplication.model.api.GoodsModel;
import indi.yume.demo.newapplication.ui.AppState;
import indi.yume.demo.newapplication.ui.presenter.QrScanPresenter;
import indi.yume.demo.newapplication.util.DealErrorUtil;
import indi.yume.demo.newapplication.util.DialogUtil;
import indi.yume.yudux.collection.LazyAction;
import indi.yume.yudux.collection.RealWorld;
import indi.yume.yudux.collection.SingleDepends;
import indi.yume.yudux.functions.Result;
import indi.yume.yudux.store.StoreAction;
import io.reactivex.Single;
import lombok.experimental.UtilityClass;

import static indi.yume.demo.newapplication.ui.fragment.qrscan.QrScanFragment.QrScanKey.CONTEXT;
import static indi.yume.demo.newapplication.ui.fragment.qrscan.QrScanFragment.QrScanKey.PRESENTER;
import static indi.yume.yudux.DSL.action;
import static indi.yume.yudux.DSL.lazy;
import static indi.yume.yudux.DSL.mainAction;
import static indi.yume.yudux.DSL.reduce;

/**
 * Created by yume on 17-5-24.
 */
@UtilityClass
public class ScanActions {

    static StoreAction<AppState> setBarCode(String barCode) {
        return mainAction(barCode,
                (code, state) -> state.withQrScanState(state.getQrScanState().withBarCode(code)));
    }

    static final StoreAction<AppState> startLoading =
            reduce(s -> s.withQrScanState(s.getQrScanState().withLoading(true)));

    static final StoreAction<AppState> endLoading =
            reduce(s -> s.withQrScanState(s.getQrScanState().withLoading(false)));

    static final StoreAction<AppState> startCamera =
            reduce(s -> s.withQrScanState(s.getQrScanState().withCameraIsRunning(true)));

    static final StoreAction<AppState> endCamera =
            reduce(s -> s.withQrScanState(s.getQrScanState().withCameraIsRunning(false)));

    static StoreAction<AppState> setStatus(@QrScanState.Status int status) {
        return reduce(s -> s.withQrScanState(s.getQrScanState().withStatus(status)));
    }

    static final LazyAction<RealWorld<QrScanFragment.QrScanKey>, SingleDepends<QrScanFragment.QrScanKey>, AppState, Result<GoodsModel, Integer>> getModelInfo =
            action(lazy(CONTEXT, PRESENTER),
                    (real, state) -> {
                        Context context = real.get(CONTEXT);
                        QrScanPresenter presenter = real.get(PRESENTER);
                        QrScanState qrScanState = state.getQrScanState();

                        if(qrScanState.getCurrentItem() != null
                                && qrScanState.getCurrentItem().getBarCode().equals(qrScanState.getBarCode()))
                            return Single.just(Result.left(QrScanState.Status_Ok));

                        return presenter.getGoodsInfoByBarCode(qrScanState.getBarCode())
                                .compose(DialogUtil.composeNetProgressDialog(context))
                                .compose(DealErrorUtil.dealErrorRetry(context))
                                .map(m -> Result.<GoodsModel, Integer>right(m))
                                .<Result<GoodsModel, Integer>>onErrorReturn(t -> {
                                    if(t instanceof IOException)
                                        return Result.left(QrScanState.Status_Error);
                                    else
                                        return Result.left(QrScanState.Status_Not_Found);
                                });
                    },
                    (result, state) -> {
                        if(result.isRight())
                            return state.withQrScanState(state.getQrScanState().withCurrentItem(result.getRight())
                                    .withStatus(QrScanState.Status_Ok));
                        else
                            return state.withQrScanState(state.getQrScanState().withStatus(result.getLeft()));
                    });
}

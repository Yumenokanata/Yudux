package indi.yume.demo.newapplication.ui.fragment.qrscan;

import android.support.annotation.Nullable;

import indi.yume.demo.newapplication.component.slidemenu.SlideMenuActions;
import indi.yume.demo.newapplication.databinding.QrScanFragmentBinding;
import indi.yume.demo.newapplication.model.api.GoodsModel;
import indi.yume.demo.newapplication.ui.AppState;

import indi.yume.demo.newapplication.util.SlideMenuScreensTag;
import indi.yume.yudux.collection.BaseDependAction;
import indi.yume.yudux.collection.ContextCollection;
import lombok.RequiredArgsConstructor;

import static indi.yume.demo.newapplication.component.cart.CartActions.putItem;
import static indi.yume.demo.newapplication.ui.AppStore.mainStore;
import static indi.yume.demo.newapplication.ui.fragment.qrscan.QrScanFragment.QrScanKey.*;
import static indi.yume.demo.newapplication.ui.fragment.qrscan.ScanActions.*;
import static indi.yume.yudux.DSL.*;

/**
 * Created by DaggerGenerator on 2017-05-23.
 */
@RequiredArgsConstructor
public class QrScanHandler {
    private final ContextCollection<QrScanFragment.QrScanKey> repo;

    public void addToCart(@Nullable GoodsModel model) {
        if(model != null)
            putItem(model);
    }

    public void payNow(@Nullable GoodsModel model) {
        addToCart(model);
        mainStore.dispatch(create(repo, effect(lazy(FRAGMENT),
                (real, state) -> {
                    QrScanFragment fragment = real.get(FRAGMENT);
                    fragment.finish();
                    SlideMenuActions.switchTag(SlideMenuScreensTag.CART);
                })));
    }

    public void toggleCamera(boolean running) {
        if(running)
            mainStore.dispatch(endCamera);
        else
            mainStore.dispatch(startCamera);
    }
}
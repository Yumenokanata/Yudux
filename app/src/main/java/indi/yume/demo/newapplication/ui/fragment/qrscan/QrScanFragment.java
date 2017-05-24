package indi.yume.demo.newapplication.ui.fragment.qrscan;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tbruyelle.rxpermissions2.RxPermissions;

import indi.yume.demo.newapplication.R;
import indi.yume.demo.newapplication.databinding.QrScanFragmentBinding;
import indi.yume.demo.newapplication.model.api.GoodsModel;
import indi.yume.demo.newapplication.ui.AppComponent;
import indi.yume.demo.newapplication.ui.MainApplication;
import indi.yume.demo.newapplication.ui.component.DaggerQrScanComponent;
import indi.yume.demo.newapplication.ui.component.QrScanComponent;
import indi.yume.demo.newapplication.ui.AppState;
import indi.yume.demo.newapplication.ui.fragment.base.BaseToolbarFragment;
import indi.yume.demo.newapplication.ui.module.QrScanModule;
import indi.yume.demo.newapplication.ui.presenter.QrScanPresenter;
import indi.yume.demo.newapplication.util.DealErrorUtil;
import indi.yume.demo.newapplication.util.DialogUtil;
import indi.yume.demo.newapplication.widget.barcode.ZXingScannerView;
import indi.yume.yudux.collection.ContextCollection;
import indi.yume.yudux.collection.LazyAction;
import indi.yume.yudux.collection.RealWorld;
import indi.yume.yudux.collection.SingleDepends;
import indi.yume.yudux.functions.Unit;
import indi.yume.yudux.store.Action;
import indi.yume.yudux.store.StoreAction;
import indi.yume.yudux.functions.Result;
import io.reactivex.Single;

import static indi.yume.demo.newapplication.ui.AppStore.mainStore;
import static indi.yume.demo.newapplication.ui.fragment.qrscan.QrScanFragment.QrScanKey.*;
import static indi.yume.demo.newapplication.ui.fragment.qrscan.QrScanState.Status_Ok;
import static indi.yume.demo.newapplication.ui.fragment.qrscan.ScanActions.*;
import static indi.yume.yudux.collection.DSL.*;
import static indi.yume.yudux.DSL.*;

/**
 * Created by DaggerGenerator on 2017-05-23.
 */
public class QrScanFragment extends BaseToolbarFragment {
    enum QrScanKey {
        CONTEXT,
        FRAGMENT,
        VIEW,
        BINDER,
        PRESENTER,
        RESULT_HANDLER,
        CAMERA,
        HANDLER
    }

    private final ContextCollection<QrScanKey> repo =
            ContextCollection.<QrScanKey>builder()
                    .withItem(HANDLER,
                            depends(),
                            (real, repo) -> new QrScanHandler(repo))
                    .withItem(BINDER,
                            depends(VIEW, HANDLER),
                            (real, repo) -> {
                                QrScanFragmentBinding binding = QrScanFragmentBinding.bind(real.getItem(VIEW));
                                binding.setHandler(real.getItem(HANDLER));

                                binding.goodsScannerView.startCamera();

                                return binding;
                            })
                    .withItem(CAMERA,
                            depends(CONTEXT, BINDER, RESULT_HANDLER),
                            (real, repo) -> {
                                ZXingScannerView scannerView = real.<QrScanFragmentBinding>getItem(BINDER).goodsScannerView;
                                Activity activity = real.get(CONTEXT);

                                new RxPermissions(activity)
                                        .request(Manifest.permission.CAMERA)
                                        .subscribe(granted -> {
                                            if(granted)
                                                mainStore.dispatch(startCamera);
                                            else
                                                repo.destroy(CAMERA);
                                        });

                                return scannerView;
                            },
                            camera -> camera.stopCamera())
                    .withItem(PRESENTER,
                            depends(CONTEXT),
                            (real, repo) -> {
                                AppComponent appComponent = MainApplication.getInstance(real.getItem(CONTEXT)).getAppComponent();
                                QrScanComponent qrScanComponent = DaggerQrScanComponent.builder()
                                        .appComponent(appComponent)
                                        .qrScanModule(new QrScanModule(this))
                                        .build();
                                QrScanPresenter presenter = qrScanComponent.getPresenter();
                                qrScanComponent.injectPresenter(presenter);

                                return presenter;
                            })
                    .withItem(RESULT_HANDLER,
                            depends(),
                            (real, repo) -> (ZXingScannerView.ResultHandler) barCode -> {
                                if(mainStore.getState().getQrScanState().isLoading())
                                    return;
                                mainStore.dispatch(startLoading);
                                mainStore.dispatch(endCamera);
                                mainStore.dispatch(setBarCode(barCode.getText()));
                                mainStore.dispatch(setStatus(QrScanState.Status_Loading));
                                mainStore.dispatch(create(repo, getModelInfo));
                                mainStore.dispatch(reduce(s -> s.getQrScanState().getStatus() != Status_Ok
                                        ? s.withQrScanState(s.getQrScanState().withCameraIsRunning(true))
                                        : s));
                                mainStore.dispatch(endLoading);
                            })
                    .build();

    {
        subscribeUntilChanged(
                mainStore,
                extra(repo, depends(BINDER)),
                s -> s.getQrScanState(),
                (real, state) -> real.<QrScanFragmentBinding>getItem(BINDER).setModel(state));
        subscribeUntilChanged(
                mainStore,
                extra(repo, depends(CAMERA, RESULT_HANDLER)),
                s -> s.getQrScanState().isCameraIsRunning(),
                (real, running) -> {
                    ZXingScannerView scannerView = real.getItem(CAMERA);

                    if(running) {
                        ZXingScannerView.ResultHandler resultHandler = real.getItem(RESULT_HANDLER);

                        scannerView.setResultHandler(resultHandler);
                        scannerView.startCamera();
                    } else {
                        scannerView.stopCamera();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.qr_scan_fragment, container, false);
        repo.ready(VIEW, view);
        repo.ready(FRAGMENT, this);
        forceRender(mainStore);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        repo.ready(CONTEXT, context);
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        repo.destroy(CONTEXT);
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        repo.destroyAll();
        super.onDestroy();
    }

    @Override
    public void inject(AppComponent appComponent){

    }
}
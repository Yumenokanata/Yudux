package indi.yume.demo.newapplication.ui.component;

import dagger.Component;
import dagger.Module;

import indi.yume.demo.newapplication.di.ActivityScope;
import indi.yume.demo.newapplication.ui.AppComponent;
import indi.yume.demo.newapplication.ui.module.QrScanModule;
import indi.yume.demo.newapplication.ui.presenter.QrScanPresenter;

/**
 * Created by DaggerGenerator on 2017/05/23.
 */
@Component(modules = QrScanModule.class,
        dependencies = AppComponent.class)
@ActivityScope
public interface QrScanComponent{

    public QrScanPresenter getPresenter();

    public void injectPresenter(QrScanPresenter presenter);

}
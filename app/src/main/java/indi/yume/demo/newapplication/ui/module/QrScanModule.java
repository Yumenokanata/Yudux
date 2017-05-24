package indi.yume.demo.newapplication.ui.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

import indi.yume.demo.newapplication.di.ActivityScope;
import indi.yume.demo.newapplication.ui.fragment.qrscan.QrScanFragment;
import indi.yume.demo.newapplication.ui.presenter.QrScanPresenter;

/**
 * Created by DaggerGenerator on 2017/05/23.
 */
@Module
public class QrScanModule extends BaseModule{
    QrScanFragment fragment;

    public QrScanModule(QrScanFragment fragment){
        this.fragment = fragment;
    }

    @ActivityScope
    @Provides
    public QrScanPresenter providePresenter(){
        return new QrScanPresenter();
    }

    @Override
    public Context provideActivity(){
        return fragment.getContext();
    }

}
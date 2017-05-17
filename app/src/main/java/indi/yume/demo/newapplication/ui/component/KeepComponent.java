package indi.yume.demo.newapplication.ui.component;

import dagger.Component;
import dagger.Module;

import indi.yume.demo.newapplication.di.ActivityScope;
import indi.yume.demo.newapplication.ui.AppComponent;
import indi.yume.demo.newapplication.ui.module.KeepModule;
import indi.yume.demo.newapplication.ui.presenter.KeepPresenter;

/**
 * Created by DaggerGenerator on 2017/05/16.
 */
@Component(modules = KeepModule.class,
        dependencies = AppComponent.class)
@ActivityScope
public interface KeepComponent{

    public KeepPresenter getPresenter();

    public void injectPresenter(KeepPresenter presenter);

}
package indi.yume.demo.newapplication.ui.component;

import dagger.Component;
import dagger.Module;

import indi.yume.demo.newapplication.di.ActivityScope;
import indi.yume.demo.newapplication.ui.AppComponent;
import indi.yume.demo.newapplication.ui.module.OtherModule;
import indi.yume.demo.newapplication.ui.presenter.OtherPresenter;

/**
 * Created by DaggerGenerator on 2017/05/24.
 */
@Component(modules = OtherModule.class,
        dependencies = AppComponent.class)
@ActivityScope
public interface OtherComponent{

    public OtherPresenter getPresenter();

    public void injectPresenter(OtherPresenter presenter);

}
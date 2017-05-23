package indi.yume.demo.newapplication.ui.component;

import dagger.Component;
import dagger.Module;

import indi.yume.demo.newapplication.di.ActivityScope;
import indi.yume.demo.newapplication.ui.AppComponent;
import indi.yume.demo.newapplication.ui.module.CartModule;
import indi.yume.demo.newapplication.ui.presenter.CartPresenter;

/**
 * Created by DaggerGenerator on 2017/05/22.
 */
@Component(modules = CartModule.class,
        dependencies = AppComponent.class)
@ActivityScope
public interface CartComponent{

    public CartPresenter getPresenter();

    public void injectPresenter(CartPresenter presenter);

}
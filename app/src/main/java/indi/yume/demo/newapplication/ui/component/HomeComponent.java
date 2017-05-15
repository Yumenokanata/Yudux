package indi.yume.demo.newapplication.ui.component;

import dagger.Component;

import indi.yume.demo.newapplication.di.ActivityScope;
import indi.yume.demo.newapplication.ui.AppComponent;
import indi.yume.demo.newapplication.ui.fragment.home.HomeFragment;
import indi.yume.demo.newapplication.ui.module.HomeModule;
import indi.yume.demo.newapplication.ui.presenter.HomePresenter;

/**
 * Created by DaggerGenerator on 2017/05/02.
 */
@Component(modules = HomeModule.class,
        dependencies = AppComponent.class)
@ActivityScope
public interface HomeComponent{

    public HomePresenter getPresenter();

    public void injectPresenter(HomePresenter presenter);

}
package indi.yume.demo.newapplication.ui.component;

import dagger.Component;
import dagger.Module;

import indi.yume.demo.newapplication.di.ActivityScope;
import indi.yume.demo.newapplication.ui.AppComponent;
import indi.yume.demo.newapplication.ui.module.MyPageModule;
import indi.yume.demo.newapplication.ui.presenter.MyPagePresenter;

/**
 * Created by DaggerGenerator on 2017/05/22.
 */
@Component(modules = MyPageModule.class,
        dependencies = AppComponent.class)
@ActivityScope
public interface MyPageComponent{

    public MyPagePresenter getPresenter();

    public void injectPresenter(MyPagePresenter presenter);

}
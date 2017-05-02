package indi.yume.demo.newapplication.ui.component;

import indi.yume.demo.newapplication.di.ActivityScope;
import indi.yume.demo.newapplication.ui.AppComponent;
import indi.yume.demo.newapplication.ui.activity.MainBaseActivity;
import indi.yume.demo.newapplication.ui.module.MainBaseModule;
import indi.yume.demo.newapplication.ui.presenter.MainBasePresenter;

import dagger.Component;

/**
 * Created by DaggerGenerator on 2017/02/14.
 */
@Component(modules = MainBaseModule.class,
        dependencies = AppComponent.class)
@ActivityScope
public interface MainBaseComponent {

    public void injectFragment(MainBaseActivity activity);

    public void injectPresenter(MainBasePresenter presenter);

}
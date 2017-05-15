package indi.yume.demo.newapplication.ui.component;

import dagger.Component;

import indi.yume.demo.newapplication.di.ActivityScope;
import indi.yume.demo.newapplication.ui.AppComponent;
import indi.yume.demo.newapplication.ui.module.SearchModule;
import indi.yume.demo.newapplication.ui.presenter.SearchPresenter;

/**
 * Created by DaggerGenerator on 2017/05/15.
 */
@Component(modules = SearchModule.class,
        dependencies = AppComponent.class)
@ActivityScope
public interface SearchComponent{

    public SearchPresenter getPresenter();

    public void injectPresenter(SearchPresenter presenter);

}
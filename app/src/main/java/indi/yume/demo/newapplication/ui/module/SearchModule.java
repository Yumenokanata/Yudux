package indi.yume.demo.newapplication.ui.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

import indi.yume.demo.newapplication.di.ActivityScope;
import indi.yume.demo.newapplication.ui.fragment.search.SearchFragment;
import indi.yume.demo.newapplication.ui.presenter.SearchPresenter;

/**
 * Created by DaggerGenerator on 2017/05/15.
 */
@Module
public class SearchModule extends BaseModule{
    SearchFragment fragment;

    public SearchModule(SearchFragment fragment){
        this.fragment = fragment;
    }

    @ActivityScope
    @Provides
    public SearchPresenter providePresenter(){
        return new SearchPresenter();
    }

    @Override
    public Context provideActivity(){
        return fragment.getContext();
    }

}
package indi.yume.demo.newapplication.ui.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

import indi.yume.demo.newapplication.di.ActivityScope;
import indi.yume.demo.newapplication.ui.fragment.cart.CartFragment;
import indi.yume.demo.newapplication.ui.presenter.CartPresenter;

/**
 * Created by DaggerGenerator on 2017/05/22.
 */
@Module
public class CartModule extends BaseModule{
    CartFragment fragment;

    public CartModule(CartFragment fragment){
        this.fragment = fragment;
    }

    @ActivityScope
    @Provides
    public CartPresenter providePresenter(){
        return new CartPresenter();
    }

    @Override
    public Context provideActivity(){
        return fragment.getContext();
    }

}
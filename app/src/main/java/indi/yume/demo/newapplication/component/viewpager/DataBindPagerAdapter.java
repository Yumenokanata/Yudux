package indi.yume.demo.newapplication.component.viewpager;

import android.view.View;

import kotlin.jvm.functions.Function1;

/**
 * Created by yume on 17-5-22.
 */

public class DataBindPagerAdapter<State> extends RenderPagerAdapter<State> {


    public DataBindPagerAdapter(int itemCount,
                                Function1<State, Object>[] mapper,
                                Function1<Object, View>[] creator) {
        super(itemCount, mapper, creator);
    }

    @Override
    public void bind(Object state, int position, View view) {

    }
}

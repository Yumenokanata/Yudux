package indi.yume.demo.newapplication.component.viewpager;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

/**
 * Created by yume on 17-5-22.
 */

public abstract class BaseItemRenderer<State> {

    @LayoutRes
    public abstract int getLayoutResId(@NonNull State data, int index, int begin);

    public abstract void bind(@NonNull State data, int index, RecyclerView.ViewHolder holder, int begin);

    public void recycle(RecyclerView.ViewHolder holder) {}
}

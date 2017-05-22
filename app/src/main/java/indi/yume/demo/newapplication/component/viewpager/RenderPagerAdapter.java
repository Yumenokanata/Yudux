package indi.yume.demo.newapplication.component.viewpager;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.Objects;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Created by yume on 17-5-22.
 */
@RequiredArgsConstructor
public abstract class RenderPagerAdapter<State> extends PagerAdapter {
    private final int itemCount;
    private final Function1<State, Object>[] mapper;
    private final Function1<Object, View>[] creator;

    private State state;

    public void setState(State state) {
        synchronized (this) {
            this.state = state;
        }
    }

    @Override
    public int getCount() {
        return itemCount;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((ItemData) object).getView() == view;
    }

    @Override
    public int getItemPosition(Object object) {
        ItemData item = (ItemData) object;
        Object newItemState = mapper[item.getIndex()].invoke(state);
        if(Objects.equals(item.getState(), newItemState))
            return POSITION_UNCHANGED;

        bind(newItemState, item.getIndex(), item.getView());

        return ((ItemData) object).getIndex();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = ((ItemData) object).getView();
        notifyDataSetChanged();
        container.removeView(view);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object itemState = mapper[position].invoke(state);
        View view = creator[position].invoke(itemState);
        bind(itemState, position, view);
        container.addView(view, 0);
        return new ItemData(position, itemState, view);
    }

    public abstract void bind(Object state, int position, View view);

    @Data
    private static class ItemData {
        final int index;
        final Object state;
        final View view;
    }
}

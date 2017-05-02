package indi.yume.demo.newapplication.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.StyleableRes;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import indi.yume.demo.newapplication.R;
import io.reactivex.functions.Action;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//import rx.functions.Action;

/**
 * Created by yume on 16-6-24.
 */

public class CustomToolbar extends Toolbar {
    public final static int TOOLBAR_MODE_NONE  = 0;
    public final static int TOOLBAR_MODE_BACK  = 1;
    public final static int TOOLBAR_MODE_MENU  = 2;
    public final static int TOOLBAR_MODE_CLOSE = 3;

    @IntDef({TOOLBAR_MODE_NONE, TOOLBAR_MODE_BACK, TOOLBAR_MODE_MENU, TOOLBAR_MODE_CLOSE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ToolbarMode{}

    @ToolbarMode
    private int mode = TOOLBAR_MODE_BACK;

    private Optional<Action> doOnClickBackButton = Optional.empty();
    private Optional<Action> doOnClickMenuButton = Optional.empty();
    private Optional<Action> doOnClickCloseButton = Optional.empty();

    public CustomToolbar(Context context) {
        this(context, null);
    }

    public CustomToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setTitle("");
        if(attrs != null)
            init(context.obtainStyledAttributes(attrs, R.styleable.CustomToolbar));
    }

    private void init(TypedArray tArray) {
        final int max = tArray.getIndexCount();
        Stream.range(0, max)
                .map(tArray::getIndex)
                .forEach(i -> setAttr(i, tArray));
    }

    @SuppressWarnings("WrongConstant")
    private void setAttr(@StyleableRes int attrIndex, TypedArray tArray) {
        switch (attrIndex) {
            case R.styleable.CustomToolbar_ct_title:
                setTitle(tArray.getString(attrIndex));
                break;
            case R.styleable.CustomToolbar_ct_navigationType:
                setMode(tArray.getInt(attrIndex, TOOLBAR_MODE_BACK));
                break;
        }
    }

    public void initListener() {
        setNavigationOnClickListener(v -> onClickNavigation());
    }

    public void setMode(@ToolbarMode int mode) {
        this.mode = mode;
        switch (mode) {
            case TOOLBAR_MODE_NONE:
                setNavigationIcon(null);
                break;
            case TOOLBAR_MODE_BACK:
                setNavigationIcon(R.drawable.ic_home_as_up_arrow);
                break;
            case TOOLBAR_MODE_MENU:
                setNavigationIcon(R.drawable.ic_home_up_menu);
                break;
            case TOOLBAR_MODE_CLOSE:
                setNavigationIcon(R.drawable.ic_home_up_close);
                break;
        }
    }

    @ToolbarMode
    public int getMode() {
        return mode;
    }

    public void setDoOnClickBackButton(Action onClickBackButton) {
        doOnClickBackButton = Optional.ofNullable(onClickBackButton);
    }

    public void setDoOnClickMenuButton(Action onClickMenuButton) {
        doOnClickMenuButton = Optional.ofNullable(onClickMenuButton);
    }

    public void setDoOnClickCloseButton(Action onClickCloseButton) {
        doOnClickCloseButton = Optional.ofNullable(onClickCloseButton);
    }

    private void onClickNavigation() {
        switch (mode) {
            case TOOLBAR_MODE_BACK:
//                doOnClickBackButton.ifPresent(Action::run);
                break;
            case TOOLBAR_MODE_MENU:
//                doOnClickMenuButton.ifPresent(Action::run);
                break;
            case TOOLBAR_MODE_CLOSE:
//                doOnClickCloseButton.ifPresent(Action::run);
                break;
        }
    }
}

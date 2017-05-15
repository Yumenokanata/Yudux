package indi.yume.demo.newapplication.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.annimon.stream.Optional;
import com.annimon.stream.function.BiConsumer;

import indi.yume.demo.newapplication.R;

/**
 * Created by yume on 16-12-13.
 *
 * Has clear button EditText.
 */

public class ClearEditText extends AppCompatEditText {
    private static final int ICON_CLEAR_DEFAULT = R.mipmap.icn_clear;

    private Drawable drawableClear;

    private Optional<BiConsumer<View, CharSequence>> clearListener = Optional.empty();

    public ClearEditText(Context context) {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClearTextView);

        int iconClear =
                typedArray.getResourceId(R.styleable.ClearTextView_iconClear, ICON_CLEAR_DEFAULT);
        drawableClear = getResources().getDrawable(iconClear);
        updateIconClear();
        typedArray.recycle();

        addTextChangedListener(new TextWatcher() {
            //<editor-fold defaultstate="collapsed" desc="Useless">
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            //</editor-fold>
            @Override
            public void afterTextChanged(Editable s) {
                updateIconClear();
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int xDown = (int) event.getX();
            if (xDown >= (getWidth() - getCompoundPaddingRight()) && xDown < getWidth()) {
                clearListener.ifPresent(act -> act.accept(this, getText().toString()));
                setText("");
                return false;
            }
        }
        return super.dispatchTouchEvent(event);
    }

    public void setClearListener(BiConsumer<View, CharSequence> listener) {
        clearListener = Optional.ofNullable(listener);
    }

    public void updateIconClear() {
        Drawable[] drawables = getCompoundDrawables();
        if (length() > 0)
            setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], drawableClear,
                    drawables[3]);
        else
            setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], null,
                    drawables[3]);
    }

    public void setIconClear(@DrawableRes int resId) {
        drawableClear = getResources().getDrawable(resId);
        updateIconClear();
    }
}

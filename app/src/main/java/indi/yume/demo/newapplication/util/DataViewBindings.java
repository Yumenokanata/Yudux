package indi.yume.demo.newapplication.util;

import android.databinding.BindingAdapter;
import android.widget.TextView;

/**
 * Created by yume on 17-5-3.
 */

public class DataViewBindings {
    @BindingAdapter("deleteText")
    public static void deleteText(TextView view, String text) {
        view.setText(StringUtil.deleteLineText(text));
    }
}

package indi.yume.demo.newapplication.ui.fragment.qrscan;

import android.databinding.BindingAdapter;
import android.support.annotation.StringRes;
import android.widget.TextView;

import lombok.experimental.UtilityClass;

/**
 * Created by yume on 17-5-24.
 */
@UtilityClass
public class DataBindUtil {
    @BindingAdapter("resText")
    public static void getString(TextView view, @StringRes int res) {
        if(res == 0)
            return;

        view.setText(res);
    }
}

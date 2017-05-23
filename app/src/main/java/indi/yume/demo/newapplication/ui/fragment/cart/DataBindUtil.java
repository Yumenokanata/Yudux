package indi.yume.demo.newapplication.ui.fragment.cart;

import android.databinding.BindingAdapter;
import android.widget.TextView;

import com.annimon.stream.Stream;

import lombok.experimental.UtilityClass;

/**
 * Created by yume on 17-5-22.
 */
@UtilityClass
public class DataBindUtil {

    public static double sumPrice(CartState state) {
        if(state == null)
            return 0;

        return Stream.of(state.getCart())
                .filter(m -> m.isSelected())
                .mapToDouble(m -> m.getModel().getSalePrice() * m.getCount())
                .sum();
    }

    public static int selectedCount(CartState state) {
        if(state == null)
            return 0;

        return (int) Stream.of(state.getCart())
                .filter(m -> m.isSelected())
                .count();
    }
}

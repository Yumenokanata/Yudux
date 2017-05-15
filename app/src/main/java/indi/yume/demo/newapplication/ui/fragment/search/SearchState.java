package indi.yume.demo.newapplication.ui.fragment.search;

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;

import indi.yume.demo.newapplication.model.api.GoodsModel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Wither;

/**
 * Created by yume on 17-5-15.
 */
@Data
@RequiredArgsConstructor
public class SearchState {
    public static final int SORT_DEFAULT  = 1;
    public static final int SORT_NAME     = 1 << 1;
    public static final int SORT_COUNT    = 1 << 2;
    public static final int SORT_PRICE    = 1 << 3;

    public static final int[] SORT_OPTIONS = new int[]{SORT_DEFAULT, SORT_NAME, SORT_COUNT, SORT_PRICE};

    @IntDef({SORT_DEFAULT, SORT_NAME, SORT_COUNT, SORT_PRICE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SortMode{}

    @Wither
    private final boolean isLoading;
    @Wither
    private final String selectedItem;
    @SortMode
    @Wither
    private final int sort;
    @Wither
    @Nullable
    private final String keyWord;
    @Wither
    private final List<GoodsModel> result;

    public SearchState() {
        this.isLoading = false;
        this.selectedItem = "";
        this.sort = SORT_DEFAULT;
        this.keyWord = null;
        this.result = Collections.emptyList();
    }
}

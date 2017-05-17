package indi.yume.demo.newapplication.ui.fragment.keep;

import java.util.Collections;
import java.util.List;

import indi.yume.demo.newapplication.model.api.GoodsModel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Wither;

/**
 * Created by yume on 2017-05-16.
 */
@Data
@RequiredArgsConstructor
public class KeepState {
    @Wither
    private final String selectedItem;
    @Wither
    private final int keepCount;
    @Wither
    private final List<GoodsModel> keepList;

    public KeepState() {
        this.selectedItem = "";
        this.keepCount = 0;
        this.keepList = Collections.emptyList();
    }
}
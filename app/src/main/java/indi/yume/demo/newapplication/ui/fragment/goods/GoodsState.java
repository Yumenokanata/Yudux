package indi.yume.demo.newapplication.ui.fragment.goods;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;

/**
 * Created by yume on 17-4-28.
 */
@Data
@AllArgsConstructor
public class GoodsState {
    @Wither
    private final String title;
    @Wither
    private final LoadMoreState<TestData.ItemModel> loadMoreState;

    public GoodsState() {
        this.title = "Default";
        this.loadMoreState = new LoadMoreState<>();
    }
}

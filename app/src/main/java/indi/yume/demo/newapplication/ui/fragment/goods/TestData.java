package indi.yume.demo.newapplication.ui.fragment.goods;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Created by yume on 17-4-28.
 */

public class TestData {

    @Data
    public static class ItemModel {
        private final long id;
        private final String title;
        private final String content;
    }
}

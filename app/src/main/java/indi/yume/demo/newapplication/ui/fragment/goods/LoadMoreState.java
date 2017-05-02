package indi.yume.demo.newapplication.ui.fragment.goods;

import java.util.Collections;
import java.util.List;

import indi.yume.yudux.functions.Function;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Wither;

import static indi.yume.yudux.DSL.plus;

/**
 * Created by yume on 17-4-28.
 */
@Data
@AllArgsConstructor
public class LoadMoreState<T> {
    @Wither private final int firstPageNum;
    @Wither private final boolean enableLoadMore;
    @Wither private final int pageNumber;
    @Wither private final List<T> data;
    @Wither private final boolean hasMore;
    @Wither private final boolean isRefresh;
    @Wither private final boolean isLoadingMore;

    public LoadMoreState() {
        this.firstPageNum = 0;
        this.enableLoadMore = true;
        this.pageNumber = firstPageNum;
        this.data = Collections.emptyList();
        this.hasMore = true;
        this.isRefresh = false;
        this.isLoadingMore = false;
    }

    public LoadMoreState<T> withFirstPageData(PageData<T> pageData) {
        return withPageNumber(pageData.getPageNum())
                .withData(pageData.getData())
                .withHasMore(pageData.isLastData || !pageData.hasMoreData)
                .withLoadingMore(pageData.getPageNum() == firstPageNum && isLoadingMore)
                .withRefresh(pageData.getPageNum() != firstPageNum && isRefresh);
    }

    public LoadMoreState<T> withMorePageData(PageData<T> pageData) {
        return withPageNumber(pageData.getPageNum())
                .withData(plus(data, pageData.getData()))
                .withHasMore(pageData.isLastData || !pageData.hasMoreData)
                .withLoadingMore(pageData.getPageNum() == firstPageNum && isLoadingMore)
                .withRefresh(pageData.getPageNum() != firstPageNum && isRefresh);
    }

    @Data
    @Builder
    public static class PageData<T> {
        private final int pageNum;
        private final List<T> data;
        private final boolean isLastData;
        private final boolean hasMoreData;
    }
}

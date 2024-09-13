package anda.travel.driver.baselibrary.view.refreshview.internal;

import anda.travel.driver.baselibrary.view.refreshview.RefreshViewListener;


public interface IExRefreshView {
    void onRefresh();

    void setRefreshing(boolean refreshing);

    void setRefreshListener(RefreshViewListener listener);

    /**
     * 开始加载更多
     */
    void onLoadMore();

    /**
     * 正在加载, 由 Adapter 调用, 用于 footerView 的提示更新
     *
     * @param loadingMore
     */
    void setLoadingMore(boolean loadingMore);

    /**
     * 没有更多数据了
     *
     * @param noMoreData
     */
    void hasNoMoreData(boolean noMoreData);

    /**
     * 设置开始加载的位置
     *
     * @param itemsBelow 还剩多少项未显示时, 开始加载
     */
    void setStartLoadingPosition(int itemsBelow);
}

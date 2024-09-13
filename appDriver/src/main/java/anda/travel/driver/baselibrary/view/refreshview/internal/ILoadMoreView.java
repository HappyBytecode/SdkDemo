package anda.travel.driver.baselibrary.view.refreshview.internal;

import android.view.View;


public interface ILoadMoreView {
    View getLoadMoreView();

    void initView();

    void onLoading();

    void loadCompleted();

    void hasNoMoreData();

    void setHideLoadMoreText(boolean hideLoadMoreText);
}

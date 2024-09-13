package anda.travel.driver.baselibrary.view.refreshview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.view.refreshview.internal.IExRefreshView;

public class ExRefreshView extends SwipeRefreshLayout implements SwipeRefreshLayout.OnRefreshListener, IExRefreshView {

    private RefreshAdapter mRefreshAdapter;
    private RecyclerView mRecyclerView;
    private RefreshViewListener mListener;

    private int[] lastPositions;
    private int lastVisibleItemPosition;
    private int currentScrollState;
    private boolean mIsLoadingMore;
    private boolean mLoadingMoreEnabled = true;
    private int mStartLoadingItemsCount = 1;
    private final int DELAY = 800;

    public enum LayoutManagerType {
        LINEAR_LAYOUT,
        GRID_LAYOUT,
        STAGGERED_GRID_LAYOUT
    }

    protected LayoutManagerType mLayoutManagerType;

    public ExRefreshView(Context context) {
        this(context, null);
    }

    public ExRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRecyclerView = new RecyclerView(context);
        mRecyclerView.addOnScrollListener(scrollListener);
        addView(mRecyclerView);
        //设置进度圈大小
        setSize(SwipeRefreshLayout.DEFAULT);
        //设置背景颜色
        setProgressBackgroundColorSchemeColor(ContextCompat.getColor(context, R.color.white));
        //设置进度圈颜色
        setColorSchemeColors(ContextCompat.getColor(context, R.color.colorPrimary));
        setOnRefreshListener(this);
        addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                mRecyclerView.getLayoutParams().height = LayoutParams.MATCH_PARENT;
                mRecyclerView.getLayoutParams().width = LayoutParams.MATCH_PARENT;
            }

            @Override
            public void onViewDetachedFromWindow(View v) {

            }
        });

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int topRowVerticalPosition =
                    (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
            setEnabled(topRowVerticalPosition >= 0 && recyclerView != null && !recyclerView.canScrollVertically(-1));
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (mLoadingMoreEnabled == false) {
                return;
            }
            currentScrollState = newState;
            if (currentScrollState == RecyclerView.SCROLL_STATE_IDLE) {
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                gainLastVisiblePosition(recyclerView);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                if (visibleItemCount > 0 && lastVisibleItemPosition >= totalItemCount - mStartLoadingItemsCount) {
                    if (!isLoadingMore() && !isRefreshing()) {
                        showFoodView();
                        setLoadingMore(true);
                        onLoadMore();
                    }
                }
            }
        }

        private void gainLastVisiblePosition(RecyclerView recyclerView) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (mLayoutManagerType == null) {
                if (layoutManager instanceof LinearLayoutManager) {
                    mLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT;
                } else if (layoutManager instanceof GridLayoutManager) {
                    mLayoutManagerType = LayoutManagerType.GRID_LAYOUT;
                } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                    mLayoutManagerType = LayoutManagerType.STAGGERED_GRID_LAYOUT;
                } else {
                    throw new RuntimeException("Unsupported LayoutManager used. Valid ones are LinearLayoutManager, " +
                            "GridLayoutManager and StaggeredGridLayoutManager");
                }
            }

            switch (mLayoutManagerType) {
                case LINEAR_LAYOUT:
                    lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                    break;
                case GRID_LAYOUT:
                    lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                    break;
                case STAGGERED_GRID_LAYOUT:
                    StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                    if (lastPositions == null) {
                        lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                    }
                    staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                    lastVisibleItemPosition = findMax(lastPositions);
                    break;
                default:
                    break;
            }
        }

        private int findMax(int[] lastPositions) {
            int max = lastPositions[0];
            for (int value : lastPositions) {
                if (value > max) {
                    max = value;
                }
            }
            return max;
        }
    };

    private boolean isLoadingMore() {
        return mIsLoadingMore;
    }

    public void setAdapter(RefreshAdapter refreshAdapter) {
        mRefreshAdapter = refreshAdapter;
        mRecyclerView.setAdapter(refreshAdapter);
        hideFoodView();
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mRecyclerView.setLayoutManager(layoutManager);
    }

    public void setRefreshListener(RefreshViewListener listener) {
        mListener = listener;
    }

    @Override
    public void onLoadMore() {
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onLoadMore();
                    setLoadingMore(false);
                    hideFoodView();
                }
            }
        }, DELAY);
    }

    @Override
    public void setLoadingMore(boolean loadingMore) {
        this.mIsLoadingMore = loadingMore;
        if (loadingMore) {
            mRefreshAdapter.getLoadMoreView().onLoading();
        } else {
            mRefreshAdapter.getLoadMoreView().loadCompleted();
        }
    }

    public void setHideLoadMoreText(boolean hideLoadMoreText) {
        mRefreshAdapter.getLoadMoreView().setHideLoadMoreText(hideLoadMoreText);
    }

    @Override
    public void hasNoMoreData(boolean noMoreData) {
        mLoadingMoreEnabled = !noMoreData;
        if (noMoreData) {
            showFoodView();
            mRefreshAdapter.getLoadMoreView().hasNoMoreData();
        } else {
//            mRefreshAdapter.getLoadMoreView().initView();
        }
    }

    @Override
    public void onRefresh() {
        hideFoodView();
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onRefresh();
                }
            }
        }, DELAY);
    }

    /**
     * @param itemsBelow 还剩多少项未显示时, 开始加载 itemsBelow > 0
     */
    @Override
    public void setStartLoadingPosition(int itemsBelow) {
        if (itemsBelow <= 0) {
            throw new Error("itemsBelow > 0");
        }

        this.mStartLoadingItemsCount = itemsBelow;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void showFoodView() {
        if (mRefreshAdapter != null)
            mRefreshAdapter.mLoadMoreInterface
                    .getLoadMoreView().setVisibility(VISIBLE);
    }

    public void hideFoodView() {
        if (mRefreshAdapter != null)
            mRefreshAdapter.mLoadMoreInterface
                    .getLoadMoreView().setVisibility(GONE);
    }

    public void initFoodView() {
        if (mRefreshAdapter != null) {
            mRefreshAdapter.mLoadMoreInterface
                    .getLoadMoreView().setVisibility(VISIBLE);
            mRefreshAdapter.getLoadMoreView().initView();
        }
    }
}

package anda.travel.driver.baselibrary.view.refreshview;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import java.util.List;

import anda.travel.driver.baselibrary.adapter.IMulItemViewType;
import anda.travel.driver.baselibrary.adapter.SuperAdapter;
import anda.travel.driver.baselibrary.view.refreshview.internal.ILoadMoreView;
import anda.travel.driver.baselibrary.view.refreshview.internal.IRefreshAdapter;
import anda.travel.driver.baselibrary.view.refreshview.internal.SimpleLoadMoreView;

public abstract class RefreshAdapter<DATA> extends SuperAdapter<DATA> implements IRefreshAdapter {

    public ILoadMoreView mLoadMoreInterface;

    public RefreshAdapter(Context context, List<DATA> list, int layoutId) {
        super(context, list, layoutId);

    }

    public RefreshAdapter(Context context, List<DATA> list, IMulItemViewType<DATA> mulItemViewType) {
        super(context, list, mulItemViewType);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (mLoadMoreInterface == null) {
            mLoadMoreInterface = new SimpleLoadMoreView(getContext());
            super.addFooterView(mLoadMoreInterface.getLoadMoreView());
            mLoadMoreInterface.initView();
        }
    }

    /**
     * 请使用 RxRefreshView 中的 setLoadMoreView
     *
     * @param loadMoreInterface
     */
    @Override
    public void setLoadMoreView(ILoadMoreView loadMoreInterface) {
        if (mLoadMoreInterface != null) {
            mFooters.remove(mFooters.size() - 1);
            mFooters.add(loadMoreInterface.getLoadMoreView());
            notifyItemChanged(getItemCount() - 1);
        } else {
            mLoadMoreInterface = loadMoreInterface;
            mFooters.add(loadMoreInterface.getLoadMoreView());
            notifyItemInserted(getItemCount() - 1);
        }
        mLoadMoreInterface.initView();
    }

    @Override
    public void addFooterView(View footer) {
        //super.addFooterView(footer);
        mFooters.add(mLoadMoreInterface != null
                        ? mFooters.size()
                        : (mFooters.isEmpty() ? 0 : mFooters.size() - 1),
                footer);
        ifGlidLayoutManager();
        notifyItemInserted(mLoadMoreInterface != null ? getItemCount() - 1 : getItemCount() - 2);
    }

    @Override
    public ILoadMoreView getLoadMoreView() {
        return mLoadMoreInterface;
    }
}

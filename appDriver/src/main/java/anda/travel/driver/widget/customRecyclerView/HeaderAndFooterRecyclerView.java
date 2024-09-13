package anda.travel.driver.widget.customRecyclerView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HeaderAndFooterRecyclerView extends RecyclerView {
    private float xDistance, yDistance, xLast, yLast;//左右和上下滑动距离判断
    private final List<View> headerViewList = new ArrayList<>();
    private final List<View> footerViewList = new ArrayList<>();
    private final ProxyAdapter proxyAdapter;

    public HeaderAndFooterRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public HeaderAndFooterRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderAndFooterRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyle) {
        super(context, attrs, defStyle);
        inspectLayoutManager(getLayoutManager());
        proxyAdapter = new ProxyAdapter(this);
        super.setAdapter(proxyAdapter);
    }

    private LinearLayout getFixedViewContainer(@FixedViewHolder.ViewType int viewType) {
        ViewHolder holder = getRecycledViewPool().getRecycledView(viewType);
        if (holder == null) {
            holder = proxyAdapter.createViewHolder(this, viewType);
        }
        getRecycledViewPool().putRecycledView(holder);
        return FixedViewHolder.assertType(holder).getViewContainer();
    }

    @NonNull
    public LinearLayout getHeaderContainer() {
        return getFixedViewContainer(FixedViewHolder.VIEW_TYPE_HEADER);
    }

    @NonNull
    public LinearLayout getFooterContainer() {
        return getFixedViewContainer(FixedViewHolder.VIEW_TYPE_FOOTER);
    }

    @NonNull
    List<View> getHeaderViewList() {
        return headerViewList;
    }

    @NonNull
    List<View> getFooterViewList() {
        return footerViewList;
    }

    public int getHeaderViewCount() {
        return headerViewList.size();
    }

    public int getFooterViewCount() {
        return footerViewList.size();
    }

    public void addHeaderView(@NonNull View view) {
        headerViewList.add(view);
        proxyAdapter.notifyHeaderAdded(view, null);
    }

    public void addHeaderView(@NonNull View view, int index) {
        headerViewList.add(index, view);
        proxyAdapter.notifyHeaderAdded(view, index);
    }

    public void removeHeaderView(@NonNull View view) {
        headerViewList.remove(view);
        proxyAdapter.notifyHeaderRemoved(view, null);
    }

    public void removeHeaderView(int index) {
        View view = headerViewList.remove(index);
        proxyAdapter.notifyHeaderRemoved(view, index);
    }

    public void addFooterView(@NonNull View view) {
        footerViewList.add(view);
        proxyAdapter.notifyFooterAdded(view, null);
    }

    public void addFooterView(@NonNull View view, int index) {
        footerViewList.add(index, view);
        proxyAdapter.notifyFooterAdded(view, index);
    }

    public void removeFooterView(@NonNull View view) {
        footerViewList.remove(view);
        proxyAdapter.notifyFooterRemoved(view, null);
    }

    public void removeFooterView(int index) {
        View view = footerViewList.remove(index);
        proxyAdapter.notifyFooterRemoved(view, index);
    }

    private void inspectLayoutManager(LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();
            FixedViewSpanSizeLookup fixedViewSpanSizeLookup = null;
            if (spanSizeLookup == null || spanSizeLookup instanceof GridLayoutManager.DefaultSpanSizeLookup) {
                fixedViewSpanSizeLookup = new FixedViewSpanSizeLookup();
                gridLayoutManager.setSpanSizeLookup(fixedViewSpanSizeLookup);
            } else if (spanSizeLookup instanceof FixedViewSpanSizeLookup) {
                fixedViewSpanSizeLookup = (FixedViewSpanSizeLookup) spanSizeLookup;
            }
            if (fixedViewSpanSizeLookup != null) {
                fixedViewSpanSizeLookup.attach(gridLayoutManager, proxyAdapter);
            }
        }
    }

    private void recoverLayoutManager(LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();
            if (spanSizeLookup instanceof FixedViewSpanSizeLookup) {
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.DefaultSpanSizeLookup());
                ((FixedViewSpanSizeLookup) spanSizeLookup).detach();
            }
        }
    }

    @Override
    public void setLayoutManager(LayoutManager layoutManager) {
        LayoutManager oldLayoutManager = getLayoutManager();
        inspectLayoutManager(layoutManager);
        super.setLayoutManager(layoutManager);
        recoverLayoutManager(oldLayoutManager);
    }

    @NonNull
    public ProxyAdapter getProxyAdapter() {
        return proxyAdapter;
    }

    @Override
    public Adapter getAdapter() {
        return proxyAdapter.getAdapter();
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(null);
        if (adapter != null) {
            proxyAdapter.setHasStableIds(adapter.hasStableIds());
        } else {
            proxyAdapter.setHasStableIds(false);
        }
        proxyAdapter.setAdapter(adapter);
        super.setAdapter(proxyAdapter);
    }

    @Override
    public void swapAdapter(Adapter adapter, boolean removeAndRecycleExistingViews) {
        super.swapAdapter(null, removeAndRecycleExistingViews);
        if (adapter != null) {
            proxyAdapter.setHasStableIds(adapter.hasStableIds());
        } else {
            proxyAdapter.setHasStableIds(false);
        }
        proxyAdapter.setAdapter(adapter);
        super.swapAdapter(proxyAdapter, removeAndRecycleExistingViews);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();

                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;

                if (xDistance > yDistance) {
                    return false;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }

}

package anda.travel.driver.baselibrary.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import anda.travel.driver.baselibrary.adapter.internal.BaseSuperAdapter;
import anda.travel.driver.baselibrary.adapter.internal.CRUD;
import anda.travel.driver.baselibrary.adapter.internal.SuperViewHolder;

public abstract class SuperAdapter<DATA> extends BaseSuperAdapter<DATA> implements CRUD<DATA> {

    private final String TAG = "SuperAdapter";
    private LayoutInflater mLayoutInflater;
    private List<View> mEmptyList;

    /**
     * 一种类型的 View
     *
     * @param context
     * @param list
     * @param layoutId
     */
    public SuperAdapter(Context context, List<DATA> list, int layoutId) {
        super(context, list, layoutId);
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    /**
     * 多重类型的 View
     * <p>
     * 如果 mulItemViewType 为空时, 请重写 offerMultiItemViewType() 方法
     *
     * @param context
     * @param list
     * @param mulItemViewType
     */
    public SuperAdapter(Context context, List<DATA> list, IMulItemViewType<DATA> mulItemViewType) {
        super(context, list, mulItemViewType);
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public SuperViewHolder onCreate(ViewGroup parent, int viewType) {
        int resource = mMulItemViewType.getLayoutId(viewType);
        return SuperViewHolder.get(mLayoutInflater.inflate(resource, parent, false));
    }

    /*
     * ------------------------------------ CRUD ------------------------------------
     */

    @Override
    public void add(DATA item) {
        add(mList.size(), item);
    }

    @Override
    public void add(int location, DATA item) {
        mList.add(location, item);
        location += headerCount();
        notifyItemInserted(location);
    }

    @Override
    public void insert(int location, DATA item) {
        add(location, item);
    }

    @Override
    public void addAll(List<DATA> items) {
        if (items == null || items.size() == 0) return;

        int start = mList.size();
        mList.addAll(items);
        start += headerCount();
        notifyItemRangeInserted(start, items.size());
    }

    @Override
    public void addAll(int location, List<DATA> items) {
        if (items == null || items.isEmpty()) {
            Log.w(TAG, "addAll: The list you passed contains no elements.");
            return;
        }
        mList.addAll(location, items);
        location += headerCount();
        notifyItemRangeInserted(location, items.size());
    }

    @Override
    public void remove(DATA item) {
        if (contains(item)) {
            remove(mList.indexOf(item));
        }
    }

    @Override
    public void remove(int location) {
        mList.remove(location);
        location += headerCount();
        notifyItemRemoved(location);

        refreshEmptyDisplay();
    }

    @Override
    public void removeAll(List<DATA> items) {
        mList.removeAll(items);
        notifyDataSetChanged();

        refreshEmptyDisplay();
    }

    @Override
    public void retainAll(List<DATA> items) {
        mList.retainAll(items);
        notifyDataSetChanged();

        refreshEmptyDisplay();
    }

    @Override
    public void set(DATA oldItem, DATA newItem) {
        set(mList.indexOf(oldItem), newItem);
    }

    @Override
    public void set(int location, DATA item) {
        mList.set(location, item);
        location += headerCount();
        notifyItemChanged(location);
    }

    @Override
    public void setAll(List<DATA> items) {
        mList.clear();
        if (items != null) mList.addAll(items);
        notifyDataSetChanged();

        refreshEmptyDisplay();
    }

    @Override
    public void replaceAll(List<DATA> items) {
        if (items == null || items.isEmpty()) {
            Log.w(TAG, "replaceAll: The list you passed contains no elements.");
            return;
        }
        if (mList.isEmpty()) {
            addAll(items);
        } else {
            mList.clear();
            mList.addAll(items);
            notifyDataSetChanged();
        }
    }

    @Override
    public boolean contains(DATA item) {
        return mList.contains(item);
    }

    @Override
    public boolean containsAll(List<DATA> items) {
        return mList.containsAll(items);
    }

    @Override
    public void clear() {
        if (!mList.isEmpty()) {
            mList.clear();
            notifyDataSetChanged();
        }
    }

    public void addEmptyLayout(View view) {
        if (mEmptyList == null) mEmptyList = new ArrayList<>();
        if (!mEmptyList.contains(view)) mEmptyList.add(view);
    }

    public void removeEmptyLayout(View view) {
        if (mEmptyList == null || mEmptyList.isEmpty()) return;
        if (mEmptyList.contains(view)) mEmptyList.remove(view);
    }

    public void refreshEmptyDisplay() {
        if (mEmptyList == null || mEmptyList.isEmpty()) return;
        int vis = (mList == null || mList.isEmpty()) ? View.VISIBLE : View.GONE;
        for (int i = 0; i < mEmptyList.size(); i++) {
            mEmptyList.get(i).setVisibility(vis);
        }
    }

    public void emptyGone() {
        if (mEmptyList == null || mEmptyList.isEmpty()) return;
        for (int i = 0; i < mEmptyList.size(); i++) {
            mEmptyList.get(i).setVisibility(View.GONE);
        }
    }

}

package anda.travel.driver.baselibrary.adapter;

public interface IMulItemViewType<DATA> {

    /**
     * Item view type, a non-negative integer is better.
     *
     * @param position current position of ViewHolder
     * @param item     model item
     * @return viewType
     */
    int getItemViewType(int position, DATA item);

    /**
     * Layout res.
     *
     * @param viewType {@link #getItemViewType(int, DATA)}
     * @return {@link androidx.annotation.LayoutRes}
     */
    int getLayoutId(int viewType);
}
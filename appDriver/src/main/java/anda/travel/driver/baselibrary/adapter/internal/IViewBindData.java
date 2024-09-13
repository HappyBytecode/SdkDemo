package anda.travel.driver.baselibrary.adapter.internal;

import android.view.ViewGroup;

public interface IViewBindData<DATA, VH> {
    /**
     * @param parent
     * @param viewType
     * @return
     */
    VH onCreate(ViewGroup parent, int viewType);

    void onBind(VH holder, int viewType, int position, DATA item);
}

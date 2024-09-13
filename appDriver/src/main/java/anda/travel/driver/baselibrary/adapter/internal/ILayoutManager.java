package anda.travel.driver.baselibrary.adapter.internal;

import androidx.recyclerview.widget.RecyclerView;

interface ILayoutManager {
    boolean hasLayoutManager();

    RecyclerView.LayoutManager getLayoutManager();
}

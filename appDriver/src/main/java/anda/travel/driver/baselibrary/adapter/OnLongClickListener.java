package anda.travel.driver.baselibrary.adapter;

import android.view.View;

public interface OnLongClickListener<T> {
    boolean onLongClick(int position, View view, T item);
}

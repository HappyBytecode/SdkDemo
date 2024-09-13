package anda.travel.driver.baselibrary.adapter;

import android.view.View;

public interface OnClickListener<T> {
    void onClick(int position, View view, T item);
}

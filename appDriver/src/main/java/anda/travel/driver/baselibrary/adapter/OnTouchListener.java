package anda.travel.driver.baselibrary.adapter;

import android.view.MotionEvent;
import android.view.View;

public interface OnTouchListener<T> {
    boolean onTouch(int position, View view, T item, MotionEvent event);
}

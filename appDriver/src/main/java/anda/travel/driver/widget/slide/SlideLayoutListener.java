package anda.travel.driver.widget.slide;

import android.view.MotionEvent;
import android.view.View;

/**
 * 功能描述：
 */
class SlideLayoutListener implements View.OnTouchListener {

    private final SlideSwitch mSlideSwitch;
    private float downX;

    public SlideLayoutListener(SlideSwitch slideSwitch) {
        mSlideSwitch = slideSwitch;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mSlideSwitch.layout.getParent().requestDisallowInterceptTouchEvent(true);
                downX = motionEvent.getX(); //控件内的相对位置
                break;
            case MotionEvent.ACTION_UP:
                mSlideSwitch.layout.getParent().requestDisallowInterceptTouchEvent(false);
                mSlideSwitch.click((int) downX);
                break;
            case MotionEvent.ACTION_MOVE:
                mSlideSwitch.layout.getParent().requestDisallowInterceptTouchEvent(true);
                break;
        }
        return true;
    }

}

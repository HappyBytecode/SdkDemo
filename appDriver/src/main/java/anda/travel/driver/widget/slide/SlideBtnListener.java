package anda.travel.driver.widget.slide;

import android.view.MotionEvent;
import android.view.View;

/**
 * 功能描述：
 */
class SlideBtnListener implements View.OnTouchListener {

    private final SlideSwitch mSlideSwitch;
    private float downX;

    public SlideBtnListener(SlideSwitch slideSwitch) {
        mSlideSwitch = slideSwitch;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int offset = 0; //相对于"按下位置"的 X轴偏移量
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mSlideSwitch.layout.getParent().requestDisallowInterceptTouchEvent(true);
                mSlideSwitch.btn.setSelected(true);
                downX = motionEvent.getRawX(); //相对于屏幕左上角的位置
                break;
            case MotionEvent.ACTION_UP:
                mSlideSwitch.layout.getParent().requestDisallowInterceptTouchEvent(false);
                mSlideSwitch.btn.setSelected(false);
                offset = (int) (motionEvent.getRawX() - downX);
                mSlideSwitch.set(offset); //btn滑动结束，自动设置position
                break;
            case MotionEvent.ACTION_MOVE:
                mSlideSwitch.layout.getParent().requestDisallowInterceptTouchEvent(true);
                mSlideSwitch.btn.setSelected(true);
                offset = (int) (motionEvent.getRawX() - downX);
                mSlideSwitch.move(offset); //btn滑动
                break;
        }
        return true;
    }

}

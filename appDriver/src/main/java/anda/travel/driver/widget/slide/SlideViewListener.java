package anda.travel.driver.widget.slide;

import android.view.MotionEvent;
import android.view.View;

/**
 * 功能描述：
 */
class SlideViewListener implements View.OnTouchListener {

    private final static long THRESHOLD = 2000; // 2000像素每秒

    private final SlideView mSlideView;
    private float downX;
    private long timeStamp;

    public SlideViewListener(SlideView slideView) {
        mSlideView = slideView;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int offset = 0; //相对于"按下位置"的 X轴偏移量
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mSlideView.layout.getParent().requestDisallowInterceptTouchEvent(true);
                mSlideView.ivBtnSlide.setSelected(true);
                downX = motionEvent.getRawX(); //相对于屏幕左上角的位置
                timeStamp = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                mSlideView.layout.getParent().requestDisallowInterceptTouchEvent(false);
                mSlideView.ivBtnSlide.setSelected(false);
                offset = (int) (motionEvent.getRawX() - downX);
                float duration = (System.currentTimeMillis() - timeStamp) * 1.0f / 1000; //计算时间间隔
                float speed = duration <= 0 ? 0 : offset / duration;
                if (speed > THRESHOLD) {
                    mSlideView.speedUp(); //btn自动滑到最右
                } else {
                    mSlideView.set(offset); //btn滑动结束，自动设置position
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mSlideView.layout.getParent().requestDisallowInterceptTouchEvent(true);
                mSlideView.ivBtnSlide.setSelected(true);
                offset = (int) (motionEvent.getRawX() - downX);
                mSlideView.move(offset); //btn滑动
                break;
        }
        return true;
    }

}

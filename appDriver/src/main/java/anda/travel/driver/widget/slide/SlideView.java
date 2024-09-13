package anda.travel.driver.widget.slide;

import static android.R.attr.textSize;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import anda.travel.driver.R;

/**
 * 功能描述：可滑动的按键
 */
public class SlideView extends LinearLayout implements Animation.AnimationListener {

    private static final int[] mAttr = {android.R.attr.text, textSize};

    public SlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, mAttr);
        mContent = ta.getString(0);
        mTextSize = ta.getDimension(TypedValue.COMPLEX_UNIT_DIP, 0);
        ta.recycle();
        initSlideView(context);
    }

    private SlideListener mListener;
    FrameLayout layout;
    private FrameLayout background;
    private FrameLayout foreground;
    ImageView ivBtnSlide;
    private TextView tv_content;

    private int mWidth; //控件宽度
    private int currentLeft;
    private boolean isSlideOver;
    private String mContent = "";
    private final float mTextSize;

    private final int SPACE_TIME = 500;//2次滑动的间隔时间，单位ms
    private long lastClickTime;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth(); //获取控件宽度
    }

    private void initSlideView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.hxyc_view_slideview, this, false);
        addView(view); //添加到布局中
        layout = view.findViewById(R.id.layout);
        background = view.findViewById(R.id.background);
        foreground = view.findViewById(R.id.foreground);
        tv_content = view.findViewById(R.id.tv_content);
        ivBtnSlide = view.findViewById(R.id.iv_btn_slide);
        Glide.with(getContext()).load(R.drawable.icon_arrow_move).into(ivBtnSlide);
        foreground.setOnTouchListener(new SlideViewListener(this));
        if (!TextUtils.isEmpty(mContent)) tv_content.setText(mContent);
        if (mTextSize > 0) tv_content.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
    }

    void set(int offset) {
        move(offset);
        isSlideOver = offset > mWidth / 2;
        int animOffset = isSlideOver ? (mWidth - currentLeft) : (0 - currentLeft); //计算动画偏移量
        starAnim(animOffset); //开启动画
    }

    void speedUp() {
        isSlideOver = true;
        int animOffset = mWidth - currentLeft; //计算动画偏移量
        starAnim(animOffset);
    }

    void move(int offset) {
        int newLeft = getNewLeft(offset);
        setForeground(newLeft);
    }

    private void setForeground(int newLeft) {
        if (mWidth == 0) return; //计算出宽度前，不执行
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) foreground.getLayoutParams();
        params.width = mWidth;
        params.setMargins(newLeft, 0, 0, 0); //控制左边距
        layout.requestLayout(); //刷新显示
        currentLeft = newLeft; //记录当前left
    }

    /**
     * 开启动画
     */
    private void starAnim(int animOffset) {
        TranslateAnimation anim = new TranslateAnimation(0f, (float) animOffset, 0f, 0f);
        anim.setDuration(150);
        anim.setFillEnabled(true);
        anim.setInterpolator(new LinearInterpolator());
        anim.setAnimationListener(this); //监听动画的结束
        foreground.startAnimation(anim);
    }

    private int getNewLeft(int offset) {
        return offset < 0 ? 0 : offset;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        setPosition(isSlideOver);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    private void setPosition(boolean isSlideOver) {
        int newLeft = isSlideOver ? mWidth : 0;
        setForeground(newLeft); //控制textView的显示
        if (isSlideOver && mListener != null) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime > SPACE_TIME) {
                lastClickTime = currentTime;
                mListener.onSlideOver(); //触发回调
            }
        }
    }

    /**
     * 监听是否滑倒底
     */
    public interface SlideListener {
        void onSlideOver(); //滑倒底将触发
    }

    public void setOnSlideListener(SlideListener listener) {
        mListener = listener;
    }

    /**
     * 重置显示
     */
    public void resetView() {
        isSlideOver = false;
        postDelayed(() -> setPosition(false), 200);
    }

    /**
     * 设置提示语
     *
     * @param content
     */
    public void setContent(String content) {
        mContent = content;
        tv_content.setText(content);
    }

    public void setForegroundDisable(boolean isDisable) {
        foreground.setSelected(isDisable);
    }

}

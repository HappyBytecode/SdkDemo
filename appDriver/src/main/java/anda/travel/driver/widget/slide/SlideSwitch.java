package anda.travel.driver.widget.slide;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import anda.travel.driver.R;
import timber.log.Timber;

/**
 * 功能描述：OrderSettingFragment中使用的控件
 */
public class SlideSwitch extends LinearLayout implements Animation.AnimationListener {

    public SlideSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initSlideSwitch(context);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlideSwitch);
        CharSequence[] array = ta.getTextArray(R.styleable.SlideSwitch_titles);
        if (array.length == 2) {
            titles = new String[2];
            for (int i = 0; i < array.length; i++) {
                titles[i] = array[i].toString();
                Timber.e("str = " + titles[i]);
            }
        }
        currentPosition = ta.getInteger(R.styleable.SlideSwitch_position, 0);
        if (currentPosition < 0 || currentPosition > 2) currentPosition = 0;
        ta.recycle();
    }

    private String[] titles; //标题
    private int currentPosition; //btn当前位置: 0～1
    private int itemWidth;
    private int currentLeft;
    private TranslateAnimation anim;
    public FrameLayout layout;
    public ImageButton btn;
    private List<TextView> viewList;
    private SlideSwithChangeListener listener;

    //初始化控件
    private void initSlideSwitch(Context context) {
        itemWidth = getResources().getDimensionPixelSize(R.dimen.slide_width) / 2;
        //Timber.d("itemWidth = " + itemWidth);
        View view = LayoutInflater.from(context).inflate(R.layout.hxyc_layout_slideswitch, this, false);
        addView(view); //添加到布局中
        layout = view.findViewById(R.id.layout);
        btn = view.findViewById(R.id.btn);
        TextView tv0 = view.findViewById(R.id.tv0);
        TextView tv1 = view.findViewById(R.id.tv1);
        viewList = new ArrayList<>();
        viewList.add(tv0);
        viewList.add(tv1);
        layout.setOnTouchListener(new SlideLayoutListener(this));
        btn.setOnTouchListener(new SlideBtnListener(this));
        /* 设置显示 */
        if (titles != null) setTitles(titles);
        setPosition(currentPosition);
    }

    /**
     * 设置tab的内容显示
     *
     * @param titles
     */
    private void setTitles(String[] titles) {
        if (titles == null || titles.length != 2) {
            Timber.e("数组长度必须为2！");
            return;
        }
        for (int i = 0; i < viewList.size(); i++) {
            viewList.get(i).setText(titles[i]);
        }
    }

    /**
     * 设置监听
     *
     * @param listener
     */
    public void setOnChangeListener(SlideSwithChangeListener listener) {
        this.listener = listener;
    }

    /**
     * 设置当前选中项（0～2）
     *
     * @param position
     */
    public void setPosition(int position) {
        currentPosition = position; //记录当前position
        move(currentPosition, 0); //移动btn
        setViewListDisplay(position); //控制textView的显示
        if (listener != null) listener.onChangePosition(currentPosition);
    }

    /**
     * 控制textView的显示
     *
     * @param position
     */
    private void setViewListDisplay(int position) {
        for (int i = 0; i < viewList.size(); i++) {
            viewList.get(i).setSelected(i == position);
        }
    }

    /**
     * btn滑动时，调用的方法
     *
     * @param offset
     */
    public void move(int offset) {
        move(currentPosition, offset);
    }

    private void move(int position, int offset) {
        int newLeft = getNewLeft(position, offset); //获取新的left
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) btn.getLayoutParams();
        params.setMargins(newLeft, 0, 0, 0); //控制左边距
        btn.requestLayout(); //刷新显示
        currentLeft = newLeft; //记录当前left
    }

    private int getNewLeft(int position, int offset) {
        int newLeft = position * itemWidth + offset;
        if (newLeft < 0) {
            newLeft = 0; //限定最小leftMargin
        } else if (newLeft > itemWidth) {
            newLeft = itemWidth; //限定最大leftMargin
        }
        //Timber.d("position = " + position + " | offset = " + offset + " | itemWidth = " + itemWidth);
        //Timber.d("newLeft = " + newLeft);
        return newLeft;
    }

    /**
     * btn滑动结束时，自动判断应选择的position
     *
     * @param offset
     */
    public void set(int offset) {
        move(currentPosition, offset);
        int newPosition = (currentLeft + (itemWidth / 2)) / itemWidth; //计算新的position
        currentPosition = newPosition; //记录新的position
        int animOffset = newPosition * itemWidth - currentLeft; //计算动画偏移量
        starAnim(animOffset); //开启动画
    }

    /**
     * 开启动画
     */
    private void starAnim(int animOffset) {
        anim = new TranslateAnimation(0f, (float) animOffset, 0f, 0f);
        anim.setDuration(150);
        anim.setFillEnabled(true);
        anim.setInterpolator(new LinearInterpolator());
        anim.setAnimationListener(this); //监听动画的结束
        btn.startAnimation(anim);
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    /**
     * 监听动画结束
     *
     * @param animation
     */
    @Override
    public void onAnimationEnd(Animation animation) {
        //Timber.d("动画结束");
        setPosition(currentPosition);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    /**
     * layout触摸监听的回调
     *
     * @param clickLeft "按下点"在控件内的相对位置
     */
    public void click(int clickLeft) {
        int newPosition = clickLeft / itemWidth; //计算新的position
        //Timber.d("clickLeft = " + clickLeft);
        //Timber.d("newPosition = " + newPosition + " | currentPosition = " + currentPosition);
        if (newPosition != currentPosition) { //position改变，才触发
            int animOffset = (newPosition - currentPosition) * itemWidth; //计算动画偏移量
            currentPosition = newPosition; //记录新的position
            starAnim(animOffset); //开启动画
        }
    }

    /**
     * 监听position变化
     */
    public interface SlideSwithChangeListener {
        void onChangePosition(int position);
    }

}

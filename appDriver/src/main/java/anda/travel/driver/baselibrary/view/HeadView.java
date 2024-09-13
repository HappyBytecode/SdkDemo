package anda.travel.driver.baselibrary.view;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.StringRes;

import anda.travel.driver.R;

/**
 * 标题控件
 */
public class HeadView extends FrameLayout {

    private static final int[] mAttr = {android.R.attr.background};

    private TextView mTitle;
    private ImageView mLeft;
    private ImageView mRight;
    private TextView mRightTxt;
    private View mDivideLine;
    private TextView mLeftTxt;

    public HeadView(Context context) {
        this(context, null);
    }

    public HeadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.hxyc_anda_head_view, this);
        mTitle = findViewById(R.id.tv_head_title);
        mLeft = findViewById(R.id.img_head_left);
        mRight = findViewById(R.id.img_head_right);
        mRightTxt = findViewById(R.id.tv_head_right);
        mLeftTxt = findViewById(R.id.tv_head_left);
        mDivideLine = findViewById(R.id.divide_line);
        //点击左键，默认结束Activity
        mLeft.setOnClickListener(view -> (scanForActivity(context)).finish());

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HeadView, defStyleAttr, 0);
        //标题内容
        String title = ta.getString(R.styleable.HeadView_title_text);
        mTitle.setText(title);
        //标题大小
        int titleSize = ta.getDimensionPixelSize(R.styleable.HeadView_title_size, 0);
        if (titleSize != 0) mTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
        //标题颜色，默认显示"白色"
        int headColor = ta.getColor(R.styleable.HeadView_title_color, context.getResources().getColor(R.color.head_view_center_textcolor));
        mTitle.setTextColor(headColor);

        //左键图标
        int leftImg = ta.getResourceId(R.styleable.HeadView_left_image, 0);
        if (leftImg != 0) mLeft.setImageResource(leftImg);
        //左键是否显示，默认显示
        boolean leftVis = ta.getBoolean(R.styleable.HeadView_left_visible, true);
        mLeft.setVisibility(leftVis ? View.VISIBLE : View.INVISIBLE);

        //右键图标
        int rightImg = ta.getResourceId(R.styleable.HeadView_right_image, 0);
        if (rightImg != 0) {
            mRight.setImageResource(rightImg);
        }
        //右键内容
        String rightTxt = ta.getString(R.styleable.HeadView_right_text);
        mRightTxt.setText(rightTxt);

        // 右键是否显示
        boolean rightVis = ta.getBoolean(R.styleable.HeadView_right_visible, false);
        mRight.setVisibility(rightVis ? View.VISIBLE : View.GONE);
        mRightTxt.setVisibility(rightVis ? View.VISIBLE : View.GONE);

        //右键内容颜色，默认显示"灰色"
        int rightColor = ta.getColor(R.styleable.HeadView_right_color, context.getResources().getColor(R.color.head_view_right_textcolor));
        mRightTxt.setTextColor(rightColor);

        // 控制下方线的显示
        boolean isDivideLineVis = ta.getBoolean(R.styleable.HeadView_divide_line_visible, true);
        mDivideLine.setVisibility(isDivideLineVis ? View.VISIBLE : View.GONE);
        ta.recycle();

        ta = context.obtainStyledAttributes(attrs, mAttr);
        //控件背景，默认显示"深色"
        Drawable drawable = ta.getDrawable(0);
        if (drawable != null) {
            setBackground(drawable);
        } else {
            setBackgroundResource(R.color.head_view_background);
        }

        ta.recycle();
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setTitle(@StringRes int title) {
        mTitle.setText(title);
    }

    public void setLeftVisibility(boolean visible) {
        mLeft.setVisibility(visible ? VISIBLE : INVISIBLE);
    }

    public void setRightImageVisibility(boolean visible) {
        mRight.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setRightTxtVisibility(boolean visible) {
        mRightTxt.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setRightTxt(String txt) {
        mRightTxt.setText(txt);
    }

    public void setRightTxt(@StringRes int txt) {
        mRightTxt.setText(txt);
    }

    public void setmLeftTxt(String txt) {
        mLeftTxt.setVisibility(VISIBLE);
        mLeftTxt.setText(txt);
    }

    public String getRightTxt() {
        return mRightTxt.getText().toString();
    }

    public View getLeftView() {
        return mLeft;
    }

    public View getRightView() {
        return mRight;
    }

    public TextView getmLeftTextView() {
        return mLeftTxt;
    }

    public TextView getRightTextView() {
        return mRightTxt;
    }

    public void setDividingLineGone() {
        mDivideLine.setVisibility(GONE);
    }

    public void setLeftImageRes(int res) {
        mLeft.setImageResource(res);
    }

    private Activity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) cont).getBaseContext());

        return null;
    }
}

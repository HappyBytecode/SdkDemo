package anda.travel.driver.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import anda.travel.driver.R;
import anda.travel.driver.data.entity.RingBean;

public class RingStatisticsView extends androidx.appcompat.widget.AppCompatTextView {
    private List<RingBean> ringBeanList = new ArrayList<RingBean>();
    private static int DEFAULT_RINGWIDTH = 8;
    private static final int DEFAULT_TEXTSIZE = 5;
    private float mRingWidth = 0;
    private float numTextSize = 0;
    private Canvas mCanvas;
    private Paint mRingPaint;
    private Paint mInnerRingPaint;
    private Paint mShadowPaint;
    private RectF mRectF;
    private float mRadius;

    public RingStatisticsView(Context context) {
        super(context);
        initSize(context);
        init();
    }

    public RingStatisticsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        initSize(context);
        init();
    }

    public RingStatisticsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        initSize(context);
        init();
    }

    /**
     * 获得属性值
     *
     * @param context
     * @param attrs
     */
    private void initAttr(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RingStatisticsView);
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = array.getIndex(i);
            if (attr == R.styleable.RingStatisticsView_ringWidth) {
                mRingWidth = array.getDimension(attr, dp2px(context, DEFAULT_RINGWIDTH));
            } else if (attr == R.styleable.RingStatisticsView_ringNumTextSize) {
                numTextSize = array.getDimension(attr, dp2px(context, DEFAULT_TEXTSIZE));
            }
        }
        array.recycle();
    }

    /**
     * 初始化画笔
     */
    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mRectF = new RectF();
        mRingPaint = new Paint();
        //抗锯齿
        mRingPaint.setAntiAlias(true);
        //防抖动
        mRingPaint.setDither(true);
        //仅描边(圆环)
        mRingPaint.setStyle(Paint.Style.STROKE);
        //圆环宽度
        mRingPaint.setStrokeWidth(mRingWidth);

        mInnerRingPaint = new Paint();
        //抗锯齿
        mInnerRingPaint.setAntiAlias(true);
        //防抖动
        mInnerRingPaint.setDither(true);
        mInnerRingPaint.setColor(Color.parseColor("#ffffff"));
        //填充整圆
        mInnerRingPaint.setStyle(Paint.Style.FILL);


        mShadowPaint = new Paint();
        //抗锯齿
        mShadowPaint.setAntiAlias(true);
        //防抖动
        mShadowPaint.setDither(true);
        mShadowPaint.setColor(Color.parseColor("#d3d3d3"));
        //圆环
        mShadowPaint.setStyle(Paint.Style.STROKE);
        mShadowPaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.OUTER));
    }

    /**
     * 初始化Size
     *
     * @param context
     */
    private void initSize(Context context) {
        if (mRingWidth == 0) {
            mRingWidth = dp2px(context, dp2px(context, DEFAULT_RINGWIDTH));
        }
        if (numTextSize == 0) {
            numTextSize = dp2px(context, DEFAULT_TEXTSIZE);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = Math.max(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(widthMeasureSpec));
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //宽和高分别去掉padding值，取min的一半即圆的半径(这里demo没有用到，可自行做一些其他计算使用)
        mRadius = Math.min(w - getPaddingLeft() - getPaddingRight(), h - getPaddingTop() - getPaddingBottom()) / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mCanvas = canvas;
        drawRingView();
        super.onDraw(canvas);
    }

    /**
     * 更新占比数据
     *
     * @param ringBeanList
     */
    public void updateRing(List<RingBean> ringBeanList) {
        if (ringBeanList != null) {
            this.ringBeanList = ringBeanList;
            postInvalidate();
        }
    }

    /**
     * 画Ring
     */
    private void drawRingView() {
        mCanvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius - mRingWidth * 2, mShadowPaint);
        //画内圆
        mCanvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius - mRingWidth * 2, mInnerRingPaint);

        float sweepAngle = 0f;
        float startAngle = 90f;
        //矩形坐标
        mRectF.set(getPaddingLeft() + mRingWidth / 2, getPaddingTop() + mRingWidth / 2, getWidth() - getPaddingRight() - mRingWidth / 2, getHeight() - getPaddingBottom() - mRingWidth / 2);
        for (int i = 0; i < ringBeanList.size(); i++) {
            mRingPaint.setColor(Color.parseColor(ringBeanList.get(i).getColor()));
            startAngle += sweepAngle;
            sweepAngle = 360 * ringBeanList.get(i).getValue();
            //画圆环
            mCanvas.drawArc(mRectF, startAngle, sweepAngle, false, mRingPaint);
        }
    }


    /**
     * dp2px
     *
     * @param context
     * @param dpValue
     * @return
     */
    private int dp2px(Context context, float dpValue) {
        final float densityScale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * densityScale + 0.5f);
    }
}


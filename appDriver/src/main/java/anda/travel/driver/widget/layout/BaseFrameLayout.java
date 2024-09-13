package anda.travel.driver.widget.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

public class BaseFrameLayout extends FrameLayout implements IBaseLayout {
    private BaseLayoutHelper mLayoutHelper;

    public BaseFrameLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public BaseFrameLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public BaseFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mLayoutHelper = new BaseLayoutHelper(context, attrs, defStyleAttr, this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = mLayoutHelper.getMeasuredWidthSpec(widthMeasureSpec);
        heightMeasureSpec = mLayoutHelper.getMeasuredHeightSpec(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int minW = mLayoutHelper.handleMiniWidth(widthMeasureSpec, getMeasuredWidth());
        int minH = mLayoutHelper.handleMiniHeight(heightMeasureSpec, getMeasuredHeight());
        if (widthMeasureSpec != minW || heightMeasureSpec != minH) {
            super.onMeasure(minW, minH);
        }
    }

    @Override
    public boolean setWidthLimit(int widthLimit) {
        if (mLayoutHelper.setWidthLimit(widthLimit)) {
            requestLayout();
            invalidate();
        }
        return true;
    }

    @Override
    public boolean setHeightLimit(int heightLimit) {
        if (mLayoutHelper.setHeightLimit(heightLimit)) {
            requestLayout();
            invalidate();
        }
        return true;
    }

    @Override
    public void setShadowElevation(int elevation) {
        mLayoutHelper.setShadowElevation(elevation);
    }

    @Override
    public int getShadowElevation() {
        return mLayoutHelper.getShadowElevation();
    }

    @Override
    public void setShadowAlpha(float shadowAlpha) {
        mLayoutHelper.setShadowAlpha(shadowAlpha);
    }

    @Override
    public float getShadowAlpha() {
        return mLayoutHelper.getShadowAlpha();
    }

    @Override
    public void setShadowColor(int shadowColor) {
        mLayoutHelper.setShadowColor(shadowColor);
    }

    @Override
    public int getShadowColor() {
        return mLayoutHelper.getShadowColor();
    }

    @Override
    public void setRadius(int radius) {
        mLayoutHelper.setRadius(radius);
    }

    @Override
    public void setRadius(int radius, int hideRadiusSide) {
        mLayoutHelper.setRadius(radius, hideRadiusSide);
    }

    @Override
    public int getRadius() {
        return mLayoutHelper.getRadius();
    }

    @Override
    public void setHideRadiusSide(int hideRadiusSide) {
        mLayoutHelper.setHideRadiusSide(hideRadiusSide);
    }

    @Override
    public void setDefaultRadiusAndShadow() {
        setRadiusAndShadow(8, 16, 0.5f);
    }

    @Override
    public int getHideRadiusSide() {
        return mLayoutHelper.getHideRadiusSide();
    }

    @Override
    public void setRadiusAndShadow(int radius, int shadowElevation) {
        mLayoutHelper.setRadiusAndShadow(radius, shadowElevation, 0.5f);
    }

    @Override
    public void setRadiusAndShadow(int radius, int shadowElevation, float shadowAlpha) {
        mLayoutHelper.setRadiusAndShadow(radius, shadowElevation, shadowAlpha);
    }

    @Override
    public void setRadiusAndShadow(int radius, int hideRadiusSide, int shadowElevation, float shadowAlpha) {
        mLayoutHelper.setRadiusAndShadow(radius, hideRadiusSide, shadowElevation, shadowAlpha);
    }

    @Override
    public void setRadiusAndShadow(int radius, int hideRadiusSide, int shadowElevation, int shadowColor, float shadowAlpha) {
        mLayoutHelper.setRadiusAndShadow(radius, hideRadiusSide, shadowElevation, shadowColor, shadowAlpha);
    }
}

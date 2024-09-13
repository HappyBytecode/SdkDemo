package anda.travel.driver.widget.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

import java.lang.ref.WeakReference;

import anda.travel.driver.R;

public class BaseLayoutHelper implements IBaseLayout {
    // size
    private int mWidthLimit = 0;
    private int mHeightLimit = 0;
    private int mWidthMini = 0;
    private int mHeightMini = 0;

    private int mRadius;
    private @IBaseLayout.HideRadiusSide
    int mHideRadiusSide = HIDE_RADIUS_SIDE_NONE;
    private int mBorderColor = 0;
    private int mBorderWidth = 1;
    private int mOuterNormalColor = 0;
    private final WeakReference<View> mOwner;
    private final boolean mIsOutlineExcludePadding = false;
    private Path mPath = new Path();

    // shadow
    private int mShadowElevation = 0;
    private float mShadowAlpha;
    private int mShadowColor = Color.BLACK;

    public BaseLayoutHelper(Context context, AttributeSet attrs, int defAttr, View owner) {
        this(context, attrs, defAttr, 0, owner);
    }

    private BaseLayoutHelper(Context context, AttributeSet attrs, int defAttr, int defStyleRes, View owner) {
        mOwner = new WeakReference<>(owner);
        PorterDuffXfermode mMode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        // round
        Paint mClipPaint = new Paint();
        mClipPaint.setAntiAlias(true);
        mShadowAlpha = 0.6f;
        RectF mBorderRect = new RectF();

        int radius = 0, shadow = 0;
        boolean useThemeGeneralShadowElevation = false;
        if (null != attrs || defAttr != 0 || defStyleRes != 0) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.HXLayout, defAttr, defStyleRes);
            int count = ta.getIndexCount();
            for (int i = 0; i < count; ++i) {
                int index = ta.getIndex(i);
                if (index == R.styleable.HXLayout_android_maxWidth) {
                    mWidthLimit = ta.getDimensionPixelSize(index, mWidthLimit);
                } else if (index == R.styleable.HXLayout_android_maxHeight) {
                    mHeightLimit = ta.getDimensionPixelSize(index, mHeightLimit);
                } else if (index == R.styleable.HXLayout_android_minWidth) {
                    mWidthMini = ta.getDimensionPixelSize(index, mWidthMini);
                } else if (index == R.styleable.HXLayout_android_minHeight) {
                    mHeightMini = ta.getDimensionPixelSize(index, mHeightMini);
                } else if (index == R.styleable.HXLayout_hx_radius) {
                    radius = ta.getDimensionPixelSize(index, 0);
                } else if (index == R.styleable.HXLayout_hx_shadowElevation) {
                    shadow = ta.getDimensionPixelSize(index, shadow);
                } else if (index == R.styleable.HXLayout_hx_shadowAlpha) {
                    mShadowAlpha = ta.getFloat(index, mShadowAlpha);
                } else if (index == R.styleable.HXLayout_hx_shadowColor) {
                    mShadowColor = ta.getColor(index, Color.BLACK);
                }
            }
            ta.recycle();
        }
        if (shadow == 0 && useThemeGeneralShadowElevation) {
            shadow = 10;

        }
        setRadiusAndShadow(radius, mHideRadiusSide, shadow, mShadowAlpha);
    }

    @Override
    public boolean setWidthLimit(int widthLimit) {
        if (mWidthLimit != widthLimit) {
            mWidthLimit = widthLimit;
            return true;
        }
        return false;
    }

    @Override
    public boolean setHeightLimit(int heightLimit) {
        if (mHeightLimit != heightLimit) {
            mHeightLimit = heightLimit;
            return true;
        }
        return false;
    }

    @Override
    public int getShadowElevation() {
        return mShadowElevation;
    }

    @Override
    public float getShadowAlpha() {
        return mShadowAlpha;
    }

    @Override
    public int getShadowColor() {
        return mShadowColor;
    }

    @Override
    public void setShadowElevation(int elevation) {
        if (mShadowElevation == elevation) {
            return;
        }
        mShadowElevation = elevation;
        invalidateOutline();
    }

    @Override
    public void setShadowAlpha(float shadowAlpha) {
        if (mShadowAlpha == shadowAlpha) {
            return;
        }
        mShadowAlpha = shadowAlpha;
        invalidateOutline();
    }

    @Override
    public void setShadowColor(int shadowColor) {
        if (mShadowColor == shadowColor) {
            return;
        }
        mShadowColor = shadowColor;
        setShadowColorInner(mShadowColor);
    }

    private void setShadowColorInner(int shadowColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            View owner = mOwner.get();
            if (owner == null) {
                return;
            }
            owner.setOutlineAmbientShadowColor(shadowColor);
            owner.setOutlineSpotShadowColor(shadowColor);
        }
    }

    private void invalidateOutline() {
        if (useFeature()) {
            View owner = mOwner.get();
            if (owner == null) {
                return;
            }
            if (mShadowElevation == 0) {
                owner.setElevation(0);
            } else {
                owner.setElevation(mShadowElevation);
            }
            owner.invalidateOutline();
        }
    }

    private void invalidate() {
        View owner = mOwner.get();
        if (owner == null) {
            return;
        }
        owner.invalidate();
    }

    @Override
    public void setHideRadiusSide(@HideRadiusSide int hideRadiusSide) {
        if (mHideRadiusSide == hideRadiusSide) {
            return;
        }
        setRadiusAndShadow(mRadius, hideRadiusSide, mShadowElevation, mShadowAlpha);
    }

    @Override
    public int getHideRadiusSide() {
        return mHideRadiusSide;
    }

    @Override
    public void setRadius(int radius) {
        if (mRadius != radius) {
            setRadiusAndShadow(radius, mShadowElevation, mShadowAlpha);
        }
    }

    @Override
    public void setRadius(int radius, @IBaseLayout.HideRadiusSide int hideRadiusSide) {
        if (mRadius == radius && hideRadiusSide == mHideRadiusSide) {
            return;
        }
        setRadiusAndShadow(radius, hideRadiusSide, mShadowElevation, mShadowAlpha);
    }

    @Override
    public int getRadius() {
        return mRadius;
    }

    @Override
    public void setDefaultRadiusAndShadow() {
        setRadiusAndShadow(8, 16);
    }

    @Override
    public void setRadiusAndShadow(int radius, int shadowElevation) {
        setRadiusAndShadow(radius, shadowElevation, 0.5f);
    }

    @Override
    public void setRadiusAndShadow(int radius, int shadowElevation, float shadowAlpha) {
        setRadiusAndShadow(radius, mHideRadiusSide, shadowElevation, shadowAlpha);
    }

    @Override
    public void setRadiusAndShadow(int radius, @IBaseLayout.HideRadiusSide int hideRadiusSide, int shadowElevation, float shadowAlpha) {
        setRadiusAndShadow(radius, hideRadiusSide, shadowElevation, mShadowColor, shadowAlpha);
    }

    @Override
    public void setRadiusAndShadow(int radius, int hideRadiusSide, int shadowElevation, int shadowColor, float shadowAlpha) {
        final View owner = mOwner.get();
        if (owner == null) {
            return;
        }

        mRadius = radius;
        mHideRadiusSide = hideRadiusSide;

        if (mRadius > 0) {
            float[] mRadiusArray;
            if (hideRadiusSide == HIDE_RADIUS_SIDE_TOP) {
                mRadiusArray = new float[]{0, 0, 0, 0, mRadius, mRadius, mRadius, mRadius};
            } else if (hideRadiusSide == HIDE_RADIUS_SIDE_RIGHT) {
                mRadiusArray = new float[]{mRadius, mRadius, 0, 0, 0, 0, mRadius, mRadius};
            } else if (hideRadiusSide == HIDE_RADIUS_SIDE_BOTTOM) {
                mRadiusArray = new float[]{mRadius, mRadius, mRadius, mRadius, 0, 0, 0, 0};
            } else if (hideRadiusSide == HIDE_RADIUS_SIDE_LEFT) {
                mRadiusArray = new float[]{0, 0, mRadius, mRadius, mRadius, mRadius, 0, 0};
            } else {
                mRadiusArray = null;
            }
        }

        mShadowElevation = shadowElevation;
        mShadowAlpha = shadowAlpha;
        mShadowColor = shadowColor;
        if (useFeature()) {
            if (mShadowElevation == 0 || isRadiusWithSideHidden()) {
                owner.setElevation(0);
            } else {
                owner.setElevation(mShadowElevation);
            }

            setShadowColorInner(mShadowColor);

            owner.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                @TargetApi(21)
                public void getOutline(View view, Outline outline) {
                    int w = view.getWidth(), h = view.getHeight();
                    if (w == 0 || h == 0) {
                        return;
                    }
                    if (isRadiusWithSideHidden()) {
                        int left = 0, top = 0, right = w, bottom = h;
                        if (mHideRadiusSide == HIDE_RADIUS_SIDE_LEFT) {
                            left -= mRadius;
                        } else if (mHideRadiusSide == HIDE_RADIUS_SIDE_TOP) {
                            top -= mRadius;
                        } else if (mHideRadiusSide == HIDE_RADIUS_SIDE_RIGHT) {
                            right += mRadius;
                        } else if (mHideRadiusSide == HIDE_RADIUS_SIDE_BOTTOM) {
                            bottom += mRadius;
                        }
                        outline.setRoundRect(left, top,
                                right, bottom, mRadius);
                        return;
                    }

                    int top = 0, bottom = Math.max(top + 1, h - 0),
                            left = 0, right = w - 0;
                    if (mIsOutlineExcludePadding) {
                        left += view.getPaddingLeft();
                        top += view.getPaddingTop();
                        right = Math.max(left + 1, right - view.getPaddingEnd());
                        bottom = Math.max(top + 1, bottom - view.getPaddingBottom());
                    }

                    float shadowAlpha = mShadowAlpha;
                    if (mShadowElevation == 0) {
                        // outline.setAlpha will work even if shadowElevation == 0
                        shadowAlpha = 1f;
                    }

                    outline.setAlpha(shadowAlpha);

                    if (mRadius <= 0) {
                        outline.setRect(left, top,
                                right, bottom);
                    } else {
                        outline.setRoundRect(left, top,
                                right, bottom, mRadius);
                    }
                }
            });
            owner.setClipToOutline(mRadius > 0);

        }
        owner.invalidate();
    }

    /**
     * 有radius, 但是有一边不显示radius。
     *
     * @return
     */
    private boolean isRadiusWithSideHidden() {
        return mRadius > 0 && mHideRadiusSide != HIDE_RADIUS_SIDE_NONE;
    }

    public int handleMiniWidth(int widthMeasureSpec, int measuredWidth) {
        if (View.MeasureSpec.getMode(widthMeasureSpec) != View.MeasureSpec.EXACTLY
                && measuredWidth < mWidthMini) {
            return View.MeasureSpec.makeMeasureSpec(mWidthMini, View.MeasureSpec.EXACTLY);
        }
        return widthMeasureSpec;
    }

    public int handleMiniHeight(int heightMeasureSpec, int measuredHeight) {
        if (View.MeasureSpec.getMode(heightMeasureSpec) != View.MeasureSpec.EXACTLY
                && measuredHeight < mHeightMini) {
            return View.MeasureSpec.makeMeasureSpec(mHeightMini, View.MeasureSpec.EXACTLY);
        }
        return heightMeasureSpec;
    }

    public int getMeasuredWidthSpec(int widthMeasureSpec) {
        if (mWidthLimit > 0) {
            int size = View.MeasureSpec.getSize(widthMeasureSpec);
            if (size > mWidthLimit) {
                int mode = View.MeasureSpec.getMode(widthMeasureSpec);
                if (mode == View.MeasureSpec.AT_MOST) {
                    widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(mWidthLimit, View.MeasureSpec.AT_MOST);
                } else {
                    widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(mWidthLimit, View.MeasureSpec.EXACTLY);
                }

            }
        }
        return widthMeasureSpec;
    }

    public int getMeasuredHeightSpec(int heightMeasureSpec) {
        if (mHeightLimit > 0) {
            int size = View.MeasureSpec.getSize(heightMeasureSpec);
            if (size > mHeightLimit) {
                int mode = View.MeasureSpec.getMode(heightMeasureSpec);
                if (mode == View.MeasureSpec.AT_MOST) {
                    heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(mWidthLimit, View.MeasureSpec.AT_MOST);
                } else {
                    heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(mWidthLimit, View.MeasureSpec.EXACTLY);
                }
            }
        }
        return heightMeasureSpec;
    }

    private static boolean useFeature() {
        return Build.VERSION.SDK_INT >= 21;
    }
}

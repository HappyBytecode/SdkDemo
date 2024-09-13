package anda.travel.driver.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 默认分隔线实现类只支持布局管理器为 LinearLayoutManager
 */
public class DividerDecoration extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;

    //使用系统主题中的R.attr.listDivider作为Item间的分割线
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private Drawable mDivider;

    private int mOrientation;//布局方向，决定绘制水平分隔线还是竖直分隔线

    public DividerDecoration(Context context, int orientation) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException(
                    "Invalid orientation. It should be either HORIZONTAL or VERTICAL");
        }
        mOrientation = orientation;
    }

    /**
     * 一个app中分隔线不可能完全一样，你可以通过这个方法传递一个Drawable 对象来定制分隔线
     */
    public void setDrawable(@NonNull Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException("Drawable cannot be null.");
        }
        mDivider = drawable;
    }

    /**
     * 画分隔线
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() == null) {
            return;
        }
        if (mOrientation == VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    /**
     * 在LinearLayoutManager方向为Vertical时，画分隔线
     */
    public void drawVertical(Canvas canvas, RecyclerView parent) {
        final int left = parent.getPaddingLeft();//★分隔线的左边 = paddingLeft值
        final int right = parent.getWidth() - parent.getPaddingRight();//★分隔线的右边 = RecyclerView 宽度－paddingRight值
//分隔线不在RecyclerView的padding那一部分绘制

        final int childCount = parent.getChildCount();//★分隔线数量=item数量
        for (int i = 0; i < childCount; i++) {
            if (i == childCount - 1) {
                return;
            }
            final View child = parent.getChildAt(i);//确定是第几个item
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;//★分隔线的上边 = item的底部 + item根标签的bottomMargin值
            final int bottom = top + mDivider.getIntrinsicHeight();//★分隔线的下边 = 分隔线的上边 + 分隔线本身高度
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
    }

    /**
     * 在LinearLayoutManager方向为Horizontal时，画分隔线
     * 理解了上面drawVertical()方法这个方法也就理解了
     */
    public void drawHorizontal(Canvas canvas, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
    }

    /**
     * 获取Item偏移量
     * 此方法是为每个Item四周预留出空间，从而让分隔线的绘制在预留的空间内
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        if (mOrientation == VERTICAL) {//竖直方向的分隔线：item向下偏移一个分隔线的高度
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        } else {//水平方向的分隔线：item向右偏移一个分隔线的宽度
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        }
    }
}

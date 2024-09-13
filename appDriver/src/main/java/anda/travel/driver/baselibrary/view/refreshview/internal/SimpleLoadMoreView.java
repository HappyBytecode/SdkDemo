package anda.travel.driver.baselibrary.view.refreshview.internal;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import anda.travel.driver.baselibrary.utils.DisplayUtil;

public class SimpleLoadMoreView implements ILoadMoreView {

    private TextView mTextView;
    private boolean mHideLoadMoreText;

    public SimpleLoadMoreView(Context context) {
        mTextView = new TextView(context);
        mTextView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                ViewGroup.LayoutParams params = mTextView.getLayoutParams();
                params.height = DisplayUtil.dp2px(context, 48);
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                mTextView.setGravity(Gravity.CENTER);
                mTextView.setLayoutParams(params);
                mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {

            }
        });

    }

    @Override
    public View getLoadMoreView() {
        return mTextView;
    }

    @Override
    public void initView() {
        String str = mHideLoadMoreText ? "" : "上拉加载更多";
        mTextView.setText(str);
    }

    @Override
    public void onLoading() {
        String str = mHideLoadMoreText ? "" : "加载中...";
        mTextView.setText(str);
    }

    @Override
    public void loadCompleted() {
        String str = mHideLoadMoreText ? "" : "加载完成";
        mTextView.setText(str);
    }

    @Override
    public void hasNoMoreData() {
        String str = mHideLoadMoreText ? "" : "没有更多了~";
        mTextView.setText(str);
    }

    @Override
    public void setHideLoadMoreText(boolean hideLoadMoreText) {
        mHideLoadMoreText = hideLoadMoreText;
    }

}

package anda.travel.driver.widget.customRecyclerView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public final class FixedViewHolder extends RecyclerView.ViewHolder {

    public static final int VIEW_TYPE_HEADER = Integer.MIN_VALUE;
    public static final int VIEW_TYPE_FOOTER = Integer.MIN_VALUE + 1;

    @IntDef({VIEW_TYPE_HEADER, VIEW_TYPE_FOOTER})
    @Retention(RetentionPolicy.SOURCE)
    @interface ViewType {
    }

    static FixedViewHolder create(@NonNull Context context) {
        return new FixedViewHolder(new LinearLayout(context));
    }

    static FixedViewHolder assertType(RecyclerView.ViewHolder holder) {
        if (holder instanceof FixedViewHolder) {
            return (FixedViewHolder) holder;
        } else {
            throw new AssertionError("Impossible fixed view holder type.");
        }
    }

    private final LinearLayout viewContainer;

    private FixedViewHolder(@NonNull LinearLayout viewContainer) {
        super(viewContainer);
        this.viewContainer = viewContainer;
    }

    @NonNull
    LinearLayout getViewContainer() {
        return viewContainer;
    }

    private void adjustViewContainerLayoutParamsAndOrientation(@NonNull HeaderAndFooterRecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            GridLayoutManager.LayoutParams layoutParams;
            int orientation;
            if (viewContainer.getLayoutParams() instanceof GridLayoutManager.LayoutParams) {
                layoutParams = (GridLayoutManager.LayoutParams) viewContainer.getLayoutParams();
                if (gridLayoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    orientation = LinearLayout.VERTICAL;
                } else {
                    layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    orientation = LinearLayout.HORIZONTAL;
                }
            } else {
                if (gridLayoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
                    layoutParams = new GridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    orientation = LinearLayout.VERTICAL;
                } else {
                    layoutParams = new GridLayoutManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    orientation = LinearLayout.HORIZONTAL;
                }
            }
            viewContainer.setLayoutParams(layoutParams);
            viewContainer.setOrientation(orientation);
        } else if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            RecyclerView.LayoutParams layoutParams;
            int orientation;
            if (viewContainer.getLayoutParams() instanceof RecyclerView.LayoutParams) {
                layoutParams = (RecyclerView.LayoutParams) viewContainer.getLayoutParams();
                if (linearLayoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    orientation = LinearLayout.VERTICAL;
                } else {
                    layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    orientation = LinearLayout.HORIZONTAL;
                }
            } else {
                if (linearLayoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
                    layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    orientation = LinearLayout.VERTICAL;
                } else {
                    layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    orientation = LinearLayout.HORIZONTAL;
                }
            }
            viewContainer.setLayoutParams(layoutParams);
            viewContainer.setOrientation(orientation);
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            StaggeredGridLayoutManager.LayoutParams layoutParams;
            int orientation;
            if (viewContainer.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                layoutParams = (StaggeredGridLayoutManager.LayoutParams) viewContainer.getLayoutParams();
                if (staggeredGridLayoutManager.getOrientation() == StaggeredGridLayoutManager.VERTICAL) {
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    orientation = LinearLayout.VERTICAL;
                } else {
                    layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    orientation = LinearLayout.HORIZONTAL;
                }
            } else {
                if (staggeredGridLayoutManager.getOrientation() == StaggeredGridLayoutManager.VERTICAL) {
                    layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    orientation = LinearLayout.VERTICAL;
                } else {
                    layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    orientation = LinearLayout.HORIZONTAL;
                }
            }
            layoutParams.setFullSpan(true);
            viewContainer.setLayoutParams(layoutParams);
            viewContainer.setOrientation(orientation);
        }
    }

    void bind(@NonNull HeaderAndFooterRecyclerView recyclerView, @NonNull List<View> viewList) {
        viewContainer.removeAllViews();
        adjustViewContainerLayoutParamsAndOrientation(recyclerView);
        for (View view : viewList) {
            if (view.getParent() instanceof ViewGroup) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            viewContainer.addView(view);
        }
    }

    void bindWithUpdateInfo(@NonNull HeaderAndFooterRecyclerView recyclerView, @NonNull FixedViewUpdateInfo updateInfo) {
        adjustViewContainerLayoutParamsAndOrientation(recyclerView);
        switch (updateInfo.getAction()) {
            case FixedViewUpdateInfo.ACTION_ADD:
                if (updateInfo.getIndex() == null) {
                    viewContainer.addView(updateInfo.getView());
                } else {
                    viewContainer.addView(updateInfo.getView(), updateInfo.getIndex());
                }
                break;
            case FixedViewUpdateInfo.ACTION_REMOVE:
                if (updateInfo.getIndex() == null) {
                    viewContainer.removeView(updateInfo.getView());
                } else {
                    viewContainer.removeViewAt(updateInfo.getIndex());
                }
                break;
            default:
                throw new AssertionError("Impossible update info action.");
        }
    }

}

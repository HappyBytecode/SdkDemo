package anda.travel.driver.widget.customRecyclerView;

import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

final class FixedViewUpdateInfo {

    static final int ACTION_ADD = 0;
    static final int ACTION_REMOVE = 1;

    @IntDef({ACTION_ADD, ACTION_REMOVE})
    @Retention(RetentionPolicy.SOURCE)
    @interface Action {
    }

    @Action
    private final int action;

    @NonNull
    private final View view;

    @Nullable
    private final Integer index;

    FixedViewUpdateInfo(@Action int action, @NonNull View view, @Nullable Integer index) {
        this.action = action;
        this.view = view;
        this.index = index;
    }

    @Action
    int getAction() {
        return action;
    }

    @NonNull
    View getView() {
        return view;
    }

    @Nullable
    Integer getIndex() {
        return index;
    }

}

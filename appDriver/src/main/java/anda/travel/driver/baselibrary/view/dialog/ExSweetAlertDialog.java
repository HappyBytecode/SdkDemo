package anda.travel.driver.baselibrary.view.dialog;

import static anda.travel.driver.baselibrary.view.dialog.ExSweetAlertDialog.AlertDialogType.NORMAL_TYPE;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.AnimRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;

import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.List;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.view.SweetAlert.OptAnimationLoader;
import anda.travel.driver.baselibrary.view.SweetAlert.ProgressHelper;
import anda.travel.driver.baselibrary.view.SweetAlert.SuccessTickView;

public class ExSweetAlertDialog extends Dialog implements View.OnClickListener {
    private View mDialogView;
    private AnimationSet mModalInAnim;
    private AnimationSet mModalOutAnim;
    private Animation mOverlayOutAnim;
    private Animation mErrorInAnim;
    private AnimationSet mErrorXInAnim;
    private AnimationSet mSuccessLayoutAnimSet;
    private Animation mSuccessBowAnim;
    private TextView mTitleTextView;
    private TextView mContentTextView;
    private String mTitleText;
    private String mContentText;
    private boolean mShowCancel;
    private boolean mShowContent;
    private String mCancelText;
    private String mConfirmText;
    private AlertDialogType mAlertType;
    private FrameLayout mErrorFrame;
    private FrameLayout mSuccessFrame;
    private FrameLayout mProgressFrame;
    private SuccessTickView mSuccessTick;
    private ImageView mErrorX;
    private View mSuccessLeftMask;
    private View mSuccessRightMask;
    private Drawable mCustomImgDrawable;
    private ImageView mCustomImage;
    private Button mConfirmButton;
    private Button mCancelButton;
    private ProgressHelper mProgressHelper;
    private FrameLayout mWarningFrame;
    private OnSweetClickListener mCancelClickListener;
    private OnSweetClickListener mConfirmClickListener;
    private boolean mCloseFromCancel;

    private boolean mIsCustomView = false;
    private int mLayoutParamsWidth = DEFAULT_PARAMS_SIZE;
    private int mLayoutParamsHeight = DEFAULT_PARAMS_SIZE;
    private View mContentView;

    public enum AlertDialogType {
        NORMAL_TYPE,
        ERROR_TYPE,
        SUCCESS_TYPE,
        WARNING_TYPE,
        CUSTOM_IMAGE_TYPE,
        PROGRESS_TYPE,
    }

    private static final int DEFAULT_PARAMS_SIZE = Integer.MIN_VALUE;

    public interface OnSweetClickListener {
        void onClick(ExSweetAlertDialog sweetAlertDialog);
    }

    public ExSweetAlertDialog(Context context) {
        this(context, NORMAL_TYPE);
    }

    public ExSweetAlertDialog(Context context, AlertDialogType alertType) {
        super(context, R.style.alert_dialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        mProgressHelper = new ProgressHelper(context);
        mAlertType = alertType;
        mErrorInAnim = OptAnimationLoader.loadAnimation(getContext(), R.anim.error_frame_in);
        mErrorXInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.error_x_in);
        // 2.3.x system don't support alpha-animation on layer-list drawable
        // remove it from animation set
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            List<Animation> childAnims = mErrorXInAnim.getAnimations();
            int idx = 0;
            for (; idx < childAnims.size(); idx++) {
                if (childAnims.get(idx) instanceof AlphaAnimation) {
                    break;
                }
            }
            if (idx < childAnims.size()) {
                childAnims.remove(idx);
            }
        }
        mSuccessBowAnim = OptAnimationLoader.loadAnimation(getContext(), R.anim.success_bow_roate);
        mSuccessLayoutAnimSet = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim
                .success_mask_layout);
        mModalInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.modal_in);
        mModalOutAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.modal_out);
        mModalOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDialogView.setVisibility(View.GONE);
                mDialogView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCloseFromCancel) {
                            ExSweetAlertDialog.super.cancel();
                        } else {
                            ExSweetAlertDialog.super.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        // dialog overlay fade out
        mOverlayOutAnim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                WindowManager.LayoutParams wlp = getWindow().getAttributes();
                wlp.alpha = 1 - interpolatedTime;
                getWindow().setAttributes(wlp);
            }
        };
        mOverlayOutAnim.setDuration(120);
    }

    public ExSweetAlertDialog(Context context, @LayoutRes int layoutId) {
        this(context, NORMAL_TYPE);
        mIsCustomView = true;
        mContentView = LayoutInflater.from(getContext()).inflate(layoutId, null);
    }

    public View getViewById(@IdRes int idRes) {
        return findViewById(idRes);
    }

    @Override
    public View findViewById(int id) {
        if (mIsCustomView) {
            return mContentView.findViewById(id);
        } else {
            return super.findViewById(id);
        }
    }

    public ExSweetAlertDialog setText(@IdRes int idRes, String text) {
        ((TextView) findViewById(idRes)).setText(text);
        return this;
    }

    public ExSweetAlertDialog setListener(@IdRes int idRes, OnSweetClickListener onSweetClickListener) {
        if (onSweetClickListener == null) {
            findViewById(idRes).setOnClickListener(null);
        } else {
            findViewById(idRes).setOnClickListener(v -> onSweetClickListener.onClick(ExSweetAlertDialog.this));
        }
        return this;
    }

    public ExSweetAlertDialog setWidth(int width) {
        mLayoutParamsWidth = width;
        return this;
    }

    public ExSweetAlertDialog setHeight(int height) {
        mLayoutParamsHeight = height;
        return this;
    }

    public ExSweetAlertDialog setCancelable1(boolean cancelable) {
        setCancelable(cancelable);
        return this;
    }

    public ExSweetAlertDialog setCanceledOnTouchOutside1(boolean cancelable) {
        setCanceledOnTouchOutside(cancelable);
        return this;
    }

    public ExSweetAlertDialog setAnimIn(@AnimRes int animInSet) {
        mModalInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), animInSet);
        return this;
    }

    public ExSweetAlertDialog setAnimOut(@AnimRes int animOutSet) {
        mModalOutAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), animOutSet);
        mModalOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDialogView.setVisibility(View.GONE);
                mDialogView.post(() -> {
                    if (mCloseFromCancel) {
                        ExSweetAlertDialog.super.cancel();
                    } else {
                        ExSweetAlertDialog.super.dismiss();
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return this;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mIsCustomView) {
            setContentView(mContentView);
            mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);
            return;
        }
        setContentView(R.layout.hxyc_alert_dialog);

        mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);
        mTitleTextView = (TextView) findViewById(R.id.title_text);
        mContentTextView = (TextView) findViewById(R.id.content_text);
        mErrorFrame = (FrameLayout) findViewById(R.id.error_frame);
        mErrorX = (ImageView) mErrorFrame.findViewById(R.id.error_x);
        mSuccessFrame = (FrameLayout) findViewById(R.id.success_frame);
        mProgressFrame = (FrameLayout) findViewById(R.id.progress_dialog);
        mSuccessTick = (SuccessTickView) mSuccessFrame.findViewById(R.id.success_tick);
        mSuccessLeftMask = mSuccessFrame.findViewById(R.id.mask_left);
        mSuccessRightMask = mSuccessFrame.findViewById(R.id.mask_right);
        mCustomImage = (ImageView) findViewById(R.id.custom_image);
        mWarningFrame = (FrameLayout) findViewById(R.id.warning_frame);
        mConfirmButton = (Button) findViewById(R.id.confirm_button);
        mCancelButton = (Button) findViewById(R.id.cancel_button);
        mProgressHelper.setProgressWheel((ProgressWheel) findViewById(R.id.progressWheel));
        mConfirmButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);

        setTitleText(mTitleText);
        setContentText(mContentText);
        setCancelText(mCancelText);
        setConfirmText(mConfirmText);
        changeAlertType(mAlertType, true);
    }

    private void restore() {
        mCustomImage.setVisibility(View.GONE);
        mErrorFrame.setVisibility(View.GONE);
        mSuccessFrame.setVisibility(View.GONE);
        mWarningFrame.setVisibility(View.GONE);
        mProgressFrame.setVisibility(View.GONE);
        mConfirmButton.setVisibility(View.VISIBLE);

        mConfirmButton.setBackgroundResource(R.drawable.blue_button_background);
        mErrorFrame.clearAnimation();
        mErrorX.clearAnimation();
        mSuccessTick.clearAnimation();
        mSuccessLeftMask.clearAnimation();
        mSuccessRightMask.clearAnimation();
    }

    private void playAnimation() {
        if (mAlertType == AlertDialogType.ERROR_TYPE) {
            mErrorFrame.startAnimation(mErrorInAnim);
            mErrorX.startAnimation(mErrorXInAnim);
        } else if (mAlertType == AlertDialogType.SUCCESS_TYPE) {
            mSuccessTick.startTickAnim();
            mSuccessRightMask.startAnimation(mSuccessBowAnim);
        }
    }

    private void changeAlertType(AlertDialogType alertType, boolean fromCreate) {
        mAlertType = alertType;
        // call after created views
        if (mDialogView != null) {
            if (!fromCreate) {
                // restore all of views state before switching alert type
                restore();
            }
            switch (mAlertType) {
                case ERROR_TYPE:
                    mErrorFrame.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS_TYPE:
                    mSuccessFrame.setVisibility(View.VISIBLE);
                    // initial rotate layout of success mask
                    mSuccessLeftMask.startAnimation(mSuccessLayoutAnimSet.getAnimations().get(0));
                    mSuccessRightMask.startAnimation(mSuccessLayoutAnimSet.getAnimations().get(1));
                    break;
                case WARNING_TYPE:
                    mConfirmButton.setBackgroundResource(R.drawable.red_button_background);
                    mWarningFrame.setVisibility(View.VISIBLE);
                    break;
                case CUSTOM_IMAGE_TYPE:
                    setCustomImage(mCustomImgDrawable);
                    break;
                case PROGRESS_TYPE:
                    mProgressFrame.setVisibility(View.VISIBLE);
                    mConfirmButton.setVisibility(View.GONE);
                    break;
            }
            if (!fromCreate) {
                playAnimation();
            }
        }
    }

    public AlertDialogType getAlerType() {
        return mAlertType;
    }

    public void changeAlertType(AlertDialogType alertType) {
        changeAlertType(alertType, false);
    }

    public String getTitleText() {
        return mTitleText;
    }

    public ExSweetAlertDialog setTitleText(String text) {
        mTitleText = text;
        if (mTitleTextView != null && mTitleText != null) {
            mTitleTextView.setText(mTitleText);
        }
        return this;
    }

    public ExSweetAlertDialog setCustomImage(Drawable drawable) {
        mCustomImgDrawable = drawable;
        if (mCustomImage != null && mCustomImgDrawable != null) {
            mCustomImage.setVisibility(View.VISIBLE);
            mCustomImage.setImageDrawable(mCustomImgDrawable);
        }
        return this;
    }

    public ExSweetAlertDialog setCustomImage(int resourceId) {
        return setCustomImage(getContext().getResources().getDrawable(resourceId));
    }

    public String getContentText() {
        return mContentText;
    }

    public ExSweetAlertDialog setContentText(String text) {
        mContentText = text;
        if (mContentTextView != null && mContentText != null) {
            showContentText(true);
            mContentTextView.setText(mContentText);
        }
        return this;
    }

    public boolean isShowCancelButton() {
        return mShowCancel;
    }

    public ExSweetAlertDialog showCancelButton(boolean isShow) {
        mShowCancel = isShow;
        if (mCancelButton != null) {
            mCancelButton.setVisibility(mShowCancel ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public boolean isShowContentText() {
        return mShowContent;
    }

    public ExSweetAlertDialog showContentText(boolean isShow) {
        mShowContent = isShow;
        if (mContentTextView != null) {
            mContentTextView.setVisibility(mShowContent ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public String getCancelText() {
        return mCancelText;
    }

    public ExSweetAlertDialog setCancelText(String text) {
        mCancelText = text;
        if (mCancelButton != null && mCancelText != null) {
            showCancelButton(true);
            mCancelButton.setText(mCancelText);
        }
        return this;
    }

    public String getConfirmText() {
        return mConfirmText;
    }

    public ExSweetAlertDialog setConfirmText(String text) {
        mConfirmText = text;
        if (mConfirmButton != null && mConfirmText != null) {
            mConfirmButton.setText(mConfirmText);
        }
        return this;
    }

    public ExSweetAlertDialog setCancelClickListener(OnSweetClickListener listener) {
        mCancelClickListener = listener;
        return this;
    }

    public ExSweetAlertDialog setConfirmClickListener(OnSweetClickListener listener) {
        mConfirmClickListener = listener;
        return this;
    }

    protected void onStart() {
        mDialogView.startAnimation(mModalInAnim);
        playAnimation();
    }

    /**
     * The real Dialog.cancel() will be invoked async-ly after the animation finishes.
     */
    @Override
    public void cancel() {
        dismissWithAnimation(true);
    }

    /**
     * The real Dialog.dismiss() will be invoked async-ly after the animation finishes.
     */
    public void dismissWithAnimation() {
        dismissWithAnimation(false);
    }

    private void dismissWithAnimation(boolean fromCancel) {
        mCloseFromCancel = fromCancel;
        if (!mIsCustomView) {
            mConfirmButton.startAnimation(mOverlayOutAnim);
        }
        mDialogView.startAnimation(mModalOutAnim);
    }

    @Override
    public void onClick(View v) {
        if (!mIsCustomView && v.getId() == R.id.cancel_button) {
            if (mCancelClickListener != null) {
                mCancelClickListener.onClick(ExSweetAlertDialog.this);
            } else {
                dismissWithAnimation();
            }
        } else if (!mIsCustomView && v.getId() == R.id.confirm_button) {
            if (mConfirmClickListener != null) {
                mConfirmClickListener.onClick(ExSweetAlertDialog.this);
            } else {
                dismissWithAnimation();
            }
        }
    }

    @Override
    public void show() {
        super.show();
        if (mLayoutParamsWidth != DEFAULT_PARAMS_SIZE || mLayoutParamsHeight != DEFAULT_PARAMS_SIZE) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.width = mLayoutParamsWidth == DEFAULT_PARAMS_SIZE ? lp.width : mLayoutParamsWidth;
            lp.height = mLayoutParamsHeight == DEFAULT_PARAMS_SIZE ? lp.height : mLayoutParamsHeight;
            getWindow().setAttributes(lp);
        }
    }

    public ProgressHelper getProgressHelper() {
        return mProgressHelper;
    }
}
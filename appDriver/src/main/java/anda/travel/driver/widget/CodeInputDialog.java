package anda.travel.driver.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.MessageFormat;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.utils.SpannableWrap;
import anda.travel.driver.widget.codeinput.VerificationCodeInputView;

public class CodeInputDialog extends Dialog {
    private ImageView iv_close;
    private VerificationCodeInputView et_code_input;
    private TextView tv_click_get_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.height = WindowManager.LayoutParams.WRAP_CONTENT;
        p.width = (int) (d.getWidth() * 0.85);
        getWindow().setAttributes(p);
    }

    public CodeInputDialog(Context context) {
        super(context, R.style.alert_dialog);
        setContentView(R.layout.hxyc_dialog_code_input);
        iv_close = findViewById(R.id.iv_close);
        et_code_input = findViewById(R.id.et_code_input);
        tv_click_get_code = findViewById(R.id.tv_click_get_code);
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.start();
        }
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    public CodeInputDialog setCloseListener(OnClickListener closeListener) {
        iv_close.setOnClickListener(v -> {
            closeDialog();
            closeListener.onclick();
        });
        return this;
    }

    public CodeInputDialog setCodeListener(OnClickListener codeListener) {
        tv_click_get_code.setOnClickListener(v -> codeListener.onclick());
        return this;
    }

    public CodeInputDialog setOnInputListener(VerificationCodeInputView.OnInputListener onInputListener) {
        et_code_input.setOnInputListener(new VerificationCodeInputView.OnInputListener() {
            @Override
            public void onComplete(String code) {
                onInputListener.onComplete(code);
            }

            @Override
            public void onInput() {

            }
        });
        return this;
    }

    public void setTimerStart() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.start();
        }
    }

    public void closeDialog() {
        et_code_input.hideSoftInput();
        if (mTimer != null) {
            mTimer.cancel();
        }
        dismiss();
    }

    private final CountDownTimer mTimer = new CountDownTimer(60 * 1000, 1000) {
        @Override
        public void onFinish() {
            tv_click_get_code.setText("点击重发");
            tv_click_get_code.setEnabled(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            SpannableWrap.setText(MessageFormat.format("{0}秒", (int) (millisUntilFinished / 1000)))
                    .textColor(getContext().getResources().getColor(R.color.colorPrimaryDark))
                    .append("后可重发")
                    .textColor(getContext().getResources().getColor(R.color.journal_gray_light))
                    .into(tv_click_get_code);
            tv_click_get_code.setEnabled(false);
        }
    }.start();

    public interface OnClickListener {
        void onclick();
    }

}




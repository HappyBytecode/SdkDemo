package anda.travel.driver.baselibrary.view.dialog;

import android.app.Dialog;
import android.content.Context;

import anda.travel.driver.R;

public class PermissionTipDialog extends Dialog {

    public PermissionTipDialog(Context context, OnClickListener closeListener) {
        super(context, R.style.alert_dialog);
        setContentView(R.layout.hxyc_dialog_permission);
        findViewById(R.id.tv_call).setOnClickListener(v -> {
            dismiss();
            closeListener.onclick();
        });
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    public interface OnClickListener {
        void onclick();
    }
}




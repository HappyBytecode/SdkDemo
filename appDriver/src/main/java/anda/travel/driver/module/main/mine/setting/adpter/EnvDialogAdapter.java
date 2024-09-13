package anda.travel.driver.module.main.mine.setting.adpter;

import android.content.Context;

import java.util.ArrayList;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.adapter.SuperAdapter;
import anda.travel.driver.baselibrary.adapter.internal.SuperViewHolder;

public class EnvDialogAdapter extends SuperAdapter<String> {

    public EnvDialogAdapter(Context context) {
        super(context, new ArrayList<>(), R.layout.hxyc_item_env_dialog);
    }

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int position, String item) {
        holder.setText(R.id.tv_env, item);
    }
}

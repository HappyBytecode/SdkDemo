package anda.travel.driver.module.main.mine.help;

import android.content.Context;

import java.util.ArrayList;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.adapter.SuperAdapter;
import anda.travel.driver.baselibrary.adapter.internal.SuperViewHolder;
import anda.travel.driver.data.entity.ProblemEntity;

class FaqAdapter extends SuperAdapter<ProblemEntity> {

    public FaqAdapter(Context context) {
        super(context, new ArrayList<>(), R.layout.hxyc_item_faq);
    }

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int position, ProblemEntity item) {
        holder.setText(R.id.tv_title, item.getTitle());
    }
}

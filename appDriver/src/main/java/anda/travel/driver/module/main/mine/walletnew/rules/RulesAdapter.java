package anda.travel.driver.module.main.mine.walletnew.rules;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.adapter.SuperAdapter;
import anda.travel.driver.baselibrary.adapter.internal.SuperViewHolder;
import anda.travel.driver.data.entity.WithdrawRuleEntity;

class RulesAdapter extends SuperAdapter<WithdrawRuleEntity> {

    private int mPosition;

    public RulesAdapter(Context context) {
        super(context, new ArrayList<>(), R.layout.hxyc_item_rules);
    }

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int position, WithdrawRuleEntity item) {
        TextView rule_title = holder.getView(R.id.rule_title);
        TextView rule_content = holder.getView(R.id.rule_content);
        View rule_line = holder.getView(R.id.rule_line);
        rule_title.setText(
                android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N ?
                        Html.fromHtml(item.getTitle(), Html.FROM_HTML_MODE_LEGACY)
                        : Html.fromHtml(item.getTitle()));
        rule_content.setText(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N ?
                Html.fromHtml(item.getContent(), Html.FROM_HTML_MODE_LEGACY)
                : Html.fromHtml(item.getContent()));
        if ((mPosition - 1) == position) {
            rule_line.setVisibility(View.GONE);
        }
    }

    public void setPosition(int mPosition) {
        this.mPosition = mPosition;
    }
}

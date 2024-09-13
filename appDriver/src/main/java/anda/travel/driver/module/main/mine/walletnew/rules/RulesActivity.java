package anda.travel.driver.module.main.mine.walletnew.rules;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.baselibrary.view.HeadView;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.data.entity.WithdrawRuleEntity;
import anda.travel.driver.module.main.mine.walletnew.rules.dagger.DaggerRulesActivityComponent;
import anda.travel.driver.module.main.mine.walletnew.rules.dagger.RulesActivityModule;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 规则说明
 */
@SuppressLint("NonConstantResourceId")
public class RulesActivity extends BaseActivity implements RulesContract.View {

    @Inject
    RulesPresenter mPresenter;

    @BindView(R2.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R2.id.head_view)
    HeadView headView;

    private RulesAdapter mAdapter;
    private boolean isBillRule; // false余额提现规则说明 true账单规则说明

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_rules);
        ButterKnife.bind(this);
        DaggerRulesActivityComponent.builder()
                .appComponent(HxClientManager.getAppComponent())
                .rulesActivityModule(new RulesActivityModule(this))
                .build().inject(this);
        isBillRule = getIntent().getBooleanExtra(IConstants.IS_BILL_RULE, false);
        mAdapter = new RulesAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isBillRule) {
            headView.setTitle("司机账单规则");
            mPresenter.reqBillRules();
        } else {
            mPresenter.subscribe();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.unsubscribe();
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void showRules(List<WithdrawRuleEntity> list) {
        if (null != list && !list.isEmpty()) {
            mAdapter.setPosition(list.size());
            mAdapter.setAll(list);
        }
    }

}

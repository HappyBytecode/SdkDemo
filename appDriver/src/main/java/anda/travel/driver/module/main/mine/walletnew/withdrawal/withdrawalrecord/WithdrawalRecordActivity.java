package anda.travel.driver.module.main.mine.walletnew.withdrawal.withdrawalrecord;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.baselibrary.view.refreshview.ExRefreshView;
import anda.travel.driver.baselibrary.view.refreshview.RefreshViewListener;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.module.main.mine.walletnew.withdrawal.withdrawalrecord.dagger.DaggerWithdrawalRecordComponent;
import anda.travel.driver.module.main.mine.walletnew.withdrawal.withdrawalrecord.dagger.WithdrawalRecordModule;
import anda.travel.driver.module.vo.WithdrawaleRecordVO;
import anda.travel.driver.util.Navigate;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 提现记录
 */
public class WithdrawalRecordActivity extends BaseActivity implements WithdrawalRecordContract.View, RefreshViewListener {
    @BindView(R2.id.ex_refresh_view)
    ExRefreshView ex_refresh_view;

    private int nowPage = 1;
    private final int REFRESH = 0;
    private final int LOAD_MORE = 1;
    private int MODE = REFRESH;
    private WithdrawalRecordAdapter adapter;

    @Inject
    WithdrawalRecordPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_withdrawale_record);
        ButterKnife.bind(this);

        DaggerWithdrawalRecordComponent.builder()
                .appComponent(HxClientManager.getAppComponent())
                .withdrawalRecordModule(new WithdrawalRecordModule(this))
                .build().inject(this);
        initView();
        mPresenter.reqWithdrawalRecord(nowPage);
    }

    private void initView() {
        ex_refresh_view.setRefreshListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ex_refresh_view.setLayoutManager(linearLayoutManager);
        adapter = new WithdrawalRecordAdapter(this);
        ex_refresh_view.setAdapter(adapter);
        adapter.setOnItemClickListener((position, view, item) -> Navigate.openWithdrawalDetail(WithdrawalRecordActivity.this, item.cashUuid));
    }

    @Override
    public void showWithdrawalRecord(List<WithdrawaleRecordVO> vos) {
        ex_refresh_view.setRefreshing(false);
        if (vos != null && vos.size() > 0) {
            if (MODE == REFRESH) {
                adapter.clear();
            }
            adapter.addAll(vos);
        } else {
            ex_refresh_view.hasNoMoreData(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void onRefresh() {
        MODE = REFRESH;
        nowPage = 1;
        mPresenter.reqWithdrawalRecord(nowPage);
    }

    @Override
    public void onLoadMore() {
        MODE = LOAD_MORE;
        nowPage++;
        mPresenter.reqWithdrawalRecord(nowPage);
    }
}

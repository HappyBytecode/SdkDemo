package anda.travel.driver.module.order.cancel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.view.HeadView;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.data.entity.CancelReasonEntity;
import anda.travel.driver.event.OrderEvent;
import anda.travel.driver.module.order.cancel.dagger.DaggerOrderCancelComponent;
import anda.travel.driver.module.order.cancel.dagger.OrderCancelModule;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 功能描述：
 */
public class OrderCancelActivity extends BaseActivity implements OrderCancelContract.View {

    @Inject
    OrderCancelPresenter mPresenter;
    private OrderCancelAdapter mAdapter;
    @BindView(R2.id.head_view)
    HeadView mHeadView;
    @BindView(R2.id.recycler_view)
    RecyclerView mRecyclerView;

    public static void actionStart(Context context, String orderUuid, int status) {
        Intent intent = new Intent(context, OrderCancelActivity.class);
        intent.putExtra(IConstants.ORDER_UUID, orderUuid);
        intent.putExtra(IConstants.ORDER_STATUS, status);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_order_cancel);
        ButterKnife.bind(this);
        DaggerOrderCancelComponent.builder().appComponent(getAppComponent())
                .orderCancelModule(new OrderCancelModule(this)).build().inject(this);
        mPresenter.setOrderUuid(getIntent().getStringExtra(IConstants.ORDER_UUID),
                getIntent().getIntExtra(IConstants.ORDER_STATUS, 0));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        View bottomView = LayoutInflater.from(this).inflate(R.layout.hxyc_layout_btn_view, mRecyclerView, false);
        bottomView.findViewById(R.id.tv_submit).setOnClickListener(v -> {
            mPresenter.submitCancel(mAdapter.getContent()); //取消订单
        });
        mAdapter = new OrderCancelAdapter(this, R.layout.hxyc_item_order_cancel);
        mAdapter.addFooterView(bottomView);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(R.id.tv_content, (position, view, item) -> {
            mAdapter.select(position); //刷新显示
        });

        mPresenter.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
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
    public void showCancelMsg(List<CancelReasonEntity> entities) {
        mAdapter.setAll(entities); //刷新显示
    }

    @Override
    public void cancelSuccess(String orderUuid) {
        toast("订单已取消");
        new OrderEvent(OrderEvent.LOCAL_CANCEL, orderUuid).post(); //发送消息
        finish(); //关闭界面
    }
}

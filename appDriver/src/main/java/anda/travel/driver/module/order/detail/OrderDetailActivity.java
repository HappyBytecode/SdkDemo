package anda.travel.driver.module.order.detail;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import anda.travel.driver.R;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.module.order.details.OrderDetailFragment;
import anda.travel.driver.module.vo.OrderVO;

/**
 * 功能描述：订单详情
 */
public class OrderDetailActivity extends BaseActivity {

    private OrderDetailFragment mOrderDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_order_detail);
        String orderUuid = getIntent().getStringExtra(IConstants.ORDER_UUID);
        boolean refresh = getIntent().getBooleanExtra(IConstants.REFRESH, false);
        OrderVO vo = (OrderVO) getIntent().getSerializableExtra(IConstants.ORDER_VO);
        if (mOrderDetailFragment == null)
            addFragment(R.id.order_container, OrderDetailFragment.Companion.newInstance(orderUuid, vo, refresh));
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof OrderDetailFragment) {
            mOrderDetailFragment = (OrderDetailFragment) fragment;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

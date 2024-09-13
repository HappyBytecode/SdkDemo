package anda.travel.driver.module.order.begin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import anda.travel.driver.R;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.module.vo.OrderVO;
import butterknife.ButterKnife;

/**
 * (预约单) 未开始
 */
public class OrderBeginActivity extends BaseActivity {

    private OrderBeginFragment mOrderBeginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_order_begin);
        ButterKnife.bind(this);
        if (mOrderBeginFragment == null)
            addFragment(R.id.order_container, OrderBeginFragment
                    .newInstance(getIntent().getStringExtra(IConstants.ORDER_UUID),
                            (OrderVO) getIntent().getSerializableExtra(IConstants.ORDER_VO)));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof OrderBeginFragment) {
            mOrderBeginFragment = (OrderBeginFragment) fragment;
        }
    }
}

package anda.travel.driver.module.order.ongoing;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;

import anda.travel.driver.R;
import anda.travel.driver.common.BaseActivityWithoutIconics;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.module.vo.OrderVO;
import anda.travel.driver.util.OrderManager;

/**
 * 功能描述：
 */
public class OrderOngoingActivity extends BaseActivityWithoutIconics {

    private OrderOngoingFragment mOrderOngoingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 20170802：保持屏幕长亮
        setContentView(R.layout.hxyc_activity_order_ongoing);

        String orderUuid = getIntent().getStringExtra(IConstants.ORDER_UUID);
        OrderVO vo = (OrderVO) getIntent().getSerializableExtra(IConstants.ORDER_VO);
        boolean needReport = getIntent().getBooleanExtra(IConstants.REPORT, false);
        if (mOrderOngoingFragment == null)
            addFragment(R.id.order_container, OrderOngoingFragment.newInstance(orderUuid, vo, needReport));

        bright(); //持有WackLock
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelBright(); //释放WackLock
        OrderManager.instance().releaseData();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof OrderOngoingFragment) {
            mOrderOngoingFragment = (OrderOngoingFragment) fragment;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed(); //不处理物理返回键
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mOrderOngoingFragment.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mOrderOngoingFragment.onRestoreInstanceState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String orderUuid = getIntent().getStringExtra(IConstants.ORDER_UUID);
        OrderVO vo = (OrderVO) getIntent().getSerializableExtra(IConstants.ORDER_VO);
        boolean needReport = getIntent().getBooleanExtra(IConstants.REPORT, false);
        addFragment(R.id.order_container, OrderOngoingFragment.newInstance(orderUuid, vo, needReport));
    }
}

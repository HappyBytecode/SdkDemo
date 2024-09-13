package anda.travel.driver.module.order.popup;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import anda.travel.driver.R;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.module.vo.OrderVO;

/**
 * 功能描述：订单弹窗
 */
public class OrderPopupActivity extends BaseActivity {

    private OrderPopupFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_order_popup);
        String orderUuid = getIntent().getStringExtra(IConstants.ORDER_UUID);
        OrderVO vo = (OrderVO) getIntent().getSerializableExtra(IConstants.ORDER_VO);
        if (mFragment == null) {
            OrderPopupFragment popupFragment = new OrderPopupFragment();
            Bundle bundle = new Bundle();
            bundle.putString(IConstants.ORDER_UUID, orderUuid);
            bundle.putSerializable(IConstants.ORDER_VO, vo);
            popupFragment.setArguments(bundle);
            addFragment(R.id.fragment_container, popupFragment);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(10);
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof OrderPopupFragment) {
            mFragment = (OrderPopupFragment) fragment;
        }
    }

    @Override
    public void onBackPressed() {
        if (mFragment != null) {
            mFragment.closeActivityByRefuse(true);
            return;
        }
        super.onBackPressed();
    }
}

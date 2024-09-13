package anda.travel.driver.module.dispatch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;

import anda.travel.driver.R;
import anda.travel.driver.common.BaseActivityWithoutIconics;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.module.amap.ANavigateFragment;
import anda.travel.driver.module.vo.DispatchVO;

public class DispatchActivity extends BaseActivityWithoutIconics {

    public static void actionStart(Context context, DispatchVO vo) {
        Intent intent = new Intent(context, DispatchActivity.class);
        intent.putExtra(IConstants.PARAMS, vo);
        context.startActivity(intent);
    }

    private DispatchFragment mDispatchFragment;
    private ANavigateFragment mANavigateFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 20170802：保持屏幕长亮

        setContentView(R.layout.hxyc_activity_dispatch);
        if (mDispatchFragment == null)
            addFragment(R.id.order_container, DispatchFragment.newInstance((DispatchVO) getIntent().getSerializableExtra(IConstants.PARAMS)));
        if (mANavigateFragment == null)
            addFragment(R.id.map_container, ANavigateFragment.newInstance(true));

        bright(); //持有WackLock
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelBright(); //释放WackLock
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof DispatchFragment) {
            mDispatchFragment = (DispatchFragment) fragment;
        } else if (fragment instanceof ANavigateFragment) {
            mANavigateFragment = (ANavigateFragment) fragment;
        }
    }
}

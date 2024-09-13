package anda.travel.driver.module.main.mine.message;

import android.os.Bundle;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.event.MessageEvent;
import anda.travel.driver.widget.EnhanceTabLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 消息中心
 */
public class MessageActivity extends BaseActivity {

    @BindView(R2.id.vp_message)
    ViewPager mVpMessage;
    @BindView(R2.id.tab_message)
    EnhanceTabLayout mTabMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        setContentView(R.layout.hxyc_activity_message);
        ButterKnife.bind(this);
        mVpMessage.setAdapter(new MessagePagerAdapter(getSupportFragmentManager(), this, false));
        mVpMessage.setCurrentItem(0);
        mTabMessage.setVisibility(View.GONE);
        HxClientManager.getAppComponent().inject(this); //依赖注入
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event) {
        if (event.type == MessageEvent.SYS_MSG) {
            mTabMessage.setMsgTotal((int) event.obj1, getString(R.string.message_service));
        }
    }
}

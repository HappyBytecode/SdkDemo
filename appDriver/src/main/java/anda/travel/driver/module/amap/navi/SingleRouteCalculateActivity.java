package anda.travel.driver.module.amap.navi;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.ParallelRoadListener;
import com.amap.api.navi.TTSPlayListener;
import com.amap.api.navi.enums.AMapNaviParallelRoadStatus;
import com.amap.api.navi.enums.BroadcastMode;
import com.amap.api.navi.model.AMapTrafficStatus;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.view.DriveWayView;
import com.amap.api.navi.view.NextTurnTipView;
import com.amap.api.navi.view.TrafficProgressBar;
import com.kyleduo.switchbutton.SwitchButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.utils.NaviUtil;
import anda.travel.driver.baselibrary.utils.NumberUtil;
import anda.travel.driver.baselibrary.utils.SP;
import anda.travel.driver.baselibrary.utils.SpannableWrap;
import anda.travel.driver.common.AppManager;
import anda.travel.driver.common.BaseActivityWithoutIconics;
import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.data.user.local.UserLocalSource;
import anda.travel.driver.event.DispatchEvent;
import anda.travel.driver.event.MapEvent;
import anda.travel.driver.event.OrderEvent;
import anda.travel.driver.event.SlideViewEvent;
import anda.travel.driver.util.CommonUtils;
import anda.travel.driver.util.FontUtils;
import anda.travel.driver.util.SpeechUtil;
import anda.travel.driver.widget.slide.SlideView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 独立的导航页
 */
public class SingleRouteCalculateActivity extends BaseActivityWithoutIconics implements AMapNaviViewListener, ParallelRoadListener {

    @BindView(R2.id.myDriveWayView)
    DriveWayView mDriveWayView;
    @BindView(R2.id.mNextTurnTipView)
    NextTurnTipView mNextTurnTipView;
    @BindView(R2.id.text_next_road_distance)
    TextView mTvNextRoadDistance;
    @BindView(R2.id.text_next_road_name)
    TextView mTvNextRoadName;
    @BindView(R2.id.myTrafficBar)
    TrafficProgressBar mTrafficBarView;
    @BindView(R2.id.tv_trip_price)
    TextView mTvTripPrice;
    @BindView(R2.id.tv_trip_distance)
    TextView mTvTripDistance;
    @BindView(R2.id.tv_trip_duration)
    TextView mTvTripDuration;
    @BindView(R2.id.slide_view)
    SlideView mSlideView;
    @BindView(R2.id.change_road)
    TextView mChangeRoad;
    @BindView(R2.id.navi_view)
    AMapNaviView mAMapNaviView;
    @BindView(R2.id.sb_voice)
    SwitchButton mSwitchModeBtn;

    private AMapNavi mAMapNavi;
    private int mParallelType = 0;

    public static void actionStart(Context context, String status) {
        Intent intent = new Intent(context, SingleRouteCalculateActivity.class);
        intent.putExtra(IConstants.SLIDE_VIEW_TEXT, status);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HxClientManager.getAppComponent().inject(this); //依赖注入
        setContentView(R.layout.hxyc_activity_navigate);
        ButterKnife.bind(this);
        getExtra(); //处理传递过来的数据

        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        mAMapNavi.addParallelRoadListener(this);
        customNavUi();

        mAMapNaviView.setAMapNaviViewListener(this);
        mAMapNaviView.getMap().getUiSettings().setLogoBottomMargin((int) CommonUtils.dpToPixel(300, this));
        //20180627 长连接订单操作（取消改派等）通知导航页关闭
        EventBus.getDefault().register(this); //添加监听

        mAMapNavi.setUseInnerVoice(true);
        mAMapNavi.setBroadcastMode(BroadcastMode.CONCISE);
        mAMapNavi.startSpeak();

        mSwitchModeBtn.setCheckedImmediatelyNoEvent(SP.getInstance(getApplicationContext()).getInt(UserLocalSource.NAV_VOICE_MODE) == 0);
        mSwitchModeBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                SP.getInstance(getApplicationContext()).putInt(UserLocalSource.NAV_VOICE_MODE, 0);
                mAMapNavi.setBroadcastMode(BroadcastMode.CONCISE);
            } else {
                SP.getInstance(getApplicationContext()).putInt(UserLocalSource.NAV_VOICE_MODE, 1);
                mAMapNavi.setBroadcastMode(BroadcastMode.DETAIL);
            }
        });
        mSlideView.setOnSlideListener(() -> { //滑动到最右，将触发状态改变
            EventBus.getDefault().post(new SlideViewEvent());
            finish();
        });

        Typeface fontType = FontUtils.getFontTypeFace(this);
        mTvTripPrice.setTypeface(fontType);
        mTvTripDuration.setTypeface(fontType);
        mTvTripDistance.setTypeface(fontType);
    }

    private void customNavUi() {
        mAMapNaviView.setLazyDriveWayView(mDriveWayView);
        mAMapNaviView.setLazyNextTurnTipView(mNextTurnTipView);
        AMapNaviViewOptions options = mAMapNaviView.getViewOptions();
        options.setLayoutVisible(false);
        options.setCarBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_car));
        options.setZoom(16);                    // 地图缩放等级
        options.setTrafficLayerEnabled(false);  // 不显示路况按钮
        options.setTrafficBarEnabled(false);
        options.setAfterRouteAutoGray(true);    // 已走过路线置灰
        options.setOverBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_overview_pressed),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_overview_normal)
        );
        options.setAutoLockCar(true);
        options.setStartPointBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.map_icon_start));
        options.setEndPointBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.map_icon_end));
        //options.setRouteOverlayOptions(getRouteOverlayOptions());
        // 设置是否显示路口放大图(路口模型图)
        options.setModeCrossDisplayShow(false);
        // 设置是否显示路口放大图(实景图)
        options.setRealCrossDisplayShow(false);
        options.setPointToCenter(0.5, 0.6);

        mTrafficBarView.setUnknownTrafficColor(Color.parseColor("#0091FF"));
        mTrafficBarView.setSmoothTrafficColor(Color.parseColor("#00BA1F"));
        mTrafficBarView.setSlowTrafficColor(Color.parseColor("#FFBA00"));
        mTrafficBarView.setJamTrafficColor(Color.parseColor("#F31D20"));
        mTrafficBarView.setVeryJamTrafficColor(Color.parseColor("#A8090B"));
        mAMapNaviView.setViewOptions(options);
    }

    @OnClick({R2.id.iv_quit_navigation, R2.id.tv_quit_navigation, R2.id.change_road})
    public void onClick(View view) {
        int id = view.getId();////退出导航
        if (id == R.id.iv_quit_navigation || id == R.id.tv_quit_navigation) {
            finish();
        } else if (id == R.id.change_road) {
            mAMapNavi.switchParallelRoad(mParallelType);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDispatchEvent(DispatchEvent event) {
        if (event.type == DispatchEvent.DISPATCH_Refresh) {
            finish(); //关闭界面
        }
    }

    private boolean isNoPlayed = false;// 只播报一次变更目的地语音

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDispatchEvent(OrderEvent event) {
        if (event.type == OrderEvent.CLOSE_NAVI) {
            finish(); //关闭界面
        }
        if (event.type == OrderEvent.SPECIAL_PRICE) {
            double totalPrice = (double) event.obj2;
            SpannableWrap.setText(NumberUtil.getFormatValue(totalPrice))
                    .sizeSp(28, this)
                    .append("元")
                    .sizeSp(12, this)
                    .into(mTvTripPrice);
        }
        /////播放语音
        // fix bug #7919 目的地语音提示与导航播报语音重合问题
        // 在播放完内置语音后再播放目的地语音提示
        if (event.type == OrderEvent.ORDER_CHANGE_ADDRESS) {
            isNoPlayed = true;
            mAMapNavi.addTTSPlayListener(new TTSPlayListener() {
                @Override
                public void onPlayStart(String s) {

                }

                @Override
                public void onPlayEnd(String s) {
                    if (isNoPlayed && AppManager.getInstance().currentActivity() instanceof SingleRouteCalculateActivity) {
                        String address = (String) event.obj1;
                        String speak = getString(R.string.change_address_navigation, address);
                        SpeechUtil.speech(speak);
                        isNoPlayed = false;
                    }
                }
            });
        }
    }

    //////////////因为导航监听是在上个页面的Fragment，所以通过事件拿到回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMapEvent(MapEvent event) {
        if (event.type == MapEvent.VIEW_NaviInfoUpdate) {
            //更新下一路口 路名及 距离
            if (event.obj1 == null) return;
            NaviInfo naviInfo = (NaviInfo) event.obj1;
            try {
                mTvNextRoadName.setText(naviInfo.getNextRoadName());
                int distance = naviInfo.getCurStepRetainDistance();
                mTvNextRoadDistance.setText(NaviUtil.formatKM(distance) + NaviUtil.getUnit(distance));
                int allLength = mAMapNavi.getNaviPath().getAllLength();
                List<AMapTrafficStatus> trafficStatuses = mAMapNavi.getTrafficStatuses(0, 0);
                mTrafficBarView.update(allLength, naviInfo.getPathRetainDistance(), trafficStatuses);
                int data = naviInfo.getPathRetainDistance();
                SpannableWrap.setText(NaviUtil.formatKM(data))
                        .sizeSp(28, this)
                        .append(NaviUtil.getUnit(data))
                        .sizeSp(12, this)
                        .into(mTvTripDistance);

                int time = naviInfo.getPathRetainTime();
                SpannableWrap.setText(NaviUtil.getMinute(time) + "")
                        .sizeSp(28, this)
                        .append("分钟")
                        .sizeSp(12, this)
                        .into(mTvTripDuration);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAMapNaviView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAMapNaviView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAMapNaviView.onDestroy();
        //20180627 长连接订单操作（取消改派等）通知导航页关闭
        EventBus.getDefault().unregister(this); //移除监听
        mAMapNavi.setUseInnerVoice(false);
        mAMapNavi.stopSpeak();
        mAMapNavi.removeParallelRoadListener(this);
    }

    private void getExtra() {
        String slide_text = getIntent().getStringExtra(IConstants.SLIDE_VIEW_TEXT);
        mSlideView.setContent(slide_text);
    }

    @Override
    public void onNaviSetting() {

    }

    @Override
    public void onNaviCancel() {

    }

    @Override
    public boolean onNaviBackClick() {
        return false;
    }

    @Override
    public void onNaviMapMode(int i) {

    }

    @Override
    public void onNaviTurnClick() {

    }

    @Override
    public void onNextRoadClick() {

    }

    @Override
    public void onScanViewButtonClick() {

    }

    @Override
    public void onLockMap(boolean b) {

    }

    @Override
    public void onNaviViewLoaded() {

    }

    @Override
    public void onMapTypeChanged(int i) {

    }

    @Override
    public void onNaviViewShowMode(int i) {

    }

    //////主辅路切换的回调
    @Override
    public void notifyParallelRoad(AMapNaviParallelRoadStatus status) {
        if (status.getmElevatedRoadStatusFlag() != 0 || status.getmParallelRoadStatusFlag() != 0) {
            mChangeRoad.setVisibility(View.VISIBLE);
            ////高架上或下
            if (status.getmElevatedRoadStatusFlag() != 0) {
                mParallelType = 2;
                if (status.getmElevatedRoadStatusFlag() == 1) {
                    mChangeRoad.setText("在桥下");
                    setChangeRoadIco(R.drawable.ic_under_bridge);
                } else {
                    mChangeRoad.setText("在桥上");
                    setChangeRoadIco(R.drawable.ic_on_bridge);
                }
            }
            if (status.getmParallelRoadStatusFlag() != 0) {
                ////主辅路切换
                mParallelType = 1;
                if (status.getmParallelRoadStatusFlag() == 1) {
                    mChangeRoad.setText("在辅路");
                    setChangeRoadIco(R.drawable.ic_relief_road);
                } else {
                    mChangeRoad.setText("在主路");
                    setChangeRoadIco(R.drawable.ic_main_road);
                }
            }
        } else {
            mChangeRoad.setVisibility(View.GONE);
        }
    }

    private void setChangeRoadIco(int res) {
        try {
            Drawable drawable = getResources().getDrawable(res);
            /// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mChangeRoad.setCompoundDrawables(null, drawable, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

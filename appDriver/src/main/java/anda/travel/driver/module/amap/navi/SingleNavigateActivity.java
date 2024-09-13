package anda.travel.driver.module.amap.navi;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.enums.BroadcastMode;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.NaviLatLng;
import com.kyleduo.switchbutton.SwitchButton;

import javax.inject.Inject;

import anda.travel.driver.BuildConfig;
import anda.travel.driver.R;
import anda.travel.driver.baselibrary.utils.SP;
import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.data.dispatch.DispatchRepository;
import anda.travel.driver.data.user.local.UserLocalSource;
import anda.travel.driver.util.SysConfigUtils;

/**
 * 独立的导航页
 */
public class SingleNavigateActivity extends BaseNaviActivity {

    public static void actionStart(Context context, LatLng originLatLng, LatLng destLatLng) {
        actionStart(context, originLatLng, destLatLng, false);
    }

    public static void actionStart(Context context, LatLng originLatLng, LatLng destLatLng, boolean isDispatch) {
        Intent intent = new Intent(context, SingleNavigateActivity.class);
        intent.putExtra(IConstants.ORIGIN, originLatLng);
        intent.putExtra(IConstants.DEST, destLatLng);
        intent.putExtra(IConstants.IS_DISPATCH, isDispatch);
        context.startActivity(intent);
    }

    @Inject
    DispatchRepository mDispatchRepository;
    private boolean mIsDispatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HxClientManager.getAppComponent().inject(this); //依赖注入
        getExtra(); //处理传递过来的数据

        setContentView(R.layout.hxyc_activity_single_navigate);
        SwitchButton mSbVoice = findViewById(R.id.sb_voice);
        mAMapNaviView = findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);
        AMapNaviViewOptions options = mAMapNaviView.getViewOptions();
        options.setCarBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_car));
        options.setAutoLockCar(true);
        mAMapNaviView.setViewOptions(options);
        customNavUi();

        mAMapNavi.setUseInnerVoice(true);
        mAMapNavi.setBroadcastMode(BroadcastMode.DETAIL);
        mAMapNavi.startSpeak();

        // 设置语音播报规则
        mSbVoice.setCheckedImmediatelyNoEvent(SP.getInstance(getApplicationContext()).getInt(UserLocalSource.NAV_VOICE_MODE) == 0);
        mSbVoice.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                SP.getInstance(getApplicationContext()).putInt(UserLocalSource.NAV_VOICE_MODE, 0);
                mAMapNavi.setBroadcastMode(BroadcastMode.CONCISE);
            } else {
                SP.getInstance(getApplicationContext()).putInt(UserLocalSource.NAV_VOICE_MODE, 1);
                mAMapNavi.setBroadcastMode(BroadcastMode.DETAIL);
            }
        });
        mAMapNavi.addAMapNaviListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsDispatch) mDispatchRepository.setIsDispatchDisplay(true);
        mAMapNaviView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsDispatch) mDispatchRepository.setIsDispatchDisplay(false);
        mAMapNaviView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAMapNaviView.onDestroy();
        mAMapNavi.setUseInnerVoice(false);
        mAMapNavi.setUseInnerVoice(false);
    }

    private void getExtra() {
        mIsDispatch = getIntent().getBooleanExtra(IConstants.IS_DISPATCH, false);
        LatLng orgin = getIntent().getParcelableExtra(IConstants.ORIGIN);
        LatLng dest = getIntent().getParcelableExtra(IConstants.DEST);
        mStartLatlng = new NaviLatLng(orgin.latitude, orgin.longitude);
        mEndLatlng = new NaviLatLng(dest.latitude, dest.longitude);
        sList.add(mStartLatlng);
        eList.add(mEndLatlng);
    }

    @Override
    public void onInitNaviSuccess() {
        super.onInitNaviSuccess();
        /**
         * 方法: int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute); 参数:
         *
         * @congestion 躲避拥堵
         * @avoidhightspeed 不走高速
         * @cost 避免收费
         * @hightspeed 高速优先
         * @multipleroute 多路径
         *
         *  说明: 以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
         *  注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
         */
        int strategy = getNaviStrategy();
//        try {
//            //再次强调，最后一个参数为true时代表多路径，否则代表单路径
//            strategy = mAMapNavi.strategyConvert(true, false, false, false, false);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        mAMapNavi.calculateDriveRoute(sList, eList, mWayPointList, strategy);
    }

    @Override
    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

    }

    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {

    }

    @Override
    public void hideModeCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        int Type = (BuildConfig.DEBUG) ? NaviType.EMULATOR : NaviType.GPS;
        mAMapNavi.startNavi(Type);
    }

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {

    }

    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {

    }

    @Override
    public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {

    }

    @Override
    public void onGpsSignalWeak(boolean b) {

    }

    private int getNaviStrategy() {
        return SysConfigUtils.get().getNaviStrategy();
//        try {
//            MyConfig config = ParseUtils.getInstance().getMyConfig();
//            if (config != null
//                    && (!TextUtils.isEmpty(config.getNaviStrategy())))
//                return Integer.valueOf(config.getNaviStrategy());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return 0; //默认的导航策略
    }

    @Override
    public int getBarColor() {
        return R.color.black;
    }

    @Override
    public void onMapTypeChanged(int i) {

    }

    @Override
    public void onNaviViewShowMode(int i) {

    }

    private void customNavUi() {
        AMapNaviViewOptions options = mAMapNaviView.getViewOptions();
        options.setCarBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_car));
        options.setZoom(16);                    // 地图缩放等级
        options.setTrafficLayerEnabled(false);  // 不显示路况按钮
        //options.setTrafficLine(false);          // 不显示路况纹理
        options.setAfterRouteAutoGray(true);    // 已走过路线置灰
        options.setTrafficLine(true);////设置是否绘制显示交通路况的线路（彩虹线），拥堵-红色，畅通-绿色，缓慢-黄色，未知-蓝色。
        options.setTrafficBarEnabled(true);//设置路况光柱条是否显示（只适用于驾车导航，需要联网）。
        options.setOverBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_overview_pressed),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_overview_normal)
        );
        options.setAutoLockCar(true);
        mAMapNaviView.setViewOptions(options);
    }
}

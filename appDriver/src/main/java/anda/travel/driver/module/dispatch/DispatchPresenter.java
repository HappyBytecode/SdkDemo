package anda.travel.driver.module.dispatch;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import anda.travel.driver.BuildConfig;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.data.dispatch.DispatchRepository;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.event.DispatchEvent;
import anda.travel.driver.event.MapEvent;
import anda.travel.driver.module.vo.DispatchVO;

public class DispatchPresenter extends BasePresenter implements DispatchContract.Presenter {

    private final DispatchContract.View mView;
    private final UserRepository mUserRepository;
    private final DispatchRepository mDispatchRepository;

    @Inject
    public DispatchPresenter(DispatchContract.View view, UserRepository userRepository, DispatchRepository dispatchRepository) {
        mView = view;
        mUserRepository = userRepository;
        mDispatchRepository = dispatchRepository;
    }

    public void onCreate(DispatchVO dispatchVO) {
        mView.setDisplay(dispatchVO);
        mView.setNaviInfoDisplay(mDispatchRepository.getRetainDistance(), mDispatchRepository.getRetainTime());
        EventBus.getDefault().register(this);
    }

    @Override
    public void subscribe() {
        super.subscribe();
        mDispatchRepository.setIsDispatchDisplay(true);
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
        mDispatchRepository.setIsDispatchDisplay(false);
    }

    public void onDestory() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMapEvent(MapEvent event) {
        switch (event.type) {
            case MapEvent.VIEW_RESET: //重置信息显示
                mView.setNaviInfoDisplay(null, null);
                break;
            case MapEvent.VIEW_NaviInfoUpdate: //设置显示
                if (event.obj1 == null) return;
                NaviInfo naviInfo = (NaviInfo) event.obj1;
                mView.setNaviInfoDisplay(naviInfo.getPathRetainDistance(), naviInfo.getPathRetainTime());
                dealwithNaviInfo(naviInfo);
                break;
            case MapEvent.VIEW_LocationChange: //位置改变
                if (event.obj1 == null) return;
                AMapNaviLocation location = (AMapNaviLocation) event.obj1;
                dealwithAMapNaviLocation(location);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDispatchEvent(DispatchEvent event) {
        if (event.type == DispatchEvent.DISPATCH_Refresh) {
            mView.closeActivity();
        }
    }

    @Override
    public LatLng getCurrentLatLng() {
        return mDispatchRepository.getDispatchCurrentLatLng();
    }

    @Override
    public boolean isEmulatorOpen() {
        return mUserRepository.getDebugEmulator();
    }

    @Override
    public void dealwithNaviInfo(NaviInfo naviInfo) {

    }

    @Override
    public void dealwithAMapNaviLocation(AMapNaviLocation location) {

    }

    @Override
    public void changeEmulator() {
        if (BuildConfig.DEBUG) {
            boolean isEmulator = !mDispatchRepository.getDispatchEmulator(); //切换
            mView.changeEmulator(isEmulator);
            mDispatchRepository.setDispatchEmulator(isEmulator);
        }
    }
}

package anda.travel.driver.module.amap;

import com.amap.api.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.event.MapEvent;

/**
 * 功能描述：
 */
public class ANavigatePresenter extends BasePresenter implements ANavigateContract.Presenter {

    private final ANavigateContract.View mView;
    private final UserRepository mUserRepository;

    @Inject
    public ANavigatePresenter(ANavigateContract.View view, UserRepository userRepository) {
        mView = view;
        mUserRepository = userRepository;
    }

    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void subscribe() {
        super.subscribe();
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
    }

    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMapEvent(MapEvent mapEvent) {
        switch (mapEvent.type) {
            case MapEvent.NAVI_EMULATOR: //模拟导航
                if (mapEvent.obj1 == null) return;
                mView.emulator((boolean) mapEvent.obj1);
                break;
            case MapEvent.NAVI_TRAFFIC: //是否显示"交通状况"
                if (mapEvent.obj1 == null) return;
                mView.showTrafficLine((boolean) mapEvent.obj1);
                break;
            case MapEvent.NAVI_PADDING: //设置上边距和下边距
                if (mapEvent.obj1 == null || mapEvent.obj2 == null) return;
                mView.setMapPadding((int) mapEvent.obj1, (int) mapEvent.obj2);
                break;
            case MapEvent.NAVI_LOCATE: //定位到车辆位置
                mView.locate(mUserRepository.getLatLng());
                break;
            case MapEvent.NAVI_CALCULATE_ROUTE: //路径规划
                if (mapEvent.obj1 == null || mapEvent.obj2 == null) return;
                mView.calculateRoute((LatLng) mapEvent.obj1, (LatLng) mapEvent.obj2);
                break;
            case MapEvent.NAVI_NAVIGATE: //导航到目标地点
                if (mapEvent.obj1 == null) return;
                if (mUserRepository.getLatLng() == null) return;
                mView.navigate(mUserRepository.getLatLng(), (LatLng) mapEvent.obj1,
                        null == mapEvent.obj2 ? 0 : (int) mapEvent.obj2);
                break;
            case MapEvent.NAVI_ORDER: //设置订单起终点
                if (mapEvent.obj1 == null || mapEvent.obj2 == null) return;
                mView.setOrderPoint((LatLng) mapEvent.obj1, (LatLng) mapEvent.obj2);
                break;
            case MapEvent.VIEW_WAIT_TIME:
                if (mapEvent.obj1 == null) return;
                mView.showLateTime((long) mapEvent.obj1);
                break;
            case MapEvent.VIEW_WAIT_TIME_CANCEL:
                mView.hideLateTime();
                break;
        }
    }

    @Override
    public LatLng getCurrentLatLng() {
        return mUserRepository.getLatLng();
    }

}

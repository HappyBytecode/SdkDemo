package anda.travel.driver.module.dispatch;

import android.content.Context;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.module.vo.DispatchVO;

/**
 * 功能描述：
 */
public interface DispatchContract {

    interface View extends IBaseView<Presenter> {

        void setDisplay(DispatchVO vo);

        void closeActivity(); //关闭界面

        void setNaviInfoDisplay(Integer distance, Integer time);

        void skipToNavigate(); //跳转到独立的导航页

        Context getContext();

        void showEmulatorBtn(); //显示模拟导航按钮

        void hideEmulatorBtn(); //隐藏模拟导航按钮

        void changeEmulator(boolean isEmulator);

    }

    interface Presenter extends IBasePresenter {

        LatLng getCurrentLatLng();

        //是否显示模拟导航按键
        boolean isEmulatorOpen();

        void dealwithNaviInfo(NaviInfo naviInfo);

        void dealwithAMapNaviLocation(AMapNaviLocation location);

        void changeEmulator(); //切换导航模式

    }

}

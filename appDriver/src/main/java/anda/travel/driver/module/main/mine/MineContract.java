package anda.travel.driver.module.main.mine;

import android.content.Context;

import java.util.ArrayList;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.data.entity.UserCenterMenuEntity;
import anda.travel.driver.module.vo.MineVO;

/**
 * 功能描述："我"Contract
 */
public interface MineContract {

    interface View extends IBaseView<Presenter> {
        /**
         * 显示司机信息
         *
         * @param vo
         */
        void showDriverInfo(MineVO vo);

        /**
         * 是否有车辆信息
         *
         * @param hasData
         */
        void setHasData(Boolean hasData);

        void showDynamicMenuItem(ArrayList<UserCenterMenuEntity.MenusBean> first
                , ArrayList<UserCenterMenuEntity.MenusBean> second
        );

        Context getContext();
    }

    interface Presenter extends IBasePresenter {

        void setIsDisplay();

        /**
         * 从本地获取司机信息
         */
        void getDriverInfo();

        /**
         * 获取司机信息
         */
        void reqDriverInfo();

        /**
         * 是否有车辆信息
         */
        void reqHasData();
    }
}

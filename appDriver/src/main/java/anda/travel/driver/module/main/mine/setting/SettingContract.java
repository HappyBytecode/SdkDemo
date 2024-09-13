package anda.travel.driver.module.main.mine.setting;

import java.io.File;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.data.entity.DriverEntity;

public interface SettingContract {

    interface View extends IBaseView<Presenter> {

        /**
         * 退出成功
         */
        void logoutSuccess();

        /**
         * 设置车辆显示
         */
        void setCarDisplay();

        void showCacheSize(String size);
    }

    interface Presenter extends IBasePresenter {
        /**
         * 退出
         */
        void reqLogout();

        /**
         * 设置悬浮窗开关
         *
         * @param isOpen isOpen
         */
        void setOpenFloatWindow(boolean isOpen);

        /**
         * 获取悬浮窗开关状态
         */
        boolean getOpenFloatWindow();

        /**
         * 设置屏幕状态
         *
         * @param isBright
         */
        void setScreenStatue(boolean isBright);

        /**
         * 获得屏幕状态
         */
        boolean getScreenStatue();

        /**
         * 获取司机信息
         */
        DriverEntity getDriveEntity();

        boolean isReportAll();

        void setIsOnSetting(boolean isOnSetting);

        void getCacheSize(File webCache, File glideCache, File audioCache);
    }
}

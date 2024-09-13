package anda.travel.driver.module.main.mine.carinfo;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.data.entity.CarInfoEntity;

public interface CarInfoContract {
    interface View extends IBaseView<CarInfoContract.Presenter> {
        /**
         * 设置车辆信息
         *
         * @param entity
         */
        void setData(CarInfoEntity entity);
    }

    interface Presenter extends IBasePresenter {
        /**
         * 获取车辆信息
         */
        void reqCarInfo();
    }
}

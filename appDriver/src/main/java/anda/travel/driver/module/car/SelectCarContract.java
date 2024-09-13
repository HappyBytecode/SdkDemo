package anda.travel.driver.module.car;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.data.entity.DriverEntity;

public interface SelectCarContract {

    interface View extends IBaseView<Presenter> {

        void setDisplay();

        void selectCarSuccess();

    }

    interface Presenter extends IBasePresenter {

        DriverEntity getDriverEntity();

        void selectCar(String carNo);

    }
}

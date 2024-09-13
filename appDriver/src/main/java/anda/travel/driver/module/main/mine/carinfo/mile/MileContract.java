package anda.travel.driver.module.main.mine.carinfo.mile;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.data.entity.MileEntity;

public interface MileContract {
    interface View extends IBaseView<MileContract.Presenter> {
        /**
         * 设置里程数据
         *
         * @param entity
         */
        void setData(MileEntity entity);

        /**
         * 添加里程据
         *
         * @param entity
         */
        void addData(MileEntity entity);

        /**
         * 刷新完成
         */
        void onRefreshComplete();

        void noMore();
    }

    interface Presenter extends IBasePresenter {
        /**
         * 获取流水数据
         */
        void reqMile(String startTime, String endTime);
    }
}

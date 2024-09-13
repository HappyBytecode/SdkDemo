package anda.travel.driver.module.main.mine.walletnew.balancedetail;

import java.util.List;

import anda.travel.driver.baselibrary.network.RequestParams;
import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.data.entity.BalanceDetailListEntity;

public interface BalanceDetailContract {

    interface View extends IBaseView<Presenter> {

        void setData(List<BalanceDetailListEntity> balanceDetailListEntityList);

        void addData(List<BalanceDetailListEntity> balanceDetailListEntityList);

        void onRefreshComplete();

        void noMore();

        void hideNoMore();

        void netErrorView(boolean isShow);
    }

    interface Presenter extends IBasePresenter {

        /**
         * 获取余额明细列表数据
         */
        void reqBalanceDetailList(RequestParams.Builder builder);

        void reload();

        void setStatus(int status);

        void setStartDate(String startDate);
    }
}

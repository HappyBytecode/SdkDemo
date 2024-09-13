package anda.travel.driver.module.amap.heatmap;

import java.util.List;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.data.entity.HeatMapEntity;

public interface HeatMapContract {

    interface View extends IBaseView<Presenter> {
        void showHeatMap(List<HeatMapEntity> datas);

        void toastFail();

        void setCanZoom(boolean isZoom);
    }

    interface Presenter extends IBasePresenter {
        void getZoomType(String versionNo, String moduleName);

        void getPoints(int timeType);
    }

}

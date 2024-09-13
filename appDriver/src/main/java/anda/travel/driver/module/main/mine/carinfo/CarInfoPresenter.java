package anda.travel.driver.module.main.mine.carinfo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.data.entity.CarInfoEntity;
import anda.travel.driver.data.entity.CarItemEntity;
import anda.travel.driver.data.entity.TBoxInfo;
import anda.travel.driver.data.user.UserRepository;

public class CarInfoPresenter extends BasePresenter implements CarInfoContract.Presenter {

    private final UserRepository mUserRepository;
    private final CarInfoContract.View mView;

    @Inject
    public CarInfoPresenter(UserRepository userRepository, CarInfoContract.View view) {
        mUserRepository = userRepository;
        mView = view;
    }

    @Override
    public void reqCarInfo() {
        mDisposable.add(mUserRepository
                .reqCarInfo()
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingView(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(entity -> mView.setData(tboxInfoToCarInfoEntity(entity))
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    /**
     * 将接口返回的数据转为本地实体类，供界面解析显示
     *
     * @param tboxInfo
     * @return
     */
    private CarInfoEntity tboxInfoToCarInfoEntity(TBoxInfo tboxInfo) {
        CarInfoEntity carInfoEntity = new CarInfoEntity();
        carInfoEntity.todayMile = tboxInfo.todayMileage;
        carInfoEntity.remainCharge = tboxInfo.soc;
        carInfoEntity.updateTime = tboxInfo.updateTime;
        carInfoEntity.vin = tboxInfo.vinCode;
        CarItemEntity entity1 = new CarItemEntity();
        entity1.key = "车辆状态";
        switch (tboxInfo.vehState) {
            case "0":
                entity1.value = "未启动";
                break;
            case "1":
                entity1.value = "启动";
                break;
            case "2":
                entity1.value = "熄火";
                break;
            case "3":
                entity1.value = "其他";
                break;
            case "-1":
                entity1.value = "无效";
                break;
            case "-2":
                entity1.value = "异常";
                break;
        }

        CarItemEntity entity2 = new CarItemEntity();
        entity2.key = "充电状态";
        switch (tboxInfo.chargeState) {
            case "0":
            case "3":
                entity2.value = "未充电";
                break;
            case "1":
                entity2.value = "停车充电";
                break;
            case "2":
                entity2.value = "行驶充电";
                break;
            case "4":
                entity2.value = "充电完成";
                break;
            case "-1":
                entity2.value = "无效";
                break;
            case "-2":
                entity2.value = "异常";
                break;
        }

        CarItemEntity entity3 = new CarItemEntity();
        entity3.key = "挡位";
        switch (tboxInfo.gear) {
            case "1":
                entity3.value = "1挡";
                break;
            case "2":
                entity3.value = "2挡";
                break;
            case "3":
                entity3.value = "3挡";
                break;
            case "4":
                entity3.value = "4挡";
                break;
            case "5":
                entity3.value = "5挡";
                break;
            case "6":
                entity3.value = "6挡";
                break;
            case "13":
                entity3.value = "倒挡";
                break;
            case "14":
                entity3.value = "自动D挡";
                break;
            case "15":
                entity3.value = "P挡";
                break;
        }

        CarItemEntity entity4 = new CarItemEntity();
        entity4.key = "累计里程";
        entity4.value = new StringBuilder(tboxInfo.totalMileage).append("公里").toString();
        List<CarItemEntity> lists = new ArrayList<>();
        lists.add(entity1);
        lists.add(entity2);
        lists.add(entity3);
        lists.add(entity4);
        carInfoEntity.items = lists;
        return carInfoEntity;
    }
}

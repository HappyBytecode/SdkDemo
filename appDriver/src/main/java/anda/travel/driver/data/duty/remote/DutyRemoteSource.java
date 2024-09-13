package anda.travel.driver.data.duty.remote;

import java.util.HashMap;

import javax.inject.Inject;

import anda.travel.driver.api.DriverApi;
import anda.travel.driver.data.duty.DutySource;
import anda.travel.driver.data.entity.AnalyzeDutyTime;
import anda.travel.driver.data.entity.LocationEntity;
import anda.travel.driver.data.entity.OrderListenerEntity;
import anda.travel.driver.data.user.UserRepository;
import rx.Observable;

/**
 * 功能描述：
 */
public class DutyRemoteSource implements DutySource {

    private final DriverApi mDriverApi;
    private final UserRepository mUserRepository;

    @Inject
    public DutyRemoteSource(DriverApi driverApi, UserRepository userRepository) {
        mDriverApi = driverApi;
        mUserRepository = userRepository;
    }

    @Override
    public Observable<String> reqDutyStatus(boolean onlyFromRemote) {
        return mDriverApi.reqDutyStatus();
    }

    @Override
    public Observable<String> reqOnDuty(boolean isForce, String imgData, String bizId) {
        LocationEntity location = mUserRepository.getCurrentLocation();
        if (location == null) return Observable.empty();

        HashMap<String, String> params = new HashMap<>();
        params.put("adcode", String.valueOf(location.adcode));
        params.put("lng", String.valueOf(location.lng));
        params.put("lat", String.valueOf(location.lat));
        params.put("isForce", String.valueOf(isForce));
        if (imgData != null) {//添加头像信息
            params.put("base64", imgData);
        }
        if (null != bizId) {//3.1.2版本增加（人脸活体检测比对需要传入bizId，让服务端查询是否真正通过验证）
            params.put("bizId", bizId);
        }
        return mDriverApi.reqOnDuty(params);
    }

    @Override
    public Observable<String> reqOffDuty(boolean isForce) {
        return reqOffDuty(0);
    }

    public Observable<String> reqOffDuty(long actualTime) {
        HashMap<String, String> params = new HashMap<>();
        params.put("actualTime", String.valueOf(actualTime)); //实际出车时长

        LocationEntity location = mUserRepository.getCurrentLocation();
        if (location == null) return Observable.empty();
        params.put("adcode", String.valueOf(location.adcode));
        params.put("lng", String.valueOf(location.lng));
        params.put("lat", String.valueOf(location.lat));
        return mDriverApi.reqOffDuty(params);
    }

    @Override
    public void setIsOnDuty(Boolean isOnDuty) {

    }

    @Override
    public int getIsOnDuty() {
        return 0;
    }

    @Override
    public void saveListenerSetting(String driverUuid, OrderListenerEntity entity) {

    }

    @Override
    public OrderListenerEntity getListenerSetting(String driverUuid) {
        return null;
    }

    @Override
    public Observable<OrderListenerEntity> reqListenerSetting(String driverUuid) {
        return null;
    }

    @Override
    public Observable<String> reqSaveListenerSetting(int remindType, String appointTimeStart, String appointTimeEnd, int selectedCarpool) {
        return mDriverApi.setRemindType(remindType, appointTimeStart, appointTimeEnd, selectedCarpool);
    }

    @Override
    public void homeResume() {

    }

    @Override
    public void orderOngoingCreate() {

    }

    @Override
    public void orderOngoingDestory() {

    }

    @Override
    public void priceCheckCreate() {

    }

    @Override
    public void priceCheckDestory() {

    }

    @Override
    public boolean getIsOrderOngoing() {
        return false;
    }


    @Override
    public void updateDutyTime(AnalyzeDutyTime dutyTime) {

    }

    @Override
    public AnalyzeDutyTime getDutyTime() {
        return null;
    }

    @Override
    public void updateDutyLog(boolean isReset, int appStatus) {

    }

}

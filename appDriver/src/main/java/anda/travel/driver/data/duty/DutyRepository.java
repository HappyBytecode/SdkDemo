package anda.travel.driver.data.duty;

import javax.inject.Inject;
import javax.inject.Singleton;

import anda.travel.driver.config.DutyStatus;
import anda.travel.driver.data.duty.local.DutyLocalSource;
import anda.travel.driver.data.duty.remote.DutyRemoteSource;
import anda.travel.driver.data.entity.AnalyzeDutyTime;
import anda.travel.driver.data.entity.OrderListenerEntity;
import rx.Observable;

/**
 * 功能描述：HomeFrg和DutyFrg使用的"数据"仓库（出车/收车状态、在线时长、接单数 等）
 */
@Singleton
public class DutyRepository implements DutySource {

    private final DutyLocalSource mLocalSource;
    private final DutyRemoteSource mRemoteSource;

    @Inject
    public DutyRepository(DutyLocalSource localSource, DutyRemoteSource remoteSource) {
        mLocalSource = localSource;
        mRemoteSource = remoteSource;
    }

    /**
     * @param onlyFromRemote
     * @return
     */
    @Override
    public Observable<String> reqDutyStatus(boolean onlyFromRemote) {
        if (onlyFromRemote) return reqRemoteDuty();

        return mLocalSource.reqDutyStatus(false)
                .switchIfEmpty(reqRemoteDuty());
    }

    //从远程获取"出车/收车状态"，需要同步本地缓存的状态
    private Observable<String> reqRemoteDuty() {
        return mRemoteSource.reqDutyStatus(true)
                .doOnNext(s -> {
                    boolean isOnDuty = DutyStatus.ON_DUTY.equals(s);
                    mLocalSource.setIsOnDuty(isOnDuty);

                    //暂不发送通知
                    //int type = isOnDuty ? DutyEvent.ON_DUTY : DutyEvent.OFF_DUTY;
                    //EventBus.getDefault().post(new DutyEvent(type));
                });
    }

    @Override
    public Observable<String> reqOnDuty(boolean isForce, String imgData, String bizId) {
        return mRemoteSource.reqOnDuty(isForce, imgData, bizId)
                .doOnNext(s -> {
                    //本地缓存"出车"状态
                    mLocalSource.setIsOnDuty(true);

                    //暂不发送通知
                    //发送Event事件，告知"已出车"
                    //EventBus.getDefault().post(new DutyEvent(DutyEvent.ON_DUTY));
                });
    }

    @Override
    public Observable<String> reqOffDuty(boolean isForce) {
        mLocalSource.updateDutyTime(null); //先更新
        return mRemoteSource.reqOffDuty(mLocalSource.getDutyTime().duration)
                .doOnNext(s -> {
                    //本地缓存"收车"状态
                    mLocalSource.setIsOnDuty(false);
                  /*  if(!isForce) {
                        //发送Event事件，告知"已收车"
                        EventBus.getDefault().post(new DutyEvent(DutyEvent.OFF_DUTY));
                    }*/
                });
    }

    @Override
    public void setIsOnDuty(Boolean isOnDuty) {
        mLocalSource.setIsOnDuty(isOnDuty);
    }

    @Override
    public int getIsOnDuty() {
        return mLocalSource.getIsOnDuty();
    }

    @Override
    public void saveListenerSetting(String driverUuid, OrderListenerEntity entity) {
        mLocalSource.saveListenerSetting(driverUuid, entity);
    }

    @Override
    public OrderListenerEntity getListenerSetting(String driverUuid) {
        return mLocalSource.getListenerSetting(driverUuid);
    }

    @Override
    public Observable<OrderListenerEntity> reqListenerSetting(String driverUuid) {
        return mRemoteSource.reqListenerSetting(driverUuid);
    }

    @Override
    public Observable<String> reqSaveListenerSetting(int remindType, String appointTimeStart, String appointTimeEnd, int selectedCarpool) {
        return mRemoteSource.reqSaveListenerSetting(remindType, appointTimeStart, appointTimeEnd, selectedCarpool);
    }

    @Override
    public void homeResume() {
        mLocalSource.homeResume();
    }

    @Override
    public void orderOngoingCreate() {
        mLocalSource.orderOngoingCreate();
    }

    @Override
    public void orderOngoingDestory() {
        mLocalSource.orderOngoingDestory();
    }

    @Override
    public void priceCheckCreate() {
        mLocalSource.priceCheckCreate();
    }

    @Override
    public void priceCheckDestory() {
        mLocalSource.priceCheckDestory();
    }

    @Override
    public boolean getIsOrderOngoing() {
        return mLocalSource.getIsOrderOngoing();
    }

    @Override
    public void updateDutyTime(AnalyzeDutyTime dutyTime) {
        mLocalSource.updateDutyTime(dutyTime);
    }

    @Override
    public AnalyzeDutyTime getDutyTime() {
        return mLocalSource.getDutyTime();
    }

    @Override
    public void updateDutyLog(boolean isReset, int appStatus) {
        mLocalSource.updateDutyLog(isReset, appStatus);
    }

}

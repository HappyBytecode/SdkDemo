package anda.travel.driver.data.duty;

import anda.travel.driver.data.entity.AnalyzeDutyTime;
import anda.travel.driver.data.entity.OrderListenerEntity;
import rx.Observable;

/**
 * 功能描述："上下班状态仓库"需实现的接口
 */
public interface DutySource {

    Observable<String> reqDutyStatus(boolean onlyFromRemote); //获取出车状态

    Observable<String> reqOnDuty(boolean isForce, String imgData, String bizId); //上班

    Observable<String> reqOffDuty(boolean isForce); //下班

    void setIsOnDuty(Boolean isOnDuty);

    int getIsOnDuty();

    /**
     * 保存听单设置到本地
     *
     * @param driverUuid
     * @param entity
     */
    void saveListenerSetting(String driverUuid, OrderListenerEntity entity);

    /**
     * 从本地获取听单设置（本地保存的）
     *
     * @param driverUuid
     * @return
     */
    OrderListenerEntity getListenerSetting(String driverUuid);

    /**
     * 从后端获取听单设置
     *
     * @param driverUuid
     * @return
     */
    Observable<OrderListenerEntity> reqListenerSetting(String driverUuid);

    /**
     * 保存听单设置
     *
     * @param remindType
     * @param appointTimeStart
     * @param appointTimeEnd
     * @param selectedCarpool
     * @return
     */
    Observable<String> reqSaveListenerSetting(int remindType, String appointTimeStart, String appointTimeEnd, int selectedCarpool);

    void homeResume();

    void orderOngoingCreate();

    void orderOngoingDestory();

    void priceCheckCreate();

    void priceCheckDestory();

    boolean getIsOrderOngoing();

    void updateDutyTime(AnalyzeDutyTime dutyTime); //更新本地出车时长记录

    AnalyzeDutyTime getDutyTime(); //获取出车时长信息

    void updateDutyLog(boolean isReset, int appStatus); //更新出车信息

}

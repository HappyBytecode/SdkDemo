package anda.travel.driver.data.duty.local;

import android.text.TextUtils;

import java.text.MessageFormat;
import java.util.Date;

import javax.inject.Inject;

import anda.travel.driver.baselibrary.utils.BackgroundUtil;
import anda.travel.driver.baselibrary.utils.DateUtil;
import anda.travel.driver.baselibrary.utils.SP;
import anda.travel.driver.config.DutyStatus;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.data.duty.DutySource;
import anda.travel.driver.data.entity.AnalyzeDutyTime;
import anda.travel.driver.data.entity.OrderListenerEntity;
import rx.Observable;
import timber.log.Timber;

/**
 * 功能描述：
 */
public class DutyLocalSource implements DutySource {

    private final SP mSP; //用于本地存储

    @Inject
    public DutyLocalSource(SP sp) {
        mSP = sp;
    }

    //是否出车
    private Boolean mIsOnDuty;
    //出车时长信息
    private AnalyzeDutyTime mDutyTime;
    //记录时间戳
    private Long mOnDutyTimestamp;
    private Long mOffDutyTimestamp;

    @Override
    public Observable<String> reqDutyStatus(boolean onlyFromRemote) {
        if (mIsOnDuty == null) return Observable.empty();
        return Observable.just(mIsOnDuty ? DutyStatus.ON_DUTY : DutyStatus.OFF_DUTY);
    }

    @Override
    public Observable<String> reqOnDuty(boolean isForce, String imgData, String bizId) {
        return null;
    }

    @Override
    public Observable<String> reqOffDuty(boolean isForce) {
        return null;
    }

    @Override
    public void setIsOnDuty(Boolean isOnDuty) {
        mIsOnDuty = isOnDuty;
        mSP.putBoolean(IConstants.IS_ON_DUTY, isOnDuty != null && isOnDuty); //保存到SP中
        if (isOnDuty != null && !isOnDuty) { //重置出车时长
            updateDutyTime(new AnalyzeDutyTime(false, System.currentTimeMillis(), 0));
        }

        if (isOnDuty != null) {
            if (isOnDuty) { //记录出车时间点
                mOnDutyTimestamp = System.currentTimeMillis();
            } else { //记录收车时间点
                mOffDutyTimestamp = System.currentTimeMillis();
            }
        }
    }

    @Override
    public int getIsOnDuty() {
        // 如果mIsOnDuty==null，从SP中取值，不要直接给mIsOnDuty赋值
        boolean isOnDuty = (mIsOnDuty == null)
                ? mSP.getBoolean(IConstants.IS_ON_DUTY, false)
                : mIsOnDuty;
        return isOnDuty ? DutyStatus.ON_DUTY_INT : DutyStatus.OFF_DUTY_INT;
    }

    @Override
    public void saveListenerSetting(String driverUuid, OrderListenerEntity entity) {
        if (entity == null || TextUtils.isEmpty(driverUuid)) return;
        mSP.putObject("set-" + driverUuid, entity); //保存到本地
    }

    @Override
    public OrderListenerEntity getListenerSetting(String driverUuid) {
        if (TextUtils.isEmpty(driverUuid)) return new OrderListenerEntity();
        // 如果要在该类中"缓存听单设置"，在退出登录时 需"清除缓存"
        OrderListenerEntity entity = mSP.getObject("set-" + driverUuid, OrderListenerEntity.class);
        if (entity == null) entity = new OrderListenerEntity();
        return entity;
    }

    @Override
    public Observable<OrderListenerEntity> reqListenerSetting(String driverUuid) {
        return null;
    }

    @Override
    public Observable<String> reqSaveListenerSetting(int remindType, String appointTimeStart, String appointTimeEnd, int selectedCarpool) {
        return null;
    }

    /**
     * 20170804调整：增加订单进行中的判断
     * 大于0－有订单进行中；小于0－无
     */
    private int mIsOrderOngoing;

    @Override
    public void homeResume() {
        mIsOrderOngoing = 0;
        print();
    }

    @Override
    public void orderOngoingCreate() {
        mIsOrderOngoing += 1;
        print();
    }

    @Override
    public void orderOngoingDestory() {
        mIsOrderOngoing -= 1;
        print();
    }

    @Override
    public void priceCheckCreate() {
        mIsOrderOngoing += 2;
        print();
    }

    @Override
    public void priceCheckDestory() {
        mIsOrderOngoing -= 2;
        print();
    }

    @Override
    public boolean getIsOrderOngoing() {
        print();
        return mIsOrderOngoing > 0;
    }

    private void print() {
        Timber.d(MessageFormat.format("-----> mIsOrderOngoing = {0}", mIsOrderOngoing));
    }

    @Override
    public void updateDutyTime(AnalyzeDutyTime dutyTime) {
        if (dutyTime != null) {
            mDutyTime = dutyTime;
            mSP.putObject(IConstants.DUTY_TIME, dutyTime);
        } else {
            AnalyzeDutyTime time = getDutyTime();
            long lastTimeStamp = time.timeStamp;
            long timeStamp = System.currentTimeMillis();
            boolean isOnDuty = mIsOnDuty != null && mIsOnDuty;
            if (time.isOnDuty && isOnDuty) {
                time.duration = time.duration + (timeStamp - lastTimeStamp); //更新出车时长
            } else {
                time.duration = 0; //重置出车时长
            }
            time.isOnDuty = isOnDuty;
            time.timeStamp = timeStamp;
            mSP.putObject(IConstants.DUTY_TIME, time);
        }
    }

    @Override
    public AnalyzeDutyTime getDutyTime() {
        if (mDutyTime != null) return mDutyTime;

        AnalyzeDutyTime time = mSP.getObject(IConstants.DUTY_TIME, AnalyzeDutyTime.class);
        if (time == null) {
            time = new AnalyzeDutyTime(mIsOnDuty != null && mIsOnDuty,
                    System.currentTimeMillis(), 0);
        }
        return time;
    }

    @Override
    public void updateDutyLog(boolean isReset, int appStatus) {
        if (isReset) {
            mOnDutyTimestamp = mOffDutyTimestamp = null;
        } else {
            StringBuilder str = new StringBuilder();
            long currentTimeMillis = System.currentTimeMillis();
            String dutyStatus;
            if (mIsOnDuty != null) {
                if (mIsOnDuty) {
                    dutyStatus = "出车中";
                    if (mOnDutyTimestamp != null) {
                        str.append(DateUtil.formatDate(new Date(mOnDutyTimestamp), DateUtil.HHmmss));
                        str.append("--");
                    }
                    str.append(DateUtil.formatDate(new Date(currentTimeMillis), DateUtil.HHmmss));
                } else {
                    dutyStatus = "已收车";
                    if (mOffDutyTimestamp != null) {
                        str.append(DateUtil.formatDate(new Date(mOffDutyTimestamp), DateUtil.HHmmss));
                        str.append("--");
                    }
                    str.append(DateUtil.formatDate(new Date(currentTimeMillis), DateUtil.HHmmss));
                }
            } else {
                dutyStatus = "未知";
                str.append(DateUtil.formatDate(new Date(currentTimeMillis), DateUtil.HHmmss));
            }
            str.append("  出车状态:");
            str.append(dutyStatus);
            str.append("  应用状态:");
            str.append(BackgroundUtil.getAppStatus(appStatus));
        }
    }

}

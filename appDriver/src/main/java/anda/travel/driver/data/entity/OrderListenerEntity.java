package anda.travel.driver.data.entity;

import android.text.TextUtils;

import anda.travel.driver.config.RemindType;

/**
 * 功能描述：听单设置
 * <p>
 * 请使用该类中的get方法，获取对应数据
 */
public class OrderListenerEntity {

    public Integer remindType; //听单偏好 (1：全部，2：实时 3：预约) 默认为"全部"
    public String appointTimeStart; //预约时段 开始时间
    public String appointTimeEnd; //预约时段 结束时间

    /**
     * 获取听单设置
     *
     * @return
     */
    public int getRemindType() {
        if (remindType == null) return RemindType.ALL.getType();

        if (remindType == RemindType.REALTIME.getType()
                || remindType == RemindType.APPOINT.getType())
            return remindType;

        return RemindType.ALL.getType();
    }

    /**
     * 获取开始时间
     *
     * @return
     */
    public Long getStartTime() {
        return TextUtils.isEmpty(appointTimeStart)
                ? null : getLongValue(appointTimeStart);
    }

    /**
     * 获取结束时间
     *
     * @return
     */
    public Long getEndTime() {
        return TextUtils.isEmpty(appointTimeEnd)
                ? null : getLongValue(appointTimeEnd);
    }

    private Long getLongValue(String str) {
        try {
            return Long.valueOf(str);
        } catch (Exception e) {
            return null;
        }
    }

}

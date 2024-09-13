package anda.travel.driver.data.entity;

/**
 * @Author moyuwan
 * @Date 17/9/4
 * <p>
 * 记录出车时长
 */
public class AnalyzeDutyTime {

    public AnalyzeDutyTime() {

    }

    public AnalyzeDutyTime(boolean isOnDuty, long timeStamp, long duration) {
        this.isOnDuty = isOnDuty;
        this.timeStamp = timeStamp;
        this.duration = duration;
    }

    public boolean isOnDuty; //记录时，是否处于出车状态
    public long timeStamp; //更新记录的时间点
    public long duration; //出车时长

}
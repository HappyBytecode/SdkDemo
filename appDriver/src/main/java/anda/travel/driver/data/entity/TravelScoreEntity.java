package anda.travel.driver.data.entity;

import java.util.List;

/**
 * Created by liuwenwu on 2022/9/27.
 * Des :
 */
public class TravelScoreEntity {

    private String time; //计分周期
    private String compOrderScore; //成单分
    private String timeScore; //时长分
    private String peakScore; //高峰分
    private String originScore; //初始分
    private String tripScore; //出行分
    private String serviceScore; //出行分
    private List<ScoreChangesEntity> changes; //昨日变动

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCompOrderScore() {
        return compOrderScore;
    }

    public void setCompOrderScore(String compOrderScore) {
        this.compOrderScore = compOrderScore;
    }

    public String getTimeScore() {
        return timeScore;
    }

    public void setTimeScore(String timeScore) {
        this.timeScore = timeScore;
    }

    public String getPeakScore() {
        return peakScore;
    }

    public void setPeakScore(String peakScore) {
        this.peakScore = peakScore;
    }

    public String getOriginScore() {
        return originScore;
    }

    public void setOriginScore(String originScore) {
        this.originScore = originScore;
    }

    public String getTripScore() {
        return tripScore;
    }

    public void setTripScore(String tripScore) {
        this.tripScore = tripScore;
    }

    public String getServiceScore() {
        return serviceScore;
    }

    public void setServiceScore(String serviceScore) {
        this.serviceScore = serviceScore;
    }

    public List<ScoreChangesEntity> getChanges() {
        return changes;
    }

    public void setChanges(List<ScoreChangesEntity> changes) {
        this.changes = changes;
    }
}

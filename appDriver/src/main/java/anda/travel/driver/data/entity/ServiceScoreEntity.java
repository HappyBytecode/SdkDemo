package anda.travel.driver.data.entity;

/**
 * Created by liuwenwu on 2022/9/26.
 * Des :
 */
public class ServiceScoreEntity {

    private String addCompOrderScore; //昨日变动-成单分
    private double addEvaluateScore; //新增分数-口碑分
    private String addPeakScore; //昨日变动-高峰分
    private String addTaskScore; //昨日变动-任务分
    private String addTimeScore; //昨日变动-时长分
    private String compOrderScore; //成单分
    private String evaluateScore; //口碑分
    private double exceedScore; //过期分数-口碑分
    private String peakScore; //高峰分
    private double percentage; //司机排名占比
    private String serviceScore; //服务分
    private String taskScore; //任务分
    private String timeScore; //时长分
    private String tripScore; //出行分
    private String originScore; //初始分
    private String updateTime; //更新时间

    public String getAddCompOrderScore() {
        return addCompOrderScore;
    }

    public void setAddCompOrderScore(String addCompOrderScore) {
        this.addCompOrderScore = addCompOrderScore;
    }

    public double getAddEvaluateScore() {
        return addEvaluateScore;
    }

    public void setAddEvaluateScore(double addEvaluateScore) {
        this.addEvaluateScore = addEvaluateScore;
    }

    public String getAddPeakScore() {
        return addPeakScore;
    }

    public void setAddPeakScore(String addPeakScore) {
        this.addPeakScore = addPeakScore;
    }

    public String getAddTaskScore() {
        return addTaskScore;
    }

    public void setAddTaskScore(String addTaskScore) {
        this.addTaskScore = addTaskScore;
    }

    public String getAddTimeScore() {
        return addTimeScore;
    }

    public void setAddTimeScore(String addTimeScore) {
        this.addTimeScore = addTimeScore;
    }

    public String getCompOrderScore() {
        return compOrderScore;
    }

    public void setCompOrderScore(String compOrderScore) {
        this.compOrderScore = compOrderScore;
    }

    public String getEvaluateScore() {
        return evaluateScore;
    }

    public void setEvaluateScore(String evaluateScore) {
        this.evaluateScore = evaluateScore;
    }

    public double getExceedScore() {
        return exceedScore;
    }

    public void setExceedScore(double exceedScore) {
        this.exceedScore = exceedScore;
    }

    public String getPeakScore() {
        return peakScore;
    }

    public void setPeakScore(String peakScore) {
        this.peakScore = peakScore;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getServiceScore() {
        return serviceScore;
    }

    public void setServiceScore(String serviceScore) {
        this.serviceScore = serviceScore;
    }

    public String getTaskScore() {
        return taskScore;
    }

    public void setTaskScore(String taskScore) {
        this.taskScore = taskScore;
    }

    public String getTimeScore() {
        return timeScore;
    }

    public void setTimeScore(String timeScore) {
        this.timeScore = timeScore;
    }

    public String getTripScore() {
        return tripScore;
    }

    public void setTripScore(String tripScore) {
        this.tripScore = tripScore;
    }

    public String getOriginScore() {
        return originScore;
    }

    public void setOriginScore(String originScore) {
        this.originScore = originScore;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}

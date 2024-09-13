package anda.travel.driver.data.entity;

import java.util.List;

/**
 * Created by liuwenwu on 2022/9/27.
 * Des :
 */
public class TaskScoreEntity {

    private String taskScore; //任务分
    private String count; //完成任务数
    private String time; //记分周期
    private List<ScoreChangesEntity> changes; //昨日变动

    public String getTaskScore() {
        return taskScore;
    }

    public void setTaskScore(String taskScore) {
        this.taskScore = taskScore;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<ScoreChangesEntity> getChanges() {
        return changes;
    }

    public void setChanges(List<ScoreChangesEntity> changes) {
        this.changes = changes;
    }
}

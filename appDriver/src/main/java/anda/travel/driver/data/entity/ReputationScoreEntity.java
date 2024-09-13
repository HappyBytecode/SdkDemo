package anda.travel.driver.data.entity;

import java.util.List;

/**
 * Created by liuwenwu on 2022/9/26.
 * Des :
 */
public class ReputationScoreEntity {

    private String evaluateScore;
    private List<EvaluateListEntity> evaluateList;
    private List<ScoreDetailEntity> details;
    private List<ScoreDetailEntity> hisDetails;
    private List<ScoreDetailEntity> hisOldDetails;
    private String date;

    public String getEvaluateScore() {
        return evaluateScore;
    }

    public void setEvaluateScore(String evaluateScore) {
        this.evaluateScore = evaluateScore;
    }

    public List<EvaluateListEntity> getEvaluateList() {
        return evaluateList;
    }

    public void setEvaluateList(List<EvaluateListEntity> evaluateList) {
        this.evaluateList = evaluateList;
    }

    public List<ScoreDetailEntity> getDetails() {
        return details;
    }

    public void setDetails(List<ScoreDetailEntity> details) {
        this.details = details;
    }

    public List<ScoreDetailEntity> getHisDetails() {
        return hisDetails;
    }

    public void setHisDetails(List<ScoreDetailEntity> hisDetails) {
        this.hisDetails = hisDetails;
    }

    public List<ScoreDetailEntity> getHisOldDetails() {
        return hisOldDetails;
    }

    public void setHisOldDetails(List<ScoreDetailEntity> hisOldDetails) {
        this.hisOldDetails = hisOldDetails;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

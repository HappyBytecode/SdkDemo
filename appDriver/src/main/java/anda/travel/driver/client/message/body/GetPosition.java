package anda.travel.driver.client.message.body;

import anda.travel.driver.client.message.Body;

/**
 * body-获取位置
 *
 * @author Zoro
 * @since 2017/3/28
 */

public class GetPosition implements Body {

    public static final int CONDITION_ORDER = 1;
    public static final int CONDITION_DRIVER = 2;

    private int condition;//条件
    private String conditionId;//条件ID

    public GetPosition(int condition, String conditionId) {
        this.condition = condition;
        this.conditionId = conditionId;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public String getConditionId() {
        return conditionId;
    }

    public void setConditionId(String conditionId) {
        this.conditionId = conditionId;
    }
}

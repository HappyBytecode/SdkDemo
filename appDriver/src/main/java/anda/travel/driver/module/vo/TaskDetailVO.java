package anda.travel.driver.module.vo;

import com.alibaba.fastjson.JSON;

import java.util.List;

import anda.travel.driver.data.entity.TaskDetailEntity;
import anda.travel.driver.baselibrary.utils.TypeUtil;

public class TaskDetailVO {

    public static TaskDetailVO createFrom(TaskDetailEntity entity) {
        if (entity == null) return new TaskDetailVO();
        String json = JSON.toJSONString(entity);
        return JSON.parseObject(json, TaskDetailVO.class);
    }

    public String uuid;

    public String taskUuid;

    public String driverUuid;

    public String taskName;

    public Integer status;

    public String statusStr;

    public String duration;

    public String durations;

    public Integer reachOrderCnt;

    public Integer reqOrderCnt;

    public Double awardAmount;

    public String awardStr;

    public String content;

    public String taskImage;

    public String taskUrl;

    public List<String> requires;

    public String description;

    public String getStatusStr() {
        return TypeUtil.getValue(statusStr);
    }

    public String getTaskName() {
        return TypeUtil.getValue(taskName);
    }

    public String getDuration() {
        return TypeUtil.getValue(duration);
    }

    public String getDurations() {
        return TypeUtil.getValue(durations);
    }

    public String getContent() {
        return TypeUtil.getValue(content);
    }

    public String getRequiresStr() {
        if (requires == null || requires.isEmpty()) return "";
        StringBuilder str = new StringBuilder();
        int size = requires.size();
        for (int i = 0; i < size; i++) {
            str.append(requires.get(i));
            if (i + 1 < size) str.append("\n");
        }
        return str.toString();
    }

    public String getAwardStr() {
        return TypeUtil.getValue(awardStr);
    }

    public String getDescription() {
        return TypeUtil.getValue(description);
    }

    public String getProgressStr() {
        if (reachOrderCnt == null || reqOrderCnt == null) return "0/0";
        StringBuilder str = new StringBuilder();
        str.append(reachOrderCnt);
        str.append("/");
        str.append(reqOrderCnt);
        return str.toString();
    }

    public int getProgress() {
        if (reachOrderCnt == null || reqOrderCnt == null) return 0;
        return reachOrderCnt;
    }

    public int getMax() {
        if (reachOrderCnt == null || reqOrderCnt == null) return 0;
        return reqOrderCnt;
    }
}

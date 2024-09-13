package anda.travel.driver.module.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import anda.travel.driver.data.entity.TaskListEntity;
import anda.travel.driver.baselibrary.utils.TypeUtil;

public class TaskListVO {

    public static TaskListVO createFrom(TaskListEntity entity) {
        if (entity == null) return new TaskListVO();
        String json = JSON.toJSONString(entity);
        return JSON.parseObject(json, TaskListVO.class);
    }

    public String uuid;
    @JSONField(name = "taskName")
    public String titleStr;
    @JSONField(name = "duration")
    public String durationStr;
    @JSONField(name = "statusStr")
    public String statusStr;

    public String getUuid() {
        return TypeUtil.getValue(uuid);
    }

    public String getTitleStr() {
        return TypeUtil.getValue(titleStr);
    }

    public String getDurationStr() {
        return TypeUtil.getValue(durationStr);
    }

    public String getStatusStr() {
        return TypeUtil.getValue(statusStr);
    }
}

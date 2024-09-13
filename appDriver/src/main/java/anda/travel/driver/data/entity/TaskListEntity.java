package anda.travel.driver.data.entity;

public class TaskListEntity {

    public String uuid;

    public Integer status;

    public String taskName;

    public String duration;

    public String statusStr;

//    public String taskUuid; // String	任务uuid
//    public String taskName; // String	任务名称
//    public Integer status; // Integer	任务状态
//    public Long startTime; // Date	任务开始时间
//    public Long endTime;
//
//    public String getTaskUuid() {
//        return TypeUtil.getValue(taskUuid);
//    }
//
//    /**
//     * 获取任务title
//     *
//     * @return
//     */
//    public String getTaskName() {
//        return TypeUtil.getValue(taskName);
//    }
//
//    /**
//     * 获取"任务状态"的提示语
//     *
//     * @return
//     */
//    public String getStrStatus() {
//        if (status == null) return "";
//        switch (status) {
//            case AndaTaskStatus.TASK_NOT_STARTED:
//                return "未开始";
//            case AndaTaskStatus.TASK_IN_PROGRESS:
//                return "进行中";
//            case AndaTaskStatus.TASK_COMPLETE:
//                return "已完成";
//            case AndaTaskStatus.TASK_INVALID:
//                return "已失效";
//        }
//        return "";
//    }
//
//    /**
//     * 获取任务时间段
//     *
//     * @return
//     */
//    public String getStrDuration() {
//        if (startTime == null) return "";
//        StringBuilder str = new StringBuilder();
//        try {
//            str.append(DateUtil.dateToString(new Date(startTime), "yyyy/MM/dd"));
//            if (endTime == null) return str.toString();
//            str.append(" - ");
//            str.append(DateUtil.dateToString(new Date(endTime), "yyyy/MM/dd"));
//            return str.toString();
//        } catch (Exception e) {
//            return "";
//        }
//    }

}

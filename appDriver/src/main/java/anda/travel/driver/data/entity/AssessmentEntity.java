package anda.travel.driver.data.entity;

/**
 * 司机考核
 */
public class AssessmentEntity {

    public String countConfirm;
    public String countComplete; //完成订单数
    public String successOrderRate;  //成单率
    public String totalIncome;   //订单流水
    public String income;       //收入流水
    public String averageScore;  //平均得分
    public String complainCnt;   //被投诉次数
    public String onlineTime;    //在线时长
    public String onserviceTime;  //服务时长
    public String onlineMileage;  //在线里程
    public String serviceMileage; //服务里程

    //2017-5-23 追加
    public Double perfScore; //表现分

    //20170809追加
    public Double dispatchMileage; //调度里程

    ///2020 0619追加
    public String peakTime;////高峰在线时长(毫秒)

}

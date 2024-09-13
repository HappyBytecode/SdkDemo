package anda.travel.driver.data.entity;

public class DispatchEntity {

    public String uuid; // 调度编号
    public Double endLat; //调度结束经度
    public Double endLng; //调度结束维度
    public String endAddress; //调度结束地址
    public Double actualTrip; //最后一次上传的调度里程
    public Integer endRange; //调度终点范围
    public String driverUuid;
    public String vehicleUuid;
    public long startTime;
    public String startCity;
    public String startAddress;
    public double startLat;
    public double startLng;
    public String startAccount;
    public String endTime;
    public String endCity;
    public String endReason;
    public String endAccount;
    public int mainStatus;
    public int subStatus;
    public String orderUuid;
    public double planTrip;
    public int planTime;
    public String actualTime;
    public String updater;
    public long updateTime;
    public long createTime;
    public String lastUpdTime;
}

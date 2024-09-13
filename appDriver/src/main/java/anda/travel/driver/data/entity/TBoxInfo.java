package anda.travel.driver.data.entity;

public class TBoxInfo {
    public String chargeState;//充电状态-1：停车充电 2：行驶充电 3：未充电 4：充电完成 -2：异常 -1：无效
    public String soc;//剩余电量 %
    public String vehState;//车辆状态-1：启动 2：熄火 3：其他 -2：异常 -1：无效
    public String updateTime;//更新时间
    public String todayMileage;//今日里程 km
    public String totalMileage;//累计里程 km
    public String gear;//挡位(1-6挡 13:倒挡，14自动D挡 15：P挡)
    public String vinCode;//vin码
}

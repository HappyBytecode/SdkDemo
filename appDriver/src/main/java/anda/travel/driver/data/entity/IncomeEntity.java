package anda.travel.driver.data.entity;

import java.util.List;

/**
 * Created by liuwenwu on 2021/11/15.
 * Des : 收入明细实体
 */
public class IncomeEntity {
    private double totalSalary; //总收入
    private double applicationIncome; //和行订单流水
    private double gaodeIncome; //高德订单流水
    private double carUserMoney; //车辆占用费
    private double peakPunitiveMoney; //高峰考核
    private int status; //账单状态 0：未出账，1：已出账 当为1时:显示车辆占用费/高峰考核费，其余状态不显示车辆占用费/高峰考核费
    private List<IncomeDetailEntity> details;

    public double getTotalSalary() {
        return totalSalary;
    }

    public void setTotalSalary(double totalSalary) {
        this.totalSalary = totalSalary;
    }

    public double getApplicationIncome() {
        return applicationIncome;
    }

    public void setApplicationIncome(double applicationIncome) {
        this.applicationIncome = applicationIncome;
    }

    public double getGaodeIncome() {
        return gaodeIncome;
    }

    public void setGaodeIncome(double gaodeIncome) {
        this.gaodeIncome = gaodeIncome;
    }

    public double getCarUserMoney() {
        return carUserMoney;
    }

    public void setCarUserMoney(double carUserMoney) {
        this.carUserMoney = carUserMoney;
    }

    public double getPeakPunitiveMoney() {
        return peakPunitiveMoney;
    }

    public void setPeakPunitiveMoney(double peakPunitiveMoney) {
        this.peakPunitiveMoney = peakPunitiveMoney;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<IncomeDetailEntity> getDetails() {
        return details;
    }

    public void setDetails(List<IncomeDetailEntity> details) {
        this.details = details;
    }
}

package anda.travel.driver.configurl;

import anda.travel.driver.baselibrary.utils.TypeUtil;

public class MyConfig {
    private String priceRules;
    private String userAgreement; //用户协议
    private String solution; //如何将应用添加到白名单的方法
    private String wxAppid;
    private String wxAppsecret;
    private String driverPrivacyProtocol;
    private String application;
    /**
     * 未使用的参数
     */
    private String about;
    private String cancelRule;
    private String noneOrder;
    private String withdrawRule;
    private String shareTrips;
    private String taxiPriceRules;
    private String apply; //注册连接
    private String NaviStrategy;
    private String pointRate; //传点频率（次数/秒）
    private String applyIsOpen; //是否显示"申请加入"

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getWxAppsecret() {
        return wxAppsecret;
    }

    public String getAbout() {
        return TypeUtil.getValue(about);
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getCancelRule() {
        return TypeUtil.getValue(cancelRule);
    }

    public void setCancelRule(String cancelRule) {
        this.cancelRule = cancelRule;
    }

    public String getNoneOrder() {
        return TypeUtil.getValue(noneOrder);
    }

    public void setNoneOrder(String noneOrder) {
        this.noneOrder = noneOrder;
    }

    public String getPriceRules() {
        return TypeUtil.getValue(priceRules);
    }

    public void setPriceRules(String priceRules) {
        this.priceRules = priceRules;
    }

    public String getWithdrawRule() {
        return withdrawRule;
    }

    public void setWithdrawRule(String withdrawRule) {
        this.withdrawRule = withdrawRule;
    }

    public String getShareTrips() {
        return TypeUtil.getValue(shareTrips);
    }

    public void setShareTrips(String shareTrips) {
        this.shareTrips = shareTrips;
    }

    public String getUserAgreement() {
        return TypeUtil.getValue(userAgreement);
    }

    public void setUserAgreement(String userAgreement) {
        this.userAgreement = userAgreement;
    }

    public String getTaxiPriceRules() {
        return taxiPriceRules;
    }

    public void setTaxiPriceRules(String taxiPriceRules) {
        this.taxiPriceRules = taxiPriceRules;
    }

    public String getApply() {
        return apply;
    }

    public void setApply(String apply) {
        this.apply = apply;
    }

    public String getNaviStrategy() {
        return NaviStrategy;
    }

    public void setNaviStrategy(String naviStrategy) {
        NaviStrategy = naviStrategy;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getWxAppid() {
        return wxAppid;
    }

    public void setWxAppid(String wxAppid) {
        this.wxAppid = wxAppid;
    }

    public String getPointRate() {
        return pointRate;
    }

    public void setPointRate(String pointRate) {
        this.pointRate = pointRate;
    }

    public String getApplyIsOpen() {
        return applyIsOpen;
    }

    public void setApplyIsOpen(String applyIsOpen) {
        this.applyIsOpen = applyIsOpen;
    }

    /**
     * 获取传点频率
     *
     * @return
     */
    public int getInterval() {
        try {
            if (pointRate != null) return Integer.valueOf(pointRate) * 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 10000; //默认为15秒
    }

    public String getDriverPrivacyProtocol() {
        return driverPrivacyProtocol;
    }

    public void setDriverPrivacyProtocol(String driverPrivacyProtocol) {
        this.driverPrivacyProtocol = driverPrivacyProtocol;
    }

    @Override
    public String toString() {
        return "MyConfig{" +
                "about='" + about + '\'' +
                ", cancelRule='" + cancelRule + '\'' +
                ", noneOrder='" + noneOrder + '\'' +
                ", priceRules='" + priceRules + '\'' +
                ", withdrawRule='" + withdrawRule + '\'' +
                ", shareTrips='" + shareTrips + '\'' +
                ", userAgreement='" + userAgreement + '\'' +
                ", taxiPriceRules='" + taxiPriceRules + '\'' +
                ", apply='" + apply + '\'' +
                ", NaviStrategy='" + NaviStrategy + '\'' +
                ", solution='" + solution + '\'' +
                ", wxAppid='" + wxAppid + '\'' +
                ", pointRate='" + pointRate + '\'' +
                ", applyIsOpen='" + applyIsOpen + '\'' +
                ", driverPrivacyProtocol='" + driverPrivacyProtocol + '\'' +

                '}';
    }

}

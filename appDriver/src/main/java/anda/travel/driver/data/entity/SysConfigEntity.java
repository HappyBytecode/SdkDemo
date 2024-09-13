package anda.travel.driver.data.entity;

import java.util.List;

/**
 * @Author moyuwan
 * @Date 17/11/1
 * <p>
 * 司机端系统配置
 */
public class SysConfigEntity {

    /**
     * cashFee : 0
     * cashScopeType : 1
     * copyright : All Rights Reserved © 2018和行约车
     * NaviStrategy : 2
     * isFace : true
     * drvCashExplain : 仅周三可以申请提现，可提现上周日之前的余额，提现成功后平台将在7个工作日内处理 目前仅支持提现银行卡 手续费由银行收取。
     * cashStartTime : 0000
     * clientLog_drv : 1
     * emergency_methods : 2
     * taxiFareItems : 2
     * auditWeek : 3
     * rchgInlet : 1
     * freeShippingAmount : 200
     * cashEndDate : 5
     * serverPhone : 4001608899
     * cashScopeDate : 1
     * introduction : 和行约车是江汽集团旗下、获交通部门批准的网约车平台，是集互联网出行、车辆动态监管于一体的互联网+出行平台。为用户提供本平台自有车辆及司机，实现司、车的高效管控，旨在为用户提供安全、高效、优质、满意的一站式出行解决方案；随呼随行、预约方便，增设后台配车，安全快捷，是您出行中最理想的选择！
     * invoiceContent : 运输服务费
     * fareBottomExplain : 1.高速费、过桥费、停车费等按司机实际垫付金额收取，不计入预估费用中。
     * 2.部分区域因订单较少，易造成返程空驶，价格会适当调整以补贴司机。
     * ad : [{"iosImgUrl":"https://admin.hexingyueche.com/images/ad/904dfda11756ac020f50be0fe45108f0/android.jpg","androidImgUrl":"https://admin.hexingyueche.com/images/ad/904dfda11756ac020f50be0fe45108f0/ios.jpg","actionUrl":"http://api.hexingyueche.com/admin/invite/register?adcode=&uuid=PASS3401049d0169ba434eb25f22b216&actUuid=48bc1c57bb0b4446bd39ce58848b6a1e&invitionType=1","title":"和行约车邀请好礼","expire":"2019-04-15"}]
     * drvCode : [{"title":"码上出发","url":"http://f2f/{drvUuid}","desc":"扫描二维码即可下单乘车","needShare":1},{"title":"码上有钱","url":"http://act/{drvUuid}&{adcode}","desc":"扫描二维码邀请新用户，有现金好礼相送","needShare":0}]
     * orderReportExplain : 订单播报说明： 1. 当司机有订单正在进行中时，无论出于什么界面，都不会弹出新订单； 2. 当司机端后台运行或锁屏时，订单播报可能只有声音，需进入应用操作接单； 3. 若希望随时都不错过订单，可选择“全部页面”；若希望在进行其他操作时，不被订单打断，可选择“仅首页”。
     * driverPay : 1
     * cashStartDate : 1
     * drvCutExplain : 行程费用（里程费、时长费、等待费、取消费、高速费、路桥费、停车费等由于行程产生的费用）均全部归司机所有，平台不抽取分成。 因平台提供召车服务，乘客自愿支付的调度费，平台将进行一定比例的抽成。
     * cashEndTime : 1000
     * lowestAmount : 1
     */
    private String cashFee;
    private String copyright;
    private String NaviStrategy;
    private String serverPhone;
    private String introduction;
    private String drvCode;
    private String orderReportExplain;
    private String driverCenterMenu;
    private String warnDistance;  /////订单结束前判断是否距离相差过大的值，如果大于此值，弹框提醒
    private String hintPaymentSwitch = "0";//是否催付的开关,1:开
    private String privacyConfig; //隐私协议配置
    private String driverOwnCashRule;//自营司机提现规则
    private int imSwitch; /////是否启用极光im
    private String drvPointInterval;
    private int orderAudioSwitch;//行程录音开关 1-开 0-关
    private String parkingFareTopLimit;//停车费
    private String highwayFareTopLimit;//高速费
    private String bridgeFareTopLimit;//过桥费
    private int floatWindowSwitch;//悬浮窗功能开关 1-开 0-关
    private int openMultipleRouteSelection;//多路线选择，1:开启；0:关闭
    private String drvScoreRule;//服务分计分规则
    private String zqDriverCenterMenu;//sdk（泽清）个人中心配置菜单
    private String zqDriverCenterIcon;

    public String getDriverOwnCashRule() {
        return driverOwnCashRule;
    }

    public void setDriverOwnCashRule(String driverOwnCashRule) {
        this.driverOwnCashRule = driverOwnCashRule;
    }

    public int getFloatWindowSwitch() {
        return floatWindowSwitch;
    }

    public void setFloatWindowSwitch(int floatWindowSwitch) {
        this.floatWindowSwitch = floatWindowSwitch;
    }

    public String getParkingFareTopLimit() {
        return parkingFareTopLimit;
    }

    public void setParkingFareTopLimit(String parkingFareTopLimit) {
        this.parkingFareTopLimit = parkingFareTopLimit;
    }

    public String getHighwayFareTopLimit() {
        return highwayFareTopLimit;
    }

    public void setHighwayFareTopLimit(String highwayFareTopLimit) {
        this.highwayFareTopLimit = highwayFareTopLimit;
    }

    public String getBridgeFareTopLimit() {
        return bridgeFareTopLimit;
    }

    public void setBridgeFareTopLimit(String bridgeFareTopLimit) {
        this.bridgeFareTopLimit = bridgeFareTopLimit;
    }

    public int getOrderAudioSwitch() {
        return orderAudioSwitch;
    }

    public void setOrderAudioSwitch(int orderAudioSwitch) {
        this.orderAudioSwitch = orderAudioSwitch;
    }

    public String getCashFee() {
        return cashFee;
    }

    public void setCashFee(String cashFee) {
        this.cashFee = cashFee;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getNaviStrategy() {
        return NaviStrategy;
    }

    public void setNaviStrategy(String NaviStrategy) {
        this.NaviStrategy = NaviStrategy;
    }

    public String getServerPhone() {
        return serverPhone;
    }

    public void setServerPhone(String serverPhone) {
        this.serverPhone = serverPhone;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getDrvCode() {
        return drvCode;
    }

    public void setDrvCode(String drvCode) {
        this.drvCode = drvCode;
    }

    public String getOrderReportExplain() {
        return orderReportExplain;
    }

    public void setOrderReportExplain(String orderReportExplain) {
        this.orderReportExplain = orderReportExplain;
    }

    public int getImSwitch() {
        return imSwitch;
    }

    public void setImSwitch(int imSwitch) {
        this.imSwitch = imSwitch;
    }

    public String getDrvPointInterval() {
        return drvPointInterval;
    }

    public void setDrvPointInterval(String drvPointInterval) {
        this.drvPointInterval = drvPointInterval;
    }

    public String getWarnDistance() {
        return warnDistance;
    }

    public void setWarnDistance(String warnDistance) {
        this.warnDistance = warnDistance;
    }

    public String getDriverCenterMenu() {
        return driverCenterMenu;
    }

    public void setDriverCenterMenu(String driverCenterMenu) {
        this.driverCenterMenu = driverCenterMenu;
    }

    public String getHintPaymentSwitch() {
        return hintPaymentSwitch;
    }

    public void setHintPaymentSwitch(String hintPaymentSwitch) {
        this.hintPaymentSwitch = hintPaymentSwitch;
    }

    public String getPrivacyConfig() {
        return privacyConfig;
    }

    public void setPrivacyConfig(String privacyConfig) {
        this.privacyConfig = privacyConfig;
    }

    public int getOpenMultipleRouteSelection() {
        return openMultipleRouteSelection;
    }

    public void setOpenMultipleRouteSelection(int openMultipleRouteSelection) {
        this.openMultipleRouteSelection = openMultipleRouteSelection;
    }

    public String getDrvScoreRule() {
        return drvScoreRule;
    }

    public void setDrvScoreRule(String drvScoreRule) {
        this.drvScoreRule = drvScoreRule;
    }

    public String getZqDriverCenterMenu() {
        return zqDriverCenterMenu;
    }

    public void setZqDriverCenterMenu(String zqDriverCenterMenu) {
        this.zqDriverCenterMenu = zqDriverCenterMenu;
    }

    public String getZqDriverCenterIcon() {
        return zqDriverCenterIcon;
    }

    public void setZqDriverCenterIcon(String zqDriverCenterIcon) {
        this.zqDriverCenterIcon = zqDriverCenterIcon;
    }
}

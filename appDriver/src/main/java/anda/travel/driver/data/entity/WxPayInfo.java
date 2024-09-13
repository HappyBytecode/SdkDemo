package anda.travel.driver.data.entity;

import java.io.Serializable;

public class WxPayInfo implements Serializable {

    private static final long serialVersionUID = -5300969089581287296L;

    // wxcb6db76a6e12114c
    private String appid;
    // 1linyu234qianxia567xm8yueyue9che
    private String appkey;
    // EE6E910D8A25E34748DECF784FE5E6A2
    private String noncestr;
    // Sign=WXPay
    private String pkg;
    // 1265209301
    private String partnerid;
    // wx2015090920182521c98c65040046345285
    private String prepayid;
    // 1441801105
    private String timestamp;
    // C4FC043167A1D6FD393405080AC3D6A4
    private String sign;
    // 0
    private String retcode;
    // ok
    private String retmsg;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getRetcode() {
        return retcode;
    }

    public void setRetcode(String retcode) {
        this.retcode = retcode;
    }

    public String getRetmsg() {
        return retmsg;
    }

    public void setRetmsg(String retmsg) {
        this.retmsg = retmsg;
    }

    @Override
    public String toString() {
        return "WxpayInfo [appid=" + appid + ", appkey=" + appkey
                + ", noncestr=" + noncestr + ", pkg=" + pkg + ", partnerid="
                + partnerid + ", prepayid=" + prepayid + ", timestamp="
                + timestamp + ", sign=" + sign + ", retcode=" + retcode
                + ", retmsg=" + retmsg + "]";
    }

}

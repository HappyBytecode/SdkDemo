package anda.travel.driver.baselibrary.network;

import java.io.Serializable;

/**
 * 数据请求的基础模型
 */
public class RequestBean implements Serializable {

    private Long returnTime; // 时间戳

    private Integer returnCode;// 错误码

    private String msg;// 提示语

    private String data;

    public Long getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Long returnTime) {
        this.returnTime = returnTime;
    }

    public Integer getReturnCode() {
        if (returnCode == null) return -1;
        return returnCode;
    }

    public void setReturnCode(Integer returnCode) {
        this.returnCode = returnCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
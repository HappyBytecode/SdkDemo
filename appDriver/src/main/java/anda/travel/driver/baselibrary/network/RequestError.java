package anda.travel.driver.baselibrary.network;

import android.text.TextUtils;

public class RequestError extends RuntimeException {

    public RequestError(RequestBean bean) {
        msg = bean.getMsg();
        returnCode = bean.getReturnCode();
        data = bean.getData();
    }

    public RequestError(int returnCode, String msg) {
        this.returnCode = returnCode;
        this.msg = msg;
    }

    private int returnCode;// 错误码

    private String msg;// 提示语

    private String data;

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
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

    @Override
    public String toString() {
        return "RequestError{" +
                "returnCode=" + returnCode +
                ", msg='" + msg + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (!TextUtils.isEmpty(message)) return message;
        if (!TextUtils.isEmpty(msg)) return msg;
        return "访问失败，请检查您的网络";
    }
}
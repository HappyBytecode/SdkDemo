package anda.travel.driver.client.message.body;

import anda.travel.driver.client.message.Body;

/**
 * body-登录响应
 *
 * @author Zoro
 * @date 2017/3/27
 */

public class RespLogin implements Body {

    public final static int TOKEN_INVALID = -1001;

    private boolean success;
    private String error;
    private int errCode;

    public RespLogin() {

    }

    public RespLogin(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public RespLogin(boolean success, String error, int errCode) {
        this.success = success;
        this.error = error;
        this.errCode = errCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }
}

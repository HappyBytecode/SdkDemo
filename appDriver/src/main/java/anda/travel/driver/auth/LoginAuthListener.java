package anda.travel.driver.auth;

/**
 * @author Lenovo
 */
public interface LoginAuthListener {
    /**
     * 登陆认证成功
     */
    void loginAuthSuccess();

    /**
     * 登陆认证失败
     *
     * @param failCode 错误码
     * @param errorMsg 错误码描述
     */
    void loginAuthFail(int failCode, String errorMsg);
}

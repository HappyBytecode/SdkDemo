package anda.travel.driver.auth;

/**
 * @author Lenovo
 */
public interface RegisterListener {
    /**
     * 注册成功
     */
    void registerSuccess();

    /**
     * 注册失败
     *
     * @param failCode failCode
     * @param errorMsg 错误码描述
     */
    void registerFail(int failCode, String errorMsg);
}

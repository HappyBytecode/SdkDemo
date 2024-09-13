package anda.travel.driver.module.login;

import android.content.Context;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;

/**
 * 功能描述：
 */
public interface LoginContract {

    interface View extends IBaseView<Presenter> {

        /**
         * 首次登录
         */
        void loginIsFirst(String phone);

        /**
         * 登录成功
         *
         * @param isSubstitute 是否是代班司机
         */
        void loginSuccess(boolean isSubstitute);

        /**
         * 登录失败
         *
         * @param errCode 错误码
         * @param errMsg  错误原因
         */
        void loginFail(int errCode, String errMsg);

        /**
         * 切换"输入的密码"是否可见
         *
         * @param showPwd
         */
        void changPwdDisplay(boolean showPwd);

        void clearPwd();

        /**
         * 显示封号弹窗
         */
        void showAccountUnavailable(String reason);

        /**
         * 关闭界面
         */
        void closeActivity();

        Context getContext();
    }

    interface Presenter extends IBasePresenter {
        /**
         * 登录
         *
         * @param phone 手机号
         * @param pwd   密码
         * @param pwd   密码
         */
        void reqLogin(String phone, String pwd);

        /**
         * 保存登录账户
         *
         * @param account
         */
        void saveAccount(String account);

        /**
         * 获得登录账户
         *
         * @return
         */
        String getAccount();
    }

}

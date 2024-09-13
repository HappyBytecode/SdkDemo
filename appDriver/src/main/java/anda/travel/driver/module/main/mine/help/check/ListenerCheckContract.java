package anda.travel.driver.module.main.mine.help.check;

import android.content.Context;

import java.util.List;

import anda.travel.driver.common.impl.IBasePresenter;
import anda.travel.driver.common.impl.IBaseView;
import anda.travel.driver.data.entity.CheckResultEntity;

public interface ListenerCheckContract {

    interface View extends IBaseView<Presenter> {

        void showCheckInit(); //显示初始状态

        void showCheckStart(); //开始检测的显示

        void showCheckStopAlert(); //显示"停止检测"的弹窗

        void showCheckStep2(); //第2项开始检测的显示

        void showCheckStep3(); //第3项开始检测的显示

        void setStepResult1(int strRes, boolean isError); //显示"网络检测"的结果

        void setStepResult2(int strRes, boolean isError); //显示"定位权限"的结果

        void setStepResult3(int strRes, boolean isError); //显示"司机状态"的结果

        void showResultError(List<CheckResultEntity> list); //显示错误原因

        void showResultNormal(List<String> list); //显示满足的条件

        void startAnimation(); //开启动画

        void stopAnimation(); //停止动画

        Context getContext();

    }

    interface Presenter extends IBasePresenter {

        void startCheck(); //开始检测

        void stopCheck(); //停止检测

        void checkNetwork(); //检查"网络连接"

        void checkLocation(); //检查"定位权限"

        void checkStatus(); //检查"司机状态"

        void checkFinish(); //检测结束

    }

}

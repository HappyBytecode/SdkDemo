package anda.travel.driver.module.main.mine.help.check;

import android.content.res.Resources;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.utils.NetworkUtil;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.config.DutyStatus;
import anda.travel.driver.config.RemindType;
import anda.travel.driver.data.duty.DutyRepository;
import anda.travel.driver.data.entity.CheckResultEntity;
import anda.travel.driver.data.entity.LocationEntity;
import anda.travel.driver.data.entity.OrderListenerEntity;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.module.vo.TimeVO;
import anda.travel.driver.socket.ISocketListener;
import anda.travel.driver.socket.SocketService;
import anda.travel.driver.util.PermissionCheckUtil;
import rx.Observable;

public class ListenerCheckPresenter extends BasePresenter implements ListenerCheckContract.Presenter {

    private final static int HeartBeatCount = 3; //发送心跳的次数

    private final ListenerCheckContract.View mView;
    private final UserRepository mUserRepository;
    private final DutyRepository mDutyRepository;
    private final List<CheckResultEntity> mErrorList = new ArrayList<>(); //错误原因列表
    private final List<String> mResultList = new ArrayList<>(); //可收听的订单说明

    private int mReceiveCount; //收到消息的次数
    private final ISocketListener mListener = message -> {
        mReceiveCount++; //次数加1
    };
    private boolean mCheckFinished; //检测是否已结束

    @Inject
    public ListenerCheckPresenter(ListenerCheckContract.View view, UserRepository userRepository, DutyRepository dutyRepository) {
        mView = view;
        mUserRepository = userRepository;
        mDutyRepository = dutyRepository;
    }

    @Override
    public void startCheck() {
        mView.showCheckStart();
        //每一项检测，耗时至少设置为2秒钟。
        //检测"网络连接"：1.网络 2.长连接
        //检测"定位权限"：1.是否获取到位置 2.定位权限
        //检测"司机状态"：1.是否出车 2.获取数据

        mErrorList.clear();
        checkNetwork(); //开始检测第一项
    }

    @Override
    public void stopCheck() {
        if (mCheckFinished) return;

        mDisposable.clear();
        mView.showCheckInit();
    }

    @Override
    public void checkNetwork() {
        mReceiveCount = 0; //重置为0
        boolean isNetworkAvailable = NetworkUtil.isNetworkAvailable(mView.getContext());
        if (isNetworkAvailable) {
            mDisposable.add(
                    Observable.interval(500, TimeUnit.MILLISECONDS).take(6) //每隔500ms触发一次onNext
                            .compose(RxUtil.applySchedulers())
                            .doOnSubscribe(() -> SocketService.getInstance().setSocketListener(mListener)) //设置消息监听
                            .subscribe(
                                    aLong -> {
                                        if (aLong < HeartBeatCount) { //前3次发送心跳
                                            SocketService.getInstance().sendHeartBeatMessage();
                                        }
                                    },
                                    ex -> {
                                        mView.setStepResult1(R.string.listener_check_status_error, true);
                                        mErrorList.add(new CheckResultEntity("无法连接到服务器", "请尝试重启应用，若仍无法连接请联系客服。"));
                                        checkLocation(); //开始检测第二项
                                        /////// 2019 0904 zhangyan 内存优化 开始检测第二项，不需要监听，释放资源
                                        SocketService.getInstance().setSocketListener(null);
                                    },
                                    () -> {
                                        if (mReceiveCount > 0) {
                                            mView.setStepResult1(R.string.listener_check_status_normal, false);
                                        } else {
                                            mView.setStepResult1(R.string.listener_check_status_error, true);
                                            mErrorList.add(new CheckResultEntity("无法连接到服务器", "请尝试重启应用，若仍无法连接请联系客服。"));
                                        }
                                        checkLocation(); //开始检测第二项
                                        //////2019 0904 zhangyan 内存优化 释放资源
                                        SocketService.getInstance().setSocketListener(null);
                                    }
                            )
            );
            return;
        }

        mView.setStepResult1(R.string.listener_check_status_error, true);
        mErrorList.add(new CheckResultEntity("无法连接到网络", "请检查您的4G/3G/WiFi网络连接是否正常。"));
        checkLocation(); //开始检测第二项
    }

    @Override
    public void checkLocation() {
        mView.showCheckStep2();
        mDisposable.add(
                Observable.interval(2000, TimeUnit.MILLISECONDS).take(1) //延时2秒执行
                        .compose(RxUtil.applySchedulers())
                        .subscribe(
                                aLong -> {
                                    boolean isIgnored = PermissionCheckUtil.isLocationPermissionIgnored(mView.getContext());
                                    LocationEntity location = mUserRepository.getCurrentLocation();
                                    if (isIgnored || location == null || location.lat == 0 || location.lng == 0) {
                                        mView.setStepResult2(R.string.listener_check_status_error, true);
                                        mErrorList.add(new CheckResultEntity("未获取到定位",
                                                "请在系统设置中打开定位服务，并允许" +
                                                        mView.getContext().getResources().getString(R.string.app_name) +
                                                        "使用定位。"));
                                    } else {
                                        mView.setStepResult2(R.string.listener_check_status_normal, false);
                                    }
                                    checkStatus(); //开始检测第三项
                                },
                                ex -> {
                                    mView.setStepResult2(R.string.listener_check_status_error, true);
                                    mErrorList.add(new CheckResultEntity("无法检测定位", "发生未知错误。"));
                                    checkStatus(); //开始检测第三项
                                }
                        )
        );
    }

    @Override
    public void checkStatus() {
        mView.showCheckStep3();
        mDisposable.add(
                mUserRepository.checkStatus()
                        .compose(RxUtil.applySchedulers())
                        .doOnSubscribe(mResultList::clear)
                        .subscribe(
                                entity -> {
                                    if (mDutyRepository.getIsOnDuty() != DutyStatus.ON_DUTY_INT) { //未出车
                                        mView.setStepResult3(R.string.listener_check_status_error, true);
                                        mErrorList.add(new CheckResultEntity("司机未出车", "出车后才能收听订单播报。"));
                                        if (TextUtils.isEmpty(entity.getCityName())) {
                                            mView.setStepResult3(R.string.listener_check_status_error, true);
                                            mErrorList.add(new CheckResultEntity("无运营区域",
                                                    "当前不属于任何运营区域，无法收听任何订单，请联系客服处理。"));
                                            return;
                                        }
                                        return;
                                    }

                                    if (TextUtils.isEmpty(entity.getCityName())) {
                                        mView.setStepResult3(R.string.listener_check_status_error, true);
                                        mErrorList.add(new CheckResultEntity("无运营区域",
                                                "当前不属于任何运营区域，无法收听任何订单，请联系客服处理。"));
                                        return;
                                    }

                                    Resources res = mView.getContext().getResources();
                                    mResultList.add(res.getString(R.string.check_result_match1, entity.getCityName()));
                                    mResultList.add(res.getString(R.string.check_result_match2, entity.getVehicleLevel()));
                                    OrderListenerEntity setting = mDutyRepository.getListenerSetting(mUserRepository.getLocalDriverUuid());
                                    int remindType = setting.getRemindType(); //获取订单类型
                                    if (remindType == RemindType.REALTIME.getType()) { //只收听实时单
                                        mResultList.add(res.getString(R.string.check_result_match3, entity.getPushDistanceWithFormat()));

                                    } else if (remindType == RemindType.APPOINT.getType()) { //只收听预约单
                                        mResultList.add(res.getString(R.string.check_result_match4,
                                                getRemindStr(setting.getStartTime(), setting.getEndTime())));

                                    } else { //收听全部订单
                                        mResultList.add(res.getString(R.string.check_result_match3, entity.getPushDistanceWithFormat()));
                                        mResultList.add(res.getString(R.string.check_result_match4,
                                                getRemindStr(setting.getStartTime(), setting.getEndTime())));

                                    }

                                    mResultList.add(res.getString(R.string.check_result_match5,
                                            res.getString(mUserRepository.isReportAll() ? R.string.report_all : R.string.report_special)));

                                    mView.setStepResult3(R.string.listener_check_status_normal, false);
                                },
                                ex -> {
                                    mView.setStepResult3(R.string.listener_check_status_error, true);
                                    if (NetworkUtil.isNetworkAvailable(mView.getContext())) {
                                        mErrorList.add(new CheckResultEntity("未获取到司机状态", "请检查网络连接是否正常，或联系客服。"));
                                    } else {
                                        if (mErrorList.size() == 0) {
                                            mErrorList.add(new CheckResultEntity("无法连接到服务器", "请尝试重启应用，若仍无法连接请联系客服。"));
                                        }
                                    }
                                    checkFinish();
                                },
                                this::checkFinish
                        )
        );
    }

    @Override
    public void checkFinish() {
        mDisposable.add(
                Observable.interval(500, TimeUnit.MILLISECONDS).take(1) //延时1秒执行
                        .compose(RxUtil.applySchedulers())
                        .subscribe(aLong -> {
                                },
                                ex -> checkFinishWithDisplay(),
                                this::checkFinishWithDisplay
                        )
        );
    }

    private void checkFinishWithDisplay() {
        mCheckFinished = true;
        if (mErrorList.size() > 0) {
            mView.showResultError(mErrorList);
        } else {
            mView.showResultNormal(mResultList);
        }
    }

    /**
     * 获取预约时段的显示
     *
     * @param startTime
     * @param endTime
     * @return
     */
    private String getRemindStr(Long startTime, Long endTime) {
        long current = System.currentTimeMillis();

        StringBuilder str = new StringBuilder();
        if (startTime == null || startTime <= current) {
            if (endTime == null || endTime <= current) {
                str.append("任意时间段");
            } else {
                str.append("从当前时间到");
                str.append(TimeVO.createFrom(endTime).getStrTime());
            }
        } else {
            if (endTime != null) {
                str.append("从");
                str.append(TimeVO.createFrom(startTime).getStrTime());
                str.append("到");
                str.append(TimeVO.createFrom(endTime).getStrTime());
            } else {
                str.append(TimeVO.createFrom(startTime).getStrTime());
                str.append("之后");
            }
        }
        return str.toString();
    }

}

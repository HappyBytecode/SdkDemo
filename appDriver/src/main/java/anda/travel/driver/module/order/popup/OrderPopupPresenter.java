package anda.travel.driver.module.order.popup;

import android.os.Handler;
import android.text.TextUtils;

import com.amap.api.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.network.RequestError;
import anda.travel.driver.baselibrary.utils.DateUtil;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.baselibrary.utils.TypeUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.config.HxErrorCode;
import anda.travel.driver.config.OrderStatus;
import anda.travel.driver.config.TimeType;
import anda.travel.driver.data.dispatch.DispatchRepository;
import anda.travel.driver.data.duty.DutyRepository;
import anda.travel.driver.data.entity.OrderEntity;
import anda.travel.driver.data.order.OrderRepository;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.event.OrderEvent;
import anda.travel.driver.module.vo.OrderVO;
import anda.travel.driver.socket.SocketPushContent;
import anda.travel.driver.util.SpeechUtil;
import rx.Observable;
import timber.log.Timber;

/**
 * 功能描述：
 */
public class OrderPopupPresenter extends BasePresenter implements OrderPopupContract.Presenter {

    private final OrderPopupContract.View mView;
    private final OrderRepository mOrderRepository; //订单仓库
    private final UserRepository mUserRepository; //用户仓库
    private final DispatchRepository mDispatchRepository;
    private String mOrderUuid = ""; //当前订单编号
    private boolean hasReport; //是否已播报语音
    private boolean mIsDistribute; //是否为派单（是否为实时单）
    private boolean mIsRedistribute; //是否为改派订单（4月20日 追加） mIsDistribute=true才需要判断该参数
    private boolean mIsDestroy; //是否已销毁（5月12日 追加）为修复bug添加
    private Integer mLoops; // 20170801追加：阶段数
    private Integer mLoopCnt; // 20170801追加：阶段轮数
    private final Integer mPushType; // 订单推送模式
    private OrderVO mOrderVO;

    @Inject
    public OrderPopupPresenter(OrderPopupContract.View view, OrderRepository orderRepository,
                               UserRepository userRepository, DispatchRepository dispatchRepository, DutyRepository dutyRepository) {
        mView = view;
        mOrderRepository = orderRepository;
        mUserRepository = userRepository;
        mDispatchRepository = dispatchRepository;
        mPushType = userRepository.getPushType();
    }

    @Override
    public void subscribe() {
        super.subscribe();
    }

    @Override
    public void setOrderUuid(String orderUuid) {
        mOrderUuid = orderUuid;
    }

    @Override
    public String getOrderUuid() {
        return mOrderUuid;
    }

    @Override
    public void getOrderByGrab() {
        HashMap<String, String> params = getParams();
        params.put("operateCode", "1"); //接受派单（1、手动接单，2、自动接单）
        Observable<OrderEntity> obs = mIsRedistribute
                ? mOrderRepository.acceptRedistributeOrder(params) //接受改派订单
                : mOrderRepository.acceptOrder(params); //接受派单
        mHandler.removeCallbacks(mTimerRun); //关闭定时
        mDisposable.add(obs
                .map(OrderVO::createFrom)
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingView(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(vo -> {
                    if (vo != null && vo.typeTime != null && vo.typeTime == TimeType.REALTIME) { //实时单
                        mDispatchRepository.dispatchComplete(mOrderUuid); //结束调度
                    }
                    mView.grabSuccess(vo, mIsDistribute);
                }, ex -> {
                    mView.hideLoadingView();
                    if (isOrderActionFail(ex)) {
                        grabFail(TypeUtil.getValue(((RequestError) ex).getMsg()));
                    } else {
                        mView.closeActivity();
                        showNetworkError(ex, R.string.network_error, mView, mUserRepository);
                    }
                }));
    }

    /* 判断是否是抢单失败 */
    private boolean isOrderActionFail(Throwable ex) {
        if (ex instanceof RequestError) {
            RequestError error = (RequestError) ex;
            int errCode = error.getReturnCode();
            //订单状态异常
            return errCode == HxErrorCode.ERROR_CODE_ORDER_NOT_EXIST //订单不存在
                    || errCode == HxErrorCode.ERROR_CODE_ORDER_STATUS_CONFIRM //订单已被抢
                    || errCode == HxErrorCode.ERROR_CODE_ORDER_STATUS_FINISH //订单已结束
                    || errCode == HxErrorCode.ERROR_CODE_ORDER_STATUS_CANCEL //订单已取消
                    || errCode == HxErrorCode.ERROR_CODE_ORDER_TIME_OUT //订单已过期
                    || errCode == HxErrorCode.ERROR_CODE_ORDER_STATUS_EXCEPTION
                    || errCode == HxErrorCode.ERROR_COMMON;
        }
        return false;
    }

    @Override
    public void grabFail(String failReason) {
        mHandler.removeCallbacks(mTimerRun);
        mView.grabFail(mTime, failReason); //更新显示，按键不可点击
    }

    @Override
    public LatLng getLatLng() {
        return mUserRepository.getLatLng();
    }

    @Override
    public boolean getIsDistributeOrRedistribute() {
        return (mIsDistribute || mIsRedistribute);
    }

    public boolean getIsDistribute() {
        return mIsDistribute;
    }

    void reportNewOrder(OrderVO vo, String strDistance) {
        if (hasReport) return;
        hasReport = true; //不重复播报
        String report; //播报内容
        StringBuilder str = new StringBuilder();
        if (vo.getRewardFlag() != null && vo.getRewardFlag() == OrderStatus.REWARD_FLAG) {
            str.append("接单奖，");
        }
        if (vo.typeTime != null
                && vo.typeTime == TimeType.REALTIME) {
            if (mIsDistribute) {
                str.append("派单实时，");
            } else {
                str.append("抢单实时，");
            }
        } else if (vo.typeTime != null
                && vo.typeTime == TimeType.APPOINTMENT) {
            if (mIsDistribute) {
                str.append("派单预约，");
            } else {
                str.append("抢单预约，" + DateUtil.getTodayOrTomorrow(vo.departTime));
            }
        }

        if (!TextUtils.isEmpty(strDistance)) {
            str.append(strDistance);
            str.append(",");
        }

        str.append("，从" + vo.getOriginAddress() + "到" + vo.getDestAddress());
        String strTip = vo.getStrTip();
        if (!TextUtils.isEmpty(strTip)) {
            str.append("，调度费" + strTip + "元");
        }
        if (!TextUtils.isEmpty(vo.remark)) {
            str.append("，" + vo.remark);
        }
        report = str.toString();
        SpeechUtil.speech(report);
    }

    private int mTime = 10;
    private final Handler mHandler = new Handler();
    private final Runnable mTimerRun = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(mTimerRun);
            mTime--; //时间减1
            if (mTime == 0) {
                if (mIsDistribute) {
                    mView.dispatchConfirm();
                } else {
                    if (mOrderVO.typeTime == TimeType.REALTIME) {
                        //自动抢单接口
                        getOrderByGrab();
                    } else {
                        ////预约单 自动拒绝，关闭
                        mView.closeActivityByRefuse(false);
                    }
                }
            }
            if (mIsDistribute) {
                mView.showDispatchBtn(mTime); //更新显示
            } else {
                mView.showGrabBtn(mTime);
            }
            if (mIsDestroy) return;
            if (mTime > 0) {
                mHandler.postDelayed(mTimerRun, 1000);
            }
        }
    };

    public void onCreate(OrderVO vo, boolean isRedistribute, Integer loops, Integer loopCnt) {
        EventBus.getDefault().register(this);
        Timber.e("是派单：" +
                "\n是改派订单：" + isRedistribute +
                "\n阶段数：" + loops +
                "\n阶段轮数：" + loopCnt);
        mIsRedistribute = isRedistribute;
        mLoops = loops;
        mLoopCnt = loopCnt;
        mOrderVO = vo;
        ///////预约单直接就是抢单模式除了改派订单
        //开启定时
        if (mOrderVO.typeTime == TimeType.APPOINTMENT && !mOrderVO.getDistribute()) {
            mIsDistribute = false;
            mView.showGrabBtn(mTime);
        } else {
            //开启定时
            if (1 == mPushType || isRedistribute || vo.getDistribute()) {
                mIsDistribute = true;
                mView.showDispatchBtn(mTime);
                mView.initDispatch(vo);
                if (vo.typeTime != null && vo.typeTime == TimeType.REALTIME) { //实时单
                    mDispatchRepository.dispatchComplete(mOrderUuid); //结束调度
                }
                /////派单成功，发一条消息给乘客端
                sendMessageToPassenger(vo);
            } else {
                mIsDistribute = false;
                mView.showGrabBtn(mTime);
            }
        }
        mHandler.postDelayed(mTimerRun, 1000); //开启定时
    }

    public void onDestory() {
        mIsDestroy = true;
        mHandler.removeCallbacks(mTimerRun); //关闭定时
        EventBus.getDefault().unregister(this);
    }

    public HashMap<String, String> getParams() {
        HashMap<String, String> params = new HashMap<>();
        params.put("orderUuid", mOrderUuid); //订单编号
        if (mLoops != null) params.put("loops", String.valueOf(mLoops)); //阶段数
        if (mLoopCnt != null) params.put("loopCnt", String.valueOf(mLoopCnt)); //阶段轮数
        return params;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderEvent(OrderEvent event) {
        if (event.obj1 == null) return;
        if (!(event.obj1 instanceof SocketPushContent)) return;

        SocketPushContent push = (SocketPushContent) event.obj1;
        if (!mOrderUuid.equals(push.orderUuid)) return;
        mView.hideLoadingView();

        if (push.data == null) return;
        if (event.type == OrderEvent.ORDER_PASSENGER_CANCEL) { //乘客取消订单
            mView.closeActivity();
        }
    }

    public void sendMessageToPassenger(OrderVO vo) {
        mDisposable.add(mUserRepository.getUserInfo().compose(RxUtil.applySchedulers()).subscribe(entity -> {
        }, throwable -> {
        }));
    }
}

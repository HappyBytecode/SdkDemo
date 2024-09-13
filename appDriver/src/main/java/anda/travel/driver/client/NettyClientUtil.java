package anda.travel.driver.client;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.client.constants.OperateCode;
import anda.travel.driver.client.message.PushCommon;
import anda.travel.driver.common.AppManager;
import anda.travel.driver.event.MessageEvent;
import anda.travel.driver.event.OrderChangeEvent;
import anda.travel.driver.event.OrderEvent;
import anda.travel.driver.module.main.MainActivity;
import anda.travel.driver.socket.SocketData;
import anda.travel.driver.socket.SocketPushContent;
import anda.travel.driver.socket.SocketService;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

class NettyClientUtil {

    public static void dealWithPushContent(PushCommon pushCommon) {
        if (pushCommon.getOperateCode() == OperateCode.SYSTEM_MESSAGE) return;
        SocketPushContent push = new SocketPushContent();
        push.opCode = pushCommon.getOperateCode();
        push.data = JSON.parseObject(pushCommon.getData(), SocketData.class);

        if (push.data != null) {
            push.orderUuid = push.data.orderDetailBean == null
                    ? push.data.orderUuid
                    : push.data.orderDetailBean.uuid;
            push.data.orderUuid = push.orderUuid;
        }
        if (TextUtils.isEmpty(push.orderUuid)) return;

        // 通过EventBus将对应的消息发送出来
        switch (push.opCode) {
            case OperateCode.ORDER_PUSH: //可抢订单推送
                Timber.i("-----> 可抢订单推送");
                //EventBus.getDefault().post(new OrderEvent(OrderEvent.ORDER_PUSH, push));
                dealWithNewOrder(OrderEvent.ORDER_PUSH, push);
                break;
            case OperateCode.ORDER_DISTRIBUTE: //订单派送
                Timber.i("-----> 订单派送");
                //EventBus.getDefault().post(new OrderEvent(OrderEvent.ORDER_DISTRIBUTE, push));
                dealWithNewOrder(OrderEvent.ORDER_DISTRIBUTE, push);
                break;
            case OperateCode.ORDER_PASSENGER_CANCEL: //乘客取消订单
                Timber.i("-----> 乘客取消订单");
                EventBus.getDefault().post(new OrderEvent(OrderEvent.ORDER_PASSENGER_CANCEL, push));
                EventBus.getDefault().post(new OrderChangeEvent());
                break;
            case OperateCode.ORDER_SYSTEM_CANCEL_TO_DRIVER: //系统取消，推送给司机
                Timber.i("-----> 系统取消订单");
                EventBus.getDefault().post(new OrderEvent(OrderEvent.ORDER_PASSENGER_CANCEL, push));
                EventBus.getDefault().post(new OrderChangeEvent());
                break;
            case OperateCode.ORDER_PASSENGER_ORDER_PAYED: //用户已支付
                Timber.i("-----> 用户已支付");
                EventBus.getDefault().post(new OrderEvent(OrderEvent.ORDER_PASSENGER_ORDER_PAYED, push));
                break;
            case OperateCode.ORDER_DISTRIBUTE_TO_OTHER: //订单被改派
                Timber.i("-----> 订单被改派");
                EventBus.getDefault().post(new OrderEvent(OrderEvent.ORDER_DISTRIBUTE_TO_OTHER, push));
                break;
            case OperateCode.ORDER_CHANGE_DISTRIBUTE: //收到改派订单
                Timber.i("-----> 收到改派订单");
                //EventBus.getDefault().post(new OrderEvent(OrderEvent.ORDER_CHANGE_DISTRIBUTE, push));
                dealWithNewOrder(OrderEvent.ORDER_CHANGE_DISTRIBUTE, push);
                break;
            case OperateCode.ORDER_PASSENGER_APPOTIME_REMIND: //预约提醒
                Timber.i("-----> 预约提醒");
                EventBus.getDefault().post(new MessageEvent(MessageEvent.REMIND));
                break;
            case OperateCode.FORCE_ORDER: //强制派单
                Timber.i("-----> 强制派单");
                //EventBus.getDefault().post(new OrderEvent(OrderEvent.FORCE_ORDER, push.orderUuid));
                dealWithNewOrder(OrderEvent.FORCE_ORDER, push.orderUuid);
                break;
            case OperateCode.DISPATH_ORDER: // 正式派单
                Timber.i("-----> 正式派单");
                dealWithNewOrder(OrderEvent.DISPATCH_ORDER, push.orderUuid);
                break;
            case OperateCode.ORDER_PRICE_CHANGE_TO_PASSENGER: //乘客价格变动（影响代付功能）
            case OperateCode.ORDER_PRICE_CHANGE_TO_DRIVER: //司机价格变动
                Timber.i("-----> 价格变动");
                EventBus.getDefault().post(new OrderEvent(OrderEvent.PRICE_CHANGE, push));
                break;
            case OperateCode.ORDER_SYSTEM_DIRECTLY:
                Timber.i("-----> 系统直接派单");
                dealWithNewOrder(OrderEvent.ORDER_SYSTEM_DIRECTLY, push);
                break;
            default:
                Timber.i(MessageFormat.format("-----> 未知类型的消息，需处理 opCode = {0}", push.opCode));
                break;
        }
    }

    /* ***** 20170717解决bug追加 ***** */
    private static Subscription mSub;

    private static void dealWithNewOrder(int eventType, Object obj) {
        if (SocketService.getInstance() != null
                && AppManager.getInstance().isAllActivityClosed()) {
            MainActivity.actionStartFromService(SocketService.getInstance()); //启动首页
            mSub = Observable.timer(1, TimeUnit.SECONDS) //延迟1秒，发送消息
                    .compose(RxUtil.applySchedulers())
                    .subscribe(s -> {
                        EventBus.getDefault().post(new OrderEvent(eventType, obj));
                        if (mSub != null) mSub.unsubscribe();
                    }, ex -> {
                        Timber.e("出现异常！");
                        if (mSub != null) mSub.unsubscribe();
                    });
            return;
        }
        EventBus.getDefault().post(new OrderEvent(eventType, obj));
    }

}

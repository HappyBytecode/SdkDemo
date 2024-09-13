package anda.travel.driver.util;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

import anda.travel.driver.R;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.config.OrderStatus;
import anda.travel.driver.data.entity.HxMessageEntity;
import anda.travel.driver.module.feedback.list.FeedBackListActivity;
import anda.travel.driver.module.login.LoginActivity;
import anda.travel.driver.module.main.MainActivity;
import anda.travel.driver.module.main.center.UserCenterActivity;
import anda.travel.driver.module.main.mine.help.HelpCenterActivity;
import anda.travel.driver.module.main.mine.help.feedback.FeedbackActivity;
import anda.travel.driver.module.main.mine.help.problem.ProblemActivity;
import anda.travel.driver.module.main.mine.journal.JournalActivity;
import anda.travel.driver.module.main.mine.message.MessageActivity;
import anda.travel.driver.module.main.mine.message.MessageDetailActivity;
import anda.travel.driver.module.main.mine.setting.SettingActivity;
import anda.travel.driver.module.main.mine.walletnew.MyWalletNewActivity;
import anda.travel.driver.module.main.mine.walletnew.balancedetail.BalanceDetailActivity;
import anda.travel.driver.module.main.mine.walletnew.rules.RulesActivity;
import anda.travel.driver.module.main.mine.walletnew.withdrawal.WithdrawalActivity;
import anda.travel.driver.module.main.mine.walletnew.withdrawal.bindalipay.BindAliPayActivity;
import anda.travel.driver.module.main.mine.walletnew.withdrawal.bindalipay.modify.BindAliPayModifyActivity;
import anda.travel.driver.module.main.mine.walletnew.withdrawal.withdrawalrecord.WithdrawalRecordActivity;
import anda.travel.driver.module.main.mine.walletnew.withdrawal.withdrawalrecord.withdrawldetails.WithdrawDetailsActivity;
import anda.travel.driver.module.main.orderlist.OrderListActivity;
import anda.travel.driver.module.order.begin.OrderBeginActivity;
import anda.travel.driver.module.order.detail.OrderDetailActivity;
import anda.travel.driver.module.order.ongoing.OrderOngoingActivity;
import anda.travel.driver.module.order.popup.OrderPopupActivity;
import anda.travel.driver.module.order.price.PriceCheckActivity;
import anda.travel.driver.module.order.setting.SettingOrderActivity;
import anda.travel.driver.module.vo.FaqVO;
import anda.travel.driver.module.vo.OrderVO;

public class Navigate {

    /**
     * 跳转登录页
     *
     * @param context
     */
    public static void openLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 跳转到主页
     *
     * @param context
     */
    public static void openMain(Context context, int fromType) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(IConstants.FROM_TYPE, fromType);
        context.startActivity(intent);
    }

    /**
     * 跳转到主页
     *
     * @param context
     */
    public static void openMain(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    /**
     * 打开"抢单页"
     *
     * @param context
     * @param orderUuid 订单编号
     */
    /**
     * 打开"抢单页"
     *
     * @param context
     * @param orderUuid 订单编号
     */
    public static void openOrderPopup(Context context, String orderUuid, OrderVO vo) {
        if (TextUtils.isEmpty(orderUuid)) {
            return;
        }
        if (context != null) {
            SpeechUtil.speech("司机师傅，您有新的和行订单，请查看");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent intent = new Intent();
                intent.setClass(context.getApplicationContext(), OrderPopupActivity.class);
                intent.putExtra(IConstants.ORDER_UUID, orderUuid);
                intent.putExtra(IConstants.ORDER_VO, vo);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                PendingIntent pendingIntent = PendingIntent.getActivity(context,
                        1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationChannel channel = new NotificationChannel("order_notice", "订单通知", NotificationManager.IMPORTANCE_HIGH);
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.createNotificationChannel(channel);
                Notification notification = new NotificationCompat.Builder(context, "order_notice")
                        .setSmallIcon(R.mipmap.hxyc_ic_launcher)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setOngoing(true)
                        .setContentTitle("新的订单")
                        .setContentText("司机师傅，您有新的订单，请查看！")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_CALL)
                        //设置全屏通知后，发送通知直接启动Activity
                        .setContentIntent(pendingIntent)
                        .build();
                manager.notify(10, notification);
                context.startActivity(intent);
            } else {
                Intent intent = new Intent();
                intent.setClass(context.getApplicationContext(), OrderPopupActivity.class);
                intent.putExtra(IConstants.ORDER_UUID, orderUuid);
                intent.putExtra(IConstants.ORDER_VO, vo);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(intent);
            }
        }
    }

    /**
     * 跳转到订单页
     *
     * @param context
     * @param orderUuid
     * @param vo
     */
    public static void openOrderBegin(Context context, String orderUuid, OrderVO vo) {
        Intent intent = new Intent(context, OrderBeginActivity.class);
        intent.putExtra(IConstants.ORDER_UUID, orderUuid);
        intent.putExtra(IConstants.ORDER_VO, vo);
        context.startActivity(intent);
    }

    /**
     * 跳转到订单页
     *
     * @param context
     * @param orderUuid
     * @param vo
     */
    public static void openOrderBegin1(Context context, String orderUuid, OrderVO vo) {
        Intent intent = new Intent(context, OrderBeginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(IConstants.ORDER_UUID, orderUuid);
        intent.putExtra(IConstants.ORDER_VO, vo);
        context.startActivity(intent);
    }

    /**
     * 跳转到订单页
     *
     * @param context
     * @param orderUuid
     * @param vo
     */
    public static void openOrder(Context context, String orderUuid, OrderVO vo) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra(IConstants.ORDER_UUID, orderUuid);
        intent.putExtra(IConstants.ORDER_VO, vo);
        context.startActivity(intent);
    }

    /**
     * 跳转到订单页
     *
     * @param context
     * @param orderUuid
     * @param vo
     */
    public static void openOrder1(Context context, String orderUuid, OrderVO vo) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(IConstants.ORDER_UUID, orderUuid);
        intent.putExtra(IConstants.ORDER_VO, vo);
        context.startActivity(intent);
    }

    /**
     * 跳转订单详情
     */
    public static void openOrderByConfirm(Context context, String orderUuid, OrderVO vo) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra(IConstants.ORDER_UUID, orderUuid);
        intent.putExtra(IConstants.ORDER_VO, vo);
        context.startActivity(intent);
    }

    /**
     * 跳转到订单页(进行中订单)
     *
     * @param context
     */
    public static void openOrderOngoing(Context context, String orderUuid, OrderVO vo) {
        openOrderOngoing(context, orderUuid, vo, false);
    }

    public static void openOrderOngoing(Context context, String orderUuid, OrderVO vo, boolean needReport) {
        Intent intent = new Intent(context, OrderOngoingActivity.class);
        intent.putExtra(IConstants.ORDER_UUID, orderUuid);
        intent.putExtra(IConstants.ORDER_VO, vo);
        intent.putExtra(IConstants.REPORT, needReport);
        context.startActivity(intent);
    }

    /**
     * 跳转到订单页(进行中订单)
     *
     * @param context
     */
    public static void openOrderOngoing1(Context context, String orderUuid, OrderVO vo) {
        openOrderOngoing1(context, orderUuid, vo, false);
    }

    public static void openOrderOngoing1(Context context, String orderUuid, OrderVO vo, boolean needReport) {
        Intent intent = new Intent(context, OrderOngoingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(IConstants.ORDER_UUID, orderUuid);
        intent.putExtra(IConstants.ORDER_VO, vo);
        intent.putExtra(IConstants.REPORT, needReport);
        context.startActivity(intent);
    }

    /**
     * 跳转到听单设置
     *
     * @param context
     * @param
     */
    public static void openOrderSetting(Context context) {
        Intent intent = new Intent(context, SettingOrderActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(0, 0);
    }

    /**
     * 跳转到我的钱包(新版)
     *
     * @param context
     */
    public static void openMyWalletNew(Context context) {
        Intent intent = new Intent(context, MyWalletNewActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到余额明细
     *
     * @param context
     */
    public static void openBalanceDetail(Context context) {
        Intent intent = new Intent(context, BalanceDetailActivity.class);
        context.startActivity(intent);
    }

    /**
     * 打开规则说明
     *
     * @param context
     */
    public static void openRules(Context context, boolean isBillRule) {
        Intent intent = new Intent(context, RulesActivity.class);
        intent.putExtra(IConstants.IS_BILL_RULE, isBillRule);
        context.startActivity(intent);
    }

    /**
     * 跳转到提现(新)
     *
     * @param context
     */
    public static void openWithdrawalNew(Context context, String balance
            , String driverAccountUuid) {
        Intent intent = new Intent(context, WithdrawalActivity.class);
        intent.putExtra("balance", balance);
        intent.putExtra("driverAccountUuid", driverAccountUuid);
        context.startActivity(intent);
    }

    /**
     * 跳转到提现记录
     *
     * @param context
     */
    public static void openWithdrawalRecord(Context context) {
        Intent intent = new Intent(context, WithdrawalRecordActivity.class);
        context.startActivity(intent);
    }

    /**
     * 打开提现详情
     *
     * @param context
     */
    public static void openWithdrawalDetail(Context context, String cashUuid) {
        Intent intent = new Intent(context, WithdrawDetailsActivity.class);
        intent.putExtra("cashUuid", cashUuid);
        context.startActivity(intent);
    }

    /**
     * 跳转到绑定支付宝
     *
     * @param context
     */
    public static void openBindAliPay(Context context) {
        Intent intent = new Intent(context, BindAliPayActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到绑定支付宝（已绑定）
     *
     * @param context
     */
    public static void openBindAliPay(Context context,  String bindAliAccount) {
        Intent intent = new Intent(context, BindAliPayActivity.class);
        intent.putExtra(IConstants.BIND_ZFB_ACCOUNT, bindAliAccount);
        context.startActivity(intent);
    }

    /**
     * 跳转到修改绑定支付宝
     *
     * @param context
     */
    public static void openBindAliPayModify(Context context, String bindAliAccount) {
        Intent intent = new Intent(context, BindAliPayModifyActivity.class);
        intent.putExtra(IConstants.BIND_ZFB_ACCOUNT, bindAliAccount);
        context.startActivity(intent);
    }

    /**
     * 打开流水
     *
     * @param context
     */
    public static void openJournal(Context context) {
        Intent intent = new Intent(context, JournalActivity.class);
        context.startActivity(intent);
    }

    /**
     * 打开我的设置
     *
     * @param context
     */
    public static void openSetting(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    /**
     * 打开帮助中心
     *
     * @param context
     */
    public static void openHelpCenter(Context context) {
        Intent intent = new Intent(context, HelpCenterActivity.class);
        context.startActivity(intent);
    }

    /**
     * 打开消息中心
     *
     * @param context
     */
    public static void openMessageCenter(Context context) {
        Intent intent = new Intent(context, MessageActivity.class);
        context.startActivity(intent);
    }

    /**
     * 打开建议反馈
     *
     * @param context
     */
    public static void openFeedback(Context context) {
        Intent intent = new Intent(context, FeedbackActivity.class);
        context.startActivity(intent);
    }

    /**
     * 打开建议反馈列表
     *
     * @param context
     */
    public static void openFeedbackList(Context context) {
        Intent intent = new Intent(context, FeedBackListActivity.class);
        context.startActivity(intent);
    }

    /**
     * 打开消息详情
     *
     * @param context
     */
    public static void openMsgDetail(Context context, HxMessageEntity entity) {
        Intent intent = new Intent(context, MessageDetailActivity.class);
        intent.putExtra("message", entity);
        context.startActivity(intent);
    }

    /**
     * 打开问题详情页
     *
     * @param context
     * @param faq
     */
    public static void openProblem(Context context, FaqVO faq) {
        Intent intent = new Intent(context, ProblemActivity.class);
        intent.putExtra("faq", faq);
        context.startActivity(intent);
    }

    /**
     * 根据订单状态，跳转对应的页面
     *
     * @param context
     * @param vo
     */
    public static void openOrderByStatus(Context context, OrderVO vo) {
        if (vo.mainStatus == OrderStatus.ORDER_MAIN_STATUS_DOING) {
            switch (vo.subStatus) {
                case OrderStatus.WAIT_BEGIN_APPOINTMENT: //跳转等待出发页
                    Navigate.openOrderBegin(context, vo.uuid, vo);
                    break;
                case OrderStatus.ARRIVE_DEST: //跳转费用确认(或输入)页
                    openPriceByType(context, vo);
                    break;
                default: //跳转行程中页
                    Navigate.openOrderOngoing(context, vo.uuid, vo);
                    break;
            }

        } else { //跳转订单详情
            Navigate.openOrder(context, vo.uuid, vo);
        }
    }

    /**
     * 根据订单状态，跳转对应的页面
     *
     * @param context
     * @param vo
     */
    public static void openOrderByStatusFromApp(Context context, OrderVO vo) {
        if (vo.mainStatus == OrderStatus.ORDER_MAIN_STATUS_DOING) {
            switch (vo.subStatus) {
                case OrderStatus.WAIT_BEGIN_APPOINTMENT: //跳转等待出发页
                    Navigate.openOrderBegin1(context, vo.uuid, vo);
                    break;
                case OrderStatus.ARRIVE_DEST: //跳转费用确认(或输入)页
                    openPriceByType1(context, vo);
                    break;
                default: //跳转行程中页
                    Navigate.openOrderOngoing1(context, vo.uuid, vo);
                    break;
            }

        } else { //跳转订单详情
            Navigate.openOrder1(context, vo.uuid, vo);
        }
    }

    /**
     * 根据是否计费，跳转"费用确认页"或"费用输入页"
     *
     * @param context
     * @param vo
     */
    public static void openPriceByType(Context context, OrderVO vo) {
        PriceCheckActivity.actionStart(context, vo.uuid, vo);
    }

    /**
     * 根据是否计费，跳转"费用确认页"或"费用输入页"
     *
     * @param context
     * @param vo
     */
    public static void openPriceByType1(Context context, OrderVO vo) {
        PriceCheckActivity.actionStart1(context, vo.uuid, vo);
    }

    public static void openUserCenter(Context context) {
        Intent intent = new Intent(context, UserCenterActivity.class);
        context.startActivity(intent);
    }

    public static void openOrderList(Context context) {
        Intent intent = new Intent(context, OrderListActivity.class);
        context.startActivity(intent);
    }
}

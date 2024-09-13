package anda.travel.driver.client;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;

import java.net.URI;
import java.text.MessageFormat;

import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.baselibrary.utils.TypeUtil;
import anda.travel.driver.client.codec.JacksonDecoder;
import anda.travel.driver.client.codec.JacksonEncoder;
import anda.travel.driver.client.constants.CommonConstants;
import anda.travel.driver.client.constants.MessageType;
import anda.travel.driver.client.constants.OperateCode;
import anda.travel.driver.client.message.AndaMessage;
import anda.travel.driver.client.message.AndaMessageCommon;
import anda.travel.driver.client.message.PushCommon;
import anda.travel.driver.client.message.body.RespLogin;
import anda.travel.driver.common.AppManager;
import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.data.entity.SysConfigEntity;
import anda.travel.driver.data.entity.SystemRemindEntity;
import anda.travel.driver.event.AwakenEvent;
import anda.travel.driver.event.DispatchEvent;
import anda.travel.driver.event.DutyEvent;
import anda.travel.driver.event.GaoDeServiceEvent;
import anda.travel.driver.event.MessageEvent;
import anda.travel.driver.event.OrderEvent;
import anda.travel.driver.module.RemindActivity;
import anda.travel.driver.socket.ISocket;
import anda.travel.driver.socket.ISocketListener;
import anda.travel.driver.socket.SocketEvent;
import anda.travel.driver.socket.SocketService;
import anda.travel.driver.socket.message.GetLocationOrderResponseMessage;
import anda.travel.driver.socket.message.UploadLocationResponseMessage;
import anda.travel.driver.socket.utils.InfoUtils;
import anda.travel.driver.util.SpeechUtil;
import anda.travel.driver.util.SysConfigUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.util.concurrent.Future;
import rx.Observable;
import timber.log.Timber;

/**
 * NettyClient
 *
 * @author Zoro
 * @date 2016/11/22
 */
public class NettyClient extends ChannelHandlerAdapter implements ISocket, JacksonDecoder.MsgListener {

    //private final String ACTION_SEND_TYPE = "action_send_type";
    private final String ACTION_SEND_MSG = "action_send_msg";
    private final int MESSAGE_INIT = 0x1;
    private final int MESSAGE_CONNECT = 0x2;
    private final int MESSAGE_SEND = 0x3;
    private final int MESSAGE_DISCONNECT = 0x4;

    private static final String DEFAULT_HOST = "120.77.213.255";
    private static final int DEFAULT_PORT = 10020;

    private ISocketListener mListener;
    private SocketService mService;
    private long receiveStamp; //最近一次收到推送的时间戳
    private int heartBeatCount; //记录心跳次数，收到推送消息时，需置空；一旦超过3次，重连。

    private int port;
    private String host;
    private Channel channel;
    private Bootstrap bootstrap;
    private Handler mWorkHandler;

    private final Handler.Callback mWorkHandlerCallback = new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_INIT: {
                    // 初始化Netty
                    EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
                    bootstrap = new Bootstrap();
                    bootstrap.channel(NioSocketChannel.class);
                    bootstrap.group(eventLoopGroup);
                    bootstrap.remoteAddress(host, port);
                    bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new DelimiterBasedFrameDecoder(CommonConstants.MAX_FRAME_LENGTH, Unpooled.copiedBuffer(CommonConstants.MESSAGE_SEPARATOR.getBytes())))
                                    .addLast(new JacksonDecoder(NettyClient.this))
                                    .addLast(new JacksonEncoder(CommonConstants.MESSAGE_SEPARATOR))
                                    .addLast(NettyClient.this);
                        }
                    });
                    break;
                }

                case MESSAGE_CONNECT: {
                    try {
                        if (TextUtils.isEmpty(host)) {
                            throw new Exception("Netty host or port is invalid");
                        }
                        ChannelFuture future = bootstrap.connect(host, port).sync();
                        if (future.isSuccess()) {
                            channel = future.channel();
                            Timber.d("-----> 长连接连接成功");
                        }
                        future.channel().closeFuture().sync();
                    } catch (Exception e) {
                        Timber.e("长连接连接出现异常");
                        e.printStackTrace();
                        // 发送消息
                        EventBus.getDefault().post(new SocketEvent(SocketEvent.CONNECT_ERROR));
                    }
                    break;
                }

                case MESSAGE_SEND: {
                    String sendMsg = msg.getData().getString(ACTION_SEND_MSG);
                    try {
                        if (channel != null && channel.isOpen()) {
                            channel.writeAndFlush(sendMsg).sync();
                        }
                    } catch (Exception e) {
                        Timber.e("发送消息出现异常");
                        e.printStackTrace();
                    }
                    break;
                }

                case MESSAGE_DISCONNECT: {
                    close();
                    break;
                }
            }
            return true;
        }
    };

    private void close() {
        Timber.e("-----> 触发close！");
        try {
            if (bootstrap != null) {
                if (bootstrap.group() != null) {
                    Future f = bootstrap.group().shutdownGracefully();
                    if (f.isSuccess()) {
                        Timber.d("-----> 主动断开长连接");
                    }
                }
            }
            bootstrap = null;
            channel = null;
            mService = null;
        } catch (Exception e) {
            Timber.e(MessageFormat.format("-----> 主动断开长连接 出现异常：{0}", e.getMessage()));
            e.printStackTrace();
        }
    }

    public NettyClient(URI serverURI, SocketService service) {
        mService = service;
        this.host = serverURI.getHost();
        this.port = serverURI.getPort();
        Timber.d("host = " + host + " | port = " + port);

        if (TextUtils.isEmpty(host)) host = DEFAULT_HOST;
        if (port == -1) port = DEFAULT_PORT;
        init(); //初始化长连接
    }

    private void init() {
        HandlerThread workThread = new HandlerThread(NettyClient.class.getName());
        workThread.start();
        mWorkHandler = new Handler(workThread.getLooper(), mWorkHandlerCallback);
        mWorkHandler.sendEmptyMessage(MESSAGE_INIT);
        HxClientManager.getAppComponent().inject(this); //依赖注入
    }

    @Override
    public boolean isSocketOpen() {
        return channel != null && channel.isOpen();
    }

    @Override
    public void connectSocket() throws Exception {
        mWorkHandler.sendEmptyMessageDelayed(MESSAGE_CONNECT, 100);
    }

    @Override
    public void closeSocket() {
        // 2017-07-12 为解决bug修改
        // mWorkHandler.sendEmptyMessage(MESSAGE_DISCONNECT);
        Observable.just("")
                .compose(RxUtil.saveSchedulers())
                .subscribe(s -> close(),
                        ex -> Timber.e("-----> 出错！"));
    }

    @Override
    public void sendMessage(String content) {
        // Netty发送内容，判断是否为空
        if (TextUtils.isEmpty(content)) return;

        Message message = new Message();
        Bundle bundle = new Bundle();
        message.what = MESSAGE_SEND;
        bundle.putString(ACTION_SEND_MSG, content);
        message.setData(bundle);
        mWorkHandler.sendMessage(message);
    }

    @Override
    public void sendMessage(AndaMessage msg) {
        ChannelUtil.dealSendMessage(channel, msg);
    }

    /**
     * 定时操作
     * 上传位置信息或心跳包
     *
     * @return 如果返回true，表示正常；
     * 返回false，表示长时间没收到推送，需要重连
     */
    @Override
    public boolean timerOperation() {
        if (getElapsedTime() > (mService != null ? mService.getInterval() : SocketService.INTERVAL)) {
            heartBeatCount++;
            return heartBeatCount <= 2; //超过2次心跳（大概30～45秒），没收到推送消息，则重连
        }
        return true;
    }

    @Override
    public void setSocketListener(ISocketListener listener) {
        mListener = listener;
    }

    //获取"当前时间"距离"上一次收到推送消息"的时间间隔
    private long getElapsedTime() { //距离最近一次收到消息的时间差
        return System.currentTimeMillis() - receiveStamp;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //channelActive()方法将会在连接被建立并且准备进行通信时被调用。
        super.channelActive(ctx);
        Timber.d("-----> 正在启动长连接");
        mService.dealWithLoginAction();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
//        receiveStamp = System.currentTimeMillis();
//        heartBeatCount = 0;
//        Timber.d("-----> 收到 长连接推送消息");
//
//        // 从服务端收到数据
//        ByteBuf buf = (ByteBuf) msg;
//        byte[] req = new byte[buf.readableBytes()];
//        buf.readBytes(req);
//        String message = new String(req, "UTF-8");
//        //verify(String body)方法对服务器返回的数据进行校验，并取出数据部分。
//        //具体校验的方法需要与后台同事进行协议。
//
//        Timber.d(message); //打印消息
//        dealWithMessage(message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        Timber.d("-----> 长连接出现异常");
        //exceptionCaught()事件处理方法是当出现Throwable对象才会被调用，
        //即当Netty由于IO错误或者处理器在处理事件时抛出的异常时。
        //在大部分情况下，捕获的异常应该被记录下来并且把关联的channel给关闭掉。
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelReadComplete();
    }

    @Override
    public void onReceiveMsg(String message) {
        receiveStamp = System.currentTimeMillis();
        heartBeatCount = 0;
        if (mListener != null) mListener.onReceiveMessage(message); //回调
        dealWithMessage(message); //处理数据
    }

    /**
     * 处理推送消息
     *
     * @param message
     */
    private void dealWithMessage(String message) {
        try {
            AndaMessageCommon msg = JSON.parseObject(message, AndaMessageCommon.class);

            switch (msg.getHeader().getMessageType()) {
                case MessageType.RESP_LOGIN: //登录反馈报文
                    RespLogin loginResponseMessage = JSON.parseObject(msg.getBody(), RespLogin.class);
                    //只有收到"登录反馈报文"，才算真正与服务端连接
                    boolean isSocketLogin = loginResponseMessage.isSuccess(); //是否真正连接成功
                    Timber.d("-----> 收到 登录反馈报文：isSocketLogin ＝ " + isSocketLogin);

                    if (loginResponseMessage.getErrCode() == RespLogin.TOKEN_INVALID) {
                        // 令牌失效，断开长连接
                        EventBus.getDefault().post(new SocketEvent(SocketEvent.LOGOUT));
                    }
                    break;
                case MessageType.PUSH: //推送报文
                    Timber.d("-----> 收到 推送报文：pushUuid = " + msg.getHeader().getMessageId());
                    String messageId = msg.getHeader().getMessageId();
                    mService.sendPushResponseMessage(messageId);

                    PushCommon pushMessage = JSON.parseObject(msg.getBody(), PushCommon.class);

                    Timber.d("-----> 收到 推送报文：pushUuid = " + pushMessage.getOperateCode());

                    switch (pushMessage.getOperateCode()) {
                        case OperateCode.NOTICE_DRIVER:
                            // 只对"系统通知"添加该处理，为解决bug
                            if (!InfoUtils.get().clientIsCorrect(msg.getHeader().getClientId())) {
                                Timber.e("-----> 不是该用户的消息，不作处理");
                                return;
                            }
                            mService.saveMessage(pushMessage.getData(), messageId); //保存"系统消息"，并发送通知
                            break;
                        case OperateCode.FORCE_OFF_DUTY: // 被强制收车
                            sendForceOffDuty(pushMessage);
                            break;
                        case OperateCode.DISPATCH_START: ////调度开始
                            EventBus.getDefault().post(new DispatchEvent(DispatchEvent.DISPATCH_Refresh));
                            break;
                        case OperateCode.DISPATCH_END: //调度结束
                            EventBus.getDefault().post(new DispatchEvent(DispatchEvent.DISPATCH_COMPLETE,
                                    getDispatchCompleteContent(pushMessage.getData())));
                            break;
                        case OperateCode.DISPATCH_REMIND:
                            EventBus.getDefault().post(new DispatchEvent(DispatchEvent.DISPATCH_REMIND, pushMessage.getData()));
                            break;
                        case OperateCode.SYS_WARNING://收到系统警告
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.SYS_WARNING, pushMessage.getData()));
                            break;
                        case OperateCode.SYS_REMIND:////收到系统提示信息
                            String pushData = pushMessage.getData();
                            if (!TextUtils.isEmpty(pushData)) {
                                SystemRemindEntity systemRemindEntity = JSON.parseObject(msg.getBody(), SystemRemindEntity.class);
                                if (SystemRemindEntity.NOTIFY_VOICE.equals(systemRemindEntity.getNotifyType())) {
                                    SpeechUtil.speech(AppManager.getInstance().currentActivity(), systemRemindEntity.getContent());
                                } else if (SystemRemindEntity.NOTIFY_DIALOG.equals(systemRemindEntity.getNotifyType())) {
                                    RemindActivity.actionStart(AppManager.getInstance().currentActivity(), systemRemindEntity.getTitle(), systemRemindEntity.getContent());
                                } else {
                                    SpeechUtil.speech(AppManager.getInstance().currentActivity(), systemRemindEntity.getContent());
                                    RemindActivity.actionStart(AppManager.getInstance().currentActivity(), systemRemindEntity.getTitle(), systemRemindEntity.getContent());
                                }
                            }
                            break;
                        case OperateCode.POINT_INTERVAL:
                            JSONObject point_interval = JSONObject.parseObject(pushMessage.getData());
                            String drvPointInterval = point_interval.getString("drvPointInterval");
                            SysConfigEntity sysConfigEntity = SysConfigUtils.get().getSysConfig();

                            if (null != sysConfigEntity
                                    && !TextUtils.isEmpty(sysConfigEntity.getDrvPointInterval())
                                    && !sysConfigEntity.getDrvPointInterval().equals(drvPointInterval)) {
                                sysConfigEntity.setDrvPointInterval(drvPointInterval);
                                SysConfigUtils.get().setSysConfig(sysConfigEntity);
                                SocketService.getInstance().reset();
                            }
                            break;
                        case OperateCode.FEMALE_NIGHT_FORBIDDEN:
                            try {
                                JSONObject female_night_forbidden = JSON.parseObject(pushMessage.getData());
                                String reason = female_night_forbidden.getString("msg");
                                String content = female_night_forbidden.getString("content");
                                EventBus.getDefault().post(new DutyEvent(DutyEvent.FEMALE_FORBIDDEN_NIGHT, reason, content));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case OperateCode.GAO_DE_IN_SERVICE:
                            EventBus.getDefault().post(new GaoDeServiceEvent(GaoDeServiceEvent.isInService));
                            break;
                        case OperateCode.GAO_DE_NOT_IN_SERVICE:
                            EventBus.getDefault().post(new GaoDeServiceEvent(GaoDeServiceEvent.isNotInService));
                            break;
                        case OperateCode.AWAKEN:
                            //////////////司机唤醒
                            try {
                                JSONObject awaken = JSON.parseObject(pushMessage.getData());
                                /////////传递需要播放的语音
                                String content = awaken.getString("rouseContent");
                                EventBus.getDefault().post(new AwakenEvent(AwakenEvent.AWAKEN_EVENT, content));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case OperateCode.ORDER_ABNORMAL://////////异常订单推送，安全提醒
                            EventBus.getDefault().post(new OrderEvent(OrderEvent.ORDER_ABNORMAL));
                            break;
                        case OperateCode.ORDER_CROSS_CITY://////////跨城订单
                            EventBus.getDefault().post(new OrderEvent(OrderEvent.ORDER_CROSS_CITY));
                            break;
                        case OperateCode.ORDER_CHANGE_ADDRESS: /////更改目的地
                            try {
                                JSONObject change_address = JSON.parseObject(pushMessage.getData());
                                /////////传递需要播放的语音
                                String address_content = change_address.getString("destAddress");
                                EventBus.getDefault().post(new OrderEvent(OrderEvent.ORDER_CHANGE_ADDRESS, address_content));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            NettyClientUtil.dealWithPushContent(pushMessage); //处理订单相关的推送消息
                            break;
                    }
                    break;
                case MessageType.RESP_UPLOAD_POSITION: //上传位置反馈报文
                    Timber.d("-----> 收到 上传位置反馈报文");
                    UploadLocationResponseMessage uploadLocationResponseMessage = JSON.parseObject(msg.getBody(),
                            UploadLocationResponseMessage.class);
                    if (TextUtils.isEmpty(uploadLocationResponseMessage.getOrderUuid())) return;
                    EventBus.getDefault().post(new OrderEvent(
                            OrderEvent.SPECIAL_PRICE,
                            uploadLocationResponseMessage.getOrderUuid(),
                            TypeUtil.getValue(uploadLocationResponseMessage.getTotalFare()), //将专车金额发送出来
                            (uploadLocationResponseMessage.getErrorCode() != null
                                    && uploadLocationResponseMessage.getErrorCode() == 30500)) //订单是否被改派
                    );
                    break;
                case MessageType.RESP_GET_POSITION: { //获取最近一次"上传的位置信息"的反馈报文
                    Timber.d("-----> 收到 获取最近一次\"上传的位置信息\"的反馈报文");
                    GetLocationOrderResponseMessage responseMessage = JSON.parseObject(msg.getBody(), GetLocationOrderResponseMessage.class);
                    EventBus.getDefault().post(new OrderEvent(OrderEvent.LAST_SPECIAL_INFO, responseMessage)); //发送出来
                    break;
                }
                case MessageType.RES_HEART_BEAT: //心跳反馈报文
                    Timber.d("-----> 收到 心跳反馈报文");
                    //无需处理，记录heartBeatCount即可
                    break;
                case MessageType.OFF_LINE_NOTICE://下线通知（服务端）
                    //清空用户信息，断开长连接
                    //Timber.d("-----> 收到 下线通知（服务端）");
                    EventBus.getDefault().post(new SocketEvent(SocketEvent.LOGOUT));
                    break;
                case MessageType.KNOCKOFF:  //收车（服务端）每4小时收车一次
                    EventBus.getDefault().post(new SocketEvent(SocketEvent.KNOCKOFF));
                    break;
            }
        } catch (Exception e) {
            Timber.d("-----> 解析推送消息 出现异常");
            e.printStackTrace();
        }
    }

    private String getDispatchCompleteContent(String data) {
        try {
            JSONObject obj = JSON.parseObject(data);
            return TypeUtil.getValue(obj.getString("content"));
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 发送"被强制收车"的通知
     *
     * @param pushMessage
     */
    private void sendForceOffDuty(PushCommon pushMessage) {
        String notice = null;
        if (pushMessage != null) {
            try {
                JSONObject obj = JSON.parseObject(pushMessage.getData());
                notice = obj.getString("content");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        EventBus.getDefault().post(new DutyEvent(DutyEvent.FORCE_OFF_DUTY, notice));
    }
}

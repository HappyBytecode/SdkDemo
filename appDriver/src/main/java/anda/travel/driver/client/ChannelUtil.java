package anda.travel.driver.client;

import com.alibaba.fastjson.JSON;

import java.text.MessageFormat;

import anda.travel.driver.client.message.AndaMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import timber.log.Timber;

/**
 * ChannelUtil
 *
 * @author Zoro
 */
class ChannelUtil {

    private static String shortChannelId(ChannelHandlerContext ctx) {
        return ctx.channel().id().asShortText();
    }

    /**
     * 发送消息
     *
     * @param ctx
     * @param andaMessage
     */
    public static <B> void sendMessage(ChannelHandlerContext ctx, AndaMessage<B> andaMessage) {
        dealSendMessage(ctx.channel(), andaMessage);
    }

    /**
     * 处理消息发送
     *
     * @param channel
     * @param andaMessage
     */
    public static <B> void dealSendMessage(Channel channel, AndaMessage<B> andaMessage) {
        channel.writeAndFlush(andaMessage);
        ReferenceCountUtil.release(andaMessage);
        Timber.d("-----> 发送报文：");
        Timber.d(JSON.toJSONString(andaMessage));
    }

    public static void close(ChannelHandlerContext ctx) {
        ChannelFuture future = ctx.close();
        if (future.isSuccess()) {
            Timber.d("-----> 关闭Channel 成功");
        } else {
            Timber.e(MessageFormat.format("-----> 关闭Channel 失败. clientId = {0}", shortChannelId(ctx)));
        }
    }
}

package anda.travel.driver.client.codec;

import com.alibaba.fastjson.JSON;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import timber.log.Timber;

/**
 * JacksonDecoder
 *
 * @author Zoro
 * @date 2017/3/28
 */
public class JacksonDecoder extends MessageToMessageDecoder<ByteBuf> {

    private MsgListener mListener;

    public JacksonDecoder() {
    }

    public JacksonDecoder(MsgListener listener) {
        mListener = listener;
    }

    /**
     * 执行解码操作
     *
     * @param ctx
     * @param msg
     * @param out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        final byte[] array;
        final int length = msg.readableBytes();
        array = new byte[length];
        msg.getBytes(msg.readerIndex(), array, 0, length);
        String message = JSON.parseObject(array, String.class);
        Timber.d("-----> 收到 长连接推送消息");
        Timber.d(message);
        out.add(message);

        if (mListener != null) mListener.onReceiveMsg(message);
    }

    public interface MsgListener {
        void onReceiveMsg(String message);
    }
}

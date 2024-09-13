package anda.travel.driver.client.codec;

import com.alibaba.fastjson.JSON;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * JacksonEncoder
 *
 * @author Zoro
 * @date 2017/3/28
 */
public class JacksonEncoder extends MessageToByteEncoder<Object> {

    private final String messageSeparator;

    public JacksonEncoder() {
        this(null);
    }

    public JacksonEncoder(String messageSeparator) {
        this.messageSeparator = messageSeparator;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        byte[] bytes = JSON.toJSONBytes(msg);
        out.writeBytes(bytes);
        if (null != messageSeparator) {
            out.writeBytes(messageSeparator.getBytes());
        }
    }
}

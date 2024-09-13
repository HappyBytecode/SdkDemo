package anda.travel.driver.socket;

import java.net.URI;
import java.net.URISyntaxException;

import anda.travel.driver.client.NettyClient;

class ISocketFactory {

    public static final String TYPE_TCP = "tcp";

    /**
     * 创建新的长连接
     *
     * @param service
     * @param url
     * @param
     * @return
     * @throws URISyntaxException
     */
    public static ISocket createClient(SocketService service, String url) throws URISyntaxException {
        return new NettyClient(new URI(url), service);
    }

}

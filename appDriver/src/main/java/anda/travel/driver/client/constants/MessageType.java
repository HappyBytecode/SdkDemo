package anda.travel.driver.client.constants;

/**
 * 报文类型
 *
 * @author Zoro
 * @date 2013/2/16
 */
public class MessageType {

    public static final int HEART_BEAT = 100;//心跳
    public static final int RES_HEART_BEAT = 101;//心跳响应

    public static final int LOGIN = 200;//登录（客户端发起）
    public static final int RESP_LOGIN = 201;//登录响应（服务端发起）

    public static final int PUSH = 300;//业务(服务端发起)
    public static final int RESP_PUSH = 301;//业务响应（客户端发起）

    public static final int GET_POSITION = 400;//业务(服务端发起)
    public static final int RESP_GET_POSITION = 401;//业务响应（客户端发起）

    public static final int UPLOAD_POSITION = 500;//业务(服务端发起)
    public static final int RESP_UPLOAD_POSITION = 501;//业务响应（客户端发起）

    /*新增：帐号Token失效收到长连接推送*/
    public static final int OFF_LINE_NOTICE = 600;//下线通知（服务端）
    public static final int KNOCKOFF = 601;//司机收车通知（服务端）

    /*3.1.2新增：司乘同显上传路线*/
    public static final int UPLOAD_ROUTE = 510;//上传路线

}

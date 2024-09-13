package anda.travel.driver.socket.message.base;

/**
 * 报文类型
 *
 * @author Zoro
 * @date 2016/11/22
 */
public enum MessageType {

    HEART_BEAT,//心跳（双方通信）
    LOGIN,//登录（双方通信，客户端发则是主动登录，服务端发则代表要求客户端重新登录）
    LOGIN_RESPONSE,//服务端对于客户端登录报文的响应
    PUSH,//推送（服务端）
    PUSH_RESPONSE, //推送响应（客户端对于服务端推送的响应）
    UPLOAD_LOCATION, //上传位置（客户端）
    UPLOAD_LOCATION_RESPONSE, //上传位置响应（服务端对于客户端上传位置的响应）
    GET_LOCATION_ORDER, //获取最近一次上传的位置（和订单有关）
    GET_LOCATION_ORDER_RESPONSE,

}

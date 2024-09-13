package anda.travel.driver.socket;

import anda.travel.driver.event.BaseEvent;

/**
 * 功能描述：
 */
public class SocketEvent extends BaseEvent {

    public final static int CONNECT = 1; //开启长连接
    public final static int DISCONNECT = 2; //断开长连接
    public final static int LOGOUT = 3; //退出登录
    public final static int KNOCKOFF = 4; //设置司机收车

    public final static int GET_LAST_SPECIAL_INFO = 101; //获取orderUuid对应的"最近一次上传的专车信息"

    public final static int UPLOAD_ROUTE_POINTS = 5;// 上传规划路径点
    public final static int VIEW_NAVI_INFO_UPDATE = 6;//导航信息更新
    public final static int UPLOAD_DRIVER_INDEX = 7;//上传司机位置下标

    public final static int UPLOAD_LOCATION_RIGHT_NOW = 201; //立即上传位置信息

    public final static int CONNECT_ERROR = 1000; // 20170803 连接异常

    public SocketEvent(int type) {
        super(type);
    }

    public SocketEvent(int type, Object obj1) {
        super(type, obj1);
    }

    public SocketEvent(int type, Object obj1, Object obj2) {
        super(type, obj1, obj2);
    }

}

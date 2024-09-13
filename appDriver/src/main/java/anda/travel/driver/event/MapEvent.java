package anda.travel.driver.event;

/**
 * 功能描述："地图"和"定位"相关的时间通知
 */
public class MapEvent extends BaseEvent {

    public final static int MAP_LOCATION = 1; //定位
    public final static int MAP_TRAFFIC = 2; //路况
    public final static int MAP_CALCULATE_ROUTE = 3; //路径规划规划
    public final static int MAP_RESET_ROUTE = 4; //重置路径规划
    public final static int MAP_PADDING = 5; //设置top和bottom的边距
    public final static int MAP_POINT = 6; //只显示点

    public final static int NAVI_TRAFFIC = 101; //是否显示交通状况
    public final static int NAVI_PADDING = 102; //设置top和bottom的边距
    public final static int NAVI_LOCATE = 103; //定位
    public final static int NAVI_CALCULATE_ROUTE = 104; //规划路劲
    public final static int NAVI_NAVIGATE = 105; //导航到目标位置
    public final static int NAVI_ORDER = 106; //发送订单起终点
    public final static int NAVI_EMULATOR = 107; //模拟导航

    public final static int VIEW_RESET = 201; //重置显示
    public final static int VIEW_NaviInfoUpdate = 202; //设置时间和距离显示
    public final static int VIEW_LocationChange = 203; //位置改变
    public final static int VIEW_CHANGE_ADDR = 204; //更改导航目的地
    public final static int VIEW_ARRIVE_DEST = 205; //导航到达目标地点
    public final static int VIEW_NAVI_SUCCESS = 206; //正常开启导航
    public final static int VIEW_RECALCULATE = 207; //偏航后重新计算路线回调

    public final static int VIEW_WAIT_TIME = 208;
    public final static int VIEW_WAIT_TIME_CANCEL = 209;

    public MapEvent(int type) {
        super(type);
    }

    public MapEvent(int type, Object obj1) {
        super(type, obj1);
    }

    public MapEvent(int type, Object obj1, Object obj2) {
        super(type, obj1, obj2);
    }

    public MapEvent(int type, Object obj1, Object obj2, Object obj3) {
        super(type, obj1, obj2, obj3);
    }
}

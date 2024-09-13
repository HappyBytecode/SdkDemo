package anda.travel.driver.config;

/**
 * 功能描述：常量
 */
public class IConstants {
    public final static String IS_NOT_FIRST = "IS_NOT_FIRST"; //是否为首次打开应用
    public final static String ORDER_UUID = "ORDER_UUID"; //订单编号
    public final static String BUSINESS_UUID = "BUSINESS_UUID"; //业务线编号
    public final static String ORDER_STATUS = "ORDER_STATUS"; //订单状态
    public final static String PARAMS = "PARAMS";
    public final static String CONFIG = "CONFIG";
    public final static String REFRESH = "REFRESH"; //优先从服务端获取数据
    public final static String PRICE = "PRICE"; //价格
    public final static String NOTICE_TYPE = "NOTICE_TYPE"; //提示语类型
    public final static String ORIGIN = "ORIGIN"; //起点
    public final static String DEST = "DEST"; //终点
    public final static String PROVINCE = "PROVINCE"; //省
    public final static String CITY = "CITY"; //市
    public final static String ORDER_VO = "ORDER_VO"; //订单详情
    public final static String RENTAL_BILL = "RENTAL_BILL"; //租金账单
    public final static String RENTAL_BILL_UUID = "RENTAL_BILL_UUID"; //租金账单
    public final static String TASK_UUID = "TASK_UUID"; //任务编号
    public final static String REPORT = "REPORT"; //是否语音播报
    public final static String BANK = "BANK"; //银行
    public final static String USER_UUID = "USER_UUID"; //用户编号
    public final static String MILE_TYPE = "MILE_TYPE"; //里程计算方案
    public final static String SPECIAL = "SPECIAL"; //是否是直辖市
    public final static String IS_ON_DUTY = "IS_ON_DUTY"; //是否出车
    public final static String REDISTRIBUTE_NOTICE = "REDISTRIBUTE_NOTICE"; //订单被改派的提醒
    public final static String ADCODE = "ADCODE"; //行政区域编码
    public final static String IS_DISPATCH = "IS_DISPATCH"; //是否为调度导航页
    public final static String DUTY_TIME = "DUTY_TIME"; //出车时长信息
    public final static String PUSH_TYPE = "PUSH_TYPE";//司机收到订单模式（派单/抢单）
    public final static String SELECTADDRESSTYPE = "selectaddresstype"; //订单响应时间

    public final static String FROM_TYPE = "FROM_TYPE";
    public final static String IS_BILL_RULE = "IS_BILL_RULE"; //是否账单规则说明
    public final static String BIND_ZFB_ACCOUNT = "BIND_ZFB_ACCOUNT"; //绑定的自己提现支付宝账号

    /**
     * 显示过小号弹窗
     */
    public static final String KEY_SHOW_ANONYMOUS_ORDER = "KEY_SHOW_ANONYMOUS_ORDER";

    /* 语音播报相关设置 */
    public final static String VOLUME_TYPE = "volume_type";
    public final static String VOLUME_OFF = "VOLUME_OFF";
    public final static String IMMOBILIZATION_VOLUME_SIZE = "immobilization_volume_size";
    public final static String SYSTEM_VOLUME_SIZE = "system_volume_size";

    /* 模拟导航相关 */
    public final static String NAVI_EMULATOR = "NAVI_EMULATOR"; //是否开启模拟导航

    /* 模拟导航的速度(km/h) */
    public final static int DefaultSpeed = 60; //模拟导航的速度(km/h)

    /* adcode相关 */
    public final static String DefaultAdcode = "0"; //默认的adcode

    /* 活动模块相关 */
    public final static String MODULE_RANK = "rank";
    public final static String MODULE_HEATMAP = "heatmap";

    /**
     * 订单状态
     */
    public final static int ORDER_DETAILS_HELP = 1; //客服
    public final static int ORDER_DETAILS_COMPLAIN = 2; //投诉
    public final static int ORDER_DETAILS_CANCEL = 3; //取消订单
    public final static int ORDER_DETAILS_REMIND = 4; //催款
    public final static int ORDER_DETAILS_CASH = 5; //收现
    public final static int ORDER_DETAILS_START = 6; //出发去接乘客
    public final static int ORDER_DETAILS_REFUSE = 7; //超员拒载

    /**
     * 是原生还是h5
     */
    public final static String HXNATIVE = "native"; ////原生

    public final static String HXWEBPAGE = "h5"; ////h5
    public static final String ENV = "ENV";

    /**
     * 个人中心菜单类型code
     */
    public final static String DRVCODE = "drvCode";
    public final static String INVITESHARE = "inviteShare";
    public final static String ASSESSSTATS = "workState";
    public final static String JOURNAL = "journal";
    public final static String LISTENCHECK = "listenCheck";
    public final static String SHOPPING = "shopping";
    public final static String MYWALLET = "myWallet";
    public final static String EVALUATION = "evaluation";
    public final static String SERVICEONLINE = "serviceOnline";
    public final static String CLEARCACHE = "clearCache";
    public final static String LEARNING = "learning";
    public final static String SETUP = "setUp";
    public final static String TASKLIST = "tasklist";
    public final static String CHARGE = "chargeCode";
    public final static String QUESTIONS = "questions";

    public static final String SLIDE_VIEW_TEXT = "SLIDE_VIEW_TEXT";
    public static final String AUDIO_FOLDER = "/audio";

    public static final int ADS_SPLASH = 22; ////启动页弹框
    public static final int ADS_DIALOG = 21; ///首页弹框
    public static final int ADS_TRANSVERSE = 23; ///首页弹框

    public static final String FIRST_CHANGE_PASSWORD = "FIRST_CHANGE_PASSWORD"; ///登录后第一次修改密码

    public static final String ALL_MODE_DRIVER = "1"; ////运营司机1 其他为2

    // 用户协议地址
    public static final String USER_AGREEMENT = "https://oss.hexingyueche.com/h5/driver/UserAgreement.html";
    // 隐私协议地址
    public static final String PRIVACY_POLICY = "https://oss.hexingyueche.com/h5/driver/driverPrivacyProtocol.html";

    /////移动的一键登录的app id和app key
    public static String CHINA_MOBILE_APP_ID = "300011995933";
    public static String CHINA_MOBILE_APP_KEY = "97B59B69D088A15F6D6E22C325D78F65";
    public static final int RANK_LIST = 1; //排行榜
    public static final int WEB_CUSTOMER = 2; //客服
    public static String MOBILE_SUCCESS = "103000";

    public static final String FEEDBACK = "feedback";

}

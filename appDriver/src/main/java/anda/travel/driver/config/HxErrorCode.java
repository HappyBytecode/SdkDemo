package anda.travel.driver.config;

/**
 * 功能描述：错误码
 */
public class HxErrorCode {

    public final static int DRIVER_ACCOUNT_INVALID = 20001; //账号被封
    public final static int DRIVER_DUTY_ERROR = 20002; //同车司机已出车
    public final static int NO_FACE_DATA_ERROR = 20003; //请联系管理员补齐人像资料，以便比对
    public final static int NEED_FACE_VERIFY_ERROR = 20004; //需要认证人脸

    public final static int FEMALE_NIGHT_WORK_ERROR = 20005; //女司机限时间段夜间无法出车
    public final static int NO_STUDY = 20006; //////////没有进行学习的司机，出车的时候提示去司机课堂学习

    public final static int ERROR_COMMON = 90000; //处理异常（失败）［共通］

    public final static int ERROR_NO_ILLEGAL = 90001; //验签错误，访问非法

    public final static int RETURN_CODE_ERROR_TOKEN_INVALID = 91002; //TOKEN失效，请重新登录

    public final static int ERROR_LACK_PARAMS = 90002; //缺少必要的参数

    public final static int ERROR_UNKNOWN = 99999; //未知处理异常

    /* ***** 抢单失败返回错误码 ***** */

    public final static int ERROR_CODE_ORDER_NOT_EXIST = 1000; //订单不存在

    public final static int ERROR_CODE_ORDER_STATUS_CONFIRM = 1001; //订单已被抢

    public final static int ERROR_CODE_ORDER_STATUS_FINISH = 1002; //订单已结束

    public final static int ERROR_CODE_ORDER_STATUS_CANCEL = 1003; //订单已取消

    public final static int ERROR_CODE_ORDER_TIME_OUT = 1004; //订单已过期

    public final static int ERROR_CODE_ORDER_STATUS_EXCEPTION = 1100; //订单状态异常

    public final static int ERROR_CODE_UPLOAD_AUDIO_EXCEPTION = 4001; //上传录音失败

    public final static int ERROR_REGISTER_NO_PASSWORD = 91004; //注册司机用户如果未设置密码

    public final static int ERROR_CODE_SERVICE_SCORE_GET = 90000; //司机日服务分不存在

}

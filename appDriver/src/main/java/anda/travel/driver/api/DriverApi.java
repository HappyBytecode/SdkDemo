package anda.travel.driver.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import anda.travel.driver.data.ad.AdvertisementEntity;
import anda.travel.driver.data.entity.AliAccountEntity;
import anda.travel.driver.data.entity.ApplyCityEntity;
import anda.travel.driver.data.entity.ApplyInfoEntity;
import anda.travel.driver.data.entity.ApplyProblemEntity;
import anda.travel.driver.data.entity.AssessmentEntity;
import anda.travel.driver.data.entity.BalanceDetailListEntity;
import anda.travel.driver.data.entity.BillEntity;
import anda.travel.driver.data.entity.CancelReasonEntity;
import anda.travel.driver.data.entity.CashSettingEntity;
import anda.travel.driver.data.entity.CheckStatusEntity;
import anda.travel.driver.data.entity.ComplainEntity;
import anda.travel.driver.data.entity.DriverEntity;
import anda.travel.driver.data.entity.DynamicIconEntity;
import anda.travel.driver.data.entity.EvaluateEntity;
import anda.travel.driver.data.entity.FeedBackEntity;
import anda.travel.driver.data.entity.HomeEntity;
import anda.travel.driver.data.entity.HtmlActEntity;
import anda.travel.driver.data.entity.HtmlVersionEntity;
import anda.travel.driver.data.entity.IncomeEntity;
import anda.travel.driver.data.entity.InviteCountEntity;
import anda.travel.driver.data.entity.JournalEntity;
import anda.travel.driver.data.entity.LogoffEntity;
import anda.travel.driver.data.entity.MessagesEntity;
import anda.travel.driver.data.entity.MileEntity;
import anda.travel.driver.data.entity.MyWalletEntity;
import anda.travel.driver.data.entity.NoticeEntity;
import anda.travel.driver.data.entity.OrderRespEntity;
import anda.travel.driver.data.entity.PayTypeEntity;
import anda.travel.driver.data.entity.ProblemEntity;
import anda.travel.driver.data.entity.RentBillInfoEntity;
import anda.travel.driver.data.entity.RentalBillEntity;
import anda.travel.driver.data.entity.ReputationScoreEntity;
import anda.travel.driver.data.entity.ServiceScoreEntity;
import anda.travel.driver.data.entity.SysConfigEntity;
import anda.travel.driver.data.entity.TBoxInfo;
import anda.travel.driver.data.entity.TaskDetailEntity;
import anda.travel.driver.data.entity.TaskListEntity;
import anda.travel.driver.data.entity.TaskScoreEntity;
import anda.travel.driver.data.entity.TravelScoreEntity;
import anda.travel.driver.data.entity.UpgradeEntity;
import anda.travel.driver.data.entity.VerifyToken;
import anda.travel.driver.data.entity.WarningEntity;
import anda.travel.driver.data.entity.WhiteListEntity;
import anda.travel.driver.data.entity.WithdrawRuleEntity;
import anda.travel.driver.data.entity.WithdrawalDetailsEntity;
import anda.travel.driver.data.entity.WithdrawalRecordEntity;
import anda.travel.driver.data.entity.WxPayInfo;
import anda.travel.driver.module.vo.UserInfoVO;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;
import rx.Observable;

/**
 * appid、noncestr和sign 这三个参数，将在RequestInterceptor中添加；
 * <p>
 * 调用接口时，无需传入这三个参数。
 */
public interface DriverApi {

    /**
     * 获取验证码
     *
     * @param mobile
     * @param type
     * @return
     */
    @POST("common/idCode/send")
    @FormUrlEncoded
    Observable<String> sendCode(@Field("mobile") String mobile,
                                @Field("type") int type);

    /**
     * 退出登录
     *
     * @param params
     * @return
     */
    @POST("quickLogout")
    @FormUrlEncoded
    Observable<String> logout(@FieldMap HashMap<String, String> params);

    /**
     * 获取用户信息
     *
     * @return
     */
    @POST("token/user/info/detail")
    Observable<DriverEntity> getUserInfo();

    /**
     * 获取出车状态
     *
     * @return
     */
    @POST("token/user/work/status")
    Observable<String> reqDutyStatus();

    /**
     * 出车
     *
     * @param params
     * @return
     */
    @POST("token/user/work/on")
    @FormUrlEncoded
    Observable<String> reqOnDuty(@FieldMap HashMap<String, String> params);

    /**
     * 收车
     *
     * @param params
     * @return
     */
    @POST("token/user/work/off")
    @FormUrlEncoded
    Observable<String> reqOffDuty(@FieldMap HashMap<String, String> params);

    /**
     * 获取投诉标签
     *
     * @return
     */
    @POST("common/tag/complain/list")
    @FormUrlEncoded
    Observable<List<ComplainEntity>> reqComplainMsg(@Field("nowPage") int nowPage);

    /**
     * 获取取消标签
     *
     * @return
     */
    @POST("common/tag/cancel/list")
    @FormUrlEncoded
    Observable<List<CancelReasonEntity>> reqCancelMsg(@Field("nowPage") int nowPage);

    /**
     * 获取首页信息接口
     *
     * @return
     */
    @POST("token/order/homepage/detail")
    Observable<HomeEntity> reqWorkInfo();

    /**
     * 新旧密码更改
     *
     * @param params
     * @return
     */
    @POST("token/user/pwd/upd")
    @FormUrlEncoded
    Observable<String> resetPw(@FieldMap HashMap<String, String> params);

    /**
     * 验证旧密码是否正确
     *
     * @param params
     * @return
     */
    @POST("token/user/pwd/check")
    @FormUrlEncoded
    Observable<String> checkPwd(@FieldMap HashMap<String, String> params);

    /**
     * 提现记录
     *
     * @param nowPage
     * @return
     */
    @POST("token/driverAccountApply/list")
    @FormUrlEncoded
    Observable<List<WithdrawalRecordEntity>> widthdrawalRecord(@Field("nowPage") int nowPage);
    /**
     * 获取账单列表
     *
     * @param nowPage
     * @param dateType
     * @param start
     * @param end
     * @param type
     * @return
     */
    @POST("token/user/bill/list")
    @FormUrlEncoded
    Observable<BillEntity> reqBills(@Field("nowPage") int nowPage,
                                    @Field("dateType") int dateType,
                                    @Field("start") String start,
                                    @Field("end") String end,
                                    @Field("type") String type);

    /**
     * 提现
     *
     * @param params
     * @return
     */
    @POST("token/driverAccountApply/save")
    @FormUrlEncoded
    Observable<String> withdrawal(@FieldMap HashMap<String, String> params);

    /**
     * 自营司机提现
     */
    @POST("token/driverOwnCashApply/save")
    @FormUrlEncoded
    Observable<String> withdrawalOwn(@FieldMap HashMap<String, String> params);

    /**
     * 获取提现详情
     *
     * @param cashUuid
     * @return
     */
    @POST("token/driverAccountApply/detail")
    @FormUrlEncoded
    Observable<WithdrawalDetailsEntity> getWithdrawalInfo(@Field("cashUuid") String cashUuid);

    /**
     * 获取提现详情
     *
     * @param cashUuid
     * @return
     */
    @POST("token/driverOwnCashApply/detail")
    @FormUrlEncoded
    Observable<WithdrawalDetailsEntity> getOwnWithdrawalInfo(@Field("cashUuid") String cashUuid);

    /**
     * 获取常见的问题
     *
     * @return
     */
    @POST("common/tag/problem/list")
    Observable<List<ProblemEntity>> getProblems();

    /**
     * 获取考核统计
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @POST("token/user/work/workstats")
    @FormUrlEncoded
    Observable<AssessmentEntity> reqAssessment(
            @Field("startTime") String startTime,
            @Field("endTime") String endTime);

    /**
     * 获取司机流水
     *
     * @param startTime
     * @param endTime
     * @param nowPage
     * @return
     */
    @POST("token/user/v2/journal")
    @FormUrlEncoded
    Observable<JournalEntity> reqJournal(
            @Field("startTime") String startTime,
            @Field("endTime") String endTime,
            @Field("nowPage") int nowPage);

    /**
     * 获取收入明细
     *
     * @param time
     * @return
     */
    @POST("token/salary/bill")
    @FormUrlEncoded
    Observable<IncomeEntity> reqIncomeDetail(
            @Field("time") String time);

    /**
     * 获取自营司机收入明细
     *
     * @param time
     * @return
     */
    @POST("token/driverOwnCashAccount/bill")
    @FormUrlEncoded
    Observable<IncomeEntity> reqOwnIncomeDetail(
            @Field("time") String time);

    /**
     * 获取租金列表
     *
     * @param params
     * @return
     */
    @POST("token/rent/v2/bill/list")
    @FormUrlEncoded
    Observable<List<RentalBillEntity>> reqRentalBillList(@FieldMap HashMap<String, Integer> params);

    /**
     * 获取里程
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @POST("token/tboxListInfo")
    @FormUrlEncoded
    Observable<MileEntity> reqMile(
            @Field("startDate") String startTime,
            @Field("endDate") String endTime);

    /**
     * 获取车辆信息
     *
     * @return
     */
    @POST("token/tboxBaseInfo")
    Observable<TBoxInfo> reqCarInfo();

    /**
     * 是否有车辆信息
     *
     * @return
     */
    @POST("token/isHadData")
    Observable<Boolean> reqHasData();

    /**
     * 获取消息列表
     *
     * @param nowPage
     * @return
     */
    @POST("token/user/message/list")
    @FormUrlEncoded
    Observable<MessagesEntity> reqMessages(
            @Field("nowPage") int nowPage);

    /**
     * 读消息
     *
     * @param msgUuid
     * @return
     */
    @POST("token/user/message/read")
    @FormUrlEncoded
    Observable<Integer> readMessage(
            @Field("msgUuid") String msgUuid);

    /**
     * 设置听单模式
     *
     * @param remindType
     * @param appointTimeStart
     * @param appointTimeEnd
     * @param selectedCarpool
     * @return
     */
    @POST("token/user/remind/setting")
    @FormUrlEncoded
    Observable<String> setRemindType(@Field("remindType") int remindType,
                                     @Field("appointTimeStart") String appointTimeStart,
                                     @Field("appointTimeEnd") String appointTimeEnd,
                                     @Field("isShare") int selectedCarpool);

    /**
     * 获取乘客评价统计数据
     *
     * @return
     */
    @POST("token/evaluate/info")
    Observable<EvaluateEntity> getEvaluates();

    /**
     * 获取任务中心列表
     *
     * @return
     */
    @POST("token/task/list")
    Observable<List<TaskListEntity>> getTaskList();

    /**
     * 获取任务详情
     *
     * @return
     */
    @POST("token/task/detail")
    @FormUrlEncoded
    Observable<TaskDetailEntity> getTaskDetail(@Field("taskUuid") String dailyTaskUuid);

    /**
     * 获取提现配置
     *
     * @return
     */
    @POST("token/user/cash/setting")
    Observable<CashSettingEntity> getCashSetting();

    /**
     * 2022/4/2
     * 获取自营司机提现配置
     */
    @POST("token/driverOwnCashAccount/setting")
    Observable<CashSettingEntity> getOwnCashSetting();

    /**
     * 检查版本信息
     *
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST("common/version/check")
    Observable<UpgradeEntity> getUpgradeInfo(@FieldMap HashMap<String, String> map);

    /**
     * 获取听单检测信息
     *
     * @return
     */
    @POST("token/user/listen/info")
    Observable<CheckStatusEntity> checkStauts();

    /**
     * 更新司机当前的登录信息
     *
     * @return
     */
    @FormUrlEncoded
    @POST("token/recording")
    Observable<OrderRespEntity> recording(@FieldMap HashMap<String, String> params);


    /* ********** 以上为已调整接口 ********** */


    /* ***** 2017-07-03追加，获取公告列表 ***** */
    @POST("token/common/annc/list")
    Observable<List<NoticeEntity>> reqNoticeList();

    /* ***** 20170809追加，获取支付方式 ***** */
    @POST("token/pay/payType/list")
    Observable<List<PayTypeEntity>> getPayTypeList();

    /* ***** 20170809追加，获取司机端系统配置 ***** */
    @POST("common/sysConfig")
    Observable<SysConfigEntity> sysConfig();

    /* ***** 20180302追加，代班司机绑定车辆 ***** */
    @POST("token/user/selectCar")
    @FormUrlEncoded
    Observable<String> selectCar(@Field("vehicleNo") String vehicleNo);

    @POST("common/getDriverInviteCount")
    @FormUrlEncoded
    Observable<InviteCountEntity> getInviteCount(@Field("actId") String actId);

    /**
     * 锁屏警告回调
     *
     * @param type 1/已知晓 2/已接收
     */
    @POST("warnCallBack")
    @FormUrlEncoded
    Observable<String> warCallBack(@Field("type") String type,
                                   @Field("warnUuid") String warnUuid);

    /**
     * 获取警告
     *
     * @return
     */
    @POST("getWarning")
    Observable<WarningEntity> getWarning();

    /**
     * 获取H5模块版本信息
     *
     * @param version
     * @param code
     * @return
     */
    @POST("common/modelVersion")
    @FormUrlEncoded
    Observable<HtmlVersionEntity> reqActHtmlVersion(@Field("versionNo") String version,
                                                    @Field("modelId") String code);

    /**
     * 获取H5模块信息
     *
     * @param platform
     * @return
     */
    @POST("common/getModelList")
    @FormUrlEncoded
    Observable<List<HtmlActEntity>> reqActHtmlList(@Field("platform") int platform);

    /**
     * 判断是否开始美团调度
     */
    @POST("meituan/token/startDis")
    @FormUrlEncoded
    Observable<String> replyDispatch(@FieldMap HashMap<String, String> params);

    /**
     * 司机端功能白名单
     *
     * @return
     */
    @POST("token/driverWhiteList/operateTag")
    Observable<List<WhiteListEntity>> reqWhiteList();

    /**
     * 广告,不传递Type，默认所有数据
     *
     * @return
     */
    @POST("common/ad/list")
    Observable<ArrayList<AdvertisementEntity>> getAd();

    /**
     * 3.0新的验证验证码
     *
     * @param mobile
     * @param identifyCode
     * @param type
     * @return
     */
    @POST("common/idCode/verify")
    @FormUrlEncoded
    Observable<String> verifyCodeNew(@Field("mobile") String mobile,
                                     @Field("identifyCode") String identifyCode,
                                     @Field("type") int type);

    /**
     * 3.0 重置密码
     */
    @POST("user/pwd/resetPassword")
    @FormUrlEncoded
    Observable<String> resetPassword(@Field("mobile") String mobile,
                                     @Field("password") String password,
                                     @Field("credential") String credential);

    /**
     * 3.0新登录
     *
     * @param params
     * @return
     */
    @POST("user/login")
    @FormUrlEncoded
    Observable<UserInfoVO> loginNew(@FieldMap HashMap<String, String> params);

    /**
     * 3.0 注册
     */
    @POST("apply/register")
    @FormUrlEncoded
    Observable<String> register(@Field("mobile") String mobile,
                                @Field("password") String password,
                                @Field("credential") String credential);

    /**
     * 移动一键登录
     *
     * @param params
     * @return
     */
    @POST("quickLogin")
    @FormUrlEncoded
    Observable<UserInfoVO> quickLogin(@FieldMap HashMap<String, String> params);

    /**
     * 获取申请信息及状态
     *
     * @return
     */
    @POST("apply/info")
    Observable<ApplyInfoEntity> getApplyInfo();

    /**
     * 获取申请常见问题列表
     *
     * @return
     */
    @POST("apply/problem/list")
    Observable<List<ApplyProblemEntity>> getApplyProblems();

    /**
     * 获取认证token
     *
     * @param url
     * @return
     */
    @POST
    Observable<VerifyToken> getVerifiedToken(@Url String url);

    @POST("token/user/face/getToken")
    Observable<VerifyToken> getVerifiedToken();

    /**
     * 获取开通城市
     *
     * @param url
     * @return
     */
    @POST
    Observable<List<ApplyCityEntity>> getApplyOpenCity(@Url String url);

    /**
     * 意见反馈
     *
     * @param body
     * @param parts
     * @return
     */
    @Multipart
    @POST("token/user/feedback/add")
    Observable<String> addFeedBack(@Part("content") RequestBody body,
                                   @Part MultipartBody.Part[] parts);

    /**
     * 获取意见反馈列表
     *
     * @param nowPage
     * @return
     */
    @POST("token/user/feedback/list")
    @FormUrlEncoded
    Observable<List<FeedBackEntity>> feedBackList(@Field("nowPage") int nowPage);

    /**
     * 获取首页的Icon配置
     *
     * @return
     */
    @POST("app/images")
    Observable<DynamicIconEntity> mainImages();

    /**
     * 司机租金账单微信支付接口(新)(2022-2-15)
     */
    @POST("token/rentBill/v2/wx/tradeUrl")
    @FormUrlEncoded
    Observable<WxPayInfo> payByWeChat(@FieldMap HashMap<String, String> params);

    /**
     * 司机租金账单支付宝支付接口（新）(2022-2-15)
     */
    @POST("token/rentBill/v2/aliPay/tradeUrl")
    @FormUrlEncoded
    Observable<String> payByAliPay(@FieldMap HashMap<String, String> params);

    /**
     * 账号注销
     *
     * @return
     */
    @POST("token/user/logoff")
    Observable<String> logoff();

    /**
     * 用户是否可账号注销
     *
     * @return
     */
    @POST("token/user/isLogoff")
    Observable<LogoffEntity> isLogoff();

    /**
     * 司机余额明细(新)(2022-6-2)
     */
    @POST("token/driverAccount/myWallet/list")
    @FormUrlEncoded
    Observable<List<BalanceDetailListEntity>> reqBalanceDetailList(@FieldMap HashMap<String, String> params);

    /**
     * 司机提现-我的钱包数据查询
     */
    @POST("token/driverAccount/myWallet")
    Observable<MyWalletEntity> getMyWallet();

    /**
     * 司机租金账单信息获取
     */
    @POST("token/rent/bill/info")
    Observable<RentBillInfoEntity> getRentBillInfo();

    /**
     * 司机提现-司机绑定账户查询
     */
    @POST("token/driverAccountApply/findBingAliAccount")
    Observable<String> findBindAliAccount();

    /**
     * 司机提现-司机最近提现成功账户查询
     */
    @POST("token/driverAccountApply/findAliAccountBySuccess")
    Observable<AliAccountEntity> findAliAccountBySuccess();

    /**
     * 司机提现-支付宝绑定/修改确认
     */
    @POST("token/driverAccount/bindAliAccount")
    @FormUrlEncoded
    Observable<String> bindAliAccount(@Field("mobile") String mobile,
                                      @Field("identifyCode") String identifyCode);

    /**
     * 司机钱包-未入账金额说明
     */
    @POST("token/driverAccount/notAvailableMoneyDesc")
    Observable<WithdrawRuleEntity> getNotAvailableMoneyDesc();

    /**
     * 获取服务分
     *
     * @return
     */
    @POST("token/driverScore/info")
    Observable<ServiceScoreEntity> reqServiceScore();

    /**
     * 获取口碑分
     *
     * @return
     */
    @POST("token/evaluateScore/Details")
    Observable<ReputationScoreEntity> reqReputationScore();

    /**
     * 获取出行分
     *
     * @return
     */
    @POST("token/tripScore/Details")
    Observable<TravelScoreEntity> reqTravelScore();

    /**
     * 获取任务分
     *
     * @return
     */
    @POST("token/taskScore/Details")
    Observable<TaskScoreEntity> reqTaskScore();

    /**
     * 变更手机号
     */
    @POST("token/user/change/mobile")
    @FormUrlEncoded
    Observable<String> changeMobile(@Field("mobile") String mobile,
                                    @Field("code") String code);

    /**
     * 注册-泽清
     */
    @POST("zq/register")
    @FormUrlEncoded
    Observable<UserInfoVO> registerZQ(@FieldMap HashMap<String, String> params);

    /**
     * 登陆认证-泽清
     */
    @POST("zq/auth")
    @FormUrlEncoded
    Observable<UserInfoVO> LoginAuthZQ(@FieldMap HashMap<String, String> params);
}


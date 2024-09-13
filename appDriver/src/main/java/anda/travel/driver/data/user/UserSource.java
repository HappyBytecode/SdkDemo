package anda.travel.driver.data.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import anda.travel.driver.baselibrary.utils.SP;
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
import anda.travel.driver.data.entity.LocationEntity;
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
import anda.travel.driver.module.vo.AddressVO;
import anda.travel.driver.module.vo.UserInfoVO;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;

public interface UserSource {

    Observable<String> reqLogout();

    Observable<String> reqLogout(HashMap<String, String> params);

    Observable<DriverEntity> getUserInfo();

    Observable<DriverEntity> getUserInfoFromRemote();

    DriverEntity getUserInfoFromLocal();

    void refreshUserInfo();

    void setUserInfo(DriverEntity entity);

    boolean isLogin();

    Observable<List<WithdrawalRecordEntity>> widthdrawalRecord(int nowPage);

    Observable<BillEntity> reqBills(int nowPage, int dateType, String start, String end, String type);

    Observable<List<ProblemEntity>> getProblems();

    Observable<WithdrawalDetailsEntity> getWithdrawalInfo(String cashUuid);

    Observable<WithdrawalDetailsEntity> getOwnWithdrawalInfo(String cashUuid);

    Observable<String> withdrawal(HashMap<String, String> params);

    Observable<String> withdrawalOwn(HashMap<String, String> params);

    Observable<AssessmentEntity> reqAssessment(String startTime, String endTime);

    Observable<JournalEntity> reqJournal(String startTime, String endTime, int page);

    Observable<IncomeEntity> reqIncomeDetail(String time);

    Observable<IncomeEntity> reqOwnIncomeDetail(String time);

    Observable<List<RentalBillEntity>> reqRentalBillList(HashMap<String, Integer> params);

    Observable<MileEntity> reqMile(String startTime, String endTime);

    Observable<TBoxInfo> reqCarInfo();

    Observable<Boolean> reqHasData();

    Observable<MessagesEntity> reqMessages(int page);

    Observable<Integer> readMessage(String msgUuid);

    Observable<EvaluateEntity> getEvaluates();

    Observable<String> checkPwd(String password);

    Observable<String> resetPw(String newPsw, String oldPsw);

    Observable<String> sendCode(String mobile, int type);

    Observable<HomeEntity> reqWorkInfo();

    Observable<List<CancelReasonEntity>> reqCancelMsg();

    Observable<List<ComplainEntity>> reqComplainMsg();

    String getLocalDriverUuid(); //获取当前用户的uuid

    List<AddressVO> getHistoryAddr(); //获取历史导航地址

    void saveProvince(String province);

    void saveCity(String city);

    String getProvince();

    String getCity();

    void setNetworkStatus(boolean isDisconnect);

    void setCurrentLocation(LocationEntity location);

    LocationEntity getCurrentLocation();

    Observable<List<TaskListEntity>> getTaskList();

    Observable<TaskDetailEntity> getTaskDetail(String taskUuid);

    /* ********** 以下为已调整的方法 ********** */

    void setCancelMsgList(List<CancelReasonEntity> list);

    List<CancelReasonEntity> getCancelMsgList();

    void setDebugEmulator(boolean isEmulator);

    boolean getDebugEmulator();

    void setFAQList(List<ProblemEntity> list);

    List<ProblemEntity> getFAQList();

    Observable<CashSettingEntity> getCashSetting();

    Observable<CashSettingEntity> getOwnCashSetting();

    String getUserUuid();

    Observable<UpgradeEntity> getUpgradeInfo(String version);

    void setIsDependDriver(int depend);

    boolean getIsDependDriver();

    Observable<CheckStatusEntity> checkStatus();

    int getMileType(); //里程计算方案

    Observable<OrderRespEntity> recording(HashMap<String, String> params); //更新司机当前的登录信息

    SP getSP();

    Observable<List<NoticeEntity>> getNoticeList(); //获取公告列表

    void setLocalNoticeList(List<NoticeEntity> list);

    List<NoticeEntity> getLocalNoticeList();

    Observable<List<PayTypeEntity>> getPayTypeList();

    List<PayTypeEntity> getLocalPayTypeList();

    void setLocalPayTypeList(List<PayTypeEntity> list);

    Observable<SysConfigEntity> reqSysConfig();

    Observable<String> selectCar(String vehicleNo); //代班司机绑定车辆

    boolean isReportAll();

    void setReportAll(boolean isReportAll);

    boolean isOnSetting();

    void setIsOnSetting(boolean isOnSetting);

    Observable<List<WithdrawRuleEntity>> withdrawRule(); //司机提现/抽成说明接口

    Observable<InviteCountEntity> getInviteCount(String actId);

    Observable<String> warnCallback(String type, String warnUuid);

    Observable<WarningEntity> getWarning();

    Observable<HtmlVersionEntity> reqActHtmlVersion(String versionNo, String modelId);

    Observable<List<HtmlActEntity>> reqActHtmlList();

    HtmlVersionEntity getHtmlModule();

    void setHtmlModule(HtmlVersionEntity entity);

    void setPushType(Integer pushType);

    Integer getPushType();

    Observable<String> replyDispatch(HashMap<String, String> params); //司机回复调度

    void setNavVoiceMode(int mode);

    int getNavVoiceMode();

    Observable<String> setDispatchOrderMode(int mode);

    void setVolumeOff(Boolean isOn);

    Boolean isVolumeOff();

    void setPushOff(Boolean isPush);

    Boolean isPushOff();

    Observable<List<WhiteListEntity>> reqWhiteList();

    Observable<ArrayList<AdvertisementEntity>> getAd();

    Observable<String> verifyCodeNew(String mobile, String identifyCode, int type);

    Observable<String> resetPassword(String mobile, String password, String credential);

    Observable<UserInfoVO> reqLoginNew(HashMap<String, String> params);

    Observable<String> register(String mobile, String password, String credential);

    Observable<UserInfoVO> oneKeyLogin(HashMap<String, String> params);

    Observable<ApplyInfoEntity> getApplyInfo();

    Observable<List<ApplyProblemEntity>> getApplyProblems();

    Observable<VerifyToken> getVerifiedToken(String url);

    Observable<VerifyToken> getVerifiedToken();

    Observable<List<ApplyCityEntity>> getApplyOpenCity(String url);

    /////添加意见反馈
    Observable<String> addFeedBack(RequestBody content, MultipartBody.Part[] parts);

    /////意见反馈列表
    Observable<List<FeedBackEntity>> feedBackList(int nowPage);

    ///获取首页皮肤配置
    Observable<DynamicIconEntity> mainImages();

    //司机租车账单微信支付
    Observable<WxPayInfo> payByWeChat(String rentalBillUuid, String spbillCreateIp);

    //司机租车账单支付宝支付
    Observable<String> payByAliPay(String rentalBillUuid);

    //账号注销
    Observable<String> logoff();

    //用户是否可账号注销
    Observable<LogoffEntity> isLogoff();

    Observable<List<WithdrawRuleEntity>> billRule(); //账单规则说明接口

    Observable<List<BalanceDetailListEntity>> reqBalanceDetailList(HashMap<String, String> params);

    //司机提现-我的钱包数据
    Observable<MyWalletEntity> getMyWallet();

    //司机租金账单信息获取
    Observable<RentBillInfoEntity> getRentBillInfo();

    //司机提现-司机绑定账户查询
    Observable<String> findBindAliAccount();

    //司机提现-司机最近提现成功账户查询
    Observable<AliAccountEntity> findAliAccountBySuccess();

    //司机提现-支付宝绑定/修改确认
    Observable<String> bindAliAccount(String mobile, String identifyCode);

    //司机钱包-未入账金额说明
    Observable<WithdrawRuleEntity> getNotAvailableMoneyDesc();

    //司机服务分
    Observable<ServiceScoreEntity> reqServiceScore();

    //司机口碑分
    Observable<ReputationScoreEntity> reqReputationScore();

    //司机出行分
    Observable<TravelScoreEntity> reqTravelScore();

    //司机任务分
    Observable<TaskScoreEntity> reqTaskScore();

    //变更手机号
    Observable<String> changeMobile(String mobile, String code);

    //注册-泽清
    Observable<UserInfoVO> registerZQ(HashMap<String, String> params);

    //注册-登陆认证
    Observable<UserInfoVO> loginAuthZQ(HashMap<String, String> params);

}

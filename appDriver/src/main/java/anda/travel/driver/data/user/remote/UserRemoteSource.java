package anda.travel.driver.data.user.remote;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.api.DriverApi;
import anda.travel.driver.api.OrderApi;
import anda.travel.driver.api.RootApi;
import anda.travel.driver.baselibrary.network.RequestParams;
import anda.travel.driver.baselibrary.utils.SP;
import anda.travel.driver.baselibrary.utils.security.EncryptionUtil;
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
import anda.travel.driver.data.user.UserSource;
import anda.travel.driver.module.vo.AddressVO;
import anda.travel.driver.module.vo.UserInfoVO;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;

public class UserRemoteSource implements UserSource {

    private final DriverApi mDriverApi;
    private final OrderApi mOrderApi;
    private final RootApi mRootApi;
    private final Context mContext;
    private Observable<DriverEntity> mShareGetUserInfoObservable;
    private long mLastGetUserInfoStamp;

    @Inject
    UserRemoteSource(DriverApi driverApi, OrderApi orderApi, RootApi rootApi, Context context) {
        mDriverApi = driverApi;
        mRootApi = rootApi;
        mOrderApi = orderApi;
        mContext = context;
    }

    @Override
    public Observable<String> reqLogout() {
        return reqLogout(new HashMap<>());
    }

    @Override
    public Observable<String> reqLogout(HashMap<String, String> params) {
        return mDriverApi.logout(params);
    }

    @Override
    public Observable<DriverEntity> getUserInfo() {
        if (mShareGetUserInfoObservable == null || mLastGetUserInfoStamp + 3000 < System.currentTimeMillis()) {
            mLastGetUserInfoStamp = System.currentTimeMillis();
            mShareGetUserInfoObservable = mDriverApi.getUserInfo().share();
        }
        return mShareGetUserInfoObservable;
    }

    @Override
    public Observable<DriverEntity> getUserInfoFromRemote() {
        return getUserInfo();
    }

    @Override
    public DriverEntity getUserInfoFromLocal() {
        return null;
    }

    @Override
    public void refreshUserInfo() {
        mShareGetUserInfoObservable = null;
    }

    @Override
    public void setUserInfo(DriverEntity entity) {

    }

    @Override
    public boolean isLogin() {
        return false;
    }

    @Override
    public Observable<List<WithdrawalRecordEntity>> widthdrawalRecord(int nowPage) {
        return mDriverApi.widthdrawalRecord(nowPage);
    }
    @Override
    public Observable<BillEntity> reqBills(int nowPage, int dateType, String start, String end, String type) {
        return mDriverApi.reqBills(nowPage, dateType, start, end, type);
    }

    @Override
    public Observable<List<ProblemEntity>> getProblems() {
        return mDriverApi.getProblems();
    }


    @Override
    public Observable<WithdrawalDetailsEntity> getWithdrawalInfo(String cashUuid) {
        return mDriverApi.getWithdrawalInfo(cashUuid);
    }

    @Override
    public Observable<WithdrawalDetailsEntity> getOwnWithdrawalInfo(String cashUuid) {
        return mDriverApi.getOwnWithdrawalInfo(cashUuid);
    }

    @Override
    public Observable<String> withdrawal(HashMap<String, String> params) {
        return mDriverApi.withdrawal(params);
    }

    @Override
    public Observable<String> withdrawalOwn(HashMap<String, String> params) {
        return mDriverApi.withdrawalOwn(params);
    }

    @Override
    public Observable<AssessmentEntity> reqAssessment(String startTime, String endTime) {
        return mDriverApi.reqAssessment(startTime, endTime);
    }

    @Override
    public Observable<JournalEntity> reqJournal(String startTime, String endTime, int page) {
        return mDriverApi.reqJournal(startTime, endTime, page);
    }

    @Override
    public Observable<IncomeEntity> reqIncomeDetail(String time) {
        return mDriverApi.reqIncomeDetail(time);
    }

    @Override
    public Observable<IncomeEntity> reqOwnIncomeDetail(String time) {
        return mDriverApi.reqOwnIncomeDetail(time);
    }

    @Override
    public Observable<List<RentalBillEntity>> reqRentalBillList(HashMap<String, Integer> params) {
        return mDriverApi.reqRentalBillList(params);
    }

    @Override
    public Observable<MileEntity> reqMile(String startTime, String endTime) {
        return mDriverApi.reqMile(startTime, endTime);
    }

    @Override
    public Observable<TBoxInfo> reqCarInfo() {
        return mDriverApi.reqCarInfo();
    }

    @Override
    public Observable<Boolean> reqHasData() {
        return mDriverApi.reqHasData();
    }

    @Override
    public Observable<MessagesEntity> reqMessages(int page) {
        return mDriverApi.reqMessages(page);
    }

    @Override
    public Observable<Integer> readMessage(String msgUuid) {
        return mDriverApi.readMessage(msgUuid);
    }

    @Override
    public Observable<EvaluateEntity> getEvaluates() {
        return mDriverApi.getEvaluates();
    }

    @Override
    public Observable<String> checkPwd(String password) {
        return null;
    }

    @Override
    public Observable<String> resetPw(String newPsw, String oldPsw) {
        return null;
//        return mDriverApi.resetPw(newPsw, oldPsw);
    }

    @Override
    public Observable<String> sendCode(String mobile, int type) {
        return mDriverApi.sendCode(mobile, type);
    }

    @Override
    public Observable<HomeEntity> reqWorkInfo() {
        return mDriverApi.reqWorkInfo();
    }

    @Override
    public Observable<List<CancelReasonEntity>> reqCancelMsg() {
        return mDriverApi.reqCancelMsg(0);
    }

    @Override
    public Observable<List<ComplainEntity>> reqComplainMsg() {
        return mDriverApi.reqComplainMsg(0);
    }

    @Override
    public String getLocalDriverUuid() {
        return null;
    }

    @Override
    public List<AddressVO> getHistoryAddr() {
        return null;
    }

    @Override
    public void saveProvince(String province) {

    }

    @Override
    public void saveCity(String city) {

    }

    @Override
    public String getProvince() {
        return null;
    }

    @Override
    public String getCity() {
        return null;
    }

    @Override
    public void setNetworkStatus(boolean isDisconnect) {

    }

    @Override
    public void setCurrentLocation(LocationEntity location) {

    }

    @Override
    public LocationEntity getCurrentLocation() {
        return null;
    }

    @Override
    public Observable<List<TaskListEntity>> getTaskList() {
        return mDriverApi.getTaskList();
    }

    @Override
    public Observable<TaskDetailEntity> getTaskDetail(String taskUuid) {
        return mDriverApi.getTaskDetail(taskUuid);
    }

    private static String getClientSign(String mobile, String noncestr) {
        String params = "mobile=" + mobile;
        params += "&noncestr=" + noncestr;
        params += "&key=Xmhldd957&o04-1mfvclk;ahgudflkd2523";
        return EncryptionUtil.md5Encode(params).toUpperCase();
    }

    /* ********** 以下为已调整的方法 ********** */

    @Override
    public void setCancelMsgList(List<CancelReasonEntity> list) {

    }

    @Override
    public List<CancelReasonEntity> getCancelMsgList() {
        return null;
    }


    @Override
    public void setDebugEmulator(boolean isEmulator) {

    }

    @Override
    public boolean getDebugEmulator() {
        return false;
    }

    @Override
    public void setFAQList(List<ProblemEntity> list) {

    }

    @Override
    public List<ProblemEntity> getFAQList() {
        return null;
    }

    @Override
    public Observable<CashSettingEntity> getCashSetting() {
        return mDriverApi.getCashSetting();
    }

    @Override
    public Observable<CashSettingEntity> getOwnCashSetting() {
        return mDriverApi.getOwnCashSetting();
    }

    @Override
    public String getUserUuid() {
        return null;
    }

    @Override
    public Observable<UpgradeEntity> getUpgradeInfo(String version) {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("platform", "android");
//        map.put("versionNo", version);
//        return mDriverApi.getUpgradeInfo(map);
        return null;
    }

    @Override
    public void setIsDependDriver(int depend) {

    }

    @Override
    public boolean getIsDependDriver() {
        return false;
    }

    @Override
    public Observable<CheckStatusEntity> checkStatus() {
        return mDriverApi.checkStauts();
    }

    @Override
    public int getMileType() {
        return 0;
    }

    @Override
    public Observable<OrderRespEntity> recording(HashMap<String, String> params) {
        return mDriverApi.recording(params);
    }

    @Override
    public SP getSP() {
        return null;
    }

    @Override
    public Observable<List<NoticeEntity>> getNoticeList() {
        return mDriverApi.reqNoticeList();
    }

    @Override
    public void setLocalNoticeList(List<NoticeEntity> list) {

    }

    @Override
    public List<NoticeEntity> getLocalNoticeList() {
        return null;
    }

    @Override
    public Observable<List<PayTypeEntity>> getPayTypeList() {
        return mDriverApi.getPayTypeList();
    }

    @Override
    public List<PayTypeEntity> getLocalPayTypeList() {
        return null;
    }

    @Override
    public void setLocalPayTypeList(List<PayTypeEntity> list) {

    }

    @Override
    public Observable<SysConfigEntity> reqSysConfig() {
        return mDriverApi.sysConfig();
    }

    @Override
    public Observable<String> selectCar(String vehicleNo) {
        return mDriverApi.selectCar(vehicleNo);
    }

    @Override
    public boolean isReportAll() {
        return false;
    }

    @Override
    public void setReportAll(boolean isReportAll) {

    }

    @Override
    public boolean isOnSetting() {
        return false;
    }

    @Override
    public void setIsOnSetting(boolean isOnSetting) {

    }

    @Override
    public Observable<List<WithdrawRuleEntity>> withdrawRule() {
        return mRootApi.withdrawRule();
    }

    @Override
    public Observable<InviteCountEntity> getInviteCount(String actId) {
        return mDriverApi.getInviteCount(actId);
    }

    @Override
    public Observable<String> warnCallback(String type, String warnUuid) {
        return mDriverApi.warCallBack(type, warnUuid);
    }

    @Override
    public Observable<WarningEntity> getWarning() {
        return mDriverApi.getWarning();
    }

    @Override
    public Observable<HtmlVersionEntity> reqActHtmlVersion(String versionNo, String modelId) {
        return mDriverApi.reqActHtmlVersion(versionNo, modelId);
    }

    @Override
    public Observable<List<HtmlActEntity>> reqActHtmlList() {
        return mDriverApi.reqActHtmlList(1);    // 1.Android 2.iOS
    }

    @Override
    public HtmlVersionEntity getHtmlModule() {
        return null;
    }

    @Override
    public void setHtmlModule(HtmlVersionEntity entity) {

    }

    @Override
    public void setPushType(Integer pushType) {

    }

    @Override
    public Integer getPushType() {
        return null;
    }

    @Override
    public Observable<String> replyDispatch(HashMap<String, String> params) {
        return mDriverApi.replyDispatch(params);
    }

    @Override
    public void setNavVoiceMode(int mode) {

    }

    @Override
    public int getNavVoiceMode() {
        return 0;
    }

    @Override
    public Observable<String> setDispatchOrderMode(int mode) {
        return mOrderApi.setDispatchOrderMode(mode);
    }

    @Override
    public void setVolumeOff(Boolean isOn) {

    }

    @Override
    public Boolean isVolumeOff() {
        return null;
    }

    @Override
    public void setPushOff(Boolean isPush) {

    }

    @Override
    public Boolean isPushOff() {
        return null;
    }

    @Override
    public Observable<List<WhiteListEntity>> reqWhiteList() {
        return mDriverApi.reqWhiteList();
    }

    @Override
    public Observable<ArrayList<AdvertisementEntity>> getAd() {
        return mDriverApi.getAd();
    }

    @Override
    public Observable<String> verifyCodeNew(String mobile, String identifyCode, int type) {
        return mDriverApi.verifyCodeNew(mobile, identifyCode, type);
    }

    @Override
    public Observable<String> resetPassword(String mobile, String password, String credential) {
        return mDriverApi.resetPassword(mobile, password, credential);
    }

    @Override
    public Observable<UserInfoVO> reqLoginNew(HashMap<String, String> params) {
        return mDriverApi.loginNew(params);
    }

    @Override
    public Observable<String> register(String mobile, String password, String credential) {
        return mDriverApi.register(mobile, password, credential);
    }

    @Override
    public Observable<UserInfoVO> oneKeyLogin(HashMap<String, String> params) {
        return mDriverApi.quickLogin(params);
    }

    @Override
    public Observable<ApplyInfoEntity> getApplyInfo() {
        return mDriverApi.getApplyInfo();
    }

    @Override
    public Observable<List<ApplyProblemEntity>> getApplyProblems() {
        return mDriverApi.getApplyProblems();
    }

    @Override
    public Observable<VerifyToken> getVerifiedToken(String url) {
        return mDriverApi.getVerifiedToken(url);
    }

    @Override
    public Observable<VerifyToken> getVerifiedToken() {
        return mDriverApi.getVerifiedToken();
    }

    @Override
    public Observable<List<ApplyCityEntity>> getApplyOpenCity(String url) {
        return mDriverApi.getApplyOpenCity(url);
    }

    @Override
    public Observable<String> addFeedBack(RequestBody content, MultipartBody.Part[] parts) {
        return mDriverApi.addFeedBack(content, parts);
    }

    @Override
    public Observable<List<FeedBackEntity>> feedBackList(int nowPage) {
        return mDriverApi.feedBackList(nowPage);
    }

    @Override
    public Observable<DynamicIconEntity> mainImages() {
        return mDriverApi.mainImages();
    }

    @Override
    public Observable<WxPayInfo> payByWeChat(String rentalBillUuid, String spbillCreateIp) {
        RequestParams.Builder builder = new RequestParams.Builder();
        builder.putParam("uuid", rentalBillUuid)
                .putParam("spbillCreateIp", spbillCreateIp);
        return mDriverApi.payByWeChat(builder.build());
    }

    @Override
    public Observable<String> payByAliPay(String rentalBillUuid) {
        RequestParams.Builder builder = new RequestParams.Builder();
        builder.putParam("uuid", rentalBillUuid);
        return mDriverApi.payByAliPay(builder.build());
    }

    @Override
    public Observable<String> logoff() {
        return mDriverApi.logoff();
    }

    @Override
    public Observable<LogoffEntity> isLogoff() {
        return mDriverApi.isLogoff();
    }

    @Override
    public Observable<List<WithdrawRuleEntity>> billRule() {
        return mRootApi.billRule();
    }

    @Override
    public Observable<List<BalanceDetailListEntity>> reqBalanceDetailList(HashMap<String, String> params) {
        return mDriverApi.reqBalanceDetailList(params);
    }

    @Override
    public Observable<MyWalletEntity> getMyWallet() {
        return mDriverApi.getMyWallet();
    }

    @Override
    public Observable<RentBillInfoEntity> getRentBillInfo() {
        return mDriverApi.getRentBillInfo();
    }

    @Override
    public Observable<String> findBindAliAccount() {
        return mDriverApi.findBindAliAccount();
    }

    @Override
    public Observable<AliAccountEntity> findAliAccountBySuccess() {
        return mDriverApi.findAliAccountBySuccess();
    }

    @Override
    public Observable<String> bindAliAccount(String mobile, String identifyCode) {
        return mDriverApi.bindAliAccount(mobile, identifyCode);
    }

    @Override
    public Observable<WithdrawRuleEntity> getNotAvailableMoneyDesc() {
        return mDriverApi.getNotAvailableMoneyDesc();
    }

    @Override
    public Observable<ServiceScoreEntity> reqServiceScore() {
        return mDriverApi.reqServiceScore();
    }

    @Override
    public Observable<ReputationScoreEntity> reqReputationScore() {
        return mDriverApi.reqReputationScore();
    }

    @Override
    public Observable<TravelScoreEntity> reqTravelScore() {
        return mDriverApi.reqTravelScore();
    }

    @Override
    public Observable<TaskScoreEntity> reqTaskScore() {
        return mDriverApi.reqTaskScore();
    }

    @Override
    public Observable<String> changeMobile(String mobile, String code) {
        return mDriverApi.changeMobile(mobile, code);
    }

    @Override
    public Observable<UserInfoVO> registerZQ(HashMap<String, String> params) {
        return mDriverApi.registerZQ(params);
    }

    @Override
    public Observable<UserInfoVO> loginAuthZQ(HashMap<String, String> params) {
        return mDriverApi.LoginAuthZQ(params);
    }
}

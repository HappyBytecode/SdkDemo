package anda.travel.driver.data.user.local;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.BuildConfig;
import anda.travel.driver.baselibrary.network.RetrofitRequestTool;
import anda.travel.driver.baselibrary.utils.SP;
import anda.travel.driver.baselibrary.utils.TypeUtil;
import anda.travel.driver.config.IConstants;
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
import anda.travel.driver.data.entity.OrderListenerEntity;
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

public class UserLocalSource implements UserSource {

    private final static String LOCATION = "LOCATION";
    private final static String CANCEL_MSG_LIST = "CANCEL_MSG_LIST";
    private final static String WITHDRAW_RULE = "WITHDRAW_RULE";
    private final static String HTML_VERSION = "HTML_VERSION";
    public final static String NAV_VOICE_MODE = "NAV_VOICE_MODE";  //0.简洁 1.普通
    public final static String USER_IDENTIFY = "user_identify";  //用户是否认证(1:运营司机，2:注册司机)

    private final SP mSP;
    private DriverEntity mUserInfo;
    private String token;
    private String mUserIdentify;
    private String mProvince;
    private String mCity;
    private Integer mPushType;//订单推送模式1：派单，2抢单

    private LocationEntity mCurrentLocation; //用户当前位置
    private List<NoticeEntity> mNoticeList; //公告

    // 20170809追加
    private List<PayTypeEntity> mPayTypeList;

    @Inject
    public UserLocalSource(SP sp) {
        mSP = sp;
    }

    @Override
    public Observable<String> reqLogout() {
        mUserInfo = null;
        /////清除登录信息
        mSP.putObject("userInfo", null);
        return reqLogout(null);
    }

    @Override
    public Observable<String> reqLogout(HashMap<String, String> params) {
        token = null;
        return Observable.just("");
    }

    @Override
    public Observable<DriverEntity> getUserInfo() {
        return Observable.just(mUserInfo); // 可为空
    }

    @Override
    public Observable<DriverEntity> getUserInfoFromRemote() {
        return null;
    }

    @Override
    public DriverEntity getUserInfoFromLocal() {
        return mUserInfo;
    }

    @Override
    public void refreshUserInfo() {
        mUserInfo = null;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public void setUserInfo(DriverEntity entity) {
        mUserInfo = entity;
        mSP.putObject("userInfo", entity);
        setIsDependDriver(entity.depend == null ? 0 : entity.depend);
        OrderListenerEntity orderListenerEntity = mSP.getObject("set-" + entity.uuid, OrderListenerEntity.class);
        if (orderListenerEntity == null) {
            try {
                orderListenerEntity = new OrderListenerEntity();
                orderListenerEntity.remindType = Integer.valueOf(entity.remindType);
                orderListenerEntity.appointTimeStart = entity.appointTimeStart;
                orderListenerEntity.appointTimeEnd = entity.appointTimeEnd;
                mSP.putObject("set-" + entity.uuid, orderListenerEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mSP.putInt(IConstants.MILE_TYPE, TypeUtil.getValue(entity.mileType, 2));
    }

    @Override
    public void setPushType(Integer pushType) {
        if (null == pushType || 0 == pushType) {
            mPushType = 1;
        } else {
            mPushType = pushType;
        }
        mSP.putInt(IConstants.PUSH_TYPE, mPushType);
    }

    @Override
    public Integer getPushType() {
        if (null == mPushType || 0 == mPushType) {
            mPushType = mSP.getInt(IConstants.PUSH_TYPE, 1);
            return mPushType;
        }
        return mPushType;
    }

    @Override
    public boolean isLogin() {
        if (TextUtils.isEmpty(token)) {
            token = RetrofitRequestTool.getToken(mSP);
        }
        //判断已登录并且是认证司机才为登陆状态
        return !TextUtils.isEmpty(token)
                && isVerified();
    }

    public boolean loginUnVerified() {
        if (TextUtils.isEmpty(mUserIdentify)) {
            mUserIdentify = mSP.getString(USER_IDENTIFY);
        }
        return !TextUtils.isEmpty(token)
                && !IConstants.ALL_MODE_DRIVER.equalsIgnoreCase(mUserIdentify);
    }

    @Override
    public Observable<List<WithdrawalRecordEntity>> widthdrawalRecord(int nowPage) {
        return null;
    }

    @Override
    public Observable<BillEntity> reqBills(int nowPage, int dateType, String start, String end, String type) {
        return null;
    }

    @Override
    public Observable<List<ProblemEntity>> getProblems() {
        return null;
    }

    @Override
    public Observable<WithdrawalDetailsEntity> getWithdrawalInfo(String cashUuid) {
        return null;
    }

    @Override
    public Observable<WithdrawalDetailsEntity> getOwnWithdrawalInfo(String cashUuid) {
        return null;
    }

    @Override
    public Observable<String> withdrawal(HashMap<String, String> params) {
        return null;
    }

    @Override
    public Observable<String> withdrawalOwn(HashMap<String, String> params) {
        return null;
    }

    @Override
    public Observable<AssessmentEntity> reqAssessment(String startTime, String endTime) {
        return null;
    }

    @Override
    public Observable<JournalEntity> reqJournal(String startTime, String endTime, int page) {
        return null;
    }

    @Override
    public Observable<IncomeEntity> reqIncomeDetail(String time) {
        return null;
    }

    @Override
    public Observable<IncomeEntity> reqOwnIncomeDetail(String time) {
        return null;
    }

    @Override
    public Observable<List<RentalBillEntity>> reqRentalBillList(HashMap<String, Integer> params) {
        return null;
    }

    @Override
    public Observable<MileEntity> reqMile(String startTime, String endTime) {
        return null;
    }

    @Override
    public Observable<TBoxInfo> reqCarInfo() {
        return null;
    }

    @Override
    public Observable<Boolean> reqHasData() {
        return null;
    }

    @Override
    public Observable<MessagesEntity> reqMessages(int page) {
        return null;
    }

    @Override
    public Observable<Integer> readMessage(String msgUuid) {
        return null;
    }

    @Override
    public Observable<EvaluateEntity> getEvaluates() {
        return null;
    }

    @Override
    public Observable<String> checkPwd(String password) {
        return null;
    }

    @Override
    public Observable<String> resetPw(String newPsw, String oldPsw) {
        return null;
    }

    @Override
    public Observable<String> sendCode(String mobile, int type) {
        return null;
    }

    @Override
    public Observable<HomeEntity> reqWorkInfo() {
        return null;
    }

    @Override
    public Observable<List<CancelReasonEntity>> reqCancelMsg() {
        return null;
    }

    @Override
    public Observable<List<ComplainEntity>> reqComplainMsg() {
        return null;
    }

    @Override
    public String getLocalDriverUuid() {
        return getUserUuid();
    }

    @Override
    public List<AddressVO> getHistoryAddr() {
        // 需限定最多只能10个
        List<AddressVO> all = DataSupport.findAll(AddressVO.class);
        int deleteCounts = all.size() - 10;
        if (deleteCounts > 0) {
            for (int i = 0; i < deleteCounts; i++) {
                all.get(0).delete(); //注意：下标始终是0
                all.remove(0); //注意：下标始终是0
            }
        }
        Collections.reverse(all); //反转数据：存储时间早的数据在后
        return all;
    }

    @Override
    public void saveProvince(String province) {
        mProvince = province;
    }

    @Override
    public void saveCity(String city) {
        mCity = city;
    }

    @Override
    public String getProvince() {
        return mProvince;
    }

    @Override
    public String getCity() {
        return mCity;
    }

    @Override
    public void setNetworkStatus(boolean isDisconnect) {

    }

    @Override
    public void setCurrentLocation(LocationEntity location) {
        mCurrentLocation = location;
        mSP.putObject(LOCATION, location);
    }

    @Override
    public LocationEntity getCurrentLocation() {
        try {
            if (mCurrentLocation == null) {
                mCurrentLocation = mSP.getObject(LOCATION, LocationEntity.class);
            }

            if (mCurrentLocation == null) {
                return new LocationEntity();
            } else {
                if (TextUtils.isEmpty(mCurrentLocation.adcode)) { //如果adcode为空，则修改
                    mCurrentLocation.adcode = mSP.getString(IConstants.ADCODE, IConstants.DefaultAdcode);
                }
                return mCurrentLocation;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new LocationEntity();
        }
    }

    @Override
    public Observable<List<TaskListEntity>> getTaskList() {
        return null;
    }

    @Override
    public Observable<TaskDetailEntity> getTaskDetail(String taskUuid) {
        return null;
    }

    /* ********** 以下为已调整的方法 ********** */

    @Override
    public void setCancelMsgList(List<CancelReasonEntity> list) {
        String json = list != null ? JSON.toJSONString(list) : "";
        mSP.putString(CANCEL_MSG_LIST, json);
    }

    @Override
    public List<CancelReasonEntity> getCancelMsgList() {
        String json = mSP.getString(CANCEL_MSG_LIST, "");
        if (TextUtils.isEmpty(json)) {
            return new ArrayList<>();
        }
        try {
            return JSON.parseArray(json, CancelReasonEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void setDebugEmulator(boolean isEmulator) {
        if (BuildConfig.DEBUG) {
            mSP.putBoolean(IConstants.NAVI_EMULATOR, isEmulator);
        }
    }

    @Override
    public boolean getDebugEmulator() {
        if (BuildConfig.DEBUG) {
            return mSP.getBoolean(IConstants.NAVI_EMULATOR, true);
        } else {
            return false;
        }
    }

    @Override
    public void setFAQList(List<ProblemEntity> list) {
        mSP.putList("FAQList", list);
    }

    @Override
    public List<ProblemEntity> getFAQList() {
        return mSP.getList("FAQList", ProblemEntity.class);
    }

    @Override
    public Observable<CashSettingEntity> getCashSetting() {
        return null;
    }

    @Override
    public Observable<CashSettingEntity> getOwnCashSetting() {
        return null;
    }

    @Override
    public String getUserUuid() {
        if (mUserInfo == null) {
            return TypeUtil.getValue(mSP.getString(IConstants.USER_UUID));
        }
        return TypeUtil.getValue(mUserInfo.uuid);
    }

    @Override
    public Observable<UpgradeEntity> getUpgradeInfo(String version) {
        return null;
    }

    @Override
    public void setIsDependDriver(int depend) {
        mSP.putInt("depend", depend);
    }

    /**
     * 是否为自营司机
     *
     * @return
     */
    @Override
    public boolean getIsDependDriver() {
        int depend = mSP.getInt("depend", 0);
        return depend == 1;
    }

    @Override
    public Observable<CheckStatusEntity> checkStatus() {
        return null;
    }

    @Override
    public int getMileType() {
        return mSP.getInt(IConstants.MILE_TYPE, 2);
    }

    @Override
    public Observable<OrderRespEntity> recording(HashMap<String, String> params) {
        return null;
    }

    @Override
    public SP getSP() {
        return null;
    }

    @Override
    public Observable<List<NoticeEntity>> getNoticeList() {
        return null;
    }

    @Override
    public void setLocalNoticeList(List<NoticeEntity> list) {
        mNoticeList = list;
    }

    @Override
    public List<NoticeEntity> getLocalNoticeList() {
        return mNoticeList;
    }

    @Override
    public Observable<List<PayTypeEntity>> getPayTypeList() {
        return null;
    }

    @Override
    public List<PayTypeEntity> getLocalPayTypeList() {
        return mPayTypeList;
    }

    @Override
    public void setLocalPayTypeList(List<PayTypeEntity> list) {
        mPayTypeList = list;
    }

    @Override
    public Observable<SysConfigEntity> reqSysConfig() {
        return null;
    }

    @Override
    public Observable<String> selectCar(String vehicleNo) {
        return null;
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
        return null;
    }

    @Override
    public Observable<InviteCountEntity> getInviteCount(String actId) {
        return null;
    }

    public List<WithdrawRuleEntity> getWithdrawRuleLocal() {
        return mSP.getList(WITHDRAW_RULE, WithdrawRuleEntity.class);
    }

    public void setWithdrawRuleLocal(List<WithdrawRuleEntity> list) {
        mSP.putList(WITHDRAW_RULE, list);
    }

    @Override
    public Observable<String> warnCallback(String type, String warnUuid) {
        return null;
    }

    @Override
    public Observable<WarningEntity> getWarning() {
        return null;
    }

    @Override
    public Observable<HtmlVersionEntity> reqActHtmlVersion(String versionNo, String modelId) {
        return null;
    }

    @Override
    public Observable<List<HtmlActEntity>> reqActHtmlList() {
        return null;
    }

    @Override
    public HtmlVersionEntity getHtmlModule() {
        return mSP.getObject(HTML_VERSION, HtmlVersionEntity.class);
    }

    @Override
    public void setHtmlModule(HtmlVersionEntity entity) {
        mSP.putObject(HTML_VERSION, entity);
    }

    @Override
    public Observable<String> replyDispatch(HashMap<String, String> params) {
        return null;
    }

    @Override
    public void setNavVoiceMode(int mode) {
        mSP.putInt(NAV_VOICE_MODE, mode);
    }

    @Override
    public int getNavVoiceMode() {
        return mSP.getInt(NAV_VOICE_MODE, 0);
    }

    @Override
    public Observable<String> setDispatchOrderMode(int mode) {
        return null;
    }

    @Override
    public void setVolumeOff(Boolean isOff) {
        mSP.putBoolean(IConstants.VOLUME_OFF, isOff);
    }

    @Override
    public Boolean isVolumeOff() {
        return mSP.getBoolean(IConstants.VOLUME_OFF, false);
    }

    @Override
    public void setPushOff(Boolean isPush) {
        mSP.putBoolean("PUSH_OFF", isPush);
    }

    @Override
    public Boolean isPushOff() {
        return mSP.getBoolean("PUSH_OFF", false);
    }

    @Override
    public Observable<List<WhiteListEntity>> reqWhiteList() {
        return null;
    }

    @Override
    public Observable<ArrayList<AdvertisementEntity>> getAd() {
        return null;
    }

    @Override
    public Observable<String> verifyCodeNew(String mobile, String identifyCode, int type) {
        return null;
    }

    @Override
    public Observable<String> resetPassword(String mobile, String password, String credential) {
        return null;
    }

    @Override
    public Observable<UserInfoVO> reqLoginNew(HashMap<String, String> params) {
        return null;
    }

    @Override
    public Observable<String> register(String mobile, String password, String credential) {
        return null;
    }

    @Override
    public Observable<UserInfoVO> oneKeyLogin(HashMap<String, String> params) {
        return null;
    }

    @Override
    public Observable<ApplyInfoEntity> getApplyInfo() {
        return null;
    }

    @Override
    public Observable<List<ApplyProblemEntity>> getApplyProblems() {
        return null;
    }

    @Override
    public Observable<VerifyToken> getVerifiedToken(String url) {
        return null;
    }

    @Override
    public Observable<VerifyToken> getVerifiedToken() {
        return null;
    }

    @Override
    public Observable<List<ApplyCityEntity>> getApplyOpenCity(String url) {
        return null;
    }

    @Override
    public Observable<String> addFeedBack(RequestBody content, MultipartBody.Part[] parts) {
        return null;
    }

    @Override
    public Observable<List<FeedBackEntity>> feedBackList(int nowPage) {
        return null;
    }

    @Override
    public Observable<DynamicIconEntity> mainImages() {
        return null;
    }

    @Override
    public Observable<WxPayInfo> payByWeChat(String rentalBillUuid, String spbillCreateIp) {
        return null;
    }

    @Override
    public Observable<String> payByAliPay(String rentalBillUuid) {
        return null;
    }

    @Override
    public Observable<String> logoff() {
        return null;
    }

    @Override
    public Observable<LogoffEntity> isLogoff() {
        return null;
    }

    @Override
    public Observable<List<WithdrawRuleEntity>> billRule() {
        return null;
    }

    @Override
    public Observable<List<BalanceDetailListEntity>> reqBalanceDetailList(HashMap<String, String> params) {
        return null;
    }

    @Override
    public Observable<MyWalletEntity> getMyWallet() {
        return null;
    }

    @Override
    public Observable<RentBillInfoEntity> getRentBillInfo() {
        return null;
    }

    @Override
    public Observable<String> findBindAliAccount() {
        return null;
    }

    @Override
    public Observable<AliAccountEntity> findAliAccountBySuccess() {
        return null;
    }

    @Override
    public Observable<String> bindAliAccount(String mobile, String identifyCode) {
        return null;
    }

    @Override
    public Observable<WithdrawRuleEntity> getNotAvailableMoneyDesc() {
        return null;
    }

    @Override
    public Observable<ServiceScoreEntity> reqServiceScore() {
        return null;
    }

    @Override
    public Observable<ReputationScoreEntity> reqReputationScore() {
        return null;
    }

    @Override
    public Observable<TravelScoreEntity> reqTravelScore() {
        return null;
    }

    @Override
    public Observable<TaskScoreEntity> reqTaskScore() {
        return null;
    }

    @Override
    public Observable<String> changeMobile(String mobile, String code) {
        return null;
    }

    @Override
    public Observable<UserInfoVO> registerZQ(HashMap<String, String> params) {
        return null;
    }

    @Override
    public Observable<UserInfoVO> loginAuthZQ(HashMap<String, String> params) {
        return null;
    }

    public boolean isVerified() {
        if (TextUtils.isEmpty(mUserIdentify)) {
            mUserIdentify = mSP.getString(USER_IDENTIFY);
        }
        return IConstants.ALL_MODE_DRIVER.equals(mUserIdentify);
    }

    public void saveVerify(String identify) {
        mUserIdentify = identify;
        mSP.putString(USER_IDENTIFY, identify);
    }

    public void saveAds(ArrayList<AdvertisementEntity> entities) {
        mSP.putList("ads", entities);
    }

    public ArrayList<AdvertisementEntity> getAdCache() {
        return (ArrayList<AdvertisementEntity>) mSP.getList("ads", AdvertisementEntity.class);
    }
}

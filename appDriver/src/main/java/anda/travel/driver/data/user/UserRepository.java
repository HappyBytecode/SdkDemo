package anda.travel.driver.data.user;

import com.amap.api.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import anda.travel.driver.api.DriverApi;
import anda.travel.driver.baselibrary.network.RequestParams;
import anda.travel.driver.baselibrary.network.RetrofitRequestTool;
import anda.travel.driver.baselibrary.utils.SP;
import anda.travel.driver.baselibrary.utils.TypeUtil;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.config.LoginStatus;
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
import anda.travel.driver.data.user.local.UserLocalSource;
import anda.travel.driver.data.user.remote.UserRemoteSource;
import anda.travel.driver.event.UserEvent;
import anda.travel.driver.module.vo.AddressVO;
import anda.travel.driver.module.vo.UserInfoVO;
import anda.travel.driver.socket.SocketEvent;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;

@Singleton
public class UserRepository implements UserSource {

    private final UserLocalSource mLocalSource;
    private final UserRemoteSource mRemoteSource;
    private final SP mSP;
    private final DriverApi mDriverApi;
    private final static String SCREEN_LIGHT = "screen_light";
    public final static String OPEN_FLOAT_WINDOW = "open_float_window";
    private final static String LOGIN_ACCOUNT = "login_account";
    private final static String REPORT_ALL = "report_all";
    private boolean mIsOnSetting;

    @Inject
    public UserRepository(UserLocalSource localSource,
                          UserRemoteSource remoteSource,
                          SP sp,
                          DriverApi driverApi) {
        mLocalSource = localSource;
        mRemoteSource = remoteSource;
        mSP = sp;
        mDriverApi = driverApi;
    }

    public void loginSuccess(DriverEntity info) {
        mLocalSource.setUserInfo(info); //保存用户信息到本地
        mLocalSource.setToken(info.token);
        setPushType(info.pushType);
        mLocalSource.setIsDependDriver(TypeUtil.getValue(info.depend)); //保存是否为自营司机
        saveToken(info.token); //保存token
        saveNeedVerify(info.identify);
        setScreenStatue(true);
        /* 保存用户uuid */
        mSP.putString(IConstants.USER_UUID, info.uuid);
    }

    @Override
    public Observable<String> reqLogout() {
        return reqLogout(null);
    }

    @Override
    public Observable<String> reqLogout(HashMap<String, String> params) {
        RequestParams.Builder builder = getParamsBuilder();
        return mRemoteSource.reqLogout(builder.build())
                .doOnNext(s -> {
                    logout();
                }).doOnError(ex -> {
                    try {
                        logout();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    public void logout() {
        mLocalSource.reqLogout();
        saveToken(""); //清空token
        saveNeedVerify(null);
        mLocalSource.setLocalPayTypeList(null); //重置支付方式
        setScreenStatue(false); //设置screen_light为false
        setSystemVolumeSize(0);//设置系统音量
        EventBus.getDefault().post(new UserEvent(UserEvent.LOGOUT));
        EventBus.getDefault().post(new SocketEvent(SocketEvent.DISCONNECT)); // 断开长连接

        /* 清除用户uuid */
        mSP.putString(IConstants.USER_UUID, "");
//        /* 关闭调度 */
//        mDispatchRepository.destoryNavi();
    }

    public void saveToken(String token) {
        RetrofitRequestTool.saveToken(mSP, token);
    }

    private void saveNeedVerify(String identify) {
        mLocalSource.saveVerify(identify);
    }

    public String getToken() {
        return RetrofitRequestTool.getToken(mSP);
    }

    @Override
    public Observable<DriverEntity> getUserInfo() {
        if (!isLogin()) return Observable.empty();
        return Observable.concat(mLocalSource.getUserInfo(), getUserInfoFromRemote())
                .takeFirst(driverEntity -> driverEntity != null);
    }

    @Override
    public Observable<DriverEntity> getUserInfoFromRemote() {
        return mRemoteSource.getUserInfo().doOnNext(mLocalSource::setUserInfo);
    }

    @Override
    public DriverEntity getUserInfoFromLocal() {
        return mLocalSource.getUserInfoFromLocal();
    }

    @Override
    public void refreshUserInfo() {
        mRemoteSource.refreshUserInfo();
        mLocalSource.refreshUserInfo();
    }

    @Override
    public void setUserInfo(DriverEntity entity) {
        mLocalSource.setUserInfo(entity);
    }

    @Override
    public void setPushType(Integer pushType) {
        mLocalSource.setPushType(pushType);
    }

    @Override
    public Integer getPushType() {
        return mLocalSource.getPushType();
    }

    @Override
    public boolean isLogin() {
        return mLocalSource.isLogin();
    }

    public boolean isVerified() {
        return mLocalSource.isVerified();
    }

    public boolean loginUnVerified() {
        return mLocalSource.loginUnVerified();
    }

    @Override
    public Observable<List<WithdrawalRecordEntity>> widthdrawalRecord(int nowPage) {
        return mRemoteSource.widthdrawalRecord(nowPage);
    }

    @Override
    public Observable<BillEntity> reqBills(int nowPage, int dateType, String start, String end, String type) {
        return mRemoteSource.reqBills(nowPage, dateType, start, end, type);
    }

    @Override
    public Observable<List<ProblemEntity>> getProblems() {
        return mRemoteSource.getProblems().doOnNext(list -> mLocalSource.setFAQList(list));
    }

    @Override
    public Observable<WithdrawalDetailsEntity> getWithdrawalInfo(String cashUuid) {
        return mRemoteSource.getWithdrawalInfo(cashUuid);
    }

    @Override
    public Observable<WithdrawalDetailsEntity> getOwnWithdrawalInfo(String cashUuid) {
        return mRemoteSource.getOwnWithdrawalInfo(cashUuid);
    }

    @Override
    public Observable<String> withdrawal(HashMap<String, String> params) {
        return mRemoteSource.withdrawal(params);
    }

    @Override
    public Observable<String> withdrawalOwn(HashMap<String, String> params) {
        return mRemoteSource.withdrawalOwn(params);
    }

    @Override
    public Observable<AssessmentEntity> reqAssessment(String startTime, String endTime) {
        return mRemoteSource.reqAssessment(startTime, endTime);
    }

    @Override
    public Observable<JournalEntity> reqJournal(String startTime, String endTime, int page) {
        return mRemoteSource.reqJournal(startTime, endTime, page);
    }

    @Override
    public Observable<IncomeEntity> reqIncomeDetail(String time) {
        return mRemoteSource.reqIncomeDetail(time);
    }

    @Override
    public Observable<IncomeEntity> reqOwnIncomeDetail(String time) {
        return mRemoteSource.reqOwnIncomeDetail(time);
    }

    @Override
    public Observable<List<RentalBillEntity>> reqRentalBillList(HashMap<String, Integer> params) {
        return mRemoteSource.reqRentalBillList(params);
    }

    @Override
    public Observable<MileEntity> reqMile(String startTime, String endTime) {
        return mRemoteSource.reqMile(startTime, endTime);
    }

    @Override
    public Observable<TBoxInfo> reqCarInfo() {
        return mRemoteSource.reqCarInfo();
    }

    @Override
    public Observable<Boolean> reqHasData() {
        return mRemoteSource.reqHasData();
    }

    @Override
    public Observable<MessagesEntity> reqMessages(int page) {
        return mRemoteSource.reqMessages(page);
    }

    @Override
    public Observable<Integer> readMessage(String msgUuid) {
        return mRemoteSource.readMessage(msgUuid);
    }

    @Override
    public Observable<EvaluateEntity> getEvaluates() {
        return mRemoteSource.getEvaluates();
    }

    @Override
    public Observable<String> checkPwd(String password) {
        RequestParams.Builder builer = getParamsBuilder();
        builer.putParam("password", password);
        return mDriverApi.checkPwd(builer.build());
    }

    @Override
    public Observable<String> resetPw(String newPsw, String oldPsw) {
        RequestParams.Builder builder = getParamsBuilder();
        builder.putParam("newPsw", newPsw)
                .putParam("oldPsw", oldPsw);
        return mDriverApi.resetPw(builder.build());
    }

    @Override
    public Observable<String> sendCode(String mobile, int type) {
        return mRemoteSource.sendCode(mobile, type);
    }

    @Override
    public Observable<HomeEntity> reqWorkInfo() {
        return mRemoteSource.reqWorkInfo();
    }

    @Override
    public Observable<List<CancelReasonEntity>> reqCancelMsg() {
        return mRemoteSource.reqCancelMsg().doOnNext(mLocalSource::setCancelMsgList);
    }

    @Override
    public Observable<List<ComplainEntity>> reqComplainMsg() {
        return mRemoteSource.reqComplainMsg();
    }

    @Override
    public String getLocalDriverUuid() {
        return mLocalSource.getLocalDriverUuid();
    }

    @Override
    public List<AddressVO> getHistoryAddr() {
        return mLocalSource.getHistoryAddr();
    }

    @Override
    public void saveProvince(String province) {
        mLocalSource.saveProvince(province);
    }

    @Override
    public void saveCity(String city) {
        mLocalSource.saveCity(city);
    }

    @Override
    public String getProvince() {
        return mLocalSource.getProvince();
    }

    @Override
    public String getCity() {
        return mLocalSource.getCity();
    }

    @Override
    public void setNetworkStatus(boolean isDisconnect) {
        mLocalSource.setNetworkStatus(isDisconnect);
    }

    @Override
    public void setCurrentLocation(LocationEntity location) {
        mLocalSource.setCurrentLocation(location);
    }

    @Override
    public LocationEntity getCurrentLocation() {
        return mLocalSource.getCurrentLocation();
    }

    @Override
    public Observable<List<TaskListEntity>> getTaskList() {
        return mRemoteSource.getTaskList();
    }

    @Override
    public Observable<TaskDetailEntity> getTaskDetail(String taskUuid) {
        return mRemoteSource.getTaskDetail(taskUuid);
    }

    /**
     * 设置悬浮窗开关
     *
     * @param isOpen 是否开启悬浮窗
     */
    public void setOpenFloatWindow(boolean isOpen) {
        mSP.putBoolean(OPEN_FLOAT_WINDOW, isOpen);
    }

    /**
     * 获取悬浮窗开关状态
     *
     * @return 悬浮窗开关
     */
    public boolean getOpenFloatWindow() {
        return mSP.getBoolean(OPEN_FLOAT_WINDOW);
    }

    /**
     * 设置屏幕状态
     *
     * @param isBright 是否常亮
     */
    public void setScreenStatue(boolean isBright) {
        mSP.putBoolean(SCREEN_LIGHT, isBright);
    }

    /**
     * 获得屏幕状态
     *
     * @return
     */
    public boolean getScreenStatue() {
        return mSP.getBoolean(SCREEN_LIGHT);
    }

    /**
     * 设置音量类型
     *
     * @param isImmobilizationVolume 是否是固定音量
     */
    public void setVolumeType(boolean isImmobilizationVolume) {
        mSP.putBoolean(IConstants.VOLUME_TYPE, isImmobilizationVolume);
    }

    /**
     * 是否是固定音量
     *
     * @return
     */
    public boolean isImmobilizationVolum() {
        return mSP.getBoolean(IConstants.VOLUME_TYPE);
    }

    /**
     * 设置固定音量大小
     *
     * @param size
     */
    public void setImmobilizationVolumeSize(int size) {
        mSP.putInt(IConstants.IMMOBILIZATION_VOLUME_SIZE, size);
    }

    /**
     * 获得固定音量大小
     *
     * @return
     */
    public int getImmobilizationVolumeSize() {
        return mSP.getInt(IConstants.IMMOBILIZATION_VOLUME_SIZE);
    }

    /**
     * 设置系统音量大小
     *
     * @param size
     */
    public void setSystemVolumeSize(int size) {
        mSP.putInt(IConstants.SYSTEM_VOLUME_SIZE, size);
    }

    /**
     * 获得系统音量大小
     *
     * @return
     */
    public int getSystemVolumeSize() {
        return mSP.getInt(IConstants.SYSTEM_VOLUME_SIZE);
    }

    /**
     * 保存账号
     *
     * @param account
     */
    public void saveAccount(String account) {
        mSP.putString(LOGIN_ACCOUNT, account);
    }

    /**
     * 获得登录账户
     *
     * @return
     */
    public String getAccount() {
        return mSP.getString(LOGIN_ACCOUNT);
    }

    /**
     * 保存账号是否第一次进入新版钱包
     *
     * @param isFirstMyWallet
     */
    public void saveIsFirstMyWallet(boolean isFirstMyWallet) {
        mSP.putBoolean(getAccount(), isFirstMyWallet);
    }

    /**
     * 获得登录账户是否第一次进入新版钱包
     *
     * @return
     */
    public boolean getIsFirstMyWallet() {
        return mSP.getBoolean(getAccount(), true);
    }


    /**
     * 保存司机的当前坐标
     *
     * @param latLng
     */
    public void saveLatLng(LatLng latLng) {
        if (latLng == null || latLng.latitude <= 0 || latLng.longitude <= 0) return;
        mSP.putString("lat", String.valueOf(latLng.latitude));
        mSP.putString("lng", String.valueOf(latLng.longitude));
    }

    /**
     * 获取司机当前坐标
     *
     * @return
     */
    public LatLng getLatLng() {
        double lat = getDoubleValue(mSP.getString("lat", "0"));
        double lng = getDoubleValue(mSP.getString("lng", "0"));
        if (lat <= 0 || lng <= 0) return null;
        return new LatLng(lat, lng);
    }

    private double getDoubleValue(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void setCancelMsgList(List<CancelReasonEntity> list) {
        mLocalSource.setCancelMsgList(list);
    }

    @Override
    public List<CancelReasonEntity> getCancelMsgList() {
        return mLocalSource.getCancelMsgList();
    }


    @Override
    public void setDebugEmulator(boolean isEmulator) {
        mLocalSource.setDebugEmulator(isEmulator);
    }

    @Override
    public boolean getDebugEmulator() {
        return mLocalSource.getDebugEmulator();
    }

    @Override
    public void setFAQList(List<ProblemEntity> list) {
        mLocalSource.setFAQList(list);
    }

    @Override
    public List<ProblemEntity> getFAQList() {
        return mLocalSource.getFAQList();
    }

    @Override
    public Observable<CashSettingEntity> getCashSetting() {
        return mRemoteSource.getCashSetting();
    }

    @Override
    public Observable<CashSettingEntity> getOwnCashSetting() {
        return mRemoteSource.getOwnCashSetting();
    }

    @Override
    public String getUserUuid() {
        return mLocalSource.getUserUuid();
    }

    @Override
    public Observable<UpgradeEntity> getUpgradeInfo(String version) {
        HashMap<String, String> map = new HashMap<>();
        map.put("platform", "android");
        map.put("versionNo", version);
        map.put("identify", "2");   //1.乘客 2.网约车 3.出租车

        //添加adcode
        LocationEntity location = mLocalSource.getCurrentLocation();
        if (location != null) map.put("adcode", location.adcode);

        return mDriverApi.getUpgradeInfo(map);
    }

    @Override
    public void setIsDependDriver(int depend) {
        mLocalSource.setIsDependDriver(depend);
    }

    @Override
    public boolean getIsDependDriver() {
        return mLocalSource.getIsDependDriver();
    }

    @Override
    public Observable<CheckStatusEntity> checkStatus() {
        return mRemoteSource.checkStatus();
    }

    @Override
    public int getMileType() {
        return mLocalSource.getMileType();
    }

    @Override
    public Observable<OrderRespEntity> recording(HashMap<String, String> params) {
        RequestParams.Builder builder = getParamsBuilder();
        builder.putParam(params);
        builder.putParam("deviceType", "1");
        return mRemoteSource.recording(builder.build());
    }

    @Override
    public SP getSP() {
        return mSP;
    }

    @Override
    public Observable<List<NoticeEntity>> getNoticeList() {
        return mRemoteSource.getNoticeList()
                .doOnNext(this::setLocalNoticeList);
    }

    @Override
    public void setLocalNoticeList(List<NoticeEntity> list) {
        mLocalSource.setLocalNoticeList(list);
    }

    @Override
    public List<NoticeEntity> getLocalNoticeList() {
        return mLocalSource.getLocalNoticeList();
    }

    @Override
    public Observable<List<PayTypeEntity>> getPayTypeList() {
        return mRemoteSource.getPayTypeList()
                .doOnNext(this::setLocalPayTypeList);
    }

    @Override
    public List<PayTypeEntity> getLocalPayTypeList() {
        return mLocalSource.getLocalPayTypeList();
    }

    @Override
    public void setLocalPayTypeList(List<PayTypeEntity> list) {
        mLocalSource.setLocalPayTypeList(list);
    }

    @Override
    public Observable<SysConfigEntity> reqSysConfig() {
        return mRemoteSource.reqSysConfig();
    }

    @Override
    public Observable<String> selectCar(final String vehicleNo) {
        return mRemoteSource.selectCar(vehicleNo);
    }

    @Override
    public boolean isReportAll() {
        return mSP.getBoolean(REPORT_ALL, true);
    }

    @Override
    public void setReportAll(boolean isReportAll) {
        mSP.putBoolean(REPORT_ALL, isReportAll);
    }

    @Override
    public boolean isOnSetting() {
        return mIsOnSetting;
    }

    @Override
    public void setIsOnSetting(boolean isOnSetting) {
        mIsOnSetting = isOnSetting;
    }

    @Override
    public Observable<List<WithdrawRuleEntity>> withdrawRule() {
        return mRemoteSource.withdrawRule().doOnNext(mLocalSource::setWithdrawRuleLocal);
    }

    @Override
    public Observable<InviteCountEntity> getInviteCount(String actId) {
        return mRemoteSource.getInviteCount(actId);
    }

    @Override
    public Observable<String> warnCallback(String type, String warnUuid) {
        return mRemoteSource.warnCallback(type, warnUuid);
    }

    @Override
    public Observable<WarningEntity> getWarning() {
        return mRemoteSource.getWarning();
    }

    @Override
    public Observable<HtmlVersionEntity> reqActHtmlVersion(String versionNo, String modelId) {
        return mRemoteSource.reqActHtmlVersion(versionNo, modelId);
    }

    @Override
    public Observable<List<HtmlActEntity>> reqActHtmlList() {
        return mRemoteSource.reqActHtmlList();
    }

    @Override
    public HtmlVersionEntity getHtmlModule() {
        return mLocalSource.getHtmlModule();
    }

    @Override
    public void setHtmlModule(HtmlVersionEntity entity) {
        mLocalSource.setHtmlModule(entity);
    }

    public List<WithdrawRuleEntity> getWithdrawRuleFromLocal() {
        return mLocalSource.getWithdrawRuleLocal();
    }

    private HashMap<String, String> getParams() {
        HashMap<String, String> params = new HashMap<>();
        LocationEntity location = mLocalSource.getCurrentLocation();
        if (location == null) return params;
        params.put("adcode", location.adcode);
        params.put("lng", String.valueOf(location.lng));
        params.put("lat", String.valueOf(location.lat));
        return params;
    }

    private RequestParams.Builder getParamsBuilder() {
        RequestParams.Builder builder = new RequestParams.Builder();
        LocationEntity location = mLocalSource.getCurrentLocation();
        if (location == null) return builder;
        builder.putParam("adcode", location.adcode);
        builder.putParam("lng", location.lng);
        builder.putParam("lat", location.lat);
        return builder;
    }

    @Override
    public Observable<String> replyDispatch(HashMap<String, String> params) {
        return mRemoteSource.replyDispatch(params);
    }

    @Override
    public void setNavVoiceMode(int mode) {
        mLocalSource.setNavVoiceMode(mode);
    }

    @Override
    public int getNavVoiceMode() {
        return mLocalSource.getNavVoiceMode();
    }

    @Override
    public Observable<String> setDispatchOrderMode(int mode) {
        return mRemoteSource.setDispatchOrderMode(mode);
    }

    @Override
    public void setVolumeOff(Boolean isOff) {
        mLocalSource.setVolumeOff(isOff);
    }

    @Override
    public Boolean isVolumeOff() {
        return mLocalSource.isVolumeOff();
    }

    @Override
    public void setPushOff(Boolean isPush) {
        mLocalSource.setPushOff(isPush);
    }

    @Override
    public Boolean isPushOff() {
        return mLocalSource.isPushOff();
    }

    @Override
    public Observable<List<WhiteListEntity>> reqWhiteList() {
        return mRemoteSource.reqWhiteList();
    }

    public String getAdCode() {
        return mSP.getString(IConstants.ADCODE);
    }

    public void saveAdcode(String adCode) {
        mSP.putString(IConstants.ADCODE, adCode);
    }

    @Override
    public Observable<ArrayList<AdvertisementEntity>> getAd() {
        return mRemoteSource.getAd();
    }

    @Override
    public Observable<String> verifyCodeNew(String mobile, String identifyCode, int type) {
        return mRemoteSource.verifyCodeNew(mobile, identifyCode, type);
    }

    @Override
    @NotNull
    public Observable<String> resetPassword(String mobile, String password, String credential) {
        return mRemoteSource.resetPassword(mobile, password, credential);
    }

    /////////////3.0的新登录功能
    @Override
    public Observable<UserInfoVO> reqLoginNew(HashMap<String, String> params) {
        RequestParams.Builder builder = getParamsBuilder();
        builder.putParam(params);
        builder.putParam("deviceType", "1");
        return mDriverApi.loginNew(builder.build())
                .doOnNext(info -> {
                    info.driver.identify = info.identify;
                    if (!info.driver.isFirst.equals(LoginStatus.IS_FIRST)) {
                        /* 非首次登录，保存用户信息 */
                        loginSuccess(info.driver);
                    } else {
                        /* 首次登录，临时保存用户数据 */
                        saveNeedVerify(info.identify);
                        saveToken(info.driver.token);
                        mLocalSource.setToken(info.driver.token);
                        mLocalSource.setUserInfo(info.driver); //保存用户信息到本地
                    }
                });
    }

    @Override
    public Observable<String> register(String mobile, String password, String credential) {
        return mRemoteSource.register(mobile, password, credential);
    }

    @Override
    public Observable<UserInfoVO> oneKeyLogin(HashMap<String, String> params) {
        RequestParams.Builder builder = getParamsBuilder();
        builder.putParam(params);
        builder.putParam("deviceType", "1");
        return mDriverApi.quickLogin(builder.build())
                .doOnNext(info -> {
                    String data = info.identify;
                    info.driver.identify = data;
                    if (!info.driver.isFirst.equals(LoginStatus.IS_FIRST)) {
                        /* 非首次登录，保存用户信息 */
                        loginSuccess(info.driver);
                    } else {
                        /* 首次登录，临时保存用户数据 */
                        mLocalSource.setUserInfo(info.driver); //保存用户信息到本地
                    }
                });
    }

    @Override
    public Observable<ApplyInfoEntity> getApplyInfo() {
        return mRemoteSource.getApplyInfo();
    }

    @Override
    public Observable<List<ApplyProblemEntity>> getApplyProblems() {
        return mRemoteSource.getApplyProblems();
    }

    @Override
    public Observable<VerifyToken> getVerifiedToken(String url) {
        return mRemoteSource.getVerifiedToken(url);
    }

    @Override
    public Observable<VerifyToken> getVerifiedToken() {
        return mRemoteSource.getVerifiedToken();
    }

    @Override
    public Observable<List<ApplyCityEntity>> getApplyOpenCity(String url) {
        return mRemoteSource.getApplyOpenCity(url);
    }

    @Override
    public Observable<String> addFeedBack(RequestBody content, MultipartBody.Part[] parts) {
        return mRemoteSource.addFeedBack(content, parts);
    }

    @Override
    public Observable<List<FeedBackEntity>> feedBackList(int nowPage) {
        return mRemoteSource.feedBackList(nowPage);
    }

    @Override
    public Observable<DynamicIconEntity> mainImages() {
        return mRemoteSource.mainImages();
    }

    @Override
    public Observable<WxPayInfo> payByWeChat(String rentalBillUuid, String spbillCreateIp) {
        return mRemoteSource.payByWeChat(rentalBillUuid, spbillCreateIp);
    }

    @Override
    public Observable<String> payByAliPay(String rentalBillUuid) {
        return mRemoteSource.payByAliPay(rentalBillUuid);
    }

    @Override
    public Observable<String> logoff() {
        return mRemoteSource.logoff();
    }

    @Override
    public Observable<LogoffEntity> isLogoff() {
        return mRemoteSource.isLogoff();
    }

    @Override
    public Observable<List<WithdrawRuleEntity>> billRule() {
        return mRemoteSource.billRule();
    }

    @Override
    public Observable<List<BalanceDetailListEntity>> reqBalanceDetailList(HashMap<String, String> params) {
        return mRemoteSource.reqBalanceDetailList(params);
    }

    @Override
    public Observable<MyWalletEntity> getMyWallet() {
        return mRemoteSource.getMyWallet();
    }

    @Override
    public Observable<RentBillInfoEntity> getRentBillInfo() {
        return mRemoteSource.getRentBillInfo();
    }

    @Override
    public Observable<String> findBindAliAccount() {
        return mRemoteSource.findBindAliAccount();
    }

    @Override
    public Observable<AliAccountEntity> findAliAccountBySuccess() {
        return mRemoteSource.findAliAccountBySuccess();
    }

    @Override
    public Observable<String> bindAliAccount(String mobile, String identifyCode) {
        return mRemoteSource.bindAliAccount(mobile, identifyCode);
    }

    @Override
    public Observable<WithdrawRuleEntity> getNotAvailableMoneyDesc() {
        return mRemoteSource.getNotAvailableMoneyDesc();
    }

    @Override
    public Observable<ServiceScoreEntity> reqServiceScore() {
        return mRemoteSource.reqServiceScore();
    }

    @Override
    public Observable<ReputationScoreEntity> reqReputationScore() {
        return mRemoteSource.reqReputationScore();
    }

    @Override
    public Observable<TravelScoreEntity> reqTravelScore() {
        return mRemoteSource.reqTravelScore();
    }

    @Override
    public Observable<TaskScoreEntity> reqTaskScore() {
        return mRemoteSource.reqTaskScore();
    }

    @Override
    public Observable<String> changeMobile(String mobile, String code) {
        return mRemoteSource.changeMobile(mobile, code);
    }

    @Override
    public Observable<UserInfoVO> registerZQ(HashMap<String, String> params) {
        return mRemoteSource.registerZQ(params);
    }

    @Override
    public Observable<UserInfoVO> loginAuthZQ(HashMap<String, String> params) {
        return mRemoteSource.loginAuthZQ(params);
    }

    public void saveAds(ArrayList<AdvertisementEntity> entities) {
        mLocalSource.saveAds(entities);
    }

    public ArrayList<AdvertisementEntity> getAdCache() {
        return mLocalSource.getAdCache();
    }
}

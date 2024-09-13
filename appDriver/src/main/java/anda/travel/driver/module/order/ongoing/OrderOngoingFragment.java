package anda.travel.driver.module.order.ongoing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.amap.api.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import anda.travel.driver.BuildConfig;
import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.utils.DisplayUtil;
import anda.travel.driver.baselibrary.utils.NaviUtil;
import anda.travel.driver.baselibrary.utils.NumberUtil;
import anda.travel.driver.baselibrary.utils.PermissionUtil;
import anda.travel.driver.baselibrary.utils.PhoneUtil;
import anda.travel.driver.baselibrary.utils.SpannableWrap;
import anda.travel.driver.baselibrary.view.HeadView;
import anda.travel.driver.common.BaseFragment;
import anda.travel.driver.config.HxLateType;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.config.OrderStatus;
import anda.travel.driver.event.MapEvent;
import anda.travel.driver.module.amap.ANavigateFragment;
import anda.travel.driver.module.amap.navi.SingleRouteCalculateActivity;
import anda.travel.driver.module.main.MainActivity;
import anda.travel.driver.module.order.cancel.OrderCancelActivity;
import anda.travel.driver.module.order.ongoing.dagger.DaggerOrderOngoingComponent;
import anda.travel.driver.module.order.ongoing.dagger.OrderOngoingModule;
import anda.travel.driver.module.vo.OrderVO;
import anda.travel.driver.sound.SoundUtils;
import anda.travel.driver.util.AnimationUtil;
import anda.travel.driver.util.FontUtils;
import anda.travel.driver.util.Navigate;
import anda.travel.driver.util.OrderManager;
import anda.travel.driver.util.SpeechUtil;
import anda.travel.driver.util.SysConfigUtils;
import anda.travel.driver.widget.CommonAlertDialog;
import anda.travel.driver.widget.layout.BaseLinearLayout;
import anda.travel.driver.widget.popwindow.EasyPopup;
import anda.travel.driver.widget.slide.SlideView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

@SuppressLint("NonConstantResourceId")
public class OrderOngoingFragment extends BaseFragment implements OrderOngoingContract.View, EasyPermissions.PermissionCallbacks {
    @BindView(R2.id.slide_view)
    SlideView mSlideView;
    @BindView(R2.id.tv_top_address)
    TextView mTvTopAddress;
    @BindView(R2.id.tv_mileage)
    TextView mTvMileage;
    @BindView(R2.id.tv_duration)
    TextView mTvDuration;
    @BindView(R2.id.tv_mileage_unit)
    TextView mTvMileageUnit;
    @BindView(R2.id.tv_duration_unit)
    TextView mTvDurationUnit;
    @BindView(R2.id.layout_sub)
    ConstraintLayout mLayoutSub;
    @BindView(R2.id.tv_cost)
    TextView mTvCost;
    @BindView(R2.id.tv_emulator)
    Button mTvEmulator;
    @BindView(R2.id.lay_newchatmsg)
    LinearLayout mLayNewChatMsg;
    @BindView(R2.id.msg_content)
    TextView mTvMsgContent;
    @BindView(R2.id.tv_new_msg)
    TextView mTvNewMsgNotice;
    @BindView(R2.id.lay_chat)
    FrameLayout mLayChat;
    @BindView(R2.id.lay_price)
    ConstraintLayout mLayPrice;
    @BindView(R2.id.tv_end_number)
    TextView mTvEndNumber;
    @BindView(R2.id.tv_start)
    TextView mTvStart;
    @BindView(R2.id.tv_end)
    TextView mTvEnd;
    @BindView(R2.id.head_view)
    HeadView mHeadView;
    @BindView(R2.id.layout_order_top)
    ViewGroup mLayoutOrderTop;
    @BindView(R2.id.layout_navigation_msg)
    ViewGroup mLayoutNavigationMsg;
    @BindView(R2.id.tv_get_passenger_mileage)
    TextView mTvGetPassengerMileage;
    @BindView(R2.id.tv_get_passenger_time)
    TextView mTvGetPassengerTime;
    @BindView(R2.id.tv_wait_no_fee)
    TextView mTvWaitNoFee;
    @BindView(R2.id.tv_wait_tips)
    TextView mTvWaitTips;
    @BindView(R2.id.tv_wait_content)
    TextView mTvWaitContent;
    @BindView(R2.id.tv_late_operate)
    TextView mTvLateOperate;
    @BindView(R2.id.layout_late)
    ViewGroup mLayoutLate;
    @BindView(R2.id.layout_info)
    BaseLinearLayout mLayoutInfo;
    @BindView(R2.id.layout_abnormal)
    ConstraintLayout mLayoutAbnormal;
    @BindView(R2.id.tv_desc)
    TextView mTvDesc;
    @BindView(R2.id.tv_abnormal)
    TextView mTvAbnormal;

    private LatLng mNaviTo; //导航的目标地点
    private boolean mEmulator; //是否开启模拟导航
    private double mTotalPrice; //临时记录价格(专车)
    private String mStrReport = ""; //(为修复bug增加)
    private boolean mNeedReport; //是否需要播报距离和时间
    private boolean mIsDestroy;
    private int mCount; //为修复"播报0公里0分钟"添加
    private static final int RC_PERMS = 124;
    private static final int RC_AUDIO = 125;
    private EasyPopup mMorePop;
    private int mLateType;
    private boolean mHasDistributeToOther; //为解决 Bug 3135 添加
    private CountDownTimer mTimer;  /////控制时间显示控件

    @Inject
    OrderOngoingPresenter mPresenter;

    public static OrderOngoingFragment newInstance(String orderUuid, OrderVO vo, boolean needReport) {
        OrderOngoingFragment fragment = new OrderOngoingFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IConstants.ORDER_UUID, orderUuid);
        bundle.putSerializable(IConstants.ORDER_VO, vo);
        bundle.putBoolean(IConstants.REPORT, needReport);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.hxyc_fragment_order_ongoing, container, false);
        ButterKnife.bind(this, mView);
        //是否语音播报
        if (getArguments() != null) {
            mNeedReport = getArguments().getBoolean(IConstants.REPORT, false);
        }
        OrderVO vo;
        if (savedInstanceState != null) {//页面重新创建时，恢复onSaveInstanceState里程信息
            OrderManager.instance().setLastMileage(savedInstanceState.getDouble("lastMile"));
            OrderManager.instance().setLatLng(savedInstanceState.getParcelable("lastPoint"));
            vo = (OrderVO) savedInstanceState.getSerializable("vo");
        } else {
            //这个地方先从数据库中恢复，防止进程死了之后重新进入，导致里程丢失的问题
            mPresenter.setLastInfoFromDb(getArguments().getString(IConstants.ORDER_UUID));
            vo = (OrderVO) getArguments().getSerializable(IConstants.ORDER_VO);
        }
        OrderManager.instance().setOrderVO(vo);
        OrderManager.instance().create();
        mPresenter.eventStart();
        /* 添加高德地图 */
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(R.id.fl_map_container, ANavigateFragment.newInstance());
        ft.commit();
        //记录传递过来的"订单编号"和"OrderVO"
        mPresenter.onCreate(getArguments().getString(IConstants.ORDER_UUID));

        mSlideView.setOnSlideListener(() -> { //滑动到最右，将触发状态改变
            mPresenter.switchToNextStatus();
        });

        initFont();

        mMorePop = EasyPopup.create()
                .setContentView(requireContext(), R.layout.hxyc_layout_popup_order_more)
                .setAnimationStyle(R.style.CurtainsPopAnim)
                .setWidth(DisplayUtil.dp2px(requireActivity(), 150))
                ///允许背景变暗
                .setBackgroundDimEnable(true)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(true)
                .apply();
        TextView tvDetails = mMorePop.findViewById(R.id.order_details);
        TextView tvService = mMorePop.findViewById(R.id.service);
        tvDetails.setOnClickListener(v -> {
            Navigate.openOrder(getContext(), vo.uuid, OrderManager.instance().getOrderVO());
            mMorePop.dismiss();
        });

        tvService.setOnClickListener(v -> {
            SysConfigUtils.get().dialServerPhone(getContext());
            mMorePop.dismiss();
        });
        mHeadView.getRightTextView().setOnClickListener(view -> mMorePop.showAsDropDown(mHeadView.getRightTextView(), -30, 10));

        mTvEndNumber.setTypeface(FontUtils.getFontTypeFace(requireContext()));

        mLayoutInfo.setRadiusAndShadow(20, 16);
        mPresenter.reqOrderDetail();
        return mView;
    }

    private void initFont() {
        Typeface bebas = FontUtils.getFontTypeFace(requireContext());
        mTvDuration.setTypeface(bebas);
        mTvMileage.setTypeface(bebas);
        mTvCost.setTypeface(bebas);
    }

    public void onSaveInstanceState(Bundle outState) {
        mPresenter.onSaveInstanceState(outState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mPresenter.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        DaggerOrderOngoingComponent.builder()
                .appComponent(getAppComponent())
                .orderOngoingModule(new OrderOngoingModule(this))
                .build().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
        jchatDisplay();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.onDestory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
        }
        mIsDestroy = true;
    }

    @OnClick({R2.id.tv_navigation, R2.id.lay_phone, R2.id.tv_emulator, R2.id.lay_chat,
            R2.id.lay_newchatmsg, R2.id.tv_late_operate})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_navigation) {
            skipToNavigate();
        } else if (id == R.id.lay_phone) {
            new CommonAlertDialog(requireContext()).builder()
                    .setTitle("匿名联系")
                    .setMsg("您与乘客将通过匿名小号联系，为保证通话畅通，请务必使用接单号码呼叫乘客")
                    .setPositiveButton("拨号", v -> PhoneUtil.skip(requireContext(), OrderManager.instance().getPassengerPhone()))
                    .setNegativeButton("取消", v -> {
                    })
                    .show();
        } else if (id == R.id.tv_emulator) { //模拟导航
            changeEmulator();
        } else if (id == R.id.lay_chat || id == R.id.lay_newchatmsg) {
            chatPermission();
        } else if (id == R.id.tv_late_operate) {
            if (mLateType == HxLateType.LATE) {
                mPresenter.lateBilling();
            } else {
                ///取消订单
                OrderCancelActivity.actionStart(requireContext(), OrderManager.instance().getUuid(), OrderManager.instance().getSubStatus());
            }
        }
    }

    @Override
    public void setDisplay() {
        OrderVO vo = OrderManager.instance().getOrderVO();
        if (vo == null || vo.mainStatus == null || vo.subStatus == null) {
            toast("订单状态异常");
            return;
        }

        if (vo.mainStatus != OrderStatus.ORDER_MAIN_STATUS_DOING) { //跳转到订单详情页
            Navigate.openOrder(getContext(), vo.uuid, vo);
            closeActivity();
            return;
        }

        if (!TextUtils.isEmpty(vo.passenger.getPassengerRealPhoneEnd())) {
            mTvEndNumber.setText(vo.passenger.getPassengerRealPhoneEnd());
        } else {
            mTvEndNumber.setVisibility(View.GONE);
            mTvDesc.setVisibility(View.GONE);
        }
        mTvStart.setText(vo.originAddress);
        mTvEnd.setText(vo.destAddress);
        mTvTopAddress.setText(vo.originAddress);
        String title = "";
        //设置订单起终点
        EventBus.getDefault().post(new MapEvent(MapEvent.NAVI_ORDER, vo.getOriginLatLng(), vo.getDestLatLng(), vo.uuid));

        switch (vo.subStatus) {
            case OrderStatus.WAIT_ARRIVE_ORIGIN:
                title = "接乘客";
                mNaviTo = vo.getOriginLatLng();
                mLayoutOrderTop.setVisibility(View.VISIBLE);
                mLayoutNavigationMsg.setVisibility(View.VISIBLE);
                //开启导航
                new Handler().postDelayed(() -> EventBus.getDefault().post(new MapEvent(MapEvent.NAVI_NAVIGATE, mNaviTo)), 1000);
                break;
            case OrderStatus.WATI_PASSENGER_GET_ON:
                title = "等待上车";
                mLayoutOrderTop.setVisibility(View.GONE);
                mLayoutLate.setVisibility(View.VISIBLE);
                //控制乘客迟到的显示(在乘客上车前)
                mPresenter.dealWithLateTime(vo.lateTime, vo.realLateTime);
                break;
            case OrderStatus.DEPART:
                title = "送乘客";
                mNaviTo = vo.getDestLatLng();
                //if (vo.isNeedCalculate()) priceVis = View.VISIBLE; //(需要计算里程)显示订单费用
                mPresenter.recordDepartTimeStamp(); //记录时间戳
                mLayoutLate.setVisibility(View.GONE);
                mTvTopAddress.setText(vo.destAddress);
                mLayoutSub.setVisibility(View.VISIBLE);
                mLayoutOrderTop.setVisibility(View.VISIBLE);
                mLayoutNavigationMsg.setVisibility(View.GONE);
                mLayoutInfo.setVisibility(View.GONE);
                //开启导航
                new Handler().postDelayed(() -> EventBus.getDefault().post(new MapEvent(MapEvent.NAVI_NAVIGATE, mNaviTo, vo.routeId)), 1000);
                break;
            case OrderStatus.WAIT_BEGIN_APPOINTMENT: //跳转到等待开始页
                Navigate.openOrderBegin(getContext(), vo.uuid, vo);
                requireActivity().finish();
                break;
            case OrderStatus.ARRIVE_DEST: //跳转到费用确认（或费用输入）页
                Navigate.openPriceByType(getContext(), vo);
                requireActivity().finish();
                break;
            default: //跳转订单详情页
                Navigate.openOrder(getContext(), vo.uuid, vo);
                requireActivity().finish();
                break;
        }

        /* debug模式，是否显示"模拟导航按钮" */
        if (BuildConfig.DEBUG && mPresenter.isEmulatorOpen())
            showEmulatorBtn();

        mHeadView.setTitle(title); //设置标题显示
        mSlideView.setContent(mPresenter.getSlideViewBtnText(vo.subStatus)); //按键内容
        mSlideView.resetView(); //重置按键显示
    }

    @Override
    public void resetBtnDisplay() {
        mSlideView.resetView(); //重置显示
    }

    @Override
    public void arriveStart() {
        //SoundUtils.getInstance().play(R.raw.speech_order_arrive_start);
        SpeechUtil.speech(R.string.speech_order_arrive_start);
        setDisplay();
    }

    @Override
    public void passengerGetOn() {
        SpeechUtil.speech(getResources().getString(R.string.speech_order_passenger_geton, OrderManager.instance()
                .getDestAddress()));
        mNeedReport = true; //需要播报距离和时间
        setDisplay();
    }

    @Override
    public void arriveEnd(OrderVO vo) { //跳转到订单详情页后，优先从服务端获取数据
        //SoundUtils.getInstance().play(R.raw.speech_order_arrive_end);
        Navigate.openPriceByType(getContext(), vo); //跳转到费用确认（或费用输入）页
        requireActivity().finish();
        SpeechUtil.speech(R.string.speech_order_arrive_end);
    }

    @Override
    public void closeActivity() {
        requireActivity().finish();
    }

    @Override
    public void setNaviInfoDisplay(Integer distance, Integer time) {
        if (distance == null) {
            mTvMileage.setText(getResources().getString(R.string.order_navi_distance_calculating));
            mTvMileageUnit.setVisibility(View.GONE);
            mTvGetPassengerMileage.setText(getResources().getString(R.string.order_navi_distance_calculating));
        } else {
            mTvMileageUnit.setVisibility(View.VISIBLE);
            String strDistance = NumberUtil.getFormatValue(distance * 1.0D / 1000, false);
            mTvMileage.setText(strDistance);
            mTvGetPassengerMileage.setText(MessageFormat.format("{0}公里", strDistance));
        }
        if (time == null) {
            mTvDuration.setText(getResources().getString(R.string.order_navi_time_calculating));
            mTvDurationUnit.setVisibility(View.GONE);
            mTvGetPassengerTime.setText(getResources().getString(R.string.order_navi_time_calculating));
        } else {
            mTvDuration.setText(MessageFormat.format("{0}", NaviUtil.getMinute(time)));
            mTvDurationUnit.setVisibility(View.VISIBLE);
            mTvGetPassengerTime.setText(MessageFormat.format("{0}分钟", NaviUtil.getMinute(time)));
        }
        if (distance != null && time != null) {
            if (mCount == 0 && distance == 0 && time == 0) {
                mCount++; //为修复Bug添加
                return;
            }
            mStrReport = "距离" + NumberUtil.getFormatValue(distance * 1.0D / 1000, false) + "公里" + "，预计需要" + NaviUtil.getMinute(time) + "分钟";
            if (mNeedReport) {
                mNeedReport = false;
                speechWithDelay();
            }
        }
    }

    private void speechWithDelay() {
        if (mIsDestroy) return;
        new Handler().postDelayed(() -> {
            String report = OrderManager.instance().isDepart()
                    ? mStrReport + "，请提醒乘客系好安全带"
                    : mStrReport;
            SpeechUtil.speech(report);
        }, 2000);
    }

    @Override
    public void skipToNavigate() {
        SingleRouteCalculateActivity.actionStart(getContext(), mPresenter.getSlideViewBtnText(OrderManager.instance().getSubStatus()));
    }

    @Override
    public void setTotalPrice(double totalPrice) {
        mTotalPrice = totalPrice;
        mTvCost.setText(NumberUtil.getFormatValue(totalPrice));
    }

    @Override
    public void showEmulatorBtn() {
        mTvEmulator.setVisibility(View.VISIBLE);
    }

    @Override
    public void changeEmulator() {
        if (BuildConfig.DEBUG) {
            mEmulator = !mEmulator;
            EventBus.getDefault().post(new MapEvent(MapEvent.NAVI_EMULATOR, mEmulator));
        } else {
            mEmulator = false;
        }
        int strRes = mEmulator ? R.string.navi_emulator_close : R.string.navi_emulator_open;
        mTvEmulator.setText(strRes);
    }

    @Override
    public void showLateTime(String strLateTime, int type) {
        mLateType = type;
        if (type == HxLateType.NORMAL) {
            visible(mTvWaitNoFee);
            SpannableWrap.setText("已到达乘客上车点,")
                    .textColor(ContextCompat.getColor(mView.getContext(), R.color.text_black))
                    .append(strLateTime)
                    .textColor(ContextCompat.getColor(mView.getContext(), R.color.popup_item_choose))
                    .append("后可以迟到计费")
                    .textColor(ContextCompat.getColor(mView.getContext(), R.color.text_black))
                    .into(mTvWaitNoFee);
        } else if (type == HxLateType.LATE) {
            gone(mTvWaitNoFee);
            visible(mTvWaitTips);
            visible(mTvWaitContent);
            visible(mTvLateOperate);
            mTvWaitContent.setText(strLateTime);
            mTvLateOperate.setText("迟到计费");
        } else {
            gone(mTvWaitNoFee);
            visible(mTvWaitTips);
            visible(mTvWaitContent);
            visible(mTvLateOperate);

            SpannableWrap.setText(strLateTime)
                    .textColor(ContextCompat.getColor(requireContext(), R.color.text_black))
                    .append(mTotalPrice + "元")
                    .textColor(ContextCompat.getColor(requireContext(), R.color.popup_item_choose))
                    .into(mTvWaitContent);

            mTvLateOperate.setText("取消订单");
        }
    }

    /**
     * 显示订单已被改派
     *
     * @param notice
     */
    @Override
    public void showOrderDistributToOther(String notice) {
        if (mHasDistributeToOther) return;
        mHasDistributeToOther = true;
//        SpeechUtil.speech(HxClientManager.getInstance().application.getApplicationContext(), notice); //播报语音
        MainActivity.actionStart(getContext(), notice);
        requireActivity().finish();
    }

    @Override
    public void speechAppInBackground() {
        SoundUtils.getInstance().play(R.raw.order_ongoing_notice);
    }

    @Override
    public void jchatDisplay() {
        showMessage(false);
        mLayChat.setVisibility(View.GONE);
    }

    private void showMessage(boolean isShow) {
        if (isShow) {
            mLayNewChatMsg.setVisibility(View.VISIBLE);
            mTvNewMsgNotice.setVisibility(View.VISIBLE);
            Observable.timer(5, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        AnimationUtil.scaleanim(mLayNewChatMsg, true);
                        mLayNewChatMsg.setVisibility(View.GONE);
                    }, ex -> {
                    });
        } else {
            mLayNewChatMsg.setVisibility(View.GONE);
            mTvNewMsgNotice.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_PERMS)
    public void chatPermission() {
        if (EasyPermissions.hasPermissions(requireContext(), PermissionUtil.PERMISSIONS_FOR_RECORD)) {

        } else {
            EasyPermissions.requestPermissions(this, "必要权限",
                    RC_PERMS, PermissionUtil.PERMISSIONS_FOR_RECORD);
        }
    }

    @Override
    @AfterPermissionGranted(RC_AUDIO)
    public void audioPermission() {
        if (EasyPermissions.hasPermissions(requireContext(), PermissionUtil.getNeededAudioPermission())) {
            mPresenter.startRecording();
        } else {
            EasyPermissions.requestPermissions(this, "为了行程中正常录音，请开启权限",
                    RC_AUDIO, PermissionUtil.getNeededAudioPermission());
        }
    }

    @Override
    public void showAbnormalView() {
        abnormalLogic();
        mTvAbnormal.setText(getString(R.string.remind_abnormal));
        SpeechUtil.speech(getString(R.string.remind_abnormal));
    }

    private void abnormalLogic() {
        ///////////如果已经显示了，再收到消息或者推送都不显示
        if (mLayoutAbnormal.getVisibility() == View.GONE) {
            //////设置显示异常订单的弹框
            mLayoutAbnormal.setVisibility(View.VISIBLE);
            AnimationUtil.scaleanim(mLayoutAbnormal, false);

            /////10s后设置不可见
            if (mTimer != null) {
                mTimer.cancel();
            }
            mTimer = new CountDownTimer(10 * 1000, 1000) {
                @Override
                public void onFinish() {
                    /////10s后设置不可见
                    AnimationUtil.scaleanim(mLayoutAbnormal, true);
                    mLayoutAbnormal.setVisibility(View.GONE);
                }

                @Override
                public void onTick(long millisUntilFinished) {

                }
            }.start();
        }
    }

    @Override
    public void showCrossCityView() {
        abnormalLogic();
        mTvAbnormal.setText(getString(R.string.remind_cross_city));
        SpeechUtil.speech(getString(R.string.remind_cross_city));
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        PermissionUtil.getPermissionSetTipDialog(perms, requireContext()).show();
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        return false;
    }
}

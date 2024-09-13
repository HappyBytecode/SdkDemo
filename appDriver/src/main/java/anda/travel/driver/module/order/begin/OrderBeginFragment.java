package anda.travel.driver.module.order.begin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.amap.api.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.utils.DisplayUtil;
import anda.travel.driver.baselibrary.utils.PermissionUtil;
import anda.travel.driver.baselibrary.utils.PhoneUtil;
import anda.travel.driver.baselibrary.view.HeadView;
import anda.travel.driver.common.BaseFragment;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.config.OrderStatus;
import anda.travel.driver.event.MapEvent;
import anda.travel.driver.module.amap.AMapFragment;
import anda.travel.driver.module.main.MainActivity;
import anda.travel.driver.module.order.begin.dagger.DaggerOrderBeginComponent;
import anda.travel.driver.module.order.begin.dagger.OrderBeginModule;
import anda.travel.driver.module.vo.OrderVO;
import anda.travel.driver.util.FontUtils;
import anda.travel.driver.util.Navigate;
import anda.travel.driver.util.SpeechUtil;
import anda.travel.driver.util.SysConfigUtils;
import anda.travel.driver.widget.CommonAlertDialog;
import anda.travel.driver.widget.popwindow.EasyPopup;
import anda.travel.driver.widget.slide.SlideView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 预约单
 */
@SuppressLint("NonConstantResourceId")
public class OrderBeginFragment extends BaseFragment implements OrderBeginContract.View, EasyPermissions.PermissionCallbacks {

    @BindView(R2.id.tv_time)
    TextView mTvTime;
    @BindView(R2.id.layout_remind)
    ViewGroup mLayoutRemind;
    @BindView(R2.id.slide_view)
    SlideView mSlideView;
    @BindView(R2.id.layout_order_slide)
    FrameLayout mLayoutOrderSlide;
    @BindView(R2.id.view_disable)
    View mViewDisable;
    @BindView(R2.id.lay_newchatmsg)
    LinearLayout mLayNewChatMsg;
    @BindView(R2.id.msg_content)
    TextView mTvMsgContent;
    @BindView(R2.id.tv_new_msg)
    TextView mTvNewMsgNotice;
    @BindView(R2.id.lay_chat)
    FrameLayout mLayChat;
    @BindView(R2.id.tv_end_number)
    TextView mTvEndNumber;
    @BindView(R2.id.tv_start)
    TextView mTvStart;
    @BindView(R2.id.tv_end)
    TextView mTvEnd;
    @BindView(R2.id.tv_desc)
    TextView mTvDesc;
    @BindView(R2.id.head_view)
    HeadView mHeadView;

    private LatLng originLatLng;
    private LatLng destLatLng;
    private static final int RC_PERMS = 124;
    private EasyPopup mMorePop;

    @Inject
    OrderBeginPresenter mPresenter;

    public static OrderBeginFragment newInstance(String orderUuid, OrderVO vo) {
        OrderBeginFragment fragment = new OrderBeginFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IConstants.ORDER_UUID, orderUuid);
        bundle.putSerializable(IConstants.ORDER_VO, vo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.hxyc_fragment_order_begin, container, false);
        ButterKnife.bind(this, mView);
        if (getArguments() != null) {
            mPresenter.onCreate(getArguments().getString(IConstants.ORDER_UUID),
                    (OrderVO) getArguments().getSerializable(IConstants.ORDER_VO));
        }

        //显示确定弹窗
        mSlideView.setOnSlideListener(this::showBeginConfirm);
        mSlideView.setContent("出发去接乘客");
        mViewDisable.setOnClickListener(v -> toast("距离出发时间1小时以内，才允许操作"));

        mMorePop = EasyPopup.create()
                .setContentView(requireContext(), R.layout.hxyc_layout_popup_order_more)
                .setWidth(DisplayUtil.dp2px(requireActivity(), 150))
                .setAnimationStyle(R.style.CurtainsPopAnim)
                ///允许背景变暗
                .setBackgroundDimEnable(true)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(true)
                .apply();
        TextView tvDetails = mMorePop.findViewById(R.id.order_details);
        TextView tvService = mMorePop.findViewById(R.id.service);
        tvDetails.setOnClickListener(v -> {
            Navigate.openOrder(getContext(), mPresenter.getOrderVO().uuid, mPresenter.getOrderVO());
            mMorePop.dismiss();
        });

        tvService.setOnClickListener(v -> {
            SysConfigUtils.get().dialServerPhone(getContext());
            mMorePop.dismiss();
        });
        mHeadView.getRightTextView().setOnClickListener(view -> mMorePop.showAsDropDown(mHeadView.getRightView(), -30, 10));

        mTvEndNumber.setTypeface(FontUtils.getFontTypeFace(requireContext()));

        /* 添加高德地图 */
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(R.id.fl_map_container, AMapFragment.newInstance());
        ft.commit();
        return mView;
    }

    @Override
    public void jchatDisplay() {
        mLayChat.setVisibility(View.GONE);
    }

    @Override
    public void closeView() {
        requireActivity().finish();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        DaggerOrderBeginComponent.builder()
                .appComponent(getAppComponent())
                .orderBeginModule(new OrderBeginModule(this))
                .build().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
        resetBtnDisplay();
        jchatDisplay();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    @OnClick({R2.id.lay_phone, R2.id.lay_chat, R2.id.lay_newchatmsg})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.lay_phone) {
            new CommonAlertDialog(requireContext()).builder()
                    .setTitle("匿名联系")
                    .setMsg("您与乘客将通过匿名小号联系，为保证通话畅通，请务必使用接单号码呼叫乘客")
                    .setPositiveButton("拨号", v ->
                            PhoneUtil.skip(requireContext(), mPresenter.getPassengerPhone()))
                    .setNegativeButton("取消", v -> {
                    })
                    .show();
        } else if (id == R.id.lay_chat || id == R.id.lay_newchatmsg) {
            chatPermission();
        }
    }

    @Override
    public void setDisplay(OrderVO vo) {
        if (vo.subStatus == null) {
            toast("订单状态异常");
            return;
        }
        if (vo.mainStatus == OrderStatus.ORDER_MAIN_STATUS_CANCEL) { //订单已被取消
            Navigate.openOrder(getContext(), vo.uuid, vo);
            closeView();
            return;
        }
        if (!TextUtils.isEmpty(vo.getStrDepartTime())) {
            mTvTime.setText(vo.getStrDepartTime());
            mLayoutRemind.setVisibility(View.VISIBLE);
        } else {
            mLayoutRemind.setVisibility(View.GONE);
        }
        originLatLng = vo.getOriginLatLng();
        destLatLng = vo.getDestLatLng();
        if (!TextUtils.isEmpty(vo.passenger.getPassengerRealPhoneEnd())) {
            mTvEndNumber.setText(vo.passenger.getPassengerRealPhoneEnd());
            mTvDesc.setVisibility(View.GONE);
        }
        mTvStart.setText(vo.originAddress);
        mTvEnd.setText(vo.destAddress);

        if (!vo.isOrderWaitBegin()) { //如果不是wait_begin状态的订单，
            Navigate.openOrderByStatus(getContext(), vo);
            requireActivity().finish();
            return;
        }

        //判断是否可以"出发去接乘客"
        boolean cannotPickUp = (vo.canPickUp == null || vo.canPickUp != 1);
        mSlideView.setForegroundDisable(cannotPickUp);
        int vis = cannotPickUp ? View.VISIBLE : View.GONE;
        mViewDisable.setVisibility(vis);

        new Handler().postDelayed(() -> {
            //规划路径
            EventBus.getDefault().post(new MapEvent(MapEvent.MAP_CALCULATE_ROUTE, originLatLng, destLatLng, vo.routeId));
        }, 200);

        new Handler().postDelayed(() -> EventBus.getDefault().post(new MapEvent(MapEvent.MAP_PADDING,
                200, 300)), 1000);
    }

    @Override
    public void showBeginConfirm() {
        new CommonAlertDialog(requireContext()).builder()
                .setTitle("现在出发去接乘客吗？")
                .setPositiveButton("确定", v -> mPresenter.reqOrderBegin())
                .setNegativeButton("取消", v -> {
                    mSlideView.resetView(); //重置显示
                }).setCancelable(false)
                .show();
    }

    @Override
    public void orderBeginSuccess(String orderUuid, OrderVO vo) {
        String strStart = TextUtils.isEmpty(vo.originAddress) ? "出发地" : vo.originAddress;
        SpeechUtil.speech(getString(R.string.speech_order_begin, strStart));
        Navigate.openOrderOngoing(getContext(), orderUuid, vo, true);
        requireActivity().finish();
    }

    @Override
    public void resetBtnDisplay() {
        mSlideView.resetView(); //重置显示
    }

    private boolean mHasDistributToOther; //为解决禅道Bug3135添加

    @Override
    public void showOrderDistributToOther(String notice) {
        if (mHasDistributToOther) return;
        mHasDistributToOther = true;
        MainActivity.actionStart(getContext(), notice);
        requireActivity().finish();
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

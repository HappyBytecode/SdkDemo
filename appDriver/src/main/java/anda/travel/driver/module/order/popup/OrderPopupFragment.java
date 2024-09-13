package anda.travel.driver.module.order.popup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.utils.DateUtil;
import anda.travel.driver.baselibrary.utils.NumberUtil;
import anda.travel.driver.common.BaseFragment;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.config.OrderStatus;
import anda.travel.driver.config.TimeType;
import anda.travel.driver.event.MapEvent;
import anda.travel.driver.event.OrderEvent;
import anda.travel.driver.module.amap.AMapFragment;
import anda.travel.driver.module.amap.assist.CalculateUtils;
import anda.travel.driver.module.order.popup.dagger.DaggerOrderPopupComponent;
import anda.travel.driver.module.order.popup.dagger.OrderPopupModule;
import anda.travel.driver.module.vo.OrderVO;
import anda.travel.driver.sound.SoundUtils;
import anda.travel.driver.util.Navigate;
import anda.travel.driver.util.RouteUtil;
import anda.travel.driver.util.SpeechUtil;
import anda.travel.driver.widget.layout.BaseFrameLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 功能描述：
 */
@SuppressLint("NonConstantResourceId")
public class OrderPopupFragment extends BaseFragment implements OrderPopupContract.View {

    @BindView(R2.id.tv_grab)
    TextView mTvGrab;
    @BindView(R2.id.layout_map)
    BaseFrameLayout mLayoutMap;
    @BindView(R2.id.tv_title)
    TextView mTvTitle;
    @BindView(R2.id.tv_reward)
    TextView mTvReward;
    @BindView(R2.id.tv_trip_time)
    TextView mTvTripTime;
    @BindView(R2.id.tv_trip_distance)
    TextView mTvTripDistance;
    @BindView(R2.id.tv_start)
    TextView mTvStart;
    @BindView(R2.id.tv_end)
    TextView mTvEnd;
    @BindView(R2.id.tfl_remark)
    TagFlowLayout mTagFlowLayout;
    @BindView(R2.id.layout_trip_time)
    ViewGroup mLayoutTripTime;
    @BindView(R2.id.tv_all_distance)
    TextView mTvAllDistance;
    @BindView(R2.id.iv_cancel)
    ImageView mIvCancel;

    @Inject
    OrderPopupPresenter mPresenter;
    private AMapFragment mAMapFragment; //高德地图片段
    private LatLng originLatLng;
    private LatLng destLatLng;
    private String mStrDistance; //显示的距离
    private OrderVO mOrderVO;
    private RouteUtil routeUtil;
    private List<String> mTags;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.hxyc_fragment_order_popup, container, false);
        ButterKnife.bind(this, mView);
        OrderVO vo = (OrderVO) getArguments().getSerializable(IConstants.ORDER_VO);
        routeUtil = new RouteUtil();
        mTags = new ArrayList<>();
        mPresenter.setOrderUuid(getArguments().getString(IConstants.ORDER_UUID)); //设置订单编号
        mPresenter.onCreate(vo, vo.getRedistribute(), vo.getLoops(), vo.getLoopCnt());
        /* 添加高德地图 */
        if (mAMapFragment == null) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.add(R.id.layout_map, AMapFragment.newInstance());
            ft.commit();
        }
        mLayoutMap.setRadius(20);
        LatLng curLatLng = mPresenter.getLatLng();
        setOrderInfo(vo, curLatLng); //等待应答，设置显示
        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        DaggerOrderPopupComponent.builder()
                .appComponent(getAppComponent())
                .orderPopupModule(new OrderPopupModule(this))
                .build().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.unsubscribe();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.onDestory();
    }

    @Override
    public void setOrderInfo(OrderVO vo, LatLng curLatLng) {
        mOrderVO = vo;
        originLatLng = vo.getOriginLatLng(); //起点
        destLatLng = vo.getDestLatLng(); //终点
        if (originLatLng != null && destLatLng != null) {
            RouteUtil AllRouteUtils = new RouteUtil();
            AllRouteUtils.driveRouter(requireContext(), new LatLonPoint(originLatLng.latitude, originLatLng.longitude), new LatLonPoint(destLatLng.latitude
                    , destLatLng.longitude));
            AllRouteUtils.setOnRouteResultListener(new RouteUtil.OnRouteResultListener() {
                @Override
                public void onRouteResultSuccess(DrivePath drivePath) {
                    float distance = drivePath.getDistance();
                    distance = distance / 1000;
                    String strDistance = NumberUtil.getFormatValue(distance);
                    mTvAllDistance.setText(strDistance);
                }

                @Override
                public void onRouteResultFailed() {

                }
            });
        }
        if (vo.getRewardFlag() != null && vo.getRewardFlag() == OrderStatus.REWARD_FLAG) {
            visible(mTvReward);
        } else {
            gone(mTvReward);
        }
        if (vo.typeTime == TimeType.REALTIME) {
            setRealtimeDisplay(vo, curLatLng);
        } else {
            setAppointmentDisplay(vo, curLatLng);
        }
        new Handler().postDelayed(() -> EventBus.getDefault().post(new MapEvent(MapEvent.MAP_CALCULATE_ROUTE, originLatLng, destLatLng, vo.routeId)), 400);
    }

    //设置实时订单的显示
    private void setRealtimeDisplay(final OrderVO vo, LatLng curLatLng) {
        LatLng originLatLng = vo.getOriginLatLng();
        mTvStart.setText(vo.originAddress);
        mTvEnd.setText(vo.destAddress);
        setServicePriceAndComment(vo);
        if (curLatLng == null || originLatLng == null) {
            mTvTitle.setText("实时订单");
            visible(mTvTitle);
            mTvTripDistance.setText("距离未知");
        } else {
            routeUtil.setOnRouteResultListener(new RouteUtil.OnRouteResultListener() {
                @Override
                public void onRouteResultSuccess(DrivePath drivePath) {
                    float distance = drivePath.getDistance();
                    initStrDistance(distance);
                    setAndReportStrDistance(vo);
                }

                @Override
                public void onRouteResultFailed() {
                    float distance = CalculateUtils.calculateLineDistance(curLatLng, originLatLng); //单位：米
                    initStrDistance(distance);
                    setAndReportStrDistance(vo);
                }
            });
            routeUtil.driveRouter(getContext(),
                    new LatLonPoint(curLatLng.latitude, curLatLng.longitude),
                    new LatLonPoint(originLatLng.latitude, originLatLng.longitude));
        }
    }

    /**
     * 设置并播报距离
     */
    private void setAndReportStrDistance(OrderVO vo) {
        mTvTitle.setText("实时订单");
        visible(mTvTitle);
        if (!mPresenter.getIsDistributeOrRedistribute()) {
            mPresenter.reportNewOrder(vo, mStrDistance); //播报新订单
        }
    }

    //设置预约订单的显示
    private void setAppointmentDisplay(OrderVO vo, LatLng curLatLng) {
        LatLng originLatLng = vo.getOriginLatLng();
        String strDepartTime = DateUtil.getSpace(vo.departTime);
        visible(mTvTitle);
        mTvTitle.setText(MessageFormat.format("{0}{1}", getString(R.string.order_popup_title_appointment), DateUtil.getRange(vo.departTime)));
        mLayoutTripTime.setVisibility(View.VISIBLE);
        mTvTripTime.setVisibility(View.VISIBLE);
        mTvTripTime.setText(strDepartTime);
        mTvStart.setText(vo.originAddress);
        mTvEnd.setText(vo.destAddress);
        if (curLatLng == null || originLatLng == null) {
            mTvTripDistance.setText("距离未知");
        } else {
            routeUtil.setOnRouteResultListener(new RouteUtil.OnRouteResultListener() {
                @Override
                public void onRouteResultSuccess(DrivePath drivePath) {
                    float distance = drivePath.getDistance();
                    initStrDistance(distance);
                    if (!mPresenter.getIsDistributeOrRedistribute()) {
                        mPresenter.reportNewOrder(vo, mStrDistance); //播报新订单
                    }
                }

                @Override
                public void onRouteResultFailed() {
                    float distance = CalculateUtils.calculateLineDistance(curLatLng, originLatLng); //单位：米
                    initStrDistance(distance);
                    if (!mPresenter.getIsDistributeOrRedistribute()) {
                        mPresenter.reportNewOrder(vo, mStrDistance); //播报新订单
                    }
                }
            });
            routeUtil.driveRouter(getContext(),
                    new LatLonPoint(curLatLng.latitude, curLatLng.longitude),
                    new LatLonPoint(originLatLng.latitude, originLatLng.longitude));
        }
        setServicePriceAndComment(vo);
    }

    private void initStrDistance(float distance) {
        float data = distance / 1000;
        String display = NumberUtil.getFormatValue(data);
        mTvTripDistance.setText(display);
        int strRes = R.string.order_popup_title_realtime_km;
        String strDistance = NumberUtil.getFormatValue(data);
        mStrDistance = getString(strRes, strDistance);
    }

    //设置留言和标签
    private void setServicePriceAndComment(OrderVO vo) {
        String strTip = vo.getStrTip();
        if (!TextUtils.isEmpty(strTip)) {
            mTags.add(getString(R.string.order_popup_service_price, strTip));
        }
        if (!TextUtils.isEmpty(vo.remark)) {
            try {
                mTags.addAll(Arrays.asList(vo.remark.split(",")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!mTags.isEmpty()) {
            mTagFlowLayout.setVisibility(View.VISIBLE);
        }
        mTagFlowLayout.setAdapter(new TagAdapter<String>(mTags) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(requireContext()).inflate(R.layout.hxyc_layout_popup_order_tag,
                        mTagFlowLayout, false);
                if (!TextUtils.isEmpty(strTip) && position == 0) {
                    tv.setBackground(requireContext().getDrawable(R.drawable.dra_tag_dispatch_fee));
                }
                tv.setText(s);
                return tv;
            }
        });
    }

    @Override
    public void grabSuccess(OrderVO vo, boolean isDistribute) {
        int rawRes = R.raw.speech_order_grab_success;
        /////抢单成功后设置不能点击
        mIvCancel.setEnabled(true);
        SoundUtils.getInstance().play(rawRes, () -> {
            if (getActivity() == null) return;
            SpeechUtil.stop();
            if (vo.isOrderWaitBegin()) { //跳转等待出发页
                Navigate.openOrderBegin(getContext(), vo.uuid, vo);

            } else if (vo.isOrderOngoing()) { //跳转进行中订单
                Navigate.openOrderOngoing(getContext(), vo.uuid, vo, true);

            } else { //跳转订单详情
                Navigate.openOrder(getContext(), vo.uuid, vo);
            }
            ////发送一条消息给乘客端
            mPresenter.sendMessageToPassenger(vo);
            getActivity().finish(); //关闭界面
        });
    }

    @Override
    public void grabFail(int time, String reason) {
        String report = reason.contains("抢单失败")
                ? reason
                : "抢单失败" + "，" + reason;
        mTvGrab.setEnabled(false);
        mTvGrab.setEnabled(false);
        SpeechUtil.speech(report); //播报语音
        closeActivity();
    }

    @Override
    public void showGrabBtn(int time) {
        mTvGrab.setText(MessageFormat.format("抢单 {0}秒", time));
    }

    @Override
    public void showDispatchBtn(int time) {
        mTvGrab.setText(MessageFormat.format("确定{0}秒", time));
    }

    @Override
    public void dispatchConfirm() {
        if (mOrderVO.isCanceled()) return;
        SpeechUtil.stop();
        if (mOrderVO.isOrderWaitBegin()) { //跳转等待出发页
            Navigate.openOrderBegin(getContext(), mOrderVO.uuid, mOrderVO);

        } else if (mOrderVO.isOrderOngoing()) { //跳转进行中订单
            Navigate.openOrderOngoing(getContext(), mOrderVO.uuid, mOrderVO, true);
        }
        closeActivity();
    }

    @Override
    public void initDispatch(OrderVO vo) {
        /////初始化派单情况下的操作
        if (vo.getRewardFlag() != null && vo.getRewardFlag() == OrderStatus.REWARD_FLAG) {
            mPresenter.reportNewOrder(vo, mStrDistance);
        } else {
            SpeechUtil.speech("接受派单成功，请准时接驾", () -> mPresenter.reportNewOrder(vo, mStrDistance));
        }
        mIvCancel.setVisibility(View.GONE);
    }

    @Override
    public void closeActivity() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            getActivity().finish();
        }
        mPresenter.onDestory();
    }

    @Override
    public void closeActivityByRefuse(boolean isPress) {
        HashMap<String, String> params = mPresenter.getParams();
        String operateCode;
        if (isPress) {
            operateCode = "5"; //3-派单或改派 手动拒绝、5-推单 手动忽略
        } else {
            operateCode = "4"; //4-自动拒单
        }
        params.put("operateCode", operateCode);
        EventBus.getDefault().post(new OrderEvent(OrderEvent.ORDER_REFUSE, params)); //发送通知
        closeActivity();
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        if (childFragment instanceof AMapFragment) {
            mAMapFragment = (AMapFragment) childFragment;
        }
    }

    @OnClick({R2.id.tv_grab, R2.id.iv_cancel})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_grab) {
            if (isBtnBuffering()) return;
            if (!mPresenter.getIsDistribute()) {
                mPresenter.getOrderByGrab();
            } else {
                dispatchConfirm();
            }
        } else if (id == R.id.iv_cancel) {
            SpeechUtil.stop();
            closeActivityByRefuse(true);
        }
    }
}

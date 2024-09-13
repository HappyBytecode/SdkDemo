package anda.travel.driver.module.main.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gyf.immersionbar.ImmersionBar;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.listener.OnPageChangeListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.utils.DateUtil;
import anda.travel.driver.baselibrary.utils.PermissionUtil;
import anda.travel.driver.baselibrary.utils.ToastUtil;
import anda.travel.driver.baselibrary.utils.TypeUtil;
import anda.travel.driver.common.AppManager;
import anda.travel.driver.common.BaseFragment;
import anda.travel.driver.config.HxMessageType;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.data.ad.AdvertisementEntity;
import anda.travel.driver.data.entity.DynamicIconEntity;
import anda.travel.driver.data.entity.HomeEntity;
import anda.travel.driver.data.entity.HtmlActEntity;
import anda.travel.driver.data.entity.HxMessageEntity;
import anda.travel.driver.data.entity.SysConfigEntity;
import anda.travel.driver.event.OrderEvent;
import anda.travel.driver.module.ads.AdDialog;
import anda.travel.driver.module.amap.heatmap.HeatMapActivity;
import anda.travel.driver.module.dispatch.DispatchActivity;
import anda.travel.driver.module.dispatch.dialog.DispatchDialogActivity;
import anda.travel.driver.module.main.MainActivity;
import anda.travel.driver.module.main.home.dagger.DaggerHomeComponent;
import anda.travel.driver.module.main.home.dagger.HomeModule;
import anda.travel.driver.module.notice.NoticeFragment;
import anda.travel.driver.module.vo.DispatchVO;
import anda.travel.driver.module.vo.OrderVO;
import anda.travel.driver.module.web.H5Activity;
import anda.travel.driver.module.web.WebActivity;
import anda.travel.driver.util.FontUtils;
import anda.travel.driver.util.Navigate;
import anda.travel.driver.util.SpeechUtil;
import anda.travel.driver.util.SysConfigUtils;
import anda.travel.driver.util.TimeUtils;
import anda.travel.driver.widget.BaseTipsDialog;
import anda.travel.driver.widget.VerticalSwipeRefreshLayout;
import anda.travel.driver.widget.layout.BaseFrameLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 功能描述：主页
 */
@SuppressLint("NonConstantResourceId")
public class HomeFragment extends BaseFragment implements HomeContract.View, EasyPermissions.PermissionCallbacks {

    @BindView(R2.id.tv_notice)
    TextView mTvNotice;
    @BindView(R2.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R2.id.swipe_refresh_layout)
    VerticalSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R2.id.layout_notice)
    LinearLayout mLayoutNotice;
    @BindView(R2.id.tv_complete_order)
    TextView mTvCompleteOrder;
    @BindView(R2.id.tv_income)
    TextView mTvIncome;
    @BindView(R2.id.tv_income_tag)
    TextView mTvIncomeTag;
    @BindView(R2.id.tv_duration)
    TextView mTvDuration;
    @BindView(R2.id.layout_top)
    ConstraintLayout mLayoutTop;
    @BindView(R2.id.banner)
    Banner mBanner;
    @BindView(R2.id.lay_transverse)
    BaseFrameLayout mLayTransverse;
    @BindView(R2.id.layout_order_info)
    ViewGroup mLayoutOrderInfo;
    @BindView(R2.id.tv_order_start)
    TextView mTvOrderStart;
    @BindView(R2.id.tv_order_end)
    TextView mTvOrderEnd;
    @BindView(R2.id.tv_order_status)
    TextView mTvOrderStatus;
    @BindView(R2.id.tv_order_title)
    TextView mTvOrderTitle;
    @BindView(R2.id.lay_order_list)
    ViewGroup mLayOrderList;
    @BindView(R2.id.heat_map)
    LinearLayout mLayoutHeatMap;
    @BindView(R2.id.rank_list)
    LinearLayout mLayoutRankList;
    @BindView(R2.id.layout_model)
    LinearLayout mLayoutModel;
    @BindView(R2.id.recycler_dynamic)
    RecyclerView mRvDynamic;

    TextView msgNumTv;

    ViewHolder mViewHolder;
    private HomeAdapter mAdapter;
    AdDialog mAdsDialog;

    @Inject
    HomePresenter mPresenter;
    private BaseTipsDialog mBaseTipsDialog;
    HtmlActEntity mHeatMap, mRankList;
    private boolean isShow;

    private DynamicIconAdapter mDynamicAdapter;
    private boolean isGetIcon;

    private static final int RC_HEAT_MAP_PERMS = 1003;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.hxyc_fragment_home, container, false);
        ButterKnife.bind(this, mView);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        msgNumTv = requireActivity().findViewById(R.id.msgNumTv);
        Typeface bebas = FontUtils.getFontTypeFace(requireContext());
        mTvIncome.setTypeface(bebas);
        mTvCompleteOrder.setTypeface(bebas);
        mTvDuration.setTypeface(bebas);

        mLayoutHeatMap.setOnClickListener(view -> LocationPermission());

        mLayoutRankList.setOnClickListener(view -> {
            if (mRankList != null) {
                openRankHtml(mRankList);
            }
        });

        mLayOrderList.setOnClickListener(view -> Navigate.openOrderList(requireContext()));

        mDynamicAdapter = new DynamicIconAdapter(requireContext());
        mRvDynamic.setLayoutManager(new GridLayoutManager(requireContext(), 4));
        mRvDynamic.setAdapter(mDynamicAdapter);
        mDynamicAdapter.setOnItemClickListener((position, view, item) -> {
            if (isBtnBuffering()) {
                return; //避免快速点击添加
            }
            dealMenuClick(item);
        });

        Fragment frgNotice = getChildFragmentManager().findFragmentById(R.id.fl_main_notice);

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();

        if (frgNotice == null) {
            ft.commit();
            ft.add(R.id.fl_main_notice, NoticeFragment.getInstance());
        }

        /* 初始化列表 */
        View bottomView = inflater.inflate(R.layout.hxyc_fragment_home_bottom, container, false);
        View mTopView = inflater.inflate(R.layout.hxyc_layout_home_dispatch, container, false);
        mViewHolder = new ViewHolder(mTopView);
        mAdapter = new HomeAdapter(getContext());
        mAdapter.addHeaderView(mTopView);
        mAdapter.addFooterView(bottomView);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        /* 设置操作监听 */
        mAdapter.setOnClickListener(R.id.layout_content, (position, view, item) -> {
            if (isBtnBuffering()) {
                return; //避免快速点击添加
            }
            dealWithMessage(item);  //跳转到对应到对应的页面
        });
        mAdapter.setOnClickListener(R.id.iv_delete, (position, view, item) -> {
            mPresenter.readMessage(item); //将系统消息设置为已读，并从列表中移除
        });
        /* 设置进度圈颜色 */
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mPresenter.reqWorkInfo();
            mPresenter.getAD(false);
            mPresenter.reqAllUnreadMessages();
            mPresenter.getMainImages();
            mPresenter.reqAllUnreadMessagesCount(1);
            NoticeFragment.getInstance().reFresh();

        });
        mPresenter.onCreate();
        mPresenter.getAD(true);
        showDefaultModel();
        mLayTransverse.setRadius(10);
        return mView;
    }

    /**
     * 处理列表被点击的事件
     */
    private void dealMenuClick(DynamicIconEntity.IconBean item) {
        /////////等于2的时候是链接,跳转链接
        if (item.getJumpType() == 2) {
            if (!TextUtils.isEmpty(item.getHref())) {
                H5Activity.actionStart(requireContext(), item.getHref(), "");
            }
        } else {
            if (DynamicCode.ORDER.value.equals(item.getCode())) {
                Navigate.openOrderList(requireContext());
            }
            if (DynamicCode.HEATMAP.value.equals(item.getCode())) {
                LocationPermission();
            }
            if (DynamicCode.RANKLIST.value.equals(item.getCode())) {
                if (mRankList != null) {
                    openRankHtml(mRankList);
                }
            }
            if (DynamicCode.MESSAGE.value.equals(item.getCode())) {
                Navigate.openMessageCenter(requireContext());
            }
        }
    }

    /**
     * 打开排行榜
     */
    private void openRankHtml(HtmlActEntity entity) {
        String filepath = requireContext().getFilesDir().getAbsolutePath() + "/html/" + entity.getModuleCode() + "/index.html";
        String url = "file://" + filepath;
        // 设置入口的点击事件
        File file = new File(filepath);
        if (file.exists()) {
            // 跳转到Html页面
            H5Activity.actionStart(getContext(), url, "和行司机排行榜", IConstants.RANK_LIST);
        } else {
            // 文件不存在 重新下载
            mPresenter.downloadHtml(requireContext().getApplicationContext());
        }
    }

    @Override
    public void showModuleList(List<HtmlActEntity> vos) {
        if (vos != null && vos.size() > 0) {
            for (HtmlActEntity entity : vos) {
                if (entity.getModuleCode().equals(IConstants.MODULE_HEATMAP)) {
                    mHeatMap = entity;
                }
                if (entity.getModuleCode().equals(IConstants.MODULE_RANK)) {
                    mRankList = entity;
                }
            }
        }
    }

    /**
     * 获取广告数据后，根据不同的数据在UI层做不同的展现
     */
    @Override
    public void skipToAd(List<AdvertisementEntity> entities) {
        if (mAdsDialog != null) {
            mAdsDialog.dismiss();
            mAdsDialog = null;
        }
        if (entities != null && !entities.isEmpty()) {
            mAdsDialog = new AdDialog(requireContext(), entities);
            mAdsDialog.show();
        }
    }

    @Override
    public void skipToAdTransverse(List<AdvertisementEntity> entities) {
        if (entities != null && !entities.isEmpty()) {
            mLayTransverse.setVisibility(View.VISIBLE);
            List<String> data = new ArrayList<>();
            for (AdvertisementEntity entity : entities) {
                data.add(entity.getImgUrl());
            }
            //设置banner样式(显示圆形指示器)
            mBanner.setIndicator(new CircleIndicator(getContext()))
                    //设置指示器位置（指示器居中）
                    .setIndicatorGravity(IndicatorConfig.Direction.CENTER)
                    .isAutoLoop(true)
                    .addBannerLifecycleObserver(this)
                    .setLoopTime(5000)
                    .setAdapter(new BannerImageAdapter<AdvertisementEntity>(entities) {
                        @Override
                        public void onBindView(BannerImageHolder holder, AdvertisementEntity data, int position, int size) {
                            //图片加载自己实现
                            Glide.with(holder.itemView)
                                    .load(data.getImgUrl())
                                    .thumbnail(Glide.with(holder.itemView).load(R.drawable.banner_default))
                                    .error(R.drawable.banner_default)
                                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                                    .into(holder.imageView);
                        }
                    }).setOnBannerListener((OnBannerListener<AdvertisementEntity>) (entity, position) -> {
                if (isBtnBuffering()) {
                    return;
                }
                if (entity == null) {
                    return;
                }
                if (!TextUtils.isEmpty(entity.getHref())) {
                    H5Activity.actionStart(requireContext(),
                            entity.getHref(),
                            "",
                            entity.getUuid());
                }
            }).addOnPageChangeListener(new OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (position == (data.size() - 1)) {
                        mBanner.isAutoLoop(false);
                        mBanner.stop();
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            }).start();
            //banner设置方法全部调用完毕时最后调用
        } else {
            mLayTransverse.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideOrderInfo() {
        mLayoutOrderInfo.setVisibility(View.GONE);
    }

    @Override
    public void setMainImages(DynamicIconEntity data) {
        //////////获取配置的信息
        ////设置上部tab颜色和背景图片
        isGetIcon = true;
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            if (data.getScreen() != null && data.getScreen().size() > 0) {
                try {
                    DynamicIconEntity.IconBean first = data.getScreen().get(0);
                    if (!TextUtils.isEmpty(first.getBackdropColour())) {
                        int color = Color.parseColor(first.getBackdropColour());
                        ((MainActivity) activity).setTabColor(color);
                        ImmersionBar.with(activity)
                                .statusBarColor(first.getBackdropColour())
                                .fitsSystemWindows(true)
                                .init();
                    }
                    if (!TextUtils.isEmpty(first.getImgUrl())) {
                        Glide.with(getActivity())
                                .load(first.getImgUrl())
                                .into(new SimpleTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        mLayoutTop.setBackground(resource);
                                    }

                                    @Override
                                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                        if (!TextUtils.isEmpty(first.getBackdropColour())) {
                                            mLayoutTop.setBackgroundColor(Color.parseColor(first.getBackdropColour()));
                                        } else {
                                            mLayoutTop.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.main_bg_black));
                                        }
                                        super.onLoadFailed(errorDrawable);
                                    }
                                });
                    } else {
                        if (!TextUtils.isEmpty(first.getBackdropColour())) {
                            mLayoutTop.setBackgroundColor(Color.parseColor(first.getBackdropColour()));
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ((MainActivity) activity).setTabColor(ContextCompat.getColor(getActivity(), R.color.main_black));
                mLayoutTop.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.main_bg_black));
                ImmersionBar.with(activity)
                        .statusBarColor(R.color.hx_statusBar_background)
                        .fitsSystemWindows(true)
                        .init();
            }
            SysConfigEntity sysConfigEntity = SysConfigUtils.get().getSysConfig();
            if (sysConfigEntity.getZqDriverCenterIcon() != null) {
                DynamicIconEntity dynamicIconEntity = JSON.parseObject(sysConfigEntity.getZqDriverCenterIcon(), DynamicIconEntity.class);
                if (dynamicIconEntity.getIcon() != null
                        && dynamicIconEntity.getIcon().size() > 0) {
                    if (dynamicIconEntity.getIcon().size() == 2) {
                        mRvDynamic.setLayoutManager(new GridLayoutManager(requireContext(), 3));
                    }
                    if (dynamicIconEntity.getIcon().size() == 3) {
                        mRvDynamic.setLayoutManager(new GridLayoutManager(requireContext(), 3));
                    }
                    mDynamicAdapter.setAll(dynamicIconEntity.getIcon());
                }
            }
        }
    }

    @Override
    public void showDefaultModel() {
        mLayoutModel.setVisibility(View.GONE);
    }

    @Override
    public void hideDefaultModel() {
        mLayoutModel.setVisibility(View.GONE);
    }

    @Override
    public void postUnreadCount(int noReadCount) {
        //服务通知未读数量
        showMsgCount(noReadCount);
    }

    @SuppressLint("SetTextI18n")
    private void showMsgCount(int allUnReadMsgCount) {
        if (allUnReadMsgCount > 99) {
            msgNumTv.setText("99+");
        } else {
            msgNumTv.setText(String.valueOf(allUnReadMsgCount));
        }
        if (allUnReadMsgCount > 0) {
            msgNumTv.setVisibility(View.VISIBLE);
        } else {
            msgNumTv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
        ///////获取动态icon配置
        if (!isGetIcon) {
            mPresenter.getMainImages();
        }
        mPresenter.reqAllUnreadMessagesCount(1);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.onDestroy();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        DaggerHomeComponent.builder()
                .appComponent(getAppComponent())
                .homeModule(new HomeModule(this))
                .build().inject(this);
    }

    @Override
    public void showHomePageInfo(HomeEntity vo) {
        mTvCompleteOrder.setText(MessageFormat.format("{0}", TypeUtil.getValue(vo.countComplete))); //订单数
        mTvIncome.setText(vo.getStrOrderIncome());
        if (vo.onWorkTime != null) {
            mTvDuration.setText(TimeUtils.getHourWithDecimal(vo.onWorkTime));
        }
        /* 设置预约提醒的显示 */
        if (vo.driverDispatchLog != null) {
            mViewHolder.mLayoutMore.setVisibility(View.VISIBLE);
            //显示放在HomePresenter中，在showHomePageInfo之后
            mViewHolder.mLayoutMore.setOnClickListener(v -> {
                if (isBtnBuffering()) {
                    return; //避免快速点击添加
                }
                if (mPresenter.getDispatchVO() == null) {
                    if (mViewHolder.mLayoutOrder.getVisibility() == View.VISIBLE) {
                        gone(mViewHolder.mLayoutDispatch);
                    } else {
                        gone(mViewHolder.mLayoutDispatch, mViewHolder.mLayoutMore);
                    }
                    return;
                }
                DispatchActivity.actionStart(getContext(), mPresenter.getDispatchVO());
            });
            String curTime = DateUtil.formatDate(new Date(vo.driverDispatchLog.startTime), "HH:mm");
            mViewHolder.mTvTime.setText(curTime);
            mViewHolder.mTvStart.setText(vo.driverDispatchLog.startAddress);
            mViewHolder.mTvEnd.setText(vo.driverDispatchLog.endAddress);
        }
    }

    @Override
    public void hideRefreshing() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void openOrderByStatus(OrderVO vo) {
        Navigate.openOrderByStatus(getContext(), vo);
    }

    @Override
    public void showNetworkNotice(boolean disconnect) {
        int vis = disconnect ? View.VISIBLE : View.GONE;
        mLayoutNotice.setVisibility(vis);
    }

    @Override
    public void showUnreadMessage(List<HxMessageEntity> list) {
        mAdapter.setAll(list);
    }

    @Override
    public void removeMessage(HxMessageEntity entity) {
        mAdapter.remove(entity);
    }

    @Override
    public void clearAllMessage() {
        mAdapter.setAll(null);
    }

    @Override
    public void openWebUrl(String url, String title) {
        WebActivity.actionStart(getContext(), url, title);
    }

    @Override
    public void showOrderOngoing(String orderUuid) {
        if (mAdsDialog != null) {
            mAdsDialog.dismiss();
            mAdsDialog = null;
        }
        if (!isShow) {
            mBaseTipsDialog = new BaseTipsDialog(requireContext());
            mBaseTipsDialog.setTipsTitle("您有订单仍在进行中");
            mBaseTipsDialog.setConfirmText("我知道了");
            mBaseTipsDialog.setConfirmListener(() -> {
                isBtnBuffering(); //记录点击时间，避免其它按钮被快速点击
                HomeOngoingUtil.setIsOrderOngoing(false);
                mPresenter.reqOrderDetail(orderUuid);
                isShow = false;
            });
            mBaseTipsDialog.show();
            isShow = true;
            HomeOngoingUtil.setIsOrderOngoing(true);
        }
    }

    @Override
    public void showAppointBegin(OrderVO orderVO) {
        mLayoutOrderInfo.setVisibility(View.VISIBLE);
        mTvOrderTitle.setText(MessageFormat.format("预约单 {0}", orderVO.getStrDepartTime()));
        mTvOrderStart.setText(orderVO.originAddress);
        mTvOrderEnd.setText(orderVO.destAddress);
        mTvOrderStatus.setText("已超时");
        mLayoutOrderInfo.setOnClickListener(view -> {
            /////需要判断是否出车
            if (mPresenter.isDuty()) {
                Navigate.openOrderBegin(requireContext(), orderVO.uuid, orderVO);
            } else {
                toast("目前为收车状态，请出车后再操作");
            }
        });
    }

    @Override
    public void showAppointNotice(OrderVO orderVO) {
        mLayoutOrderInfo.setVisibility(View.VISIBLE);
        mTvOrderTitle.setText(MessageFormat.format("预约单 {0}", orderVO.getStrDepartTime()));
        mTvOrderStart.setText(orderVO.originAddress);
        mTvOrderEnd.setText(orderVO.destAddress);
        mTvOrderStatus.setText("待服务");
        mLayoutOrderInfo.setOnClickListener(view -> {
            if (mPresenter.isDuty()) {
                Navigate.openOrderBegin(requireContext(), orderVO.uuid, orderVO);
            } else {
                toast("目前为收车状态，请出车后再操作");
            }
        });
    }

    /**
     * 处理消息点击后的跳转
     */
    @Override
    public void dealWithMessage(HxMessageEntity entity) {
        if (entity == null) {
            return;
        }
        if (entity.getType() == null) {
            return;
        }

        switch (entity.getType()) {
            case HxMessageType.MSG_SYSTEM:
            case HxMessageType.MSG_ACTIVITY:
                if (!TextUtils.isEmpty(entity.getLinkUrl())) {
                    //跳转到对应的网页，并将消息设置为已读
                    String title = TextUtils.isEmpty(entity.getTitle()) ? "系统消息" : entity.getTitle();
                    H5Activity.actionStart(getContext(), entity.getLinkUrl(), title);
                }
                break;
            case HxMessageType.MSG_FEEDBACK_REPLY:
                //跳转到意见反馈
                Navigate.openFeedbackList(getContext());
                break;
            case HxMessageType.MSG_ORDER:
                //跳转到对应的订单详情页，并将消息设置为已读
                if (!TextUtils.isEmpty(entity.getOrderUuid())) {
                    mPresenter.reqOrderDetail(entity.getOrderUuid(), entity);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void showDispatchCompleteDialog(String reason) {
        if (mAdsDialog != null) {
            mAdsDialog.dismiss();
            mAdsDialog = null;
        }
        if (mBaseTipsDialog != null) {
            mBaseTipsDialog.dismiss();
        }
        mBaseTipsDialog = new BaseTipsDialog(requireContext());
        mBaseTipsDialog.setTipsTitle("完成调度");
        mBaseTipsDialog.setTipsContent(reason);
        mBaseTipsDialog.setConfirmText("我知道了");
        mBaseTipsDialog.setConfirmListener(() -> {
            ////结束调度，隐藏调度信息
            mViewHolder.mLayoutMore.setVisibility(View.GONE);
        });
        mBaseTipsDialog.show();
    }

    @Override
    public void showDispatchRemindDialog(DispatchVO vo) {
        SpeechUtil.speech(getActivity(), vo.content);
        DispatchDialogActivity.Companion.actionStart(requireActivity(), vo);
    }

    @Override
    public void openOrderFailed() {
        ToastUtil.getInstance().toast("当前是收车状态，请出车后到订单页开始行程");
    }

    /**
     * fix #7920 bug 目的地变更首页进行播报
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderChangeEvent(OrderEvent event) {
        if (OrderEvent.ORDER_CHANGE_ADDRESS == event.type
                && AppManager.getInstance().currentActivity() instanceof MainActivity) {
            String address = (String) event.obj1;
            String speak = getString(R.string.change_address_normal, address);
            SpeechUtil.speech(speak);
        }
    }

    static class ViewHolder {
        @BindView(R2.id.layout_order)
        ViewGroup mLayoutOrder;
        @BindView(R2.id.layout_more)
        LinearLayout mLayoutMore;
        @BindView(R2.id.layout_dispatch)
        ConstraintLayout mLayoutDispatch;
        @BindView(R2.id.tv_time)
        TextView mTvTime;
        @BindView(R2.id.tv_start)
        TextView mTvStart;
        @BindView(R2.id.tv_end)
        TextView mTvEnd;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @AfterPermissionGranted(RC_HEAT_MAP_PERMS)
    public void LocationPermission() {
        if (EasyPermissions.hasPermissions(requireContext(), PermissionUtil.getHeatMapNeededPermission())) {
            if (mHeatMap != null) {
                HeatMapActivity.actionStart(getContext(), mHeatMap.getLastVersionNo(), mHeatMap.getUuid());
            }
        } else {
            EasyPermissions.requestPermissions(this, "为了正常使用，请开启权限",
                    RC_HEAT_MAP_PERMS, PermissionUtil.getHeatMapNeededPermission());
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}

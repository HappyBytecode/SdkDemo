package anda.travel.driver.module.dispatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.amap.api.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import anda.travel.driver.BuildConfig;
import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.utils.NumberUtil;
import anda.travel.driver.baselibrary.utils.TypeUtil;
import anda.travel.driver.baselibrary.view.HeadView;
import anda.travel.driver.common.BaseFragment;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.event.MapEvent;
import anda.travel.driver.module.amap.navi.SingleNavigateActivity;
import anda.travel.driver.module.dispatch.dagger.DaggerDispatchComponent;
import anda.travel.driver.module.dispatch.dagger.DispatchModule;
import anda.travel.driver.module.vo.DispatchVO;
import anda.travel.driver.util.SysConfigUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class DispatchFragment extends BaseFragment implements DispatchContract.View {

    public static DispatchFragment newInstance(DispatchVO vo) {
        DispatchFragment fragment = new DispatchFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(IConstants.PARAMS, vo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @BindView(R2.id.head_view)
    HeadView mHeadView;
    @BindView(R2.id.tv_top_start)
    TextView mTvTopStart;
    @BindView(R2.id.tv_top_more)
    TextView mTvTopMore;
    @BindView(R2.id.tv_left)
    TextView mTvLeft;
    @BindView(R2.id.tv_right)
    TextView mTvRight;
    @BindView(R2.id.layout_sub)
    LinearLayout mLayoutSub;
    @BindView(R2.id.tv_emulator)
    Button mTvEmulator;
    @BindView(R2.id.tv_bottom)
    TextView mTvBottom;

    private boolean mShowTraffic = true; //是否显示道路拥堵情况（默认开启）
    private LatLng mNaviTo; //导航的目标地点

    @Inject
    DispatchPresenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.hxyc_fragment_dispatch, container, false);
        ButterKnife.bind(this, mView);
        if (getArguments() != null) {
            mPresenter.onCreate((DispatchVO) getArguments().getSerializable(IConstants.PARAMS));
        }
        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        DaggerDispatchComponent.builder()
                .appComponent(getAppComponent())
                .dispatchModule(new DispatchModule(this))
                .build().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
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

    @OnClick({R2.id.iv_navigate, R2.id.iv_service, R2.id.iv_traffic, R2.id.iv_location, R2.id.tv_emulator})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_navigate) { //跳转到独立的导航页
            if (isBtnBuffering()) return;
            skipToNavigate();
        } else if (id == R.id.iv_service) { //联系客服
            if (isBtnBuffering()) return;
            SysConfigUtils.get().dialServerPhone(getContext());
        } else if (id == R.id.iv_traffic) { //显示路况
            mShowTraffic = !mShowTraffic;
            EventBus.getDefault().post(new MapEvent(MapEvent.NAVI_TRAFFIC, mShowTraffic));
        } else if (id == R.id.iv_location) { //定位
            if (isBtnBuffering()) return;
            EventBus.getDefault().post(new MapEvent(MapEvent.NAVI_LOCATE));
        } else if (id == R.id.tv_emulator) { //模拟导航
            if (isBtnBuffering()) return;
            mPresenter.changeEmulator();
        }
    }

    public void setDisplay(DispatchVO vo) {
        if (vo == null) return;
        /* debug模式，是否显示"模拟导航按钮" */
        if (BuildConfig.DEBUG && mPresenter.isEmulatorOpen())
            showEmulatorBtn();

        mTvTopStart.setText(TypeUtil.getValue(vo.endAddress));
        mTvTopStart.setSelected(true);
        mNaviTo = vo.getEndLatLng();
        new Handler().postDelayed(() -> EventBus.getDefault().post(new MapEvent(MapEvent.NAVI_NAVIGATE, mNaviTo)), 1000);
    }

    @Override
    public void closeActivity() {
        requireActivity().finish();
    }

    @Override
    public void setNaviInfoDisplay(Integer distance, Integer time) {
        String strDistance = distance == null
                ? getResources().getString(R.string.order_navi_distance_calculating)
                : NumberUtil.getFormatValue(distance * 1.0D / 1000, false) + "公里";
        mTvLeft.setText(strDistance);
        String strTime = time == null
                ? getResources().getString(R.string.order_navi_time_calculating)
                : getMinute(time) + "分钟";
        mTvRight.setText(strTime);
    }

    public void skipToNavigate() {
        LatLng currentLatLng = mPresenter.getCurrentLatLng();
        if (currentLatLng == null) {
            toast("未获取到您当前的坐标");
            return;
        }
        if (mNaviTo == null) {
            toast("未获取到导航目的地坐标");
            return;
        }
        SingleNavigateActivity.actionStart(getContext(), currentLatLng, mNaviTo, true);
    }

    @Override
    public void showEmulatorBtn() {
        mTvEmulator.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmulatorBtn() {
        mTvEmulator.setVisibility(View.GONE);
    }

    @Override
    public void changeEmulator(boolean isEmulator) {
        int strRes = isEmulator ? R.string.navi_emulator_close : R.string.navi_emulator_open;
        mTvEmulator.setText(strRes);
    }

    private int getMinute(int secTime) {
        int minute = secTime / 60;
        if (secTime % 60 >= 30) minute += 1; //秒数多于30，分钟加1
        return minute;
    }
}

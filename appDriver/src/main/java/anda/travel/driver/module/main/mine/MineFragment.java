package anda.travel.driver.module.main.mine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.utils.GlideCircleTransform;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.baselibrary.utils.ToastUtil;
import anda.travel.driver.baselibrary.utils.file.FileUtil;
import anda.travel.driver.common.BaseFragment;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.data.entity.UserCenterMenuEntity;
import anda.travel.driver.module.main.mine.carinfo.CarInfoActivity;
import anda.travel.driver.module.main.mine.dagger.DaggerMineComponent;
import anda.travel.driver.module.main.mine.dagger.MineModule;
import anda.travel.driver.module.main.mine.help.check.ListenerCheckActivity;
import anda.travel.driver.module.task.TaskListActivity;
import anda.travel.driver.module.vo.MineVO;
import anda.travel.driver.module.web.WebActivity;
import anda.travel.driver.util.Navigate;
import anda.travel.driver.widget.HelpDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ren.yale.android.cachewebviewlib.WebViewCacheInterceptorInst;
import rx.Observable;

/**
 * 功能描述：我
 */
@SuppressLint("NonConstantResourceId")
public class MineFragment extends BaseFragment implements MineContract.View {

    @BindView(R2.id.iv_driver_avatar)
    ImageView iv_driver_avatar;
    @BindView(R2.id.tv_driver_name)
    TextView tv_driver_name;
    @BindView(R2.id.tv_licence_plate)
    TextView tv_licence_plate;
    @BindView(R2.id.tv_car_type)
    TextView tv_car_type;
    @BindView(R2.id.recycler_first)
    RecyclerView mRvFist;
    @BindView(R2.id.recycler_second)
    RecyclerView mRvSecond;
    @BindView(R2.id.tv_check_car)
    TextView mTvCheckCar;
    @Inject
    MinePresenter mPresenter;
    private String avatarUrl;
    private DynamicMenuAdapter mFirstAdapter;
    private DynamicMenuAdapter mSecondAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.hxyc_fragment_mine, container, false);
        ButterKnife.bind(this, mView);
        initDynamicMenu();
        mPresenter.onCreate();
        return mView;
    }

    private void initDynamicMenu() {
        mFirstAdapter = new DynamicMenuAdapter(requireContext());
        mRvFist.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        mRvFist.setAdapter(mFirstAdapter);
        mRvFist.setHasFixedSize(true);
        mRvFist.setNestedScrollingEnabled(false);
        mFirstAdapter.setOnItemClickListener((position, view, item) -> {
            if (isBtnBuffering()) {
                return; //避免快速点击添加
            }
            dealMenuClick(item);
        });

        mSecondAdapter = new DynamicMenuAdapter(requireContext());
        mRvSecond.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        mRvSecond.setAdapter(mSecondAdapter);
        mRvSecond.setHasFixedSize(true);
        mRvSecond.setNestedScrollingEnabled(false);
        mSecondAdapter.setOnItemClickListener((position, view, item) -> {
            if (isBtnBuffering()) {
                return; //避免快速点击添加
            }
            dealMenuClick(item);
        });
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        DaggerMineComponent.builder()
                .appComponent(getAppComponent())
                .mineModule(new MineModule(this))
                .build().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    @Override
    public void showDriverInfo(MineVO vo) {
        mPresenter.setIsDisplay(); //设置已显示
        avatarUrl = vo.driverAvatar;
        Glide.with(this).load(avatarUrl)
                .placeholder(R.drawable.my_siji_morentouxian)
                .transform(new GlideCircleTransform(requireContext()))
                .into(iv_driver_avatar);
        tv_driver_name.setText(vo.getName());
        if (!TextUtils.isEmpty(vo.licencePlate)) {
            tv_licence_plate.setVisibility(View.VISIBLE);
            tv_licence_plate.setText(vo.licencePlate);
        } else {
            tv_licence_plate.setVisibility(View.GONE);
            tv_licence_plate.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            tv_licence_plate.setText("");
        }
        tv_car_type.setText(vo.getBrandAndColor());
    }

    private void dealMenuClick(UserCenterMenuEntity.MenusBean item) {
        if (IConstants.HXNATIVE.equals(item.getType())) {
            switch (item.getCode()) {
                case IConstants.MYWALLET:
                    Navigate.openMyWalletNew(getContext());
                    break;
                case IConstants.QUESTIONS:
                    Navigate.openHelpCenter(getContext());
                    break;
                case IConstants.TASKLIST:
                    TaskListActivity.actionStart(getActivity());
                    break;
                case IConstants.JOURNAL:
                    Navigate.openJournal(getContext());
                    break;
                case IConstants.LISTENCHECK: ////听单检测
                    ListenerCheckActivity.actionStart(requireContext());
                    break;
                case IConstants.CLEARCACHE:
                    Observable.create((Observable.OnSubscribe<String>) subscriber -> {
                        Glide.get(requireContext()).clearDiskCache();
                        WebViewCacheInterceptorInst.getInstance().clearCache();
                        /////清除录音的缓存
                        FileUtil.deleteFile(requireActivity().getExternalFilesDir(null).toString() + IConstants.AUDIO_FOLDER);
                        subscriber.onNext("");
                    }).compose(RxUtil.applySchedulers()).subscribe(cache -> ToastUtil.getInstance().toast("清除缓存成功！"), throwable -> ToastUtil.getInstance().toast("清除缓存失败，请重试"));
                    break;
                case IConstants.SERVICEONLINE:
                    new HelpDialog(requireContext()).show();
                    break;
                default:
                    break;
            }
        } else if (IConstants.HXWEBPAGE.equals(item.getType())) {
            WebActivity.actionStart(getContext(), item.getUrl(), item.getName());
        }
    }

    @Override
    public void setHasData(Boolean hasData) {
        if (hasData) {
            mTvCheckCar.setVisibility(View.VISIBLE);
        } else {
            mTvCheckCar.setVisibility(View.GONE);
        }
    }

    @Override
    public void showDynamicMenuItem(ArrayList<UserCenterMenuEntity.MenusBean> first, ArrayList<UserCenterMenuEntity.MenusBean> second) {
        mFirstAdapter.addAll(first);
        mSecondAdapter.addAll(second);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick(R2.id.tv_check_car)
    public void onClick(View view) {
        if (view.getId() == R.id.tv_check_car) {
            CarInfoActivity.actionStart(requireContext(), "您好" + tv_driver_name.getText().toString() + "!");
        }
    }
}

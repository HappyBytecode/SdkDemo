package anda.travel.driver.module.main.mine;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.config.IConstants;
import anda.travel.driver.data.entity.DriverEntity;
import anda.travel.driver.data.entity.SysConfigEntity;
import anda.travel.driver.data.entity.UserCenterMenuEntity;
import anda.travel.driver.data.entity.UserCenterMenuEntity.MenusBean;
import anda.travel.driver.data.user.UserRepository;
import anda.travel.driver.module.vo.MineVO;
import anda.travel.driver.util.RefreshUtil;
import anda.travel.driver.util.SysConfigUtils;

/**
 * 功能描述：
 */
public class MinePresenter extends BasePresenter implements MineContract.Presenter {

    MineContract.View mView;
    UserRepository mUserRepository;
    boolean mIsDisplay; //界面上是否显示了司机信息

    @Inject
    public MinePresenter(MineContract.View view, UserRepository userRepository) {
        mView = view;
        mUserRepository = userRepository;
    }

    public void onCreate() {
        reqMenuConfig();
        reqHasData();
    }

    private void reqMenuConfig() {
        /////动态菜单的数据处理
        SysConfigEntity sysConfigEntity = SysConfigUtils.get().getSysConfig();
        if (sysConfigEntity == null) {
            return;
        }
        String menu = sysConfigEntity.getZqDriverCenterMenu();
        if (!TextUtils.isEmpty(menu)) {
            try {
                UserCenterMenuEntity.MenusBean[] codes = JSON.parseObject(menu, new TypeReference<MenusBean[]>() {
                });
                List<MenusBean> data = Arrays.asList(codes);
                if (!data.isEmpty()) {
                    List<UserCenterMenuEntity.MenusBean> temp = new ArrayList<>();
                    temp.addAll(data);

                    ArrayList<UserCenterMenuEntity.MenusBean> first = new ArrayList<>();
                    ArrayList<UserCenterMenuEntity.MenusBean> second = new ArrayList<>();
                    for (UserCenterMenuEntity.MenusBean bean : temp) {
                        if (!bean.getCode().equals(IConstants.CHARGE)) {
                            if (bean.getBanner() == 1) {
                                first.add(bean);
                            } else if (bean.getBanner() == 2) {
                                second.add(bean);
                            }
                        }
                    }
                    /////根据sort进行排序
                    Collections.sort(first, (t1, t2) -> {
                        if (t1.getSort() > t2.getSort()) {
                            return 1;
                        } else {
                            return 0;
                        }
                    });
                    Collections.sort(second, (t1, t2) -> {
                        if (t1.getSort() > t2.getSort()) {
                            return 1;
                        } else {
                            return 0;
                        }
                    });
                    mView.showDynamicMenuItem(first, second);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 在onResume中调用
     */
    @Override
    public void subscribe() {
        super.subscribe();
        getDriverInfo(); //从本地获取用户信息
        if (!mIsDisplay || RefreshUtil.isRefresh()) {
            RefreshUtil.setRefresh(false);
            reqDriverInfo(); //刷新用户信息
        }
    }

    @Override
    public void setIsDisplay() {
        mIsDisplay = true;
    }

    @Override
    public void getDriverInfo() {
        DriverEntity userInfo = mUserRepository.getUserInfoFromLocal();
        if (userInfo == null) return;
        MineVO vo = MineVO.createFrom(userInfo);
        mView.showDriverInfo(vo);
    }

    @Override
    public void reqDriverInfo() {
        mDisposable.add(mUserRepository.getUserInfoFromRemote()
                .map(MineVO::createFrom)
                .compose(RxUtil.applySchedulers())
                .subscribe(mineVO -> mView.showDriverInfo(mineVO)
                        , ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    @Override
    public void reqHasData() {
        mDisposable.add(mUserRepository
                .reqHasData()
                .compose(RxUtil.applySchedulers())
                .subscribe(hasData -> mView.setHasData(hasData)
                        , ex -> {
                        }));
    }
}

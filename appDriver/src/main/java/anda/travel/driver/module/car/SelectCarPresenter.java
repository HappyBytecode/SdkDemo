package anda.travel.driver.module.car;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.data.entity.DriverEntity;
import anda.travel.driver.data.user.UserRepository;

/**
 * @Author moyuwan
 * @Date 18/3/4
 */
public class SelectCarPresenter extends BasePresenter implements SelectCarContract.Presenter {

    private final SelectCarContract.View mView;
    private final UserRepository mUserRepository;

    @Inject
    public SelectCarPresenter(SelectCarContract.View view, UserRepository userRepository) {
        mView = view;
        mUserRepository = userRepository;
    }

    @Override
    public DriverEntity getDriverEntity() {
        return mUserRepository.getUserInfoFromLocal();
    }

    @Override
    public void selectCar(final String carNo) {
        mDisposable.add(
                mUserRepository.selectCar(carNo)
                        .compose(RxUtil.applySchedulers())
                        .subscribe(s -> {
                            DriverEntity entity = mUserRepository.getUserInfoFromLocal();
                            if (entity == null) return;
                            entity.vehicleNo = carNo;
                            mUserRepository.setUserInfo(entity);
                            mView.toast("车辆绑定成功");
                            mView.selectCarSuccess();
                        }, ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository))
        );
    }
}

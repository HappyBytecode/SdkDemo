package anda.travel.driver.module.main.mine.setting;

import java.io.File;

import javax.inject.Inject;

import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.baselibrary.utils.file.FileUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.data.entity.DriverEntity;
import anda.travel.driver.data.user.UserRepository;
import rx.Observable;

public class SettingPresenter extends BasePresenter implements SettingContract.Presenter {

    private final SettingContract.View mView;
    private final UserRepository mUserRepository;

    @Inject
    public SettingPresenter(SettingContract.View view, UserRepository userRepository) {
        mView = view;
        mUserRepository = userRepository;
    }

    @Override
    public void reqLogout() {
        mDisposable.add(mUserRepository.reqLogout()
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingViewWithDelay(true))
                .doAfterTerminate(mView::hideLoadingView)
                .subscribe(s -> mView.logoutSuccess()
                        , ex -> mView.logoutSuccess()));
    }

    @Override
    public void setOpenFloatWindow(boolean isOpen) {
        mUserRepository.setOpenFloatWindow(isOpen);
    }

    @Override
    public boolean getOpenFloatWindow() {
        return mUserRepository.getOpenFloatWindow();
    }

    @Override
    public void setScreenStatue(boolean isBright) {
        mUserRepository.setScreenStatue(isBright);
    }

    @Override
    public boolean getScreenStatue() {
        return mUserRepository.getScreenStatue();
    }

    @Override
    public DriverEntity getDriveEntity() {
        return mUserRepository.getUserInfoFromLocal();
    }

    @Override
    public boolean isReportAll() {
        return mUserRepository.isReportAll();
    }

    @Override
    public void setIsOnSetting(boolean isOnSetting) {
        mUserRepository.setIsOnSetting(isOnSetting);
    }

    @Override
    public void getCacheSize(File webCache, File glideCache, File audioCache) {
        mDisposable.add(Observable.
                create((Observable.OnSubscribe<String>) subscriber -> {
                    long webSize = FileUtil.getFolderSize(webCache);
                    long glideSize = FileUtil.getFolderSize(glideCache);
                    long audioSize = FileUtil.getFolderSize(audioCache);
                    long folderSize = webSize + glideSize + audioSize;
                    subscriber.onNext(FileUtil.getFileSizeFormat(folderSize));
                }).compose(RxUtil.applySchedulers()).subscribe(mView::showCacheSize,
                throwable -> {
                    mView.showCacheSize("");
                    throwable.printStackTrace();
                }));
    }
}

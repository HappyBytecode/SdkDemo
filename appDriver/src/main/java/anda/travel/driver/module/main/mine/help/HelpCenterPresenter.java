package anda.travel.driver.module.main.mine.help;

import javax.inject.Inject;

import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.data.user.UserRepository;

public class HelpCenterPresenter extends BasePresenter implements HelpCenterContract.Presenter {

    private final UserRepository mUserRepository;
    private final HelpCenterContract.View mView;

    @Inject
    public HelpCenterPresenter(UserRepository userRepository, HelpCenterContract.View view) {
        mUserRepository = userRepository;
        mView = view;
    }

    public void onCreate() {
        reqFAQs(); //刷新数据
    }

    @Override
    public void reqFAQs() {
        mDisposable.add(mUserRepository.getProblems()
//                .flatMapIterable(entities -> entities)
//                .map(FaqVO::createFrom)
//                .toList()
                .compose(RxUtil.applySchedulers())
//                .doOnSubscribe(() -> mView.showLoadingViewWithDelay(true))
//                .doAfterTerminate(() -> mView.hideLoadingView())
                .subscribe(mView::showFAQs
                        , ex -> mView.showFAQs(mUserRepository.getFAQList())));
    }

    @Override
    public void uploadError() {

    }

//    private void dealwithHxContact(DriverEntity entity) {
//        if (entity == null) {
//            mView.toast("无法联系客服，请稍候重试");
//            return;
//        }
//
//        final String userAvatar = TypeUtil.getValue(entity.avatar); //用户头像
//        final String contactUserName = entity.contactHxName; //指定的环信客服
//        if (TextUtils.isEmpty(contactUserName)) {
//            mView.toast("未获取到客服信息");
//            return;
//        }
//
//        if (HxManager.isLogin()) {
//            //已登录环信
//            HxManager.chat(mView.getContext(), contactUserName, userAvatar);
//            return;
//
//        }
//
//        //未登录环信
//        mView.toast("正在跳转在线客服，请稍候");
//        HxManager.login(entity.driverHxName, entity.driverHxPwd, new HxManager.HxLoginListener() {
//            @Override
//            public void loginSuccess() {
//                HxManager.chat(mView.getContext(), contactUserName, userAvatar);
//            }
//
//            @Override
//            public void loginFail() {
//                mView.toast("无法联系客服，请稍候重试");
//            }
//        });
//    }
}

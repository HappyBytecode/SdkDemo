package anda.travel.driver.module.main.mine.walletnew.rules;

import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.data.entity.WithdrawRuleEntity;
import anda.travel.driver.data.user.UserRepository;

public class RulesPresenter extends BasePresenter implements RulesContract.Presenter {

    private final UserRepository mUserRepository;
    private final RulesContract.View mView;

    @Inject
    public RulesPresenter(UserRepository userRepository, RulesContract.View view) {
        mUserRepository = userRepository;
        mView = view;
    }

    @Override
    public void subscribe() {
        super.subscribe();
        setDisplay();
        reqRules();
    }

    private void setDisplay() {
        List<WithdrawRuleEntity> list = mUserRepository.getWithdrawRuleFromLocal();
        if (list != null) mView.showRules(list);
    }

    @Override
    public void reqRules() {
        mDisposable.add(
                mUserRepository
                        .withdrawRule()
                        .compose(RxUtil.applySchedulers())
                        .subscribe(mView::showRules, ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository))
        );
    }

    @Override
    public void reqBillRules() {
        mDisposable.add(
                mUserRepository
                        .billRule()
                        .compose(RxUtil.applySchedulers())
                        .subscribe(mView::showRules,
                                ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository))
        );
    }

}

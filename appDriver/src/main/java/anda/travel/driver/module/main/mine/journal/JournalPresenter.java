package anda.travel.driver.module.main.mine.journal;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.data.user.UserRepository;

public class JournalPresenter extends BasePresenter implements JournalContract.Presenter {

    private final UserRepository mUserRepository;
    private final JournalContract.View mView;

    private int mPage = 1;

    @Inject
    public JournalPresenter(UserRepository userRepository, JournalContract.View view) {
        mUserRepository = userRepository;
        mView = view;
    }

    @Override
    public void reqJournal(String startTime, String endTime, int page) {
        mDisposable.add(mUserRepository
                .reqJournal(startTime, endTime, page)
                .compose(RxUtil.applySchedulers())
                .doAfterTerminate(() -> {
                    if (page <= 1) { //关闭下拉刷新图标
                        mView.onRefreshComplete();
                    }
                })
                .subscribe(entity -> {
                    if (entity.details.size() == 0 && page > 1) {
                        //说明此时加载的页面的上一页是最后一页，做toast提示
                        mView.noMore();
                        return;
                    }
                    mPage = page; //设置下标
                    if (mPage <= 1) { //刷新数据
                        mView.setData(entity);
                    } else { //添加数据
                        mView.addData(entity);
                    }
                }, ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

    public void reqRefresh(String startTime, String endTime) {
        reqJournal(startTime, endTime, 1);
    }

    public void reqMore(String startTime, String endTime) {
        reqJournal(startTime, endTime, mPage + 1);
    }
}

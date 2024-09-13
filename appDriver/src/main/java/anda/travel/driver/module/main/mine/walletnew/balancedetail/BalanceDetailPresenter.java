package anda.travel.driver.module.main.mine.walletnew.balancedetail;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.baselibrary.network.RequestParams;
import anda.travel.driver.baselibrary.utils.NetworkUtil;
import anda.travel.driver.baselibrary.utils.RxUtil;
import anda.travel.driver.common.BasePresenter;
import anda.travel.driver.data.entity.BalanceDetailListEntity;
import anda.travel.driver.data.user.UserRepository;

public class BalanceDetailPresenter extends BasePresenter implements BalanceDetailContract.Presenter {

    private final BalanceDetailContract.View mView;
    private final UserRepository mUserRepository;

    public static final int load_refresh = 0;//加载类别，0：刷新加载
    public static final int load_more = 1;//加载类别，1：上拉加载

    private int mLoadType = load_refresh;

    private int mPage = 1;

    private int status = 1;

    private String selectDateStr;

    @Inject
    public BalanceDetailPresenter(UserRepository userRepository, BalanceDetailContract.View view) {
        mUserRepository = userRepository;
        mView = view;
    }

    public void onCreate() {

    }

    @Override
    public void subscribe() {
        super.subscribe();
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public void setStartDate(String startDate) {
        this.selectDateStr = startDate;
    }

    /**
     * 刷新数据
     */
    public void reqRefresh() {
        mLoadType = load_refresh;
        if (!NetworkUtil.isNetworkAvailable(HxClientManager.getInstance().application.getApplicationContext())) {
            mView.netErrorView(true);
            return;
        } else {
            mView.netErrorView(false);
        }
        RequestParams.Builder builder = new RequestParams.Builder();
        builder.putParam("startDate", selectDateStr);
        builder.putParam("status", status);
        builder.putParam("nowPage", 1);
        reqBalanceDetailList(builder);
    }

    /**
     * 加载更多
     */
    public void reqMore() {
        mLoadType = load_more;
        if (!NetworkUtil.isNetworkAvailable(HxClientManager.getInstance().application.getApplicationContext())) {
            mView.netErrorView(true);
            return;
        } else {
            mView.netErrorView(false);
        }
        RequestParams.Builder builder = new RequestParams.Builder();
        builder.putParam("startDate", selectDateStr);
        builder.putParam("status", status);
        builder.putParam("nowPage", mPage + 1);
        reqBalanceDetailList(builder);
    }

    @Override
    public void reload() {
        //如果网络异常，点击重新加载时给出提示
        if (!NetworkUtil.isNetworkAvailable(HxClientManager.getInstance().application.getApplicationContext())) {
            mView.toast(R.string.network_error);
            return;
        }
        if (mLoadType == load_refresh) {
            mView.hideNoMore();
            reqRefresh();
        } else {
            reqMore();
        }
    }

    @Override
    public void reqBalanceDetailList(RequestParams.Builder builder) {
        int page = Integer.parseInt(builder.build().get("nowPage"));
        mDisposable.add(mUserRepository.reqBalanceDetailList(builder.build())
                .compose(RxUtil.applySchedulers())
                .doOnSubscribe(() -> mView.showLoadingViewWithDelay(true))
                .doAfterTerminate(() -> {
                    mView.hideLoadingView();
                    if (page <= 1) { //关闭下拉刷新图标
                        mView.onRefreshComplete();
                    }
                })
                .subscribe(entities -> {
                    if (entities.size() == 0 && page > 1) {
                        //说明此时加载的页面的上一页是最后一页，做toast提示
                        mView.noMore();
                        return;
                    }
                    List<BalanceDetailListEntity> mList = new ArrayList<>();
                    for (int i = 0; i < entities.size(); i++) {
                        entities.get(i).isTotal = true;
                        mList.add(entities.get(i));
                        List<BalanceDetailListEntity.BalanceDetailEntity> balanceDetailEntities
                                = entities.get(i).items;
                        if (null != balanceDetailEntities && !balanceDetailEntities.isEmpty()) {
                            for (int j = 0; j < balanceDetailEntities.size(); j++) {
                                BalanceDetailListEntity balanceDetailListEntity = new BalanceDetailListEntity();
                                balanceDetailListEntity.name = balanceDetailEntities.get(j).name;
                                balanceDetailListEntity.operateTime = balanceDetailEntities.get(j).operateTime;
                                balanceDetailListEntity.amount = balanceDetailEntities.get(j).amount;
                                balanceDetailListEntity.remark = balanceDetailEntities.get(j).remark;
                                balanceDetailListEntity.isTotal = false;
                                mList.add(balanceDetailListEntity);
                            }
                        }
                    }
                    mPage = page; //设置下标
                    if (mPage <= 1) { //刷新数据
                        mView.setData(mList);
                    } else { //添加数据
                        mView.addData(mList);
                    }
                }, ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository)));
    }

}

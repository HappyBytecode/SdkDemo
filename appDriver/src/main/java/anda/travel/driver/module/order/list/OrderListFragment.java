package anda.travel.driver.module.order.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.utils.ToastUtil;
import anda.travel.driver.baselibrary.view.refreshview.ExRefreshView;
import anda.travel.driver.baselibrary.view.refreshview.RefreshViewListener;
import anda.travel.driver.common.BaseFragment;
import anda.travel.driver.module.order.list.dagger.DaggerOrdreListComponent;
import anda.travel.driver.module.order.list.dagger.OrderListModule;
import anda.travel.driver.module.vo.OrderSummaryVO;
import anda.travel.driver.module.vo.OrderVO;
import anda.travel.driver.util.Navigate;
import anda.travel.driver.widget.popwindow.ComplexPopup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 功能描述：订单列表
 */
@SuppressLint("NonConstantResourceId")
public class OrderListFragment extends BaseFragment implements OrderListContract.View {

    @BindView(R2.id.tv_empty)
    TextView mTvEmpty;
    @BindView(R2.id.refresh)
    ExRefreshView mRefresh;
    @BindView(R2.id.show_popup_choice)
    TextView mShowPopupChoice;
    @BindView(R2.id.vs_net_error)
    ViewStub mNetErrorView;
    @BindView(R2.id.order_list_content)
    FrameLayout mOrderListContent;

    private OrderListAdapter mAdapter;

    @Inject
    OrderListPresenter mPresenter;

    private ComplexPopup mComplexPopup;

    private boolean mIsShowNetError;//是否显示了网络异常界面
    private boolean mIsNetErrorInflate;//neterrorview是否导入
    private boolean mIsShow;

    public static OrderListFragment newInstance() {
        return new OrderListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.hxyc_fragment_order_list, container, false);
        ButterKnife.bind(this, mView);
        mAdapter = new OrderListAdapter(getContext(), R.layout.hxyc_item_order_list);
        mRefresh.setLayoutManager(new LinearLayoutManager(getContext()));
        mRefresh.setAdapter(mAdapter);
        mAdapter.addEmptyLayout(mTvEmpty); //设置数据为空时，需显示的控件
        mAdapter.setOnClickListener(R.id.layout_order, (position, view, item) -> {
            if (isBtnBuffering()) return; //避免快速点击
            mPresenter.reqOrderDetail(item.uuid); //先获取订单详情
        });
        mAdapter.setOnClickListener(R.id.tv_for_payment, (position, view, item) -> mPresenter.rushFare(item.uuid));
        mRefresh.setRefreshListener(new RefreshViewListener() {
            @Override
            public void onRefresh() {
                hideNoMore();
                mPresenter.reqRefresh();
            }

            @Override
            public void onLoadMore() {
                mPresenter.reqMore();
            }
        });

        mComplexPopup = ComplexPopup.create(requireContext())
                .setDimView(mOrderListContent)
                .setAnimationStyle(R.style.CurtainsPopAnim)
                .apply();
        mComplexPopup.setChoiceCallBack((type, result) -> {
            mShowPopupChoice.setText(result);
            mPresenter.setCurOrderType(type);
        });
        mRefresh.setHideLoadMoreText(false);
        mPresenter.subscribe();
        mPresenter.onRegister();
        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        DaggerOrdreListComponent.builder()
                .appComponent(getAppComponent())
                .orderListModule(new OrderListModule(this))
                .build().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.unsubscribe();
    }

    @OnClick({R2.id.ll__popup_choice})
    public void onClick(View view) {
        if (view.getId() == R.id.ll__popup_choice) {
            mIsShow = !mIsShow;
            if (mIsShow) {
                mComplexPopup.showAsDropDown(mShowPopupChoice);
            } else {
                mComplexPopup.dismiss();
            }
        }
    }

    @Override
    public void setData(List<OrderSummaryVO> vos) {
        mAdapter.setAll(vos);
    }

    @Override
    public void addData(List<OrderSummaryVO> vos) {
        mAdapter.addAll(vos);
    }

    @Override
    public void onRefreshComplete() {
        mRefresh.setRefreshing(false);
    }

    @Override
    public void noMore() {
        mRefresh.hasNoMoreData(true);
    }

    @Override
    public void hideNoMore() {
        mRefresh.hasNoMoreData(false);
    }

    @Override
    public void openOrderByStatus(OrderVO vo) {
        Navigate.openOrderByStatus(getContext(), vo);
    }

    @Override
    public void openOrderFailed(OrderVO v) {
        ToastUtil.getInstance().toast("当前是收车状态，请先出车再开始行程");
    }

    @Override
    public void netErrorView(boolean isShow) {
        if (isShow && !mIsShowNetError) {
            if (mTvEmpty.getVisibility() == View.VISIBLE) {
                //如果空界面显示的情况下，需要隐藏空界面，再去显示网络异常界面，否则会重叠
                mAdapter.emptyGone();
            }
            //需要显示但是实际没有显示网络错误页面的时候，需要去显示网络错误页
            if (!mIsNetErrorInflate) {
                mIsNetErrorInflate = true;
                View netErrorView = mNetErrorView.inflate();//这里相当于把mNetErrorView显示
                mRefresh.setVisibility(View.GONE);
                mIsShowNetError = true;
                netErrorView.findViewById(R.id.tv_reload).setOnClickListener(v -> mPresenter.reload());
            } else {
                mNetErrorView.setVisibility(View.VISIBLE);
                mRefresh.setVisibility(View.GONE);
                mIsShowNetError = true;
            }
        } else if (!isShow && mIsShowNetError) {
            //不需要显示但是实际已经显示网络错误页的时候，需要隐藏网络错误页
            mNetErrorView.setVisibility(View.GONE);
            mRefresh.setVisibility(View.VISIBLE);
            mIsShowNetError = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onRelease();
    }
}

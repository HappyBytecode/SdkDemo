package anda.travel.driver.module.main.mine.message;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.view.refreshview.ExRefreshView;
import anda.travel.driver.baselibrary.view.refreshview.RefreshViewListener;
import anda.travel.driver.common.BaseFragment;
import anda.travel.driver.config.HxMessageType;
import anda.travel.driver.config.MessageStatus;
import anda.travel.driver.data.entity.HxMessageEntity;
import anda.travel.driver.module.main.mine.message.dagger.DaggerMessageComponent;
import anda.travel.driver.module.main.mine.message.dagger.MessageModule;
import anda.travel.driver.module.vo.OrderVO;
import anda.travel.driver.module.web.WebActivity;
import anda.travel.driver.util.Navigate;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageFragment extends BaseFragment implements MessageContract.View {

    @BindView(R2.id.ex_refresh_view)
    ExRefreshView mRefresh;
    @BindView(R2.id.tv_empty)
    TextView mTvEmpty;

    @Inject
    MessagePresenter mPresenter;
    private MessageAdapter mAdapter;

    private int prePosition = -1;// 上一次

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.hxyc_fragment_message, container, false);
        ButterKnife.bind(this, mView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRefresh.setLayoutManager(linearLayoutManager);
        mAdapter = new MessageAdapter(getActivity());
        mAdapter.setOnClickListener(MessageAdapter.MESSAGE_PIC, R.id.layout_item, (position, view, item) -> {
            if (isBtnBuffering() && position == prePosition) {//处理相同item的快速点击问题
                prePosition = position;
                return;
            }
            dealwithMessageAction(item); //处理消息点击后的跳转
            if (item.getStatus() == MessageStatus.UNREAD) {
                mPresenter.readMessage(item.getUuid());
                item.setStatus(MessageStatus.READ);
                mAdapter.notifyItemChanged(position);
            }
        });
        mAdapter.setOnClickListener(MessageAdapter.MESSAGE_NOPIC, R.id.layout_item, (position, view, item) -> {
            if (isBtnBuffering() && position == prePosition) {//处理相同item的快速点击问题
                prePosition = position;
                return;
            }
            dealwithMessageAction(item); //处理消息点击后的跳转
            if (item.getStatus() == MessageStatus.UNREAD) {
                mPresenter.readMessage(item.getUuid());
                item.setStatus(MessageStatus.READ);
                mAdapter.notifyItemChanged(position);
            }
        });

        mAdapter.addEmptyLayout(mTvEmpty);
        mRefresh.setAdapter(mAdapter);
        mRefresh.setRefreshListener(new RefreshViewListener() {
            @Override
            public void onRefresh() {
                mRefresh.hasNoMoreData(false);
                mPresenter.reqRefresh();
            }

            @Override
            public void onLoadMore() {
                mPresenter.reqMore();
            }
        });
        mRefresh.setHideLoadMoreText(false); //暂时隐藏上拉加载文字提示
        mPresenter.reqRefresh(); //获取数据

        return mView;
    }

    public static MessageFragment newInstance() {
        return new MessageFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        DaggerMessageComponent.builder()
                .appComponent(getAppComponent())
                .messageModule(new MessageModule(this))
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
    public void setData(List<HxMessageEntity> vos) {
        mAdapter.setAll(vos);
    }

    @Override
    public void addData(List<HxMessageEntity> vos) {
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
    public void cleanSucc() {
        toast(getResources().getString(R.string.delete_success));
        mAdapter.setAll(null); //清空数据
    }

    @Override
    public void cleanFail(int errorCode, String errorMsg) {
        toast(errorMsg);
    }

    @Override
    public void dealwithMessageAction(HxMessageEntity entity) {
        if (entity == null) return;
        if (entity.getType() == null) return;
        if (!TextUtils.isEmpty(entity.getOrderUuid())) { //订单相关
            mPresenter.reqOrderDetail(entity.getOrderUuid()); //先获取订单详情
            return;
        }

        if (!TextUtils.isEmpty(entity.getLinkUrl())) { //跳转到网页
            WebActivity.actionStart(getActivity(), entity.getLinkUrl(), entity.getTitle());
            return;
        }
        switch (entity.getType()) {
            case HxMessageType.MSG_SYSTEM:
                Navigate.openMsgDetail(getActivity(), entity);//跳转到系统详情
                break;
            case HxMessageType.MSG_FEEDBACK_REPLY:
                Navigate.openFeedbackList(getActivity()); //跳转到意见反馈
                break;
        }
    }

    @Override
    public void openOrderByStatus(OrderVO vo) {
        Navigate.openOrderByStatus(getActivity(), vo);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}

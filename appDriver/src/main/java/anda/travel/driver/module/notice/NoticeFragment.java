package anda.travel.driver.module.notice;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;

import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.common.BaseFragment;
import anda.travel.driver.auth.HxClientManager;
import anda.travel.driver.data.entity.NoticeEntity;
import anda.travel.driver.module.notice.dagger.DaggerNoticeComponent;
import anda.travel.driver.module.notice.dagger.NoticeModule;
import anda.travel.driver.module.notice.list.NoticeListActivity;
import anda.travel.driver.util.TimeUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NoticeFragment extends BaseFragment implements NoticeContract.View {

    @BindView(R2.id.img_close)
    ImageView mImgClose;
    @BindView(R2.id.layout_notice)
    LinearLayout mLayoutNotice;
    @BindView(R2.id.tv_notice)
    ViewFlipper mViewFlipper;

    @Inject
    NoticePresenter mPresenter;

    private static NoticeFragment instance;

    public NoticeFragment() {

    }

    public static NoticeFragment getInstance() {
        if (instance == null) {
            instance = new NoticeFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.hxyc_fragment_notice, container, false);
        ButterKnife.bind(this, mView);
        mPresenter.reqNoticeList();
        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        DaggerNoticeComponent.builder().appComponent(HxClientManager.getAppComponent())
                .noticeModule(new NoticeModule(this)).build().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.unsubscribe();
    }

    @Override
    public void showNoticeList(List<NoticeEntity> list) {
        if (list == null || list.isEmpty()) {
            mLayoutNotice.setVisibility(View.GONE);
            return;
        }
        mLayoutNotice.setVisibility(View.VISIBLE);
        //加入显示内容,集合类型
        startFlipping(requireContext(), mViewFlipper, list);
    }

    @OnClick({R2.id.layout_notice, R2.id.tv_notice, R2.id.img_close})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.layout_notice || id == R.id.tv_notice) {
            NoticeListActivity.actionStart(getContext());
        } else if (id == R.id.img_close) {
            mLayoutNotice.setVisibility(View.GONE);
        }
    }

    public void startFlipping(Context context, ViewFlipper vf, List<NoticeEntity> info) {
        if (vf.isFlipping()) {
            vf.stopFlipping();
        }
        vf.removeAllViews();
        vf.setInAnimation(context, R.anim.notice_in);
        vf.setOutAnimation(context, R.anim.notice_out);
        int len = info.size();
        for (int i = 0; i < len; i++) {
            NoticeEntity data = info.get(i);
            View v = ((Activity) context).getLayoutInflater().inflate(R.layout.hxyc_layout_notice_item, null);
            TextView titleTv = v.findViewById(R.id.tv_notice_item_title);
            titleTv.setText(data.getTitle());
            TextView timeTv = v.findViewById(R.id.tv_notice_item_time);
            timeTv.setText(TimeUtils.paseTime(data.getCreateTime()));
            vf.addView(v);
        }
        vf.startFlipping();
    }

    public void reFresh() {
        if (null != mPresenter) {
            mPresenter.reqNoticeList();
        }
    }
}

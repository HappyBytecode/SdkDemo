package anda.travel.driver.module.notice.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.view.HeadView;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.config.NoticeType;
import anda.travel.driver.data.entity.NoticeEntity;
import anda.travel.driver.module.notice.NoticeDetailActivity;
import anda.travel.driver.module.notice.list.dagger.DaggerNoticeListComponent;
import anda.travel.driver.module.notice.list.dagger.NoticeListModule;
import anda.travel.driver.module.web.H5Activity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 公告列表
 */
public class NoticeListActivity extends BaseActivity implements NoticeListContract.View {

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, NoticeListActivity.class);
        context.startActivity(intent);
    }

    @BindView(R2.id.head_view)
    HeadView mHeadView;
    @BindView(R2.id.rec_list)
    RecyclerView mRecList;

    private NoticeListAdapter mAdapter;

    @Inject
    NoticeListPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_notice_list);
        ButterKnife.bind(this);
        DaggerNoticeListComponent.builder().appComponent(getAppComponent())
                .noticeListModule(new NoticeListModule(this)).build().inject(this);
        mAdapter = new NoticeListAdapter(this);
        mAdapter.setOnClickListener(R.id.lay_item, (position, view, item) -> {
            if (item.getType() != null && item.getType() == NoticeType.TYPE_WEB) {
                H5Activity.actionStart(this, item.getHref(), item.getTitle());
            } else {
                NoticeDetailActivity.actionStart(this, item);
            }
        });
        mRecList.setAdapter(mAdapter);
        mPresenter.reqNoticeList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.unsubscribe();
    }

    @Override
    public void showNoticeList(List<NoticeEntity> list) {
        mAdapter.setAll(list);
    }

    @Override
    public boolean isActive() {
        return false;
    }
}

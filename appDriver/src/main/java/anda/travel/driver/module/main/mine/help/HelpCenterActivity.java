package anda.travel.driver.module.main.mine.help;

import android.content.Context;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gyf.immersionbar.ImmersionBar;

import java.util.List;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.data.entity.ProblemEntity;
import anda.travel.driver.module.main.mine.help.dragger.DaggerHelpCenterComponent;
import anda.travel.driver.module.main.mine.help.dragger.HelpCenterModule;
import anda.travel.driver.module.vo.FaqVO;
import anda.travel.driver.util.Navigate;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 帮助中心
 */
public class HelpCenterActivity extends BaseActivity implements HelpCenterContract.View {

    @BindView(R2.id.recycler_view)
    RecyclerView recycler_view;

    @Inject
    HelpCenterPresenter mPresenter;
    private FaqAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_help_center);
        ButterKnife.bind(this);

        DaggerHelpCenterComponent.builder()
                .appComponent(getAppComponent())
                .helpCenterModule(new HelpCenterModule(this))
                .build().inject(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_view.setLayoutManager(linearLayoutManager);
        adapter = new FaqAdapter(this);
        recycler_view.setAdapter(adapter);
        recycler_view.setNestedScrollingEnabled(false);
        adapter.setOnItemClickListener((position, view, item) -> {
            if (item == null) return;
            Navigate.openProblem(HelpCenterActivity.this, FaqVO.createFrom(item));
        });

        mPresenter.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void showFAQs(List<ProblemEntity> faqs) {
        adapter.setAll(faqs);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        ImmersionBar.with(this)
                .statusBarColor(android.R.color.white)
                .statusBarDarkFont(true)
                .fitsSystemWindows(true)
                .init();
    }
}

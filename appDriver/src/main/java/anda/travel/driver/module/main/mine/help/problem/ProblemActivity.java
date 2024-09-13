package anda.travel.driver.module.main.mine.help.problem;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import javax.inject.Inject;

import anda.travel.driver.R;
import anda.travel.driver.R2;
import anda.travel.driver.baselibrary.view.HeadView;
import anda.travel.driver.common.BaseActivity;
import anda.travel.driver.module.main.mine.help.problem.dagger.DaggerProblemComponent;
import anda.travel.driver.module.main.mine.help.problem.dagger.ProblemModule;
import anda.travel.driver.module.vo.FaqVO;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProblemActivity extends BaseActivity implements ProblemContract.View {

    @BindView(R2.id.head_view)
    HeadView head_view;
    @BindView(R2.id.web_view)
    WebView web_view;
    @BindView(R2.id.progressBar)
    ProgressBar mProgressBar;

    @Inject
    ProblemPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxyc_activity_problem);
        ButterKnife.bind(this);

        DaggerProblemComponent.builder()
                .appComponent(getAppComponent())
                .problemModule(new ProblemModule(this))
                .build().inject(this);

        FaqVO faq = (FaqVO) getIntent().getSerializableExtra("faq");
        String url = faq.link_url;
        String title = faq.title;
        head_view.setTitle(title);
        web_view.getSettings().setJavaScriptEnabled(true);
        web_view.setWebViewClient(new ReWebViewClient());
        web_view.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgressBar.setProgress(newProgress);
                super.onProgressChanged(view, newProgress);
            }
        });
        web_view.loadUrl(url);
    }

    class ReWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mProgressBar.setVisibility(View.GONE);
            super.onPageFinished(view, url);
        }
    }

    @Override
    public boolean isActive() {
        return false;
    }
}

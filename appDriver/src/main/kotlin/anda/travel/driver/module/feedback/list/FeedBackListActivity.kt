package anda.travel.driver.module.feedback.list

import anda.travel.driver.common.AppConfig
import anda.travel.driver.common.BaseActivity
import anda.travel.driver.config.IConstants
import anda.travel.driver.data.entity.FeedBackEntity
import anda.travel.driver.module.feedback.details.FeedBackDetailsActivity
import anda.travel.driver.module.feedback.list.di.DaggerFeedBackListComponent
import anda.travel.driver.module.feedback.list.di.FeedBackListModule
import anda.travel.driver.baselibrary.view.refreshview.RefreshViewListener
import anda.travel.driver.databinding.HxycActivityFeedbackListBinding
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import javax.inject.Inject

class FeedBackListActivity : BaseActivity(), FeedBackListContract.View {

    private lateinit var binding: HxycActivityFeedbackListBinding
    private lateinit var mListAdapter: FeedBackListAdapter
    private var mNowPage = 1
    private var mList = mutableListOf<FeedBackEntity>()

    @Inject
    lateinit var mPresenter: FeedBackListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerFeedBackListComponent.builder().appComponent(appComponent)
            .feedBackListModule(FeedBackListModule(this))
            .build().inject(this)
        binding = HxycActivityFeedbackListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initListener()
        mPresenter.getList(mNowPage)
        AppConfig.setFloatView(false)
    }

    private fun initView() {
        val linearLayoutManager = LinearLayoutManager(this@FeedBackListActivity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.exRefreshView.setLayoutManager(linearLayoutManager)
        mListAdapter = FeedBackListAdapter(this@FeedBackListActivity)
        mListAdapter.addEmptyLayout(binding.tvEmpty) //设置数据为空时，需显示的控件
        binding.exRefreshView.setAdapter(mListAdapter)
        binding.exRefreshView.setHideLoadMoreText(false)
    }

    private fun initListener() {
        mListAdapter.setOnItemClickListener { _, _, item ->
            val intent = Intent(this@FeedBackListActivity, FeedBackDetailsActivity::class.java)
            intent.putExtra(IConstants.FEEDBACK, item)
            startActivity(intent)
        }

        binding.exRefreshView.setRefreshListener(object : RefreshViewListener {
            override fun onRefresh() {
                binding.exRefreshView.hasNoMoreData(false)
                mNowPage = 1
                mPresenter.getList(mNowPage)
            }

            override fun onLoadMore() {
                mNowPage++
                mPresenter.getList(mNowPage)
            }
        })
    }

    override fun setData(it: List<FeedBackEntity>) {
        binding.exRefreshView.isRefreshing = false
        if (it.isEmpty() && mNowPage > 1 && mList.isNotEmpty()) {
            binding.exRefreshView.hasNoMoreData(true)
            return
        }
        if (mNowPage > 1) {
            mList.addAll(it)
            mListAdapter.addAll(mList)
        } else {
            mList.clear()
            mList.addAll(it)
            binding.exRefreshView.isRefreshing = false
            mListAdapter.setAll(mList)
        }
    }

    override fun isActive(): Boolean = true

}

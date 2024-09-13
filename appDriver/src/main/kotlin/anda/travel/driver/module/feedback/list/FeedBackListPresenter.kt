package anda.travel.driver.module.feedback.list

import anda.travel.driver.R
import anda.travel.driver.common.BasePresenter
import anda.travel.driver.data.user.UserRepository
import anda.travel.driver.baselibrary.utils.RxUtil
import javax.inject.Inject

class FeedBackListPresenter @Inject constructor(
    private val mView: FeedBackListContract.View,
    private val mUserRepository: UserRepository
) : BasePresenter(), FeedBackListContract.Presenter {

    fun getList(nowPage: Int) {
        mDisposable.add(mUserRepository.feedBackList(nowPage).compose(RxUtil.applySchedulers())
            .doOnSubscribe { mView.showLoadingViewWithDelay(true) }
            .doAfterTerminate { mView.hideLoadingView() }
            .subscribe({
                mView.setData(it)
            }, { ex -> showNetworkError(ex, R.string.network_error, mView, mUserRepository) })
        )
    }
}

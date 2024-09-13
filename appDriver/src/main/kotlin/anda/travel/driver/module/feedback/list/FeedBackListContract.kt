package anda.travel.driver.module.feedback.list

import anda.travel.driver.common.impl.IBasePresenter
import anda.travel.driver.common.impl.IBaseView
import anda.travel.driver.data.entity.FeedBackEntity

interface FeedBackListContract {
    interface View : IBaseView<Presenter> {
        fun setData(it: List<FeedBackEntity>)
    }

    interface Presenter : IBasePresenter
}

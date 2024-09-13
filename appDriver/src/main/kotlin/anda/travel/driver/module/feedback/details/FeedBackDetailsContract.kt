package anda.travel.driver.module.feedback.details

import anda.travel.driver.common.impl.IBasePresenter
import anda.travel.driver.common.impl.IBaseView

interface FeedBackDetailsContract {
    interface View : IBaseView<Presenter> {
        fun setData()
    }

    interface Presenter : IBasePresenter
}

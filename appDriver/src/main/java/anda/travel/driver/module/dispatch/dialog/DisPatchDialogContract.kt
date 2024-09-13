package anda.travel.driver.module.dispatch.dialog

import anda.travel.driver.common.impl.IBasePresenter
import anda.travel.driver.common.impl.IBaseView
import anda.travel.driver.module.vo.DispatchVO

interface DisPatchDialogContract {
    interface View : IBaseView<Presenter>

    interface Presenter : IBasePresenter {
        fun replyDispatch(vo: DispatchVO, status: Int)
    }

}

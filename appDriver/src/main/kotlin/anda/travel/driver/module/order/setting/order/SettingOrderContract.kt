package anda.travel.driver.module.order.setting.order

import anda.travel.driver.common.impl.IBasePresenter
import anda.travel.driver.common.impl.IBaseView
import anda.travel.driver.config.RemindType
import android.content.Context

interface SettingOrderContract {

    interface View : IBaseView<Presenter> {

        fun getViewContext(): Context?

        /**
         * 改变开时间的显示
         */
        fun changeStart(start: String)

        /**
         * 改变结束时间的显示
         *
         * @param end
         */
        fun changeEnd(end: String)

        fun initMode(isSimple: Boolean)

        fun setMode(mode: Int)

        fun showRemindType(type: Int)

    }

    interface Presenter : IBasePresenter {
        /**
         * 设置听单偏好
         *
         * @param remindType
         */
        fun selectRemindType(remindType: RemindType)

        /**
         * 选择开始时间
         */
        fun selectStart()

        /**
         * 选择结束时间
         */
        fun selectEnd()

        /**
         * 保存听单偏好
         */
        fun reqSaveRemindType()

        fun setIsOnSetting(isOnSetting: Boolean)

        fun reqNavMode()

        fun switchNavMode(isChecked: Boolean)

        fun reqMode()

        fun saveSetting(mode: Int)

    }
}

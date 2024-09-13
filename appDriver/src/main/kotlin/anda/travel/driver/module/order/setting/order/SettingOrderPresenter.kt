package anda.travel.driver.module.order.setting.order

import anda.travel.driver.R
import anda.travel.driver.common.BasePresenter
import anda.travel.driver.config.RemindType
import anda.travel.driver.data.duty.DutyRepository
import anda.travel.driver.data.entity.OrderListenerEntity
import anda.travel.driver.data.user.UserRepository
import anda.travel.driver.module.vo.TimeVO
import anda.travel.driver.util.DeviceUtil
import anda.travel.driver.widget.select.TimeDialog
import anda.travel.driver.baselibrary.network.RequestError
import anda.travel.driver.baselibrary.utils.RxUtil
import android.text.TextUtils
import rx.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class SettingOrderPresenter @Inject constructor(
    private val mView: SettingOrderContract.View,
    private val mDutyRepository: DutyRepository,
    private val mUserRepository: UserRepository
) : BasePresenter(), SettingOrderContract.Presenter {
    companion object {
        private const val MAX_HOUR = 23
        const val DISPATCH_MODE = 1//派单模式
        const val TRAP_MODE = 2//抢单模式
    }

    private var mEntity: OrderListenerEntity? = null
    private var mStartVo: TimeVO? = null
    private var mEndVo: TimeVO? = null

    fun onCreate() {
        mEntity = mDutyRepository.getListenerSetting(mUserRepository.localDriverUuid)
        val remindType = mEntity?.remindType
        if (remindType != null) {
            mView.showRemindType(remindType)
        } else {
            mView.showRemindType(RemindType.ALL.type)
        }

        mStartVo = TimeVO.createFrom(mEntity?.startTime)
        mEndVo = TimeVO.createFrom(mEntity?.endTime)

        val currentTime = System.currentTimeMillis()
        if (mStartVo != null && currentTime > mStartVo!!.timeStmap) {
            mStartVo = null // 当前时间大于开始时间，将mStartVO置空
        }
        if (mEndVo != null && currentTime > mEndVo!!.timeStmap) {
            mEndVo = null // 当前时间大于结束时间，将mEndVO置空
        }

        mView.changeStart(if (mStartVo == null) "" else mStartVo!!.strTime)
        mView.changeEnd(if (mEndVo == null) "" else mEndVo!!.strTime)
    }

    override fun selectRemindType(remindType: RemindType) {
        mEntity!!.remindType = remindType.type // 改变听单偏好
    }

    override fun selectStart() {
        TimeDialog(
            mView.getViewContext()
        ) { dialog: TimeDialog, vo: TimeVO ->
            dialog.dismiss()
            mStartVo = vo
            mView.changeStart(mStartVo!!.strTime)
            /* "结束时间"必需大于"开始时间"，如果不满足条件，需重置mEndVO */
            if (mEndVo != null && mEndVo!!.timeStmap <= mStartVo!!.timeStmap) {
                mEndVo = null
                mView.changeEnd("")
            }
        }.setSelectTime(mStartVo).builder().show()
    }

    override fun selectEnd() {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, 2)
        val maxVO = TimeVO.createFrom(cal.timeInMillis)
        if (!isMatchCondition(mStartVo, maxVO)) {
            mView.toast("预约设置时间只能在3天内")
            return
        }

        TimeDialog(
            mView.getViewContext()
        ) { dialog: TimeDialog, vo: TimeVO ->
            dialog.dismiss()
            mEndVo = vo
            mView.changeEnd(mEndVo!!.strTime)
        }.setStartTime(mStartVo) // 需要设置开始时间
            .setSelectTime(mEndVo).builder().show()
    }

    override fun reqSaveRemindType() {
        val remindType = mEntity!!.getRemindType()
        val startTimeStamp = if (mStartVo == null) "" else mStartVo!!.timeStmap.toString()
        val endTimeStamp = if (mEndVo == null) "" else mEndVo!!.timeStmap.toString()
        mDisposable.add(mDutyRepository.reqSaveListenerSetting(
            remindType,
            startTimeStamp.trim(),
            endTimeStamp.trim(),
            2
        )
            .compose(RxUtil.applySchedulers())
            .doOnSubscribe { mView.showLoadingViewWithDelay(true) }
            .doAfterTerminate { mView.hideLoadingView() }
            .subscribe({
            }) { ex: Throwable? ->
                showNetworkError(
                    ex,
                    R.string.network_error,
                    mView,
                    mUserRepository
                )
            })
    }

    override fun setIsOnSetting(isOnSetting: Boolean) {
        mUserRepository.setIsOnSetting(isOnSetting)
    }

    /**
     * 是否符合条件
     * startVO 小于或等于 maxVO，返回true
     * startVO 大于 maxVO，返回false
     *
     * @param startVO
     * @param maxVO
     * @return
     */
    private fun isMatchCondition(startVO: TimeVO?, maxVO: TimeVO): Boolean {
        return when {
            startVO == null -> true
            startVO.year < maxVO.year -> true
            startVO.year == maxVO.year -> return when {
                startVO.month < maxVO.month -> true
                startVO.month == maxVO.month -> if (startVO.day < maxVO.day) true else startVO.hour < MAX_HOUR
                else -> false
            }
            else -> false
        }
    }

    private var mIsSimple: Boolean = false

    override fun reqNavMode() {
        mIsSimple = mUserRepository.navVoiceMode == 0
        mView.initMode(mIsSimple)
    }

    override fun switchNavMode(isChecked: Boolean) {
        if (isChecked) {
            mUserRepository.navVoiceMode = 0
        } else {
            mUserRepository.navVoiceMode = 1
        }
    }

    // 获取当前抢单设置
    override fun reqMode() {
        val mode = if (mUserRepository.pushType == null ||
            mUserRepository.pushType == DISPATCH_MODE
        )
            DISPATCH_MODE
        else
            TRAP_MODE
        mView.setMode(mode)
    }

    override fun saveSetting(mode: Int) {
        val startTimeStamp = if (mStartVo == null) "" else mStartVo!!.timeStmap.toString()
        val endTimeStamp = if (mEndVo == null) "" else mEndVo!!.timeStmap.toString()
        mEntity!!.appointTimeStart = startTimeStamp
        mEntity!!.appointTimeEnd = endTimeStamp
        mDutyRepository.saveListenerSetting(mUserRepository.localDriverUuid, mEntity) // 保存到本地

        val params = DeviceUtil.getDeviceInfo(mView.getViewContext())
        mDisposable.add(mUserRepository.setDispatchOrderMode(mode)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .flatMap { mUserRepository.recording(params.build()) }
            .compose(RxUtil.applySchedulers())
            .subscribe({
                if (!TextUtils.isEmpty(it.pushType)) {
                    mUserRepository.pushType = Integer.parseInt(it.pushType)
                } else {
                    mUserRepository.pushType = DISPATCH_MODE
                }
                /////成功后调用接口请求另一个接口
                reqSaveRemindType()
            }, {
                if ((it is RequestError)) {
                    mView.toast(it.msg)
                }
                /////失败后调用接口请求另一个接口
                reqSaveRemindType()
            })
        )
    }

    fun cleanStartTime() {
        mStartVo = null
    }

    fun cleanEndTime() {
        mEndVo = null
    }
}

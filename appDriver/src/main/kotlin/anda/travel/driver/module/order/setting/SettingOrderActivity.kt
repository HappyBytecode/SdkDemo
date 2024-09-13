package anda.travel.driver.module.order.setting

import anda.travel.driver.common.BaseActivity
import anda.travel.driver.config.RemindType
import anda.travel.driver.data.user.UserRepository
import anda.travel.driver.databinding.HxycActivitySettingOrderBinding
import anda.travel.driver.module.order.setting.order.SettingOrderContract
import anda.travel.driver.module.order.setting.order.SettingOrderPresenter
import anda.travel.driver.module.order.setting.order.di.DaggerSettingOrderComponent
import anda.travel.driver.module.order.setting.order.di.SettingOrderModule
import anda.travel.driver.util.SpeechUtil
import anda.travel.driver.baselibrary.utils.RxUtil
import android.content.Context
import android.os.Bundle
import android.view.View
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

class SettingOrderActivity : BaseActivity(), SettingOrderContract.View {

    @Inject
    lateinit var mUserRepository: UserRepository

    private lateinit var binding: HxycActivitySettingOrderBinding

    @Inject
    lateinit var mPresenter: SettingOrderPresenter

    private var isNavConcise = false

    private var isInitMode = false

    private var mInitMode = SettingOrderPresenter.TRAP_MODE

    private val mDisposable by lazy { CompositeSubscription() }

    private var mRemindType: RemindType = RemindType.ALL

    private var isRealTimeChecked = false

    private var isAppointmentChecked = false

    private var isShowDispatch = false ////读取配置，是否能支持设置派单抢单

    private var isShowNavMode = false ////读取配置。是否设置语音简洁模式

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HxycActivitySettingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        DaggerSettingOrderComponent.builder()
            .appComponent(appComponent)
            .settingOrderModule(SettingOrderModule(this))
            .build().inject(this)
        initListener()
        reqWhiteList()
        mPresenter.onCreate()
        mPresenter.reqNavMode()
        mPresenter.reqMode()
    }

    private fun initListener() {
        binding.ivSettingClose.setOnClickListener {
            finish()
        }

        binding.sbRealTime.setOnCheckedChangeListener { _, isChecked ->
            isRealTimeChecked = isChecked
            if (!isAppointmentChecked && !isChecked) {
                binding.sbAppointment.setCheckedImmediately(true)
            }
            if (isShowDispatch) {
                if (isChecked) {
                    binding.tvSettingRealLabel.visibility = View.VISIBLE
                    binding.ssSettingMode.visibility = View.VISIBLE
                } else {
                    binding.tvSettingRealLabel.visibility = View.GONE
                    binding.ssSettingMode.visibility = View.GONE
                }
            }
        }

        binding.sbAppointment.setOnCheckedChangeListener { _, isChecked ->
            isAppointmentChecked = isChecked
            if (!isRealTimeChecked && !isChecked) {
                binding.sbRealTime.setCheckedImmediately(true)
            }
            if (isChecked) {
                binding.tvSettingAppointmentLabel.visibility = View.VISIBLE
                binding.layAppointmentStart.visibility = View.VISIBLE
                binding.layAppointmentEnd.visibility = View.VISIBLE
                binding.tvFromToEnd.visibility = View.VISIBLE
            } else {
                binding.tvSettingAppointmentLabel.visibility = View.GONE
                binding.layAppointmentStart.visibility = View.GONE
                binding.layAppointmentEnd.visibility = View.GONE
                binding.tvFromToEnd.visibility = View.GONE
            }
        }

        binding.sbVoice.setOnCheckedChangeListener { _, isChecked ->
            isNavConcise = isChecked
        }

        binding.tvSettingSave.setOnClickListener {
            mPresenter.switchNavMode(isNavConcise)

            mRemindType = if (isAppointmentChecked && !isRealTimeChecked) {
                RemindType.APPOINT
            } else if (isAppointmentChecked && isRealTimeChecked) {
                RemindType.ALL
            } else {
                RemindType.REALTIME
            }
            mPresenter.selectRemindType(mRemindType)

            mPresenter.saveSetting(mInitMode)

            SpeechUtil.speech(this, "完成设置")
            finish()
        }

        binding.ssSettingMode.setOnChangeListener {
            if (it == 0) {
                mInitMode = SettingOrderPresenter.TRAP_MODE
                if (!isInitMode) {
                    SpeechUtil.speech(this, "抢单模式")
                }
            } else {
                mInitMode = SettingOrderPresenter.DISPATCH_MODE
                if (!isInitMode) {
                    SpeechUtil.speech(this, "派单模式")
                }
            }
            isInitMode = false
        }

        binding.tvAppointmentStart.setOnClickListener {
            mPresenter.selectStart()
        }

        binding.tvAppointmentEnd.setOnClickListener {
            mPresenter.selectEnd()
        }

        binding.ivStartClose.setOnClickListener {
            binding.tvAppointmentStart.text = "从现在"
            it.visibility = View.GONE
            mPresenter.cleanStartTime()
        }

        binding.ivEndClose.setOnClickListener {
            binding.tvAppointmentEnd.text = "任意时间"
            it.visibility = View.GONE
            mPresenter.cleanEndTime()
        }
    }

    private fun reqWhiteList() {
        mDisposable.add(
            mUserRepository.reqWhiteList()
                .compose(RxUtil.applySchedulers())
                .subscribe(
                    {
                        for (value in it) {
                            if (value.uuid == "1") {
                                isShowDispatch = value.status == 1
                                if (isShowDispatch) {
                                    if (isRealTimeChecked) {
                                        binding.tvSettingRealLabel.visibility = View.VISIBLE
                                        binding.ssSettingMode.visibility = View.VISIBLE
                                    }
                                } else {
                                    binding.tvSettingRealLabel.visibility = View.GONE
                                    binding.ssSettingMode.visibility = View.GONE
                                }

                            } else if (value.uuid == "2") {
                                isShowNavMode = value.status == 1
                                if (isShowNavMode) {
                                    binding.sbVoice.visibility = View.VISIBLE
                                    binding.tvSettingVoice.visibility = View.VISIBLE
                                    binding.divideLineTwo.visibility = View.VISIBLE
                                } else {
                                    binding.sbVoice.visibility = View.GONE
                                    binding.tvSettingVoice.visibility = View.GONE
                                    binding.divideLineTwo.visibility = View.GONE
                                }
                            }
                        }
                    },
                    {

                    })
        )
    }

    override fun getViewContext(): Context? {
        return this
    }

    override fun changeStart(start: String) {
        if (start.isNotEmpty()) {
            binding.tvAppointmentStart.text = start
            binding.ivStartClose.visibility = View.VISIBLE
        }
    }

    override fun changeEnd(end: String) {
        if (end.isNotEmpty()) {
            binding.tvAppointmentEnd.text = end
            binding.ivEndClose.visibility = View.VISIBLE
        }
    }

    //////设置语音导航模式
    override fun initMode(isSimple: Boolean) {
        isNavConcise = isSimple
        binding.sbVoice.setCheckedImmediatelyNoEvent(isSimple)
    }

    ////抢派单模式
    override fun setMode(mode: Int) {
        mInitMode = mode
        isInitMode = true
        when (mode) {
            SettingOrderPresenter.TRAP_MODE -> {
                binding.ssSettingMode.setPosition(0)
            }
            SettingOrderPresenter.DISPATCH_MODE -> {
                binding.ssSettingMode.setPosition(1)
            }
        }
    }

    override fun showRemindType(type: Int) {
        when (type) {
            RemindType.REALTIME.type -> {
                isRealTimeChecked = true
                isAppointmentChecked = false
                binding.sbRealTime.setCheckedImmediately(true)
                binding.sbAppointment.setCheckedImmediately(false)
            }
            RemindType.APPOINT.type -> {
                isAppointmentChecked = true
                isRealTimeChecked = false
                binding.sbAppointment.setCheckedImmediately(true)
                binding.sbRealTime.setCheckedImmediately(false)
            }
            RemindType.ALL.type -> {
                isRealTimeChecked = true
                isAppointmentChecked = true
                binding.sbRealTime.setCheckedImmediately(true)
                binding.sbAppointment.setCheckedImmediately(true)
            }
        }

        if (isRealTimeChecked && isShowDispatch) {
            binding.tvSettingRealLabel.visibility = View.VISIBLE
            binding.ssSettingMode.visibility = View.VISIBLE
        } else {
            binding.tvSettingRealLabel.visibility = View.GONE
            binding.ssSettingMode.visibility = View.GONE
        }
        if (isAppointmentChecked) {
            binding.tvSettingAppointmentLabel.visibility = View.VISIBLE
            binding.layAppointmentStart.visibility = View.VISIBLE
            binding.layAppointmentEnd.visibility = View.VISIBLE
            binding.tvFromToEnd.visibility = View.VISIBLE
        } else {
            binding.tvSettingAppointmentLabel.visibility = View.GONE
            binding.layAppointmentStart.visibility = View.GONE
            binding.layAppointmentEnd.visibility = View.GONE
            binding.tvFromToEnd.visibility = View.GONE
        }
    }

    override fun isActive(): Boolean {
        return true
    }
}

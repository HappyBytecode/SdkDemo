package anda.travel.driver.module.order.details

import anda.travel.driver.R
import anda.travel.driver.R2
import anda.travel.driver.baselibrary.utils.DisplayUtil.dp2px
import anda.travel.driver.baselibrary.utils.NumberUtil
import anda.travel.driver.baselibrary.utils.TypeUtil
import anda.travel.driver.common.AppConfig
import anda.travel.driver.common.BaseFragment
import anda.travel.driver.config.*
import anda.travel.driver.configurl.ParseUtils
import anda.travel.driver.data.entity.OrderCostEntity
import anda.travel.driver.data.entity.WarningContentEntity
import anda.travel.driver.event.DutyEvent
import anda.travel.driver.event.MessageEvent
import anda.travel.driver.module.order.complain.OrderComplainActivity
import anda.travel.driver.module.order.detail.OrderDetailContract
import anda.travel.driver.module.order.detail.OrderDetailPresenter
import anda.travel.driver.module.order.detail.dagger.DaggerOrderDetailComponent
import anda.travel.driver.module.order.detail.dagger.OrderDetailModule
import anda.travel.driver.module.order.pay.PayDialogFragment
import anda.travel.driver.module.order.price.PriceDetailAdapter
import anda.travel.driver.module.vo.OrderVO
import anda.travel.driver.module.web.WebActivity
import anda.travel.driver.util.FontUtils
import anda.travel.driver.util.SpeechUtil
import anda.travel.driver.util.SysConfigUtils
import anda.travel.driver.widget.CommonAlertDialog
import anda.travel.driver.widget.layout.BaseLinearLayout
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import com.zhy.view.flowlayout.TagFlowLayout
import org.greenrobot.eventbus.EventBus
import pub.devrel.easypermissions.EasyPermissions
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * 订单详情页面
 */
@SuppressLint("NonConstantResourceId")
class OrderDetailFragment : BaseFragment(), OrderDetailContract.View, View.OnClickListener {
    @BindView(R2.id.order_detail_base)
    lateinit var orderDetailBase: BaseLinearLayout

    @BindView(R2.id.order_detail_service)
    lateinit var orderDetailService: BaseLinearLayout

    @BindView(R2.id.order_detail_status)
    lateinit var orderDetailStatus: BaseLinearLayout

    @BindView(R2.id.tv_name)
    lateinit var tvName: TextView

    @BindView(R2.id.tv_price)
    lateinit var tvPrice: TextView

    @BindView(R2.id.tv_complaint)
    lateinit var tvComplaint: TextView

    @BindView(R2.id.tv_call_service)
    lateinit var tvCallService: TextView

    @BindView(R2.id.btn_for_payment)
    lateinit var btnForPayment: TextView

    @BindView(R2.id.btn_payment_for_other)
    lateinit var btnPaymentForOther: TextView

    @BindView(R2.id.tv_rule)
    lateinit var tvRule: TextView

    @BindView(R2.id.tv_time)
    lateinit var tvTime: TextView

    @BindView(R2.id.tv_start)
    lateinit var tvStart: TextView

    @BindView(R2.id.tv_end)
    lateinit var tvEnd: TextView

    @BindView(R2.id.tv_order_status)
    lateinit var tvOrderStatus: TextView

    @BindView(R2.id.tv_trip_message)
    lateinit var tvTripMessage: TextView

    @BindView(R2.id.tv_unit)
    lateinit var tvUnit: TextView

    @BindView(R2.id.rv_fee_list)
    lateinit var rvFeeList: RecyclerView

    @BindView(R2.id.tfl_remark)
    lateinit var tflRemark: TagFlowLayout

    @BindView(R2.id.tv_switch_show_fare)
    lateinit var tvSwitchShowFare: TextView

    @BindView(R2.id.layout_rules)
    lateinit var layoutRules: LinearLayout

    @Inject
    lateinit var mPresenter: OrderDetailPresenter
    private var mTags = ArrayList<String?>()
    private var mIsShowRules = false
    var mAdapter: PriceDetailAdapter? = null

    companion object {
        fun newInstance(orderUuid: String?, vo: OrderVO?, refresh: Boolean): OrderDetailFragment {
            val fragment = OrderDetailFragment()
            val bundle = Bundle()
            bundle.putString(IConstants.ORDER_UUID, orderUuid)
            bundle.putSerializable(IConstants.ORDER_VO, vo)
            bundle.putBoolean(IConstants.REFRESH, refresh)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerOrderDetailComponent.builder()
                .appComponent(appComponent)
                .orderDetailModule(OrderDetailModule(this))
                .build().inject(this)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.hxyc_fragment_order_detail, container, false)

        ButterKnife.bind(this, mView)
        mPresenter.onCreate()
        mPresenter.orderUuid = requireArguments().getString(IConstants.ORDER_UUID) //设置订单编号
        mPresenter.setIntentOrderVO(requireArguments().getSerializable(IConstants.ORDER_VO) as OrderVO?) //设置订单信息
        val refresh = requireArguments().getBoolean(IConstants.REFRESH, false)
        if (refresh) mPresenter.setOrderRefresh()
        initUI()
        return mView
    }

    @SuppressLint("CheckResult")
    private fun initUI() {
        orderDetailBase.setRadiusAndShadow(
                dp2px(requireContext(), 8f),
                dp2px(requireContext(), 8f),
                0.5f
        )
        orderDetailService.setRadiusAndShadow(
                dp2px(requireContext(), 8f),
                dp2px(requireContext(), 8f),
                0.5f
        )
        orderDetailStatus.setRadiusAndShadow(
                dp2px(requireContext(), 8f),
                dp2px(requireContext(), 8f),
                0.5f
        )

        val observable = Observable.create<Typeface> { subscriber ->
            val bFonts = FontUtils.getFontTypeFace(requireContext())
            subscriber.onNext(bFonts) //发送完成
            subscriber.onCompleted() //发送完成
        }

        val subscriber: Subscriber<Typeface> = object : Subscriber<Typeface>() {
            override fun onNext(bFonts: Typeface?) {
                tvName.typeface = bFonts
                tvPrice.typeface = bFonts
            }

            override fun onCompleted() {

            }

            override fun onError(e: Throwable?) {

            }
        }

        observable.subscribeOn(Schedulers.io())//订阅者运行在子线程
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber)

        tvComplaint.setOnClickListener(this)
        tvCallService.setOnClickListener(this)
        btnForPayment.setOnClickListener(this)
        btnPaymentForOther.setOnClickListener(this)
        orderDetailStatus.setOnClickListener(this)
        tvRule.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        mPresenter.subscribe()
    }

    override fun onPause() {
        super.onPause()
        mPresenter.unsubscribe()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter.onDestroy()
    }

    override fun setOrderInfo(vo: OrderVO) {
        tvName.text = vo.passenger?.let { vo.passenger.passengerRealPhoneEnd } ?: let { "" }
        tvTime.text = vo.strDepartTime
        tvStart.text = TypeUtil.getValue(vo.originAddress)
        tvEnd.text = TypeUtil.getValue(vo.destAddress)
        if (vo.mainStatus == null || vo.subStatus == null) {
            toast("订单状态异常")
            return
        }
        var strStatus = ""
        var showPrice = false
        var payStatus = false ////是否是支付状态
        var showPayForOther = false
        var strStatusNotice = ""
        when (vo.mainStatus) {
            //订单取消（完结）
            OrderStatus.ORDER_MAIN_STATUS_CANCEL -> {
                strStatus = if (vo.subStatus != OrderStatus.CLOSE) "已取消" else "已关闭"
                strStatusNotice = "订单已被" + vo.getCancelObject() + "取消"
                if (vo.payStatus != null) {
                    if (vo.payStatus == HxPayStatus.FARE_CONFIRMED_NOT_PAY) {
                        strStatus = "待支付"
                        payStatus = true
                        showPrice = true
                        showPayForOther = false
                    } else if (vo.payStatus == HxPayStatus.FARE_PAYED || vo.payStatus == HxPayStatus.FARE_NOT_CONFIRM) {
                        ////已支付或者未确认
                        showPrice = true
                        payStatus = true
                    } else if (vo.payStatus == HxPayStatus.NO_FARE) {
                        showPrice = false
                        payStatus = false
                    }
                }
            }
            /////支付完成、订单结束
            OrderStatus.ORDER_MAIN_STATUS_PAYED -> {
                strStatus = "已支付"
                showPrice = true ///显示金额
                strStatusNotice = ""
                payStatus = true
                showPayForOther = false
            }
            OrderStatus.ORDER_MAIN_STATUS_DONE -> {
                strStatus = "待支付"
                payStatus = true
                showPrice = true
                showPayForOther = true
            }
            else -> {
                when (vo.subStatus) {
                    OrderStatus.WAIT_BEGIN_APPOINTMENT -> {
                        strStatus = "预约单"
                        strStatusNotice = getStrRemainTime(vo)
                    }
                    OrderStatus.WAIT_ARRIVE_ORIGIN -> {
                        strStatus = "进行中"
                        strStatusNotice = "已出发去接乘客，未到达上车地点"
                    }
                    OrderStatus.WATI_PASSENGER_GET_ON -> {
                        strStatus = "进行中"
                        strStatusNotice = "已到达上车地点，等待乘客上车"
                    }
                    OrderStatus.DEPART -> {
                        strStatus = "进行中"
                        strStatusNotice = "乘客已上车，正在前往目的地"
                    }
                    OrderStatus.ARRIVE_DEST -> {
                        strStatus = "待支付"
                        strStatusNotice = ""
                    }
                }
            }
        }
        tvOrderStatus.text = strStatus
        tvTripMessage.text = strStatusNotice
        if (showPrice) {
            showTotalFare(TypeUtil.getValue(vo.drvTotalFare))
        } else {
            gone(tvPrice, tvUnit)
        }

        if (payStatus) {
            if (vo.canHurryPay != null) {
                if (canHarryPay(vo.canHurryPay)) {
                    gone(btnForPayment)
                } else {
                    gone(btnForPayment)
                }
            } else {
                gone(btnForPayment)
            }
            if (showPayForOther) {
                gone(btnPaymentForOther)
            } else {
                gone(btnPaymentForOther)
            }
            ////填充数据
            mAdapter = PriceDetailAdapter(requireContext(), R.layout.hxyc_item_order_cost)
            rvFeeList.layoutManager = LinearLayoutManager(requireContext())
            rvFeeList.adapter = mAdapter
            mPresenter.reqFareItems()
        } else {
            gone(btnForPayment)
            gone(btnPaymentForOther)
        }

        //设置更多信息的显示（调度费、留言）
        setLayoutMoreDisplay(vo)
    }

    private fun setLayoutMoreDisplay(vo: OrderVO) {
        if (mTags.isNotEmpty()) {
            mTags.clear()
        }
        val strTip = vo.strTip
        if (!TextUtils.isEmpty(strTip)) {
            mTags.add(getString(R.string.order_popup_service_price, strTip))
        }
        if (!TextUtils.isEmpty(vo.remark)) {
            try {
                mTags.addAll(listOf(*vo.remark.split(",").toTypedArray()))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (mTags.isNotEmpty()) {
            visible(tflRemark)
        } else {
            gone(tflRemark)
        }

        tflRemark.adapter = object : TagAdapter<String?>(mTags.toMutableList()) {
            override fun getView(parent: FlowLayout, position: Int, s: String?): View {
                val tv = LayoutInflater.from(requireContext()).inflate(
                        R.layout.hxyc_layout_remarks_tag,
                        tflRemark, false
                ) as TextView
                if (!TextUtils.isEmpty(strTip) && position == 0) {
                    tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.order_fee_tag))
                    tv.setBackgroundResource(R.drawable.dra_order_fee_tag)
                }
                tv.text = s
                return tv
            }
        }
    }

    override fun showTotalFare(totalFare: Double) {
        visible(tvPrice, tvUnit)
        tvPrice.text = NumberUtil.getFormatValue(totalFare)
    }

    override fun showWarningInfo(entity: WarningContentEntity) {
        SpeechUtil.speech(context, entity.warnContent)
        CommonAlertDialog(requireContext()).builder()
                .setTitle("系统警告")
                .setPositiveButton("我知道了") {
                    mPresenter.warnCallback(WarnType.READED, entity.warnUuid)
                    EventBus.getDefault().post(MessageEvent(MessageEvent.SYS_WARNING_READ))
                    EventBus.getDefault().post(DutyEvent(DutyEvent.SYS_OFF_DUTY))
                }.setNegativeButton("取消") { }.setCancelable(false).show()
    }

    override fun setDisplay(entity: OrderCostEntity?) {
        if (entity?.costItemBean != null) {
            visible(tvSwitchShowFare)
        } else {
            gone(tvSwitchShowFare)
        }
        mAdapter?.setAll(entity?.costItemBean)
    }

    override fun showPayConfirm() {
        val isPumping: Int
        var title = resources.getString(R.string.order_pay_title_notice)
        val vo = mPresenter.orderVO
        if (vo?.isPumping != null) {
            isPumping = vo.isPumping
            val str = java.lang.StringBuilder()
            when (isPumping) {
                PumpingType.COMPLETE, PumpingType.COMPLETE_WITH_PUMP -> {
                    str.append("乘客需支付现金")
                            .append(NumberUtil.getFormatValue(vo.totalFare))
                            .append("元，请确认收足款项后再点击确认！")
                    val hasPumping = vo.pumpinFare != null && vo.pumpinFare > 0
                    val hasSubsidy = vo.subsidyFare != null && vo.subsidyFare > 0
                    if (hasPumping || hasSubsidy) {
                        str.append("确认后")
                        if (hasPumping) {
                            str.append("将从余额扣除抽成")
                                    .append(NumberUtil.getFormatValue(vo.pumpinFare)).append("元")
                        }
                        if (hasPumping && hasSubsidy) str.append("，")
                        if (hasSubsidy) {
                            str.append("发放补贴")
                                    .append(NumberUtil.getFormatValue(vo.subsidyFare)).append("元")
                        }
                        str.append("。")
                    }
                    title = str.toString()
                }
            }
        } else {
            isPumping = 0
        }

        CommonAlertDialog(requireContext()).builder()
                .setTitle(title)
                .setPositiveButton("我知道了") {
                    when (isPumping) {
                        PumpingType.PAY_FOR_PASSENGER -> {
                            val f = PayDialogFragment()
                            val args = Bundle()
                            args.putString(IConstants.ORDER_UUID, mPresenter.orderUuid)
                            if (mPresenter.orderVO != null && mPresenter.orderVO.totalFare != null) {
                                args.putDouble(IConstants.PRICE, mPresenter.orderVO.totalFare)
                            }
                            args.putBoolean(IConstants.NOTICE_TYPE, false)
                            f.arguments = args
                            val fragmentManager: FragmentManager =
                                    requireActivity().supportFragmentManager
                            f.show(fragmentManager, PayDialogFragment::class.java.name)
                        }
                        else -> {
                            mPresenter.completeOrder(isPumping)
                        }
                    }
                }.setNegativeButton("取消") { }.setCancelable(false).show()
    }

    override fun getMyContext(): Context {
        return requireContext()
    }

    override fun skipToPayPumping(orderUuid: String, fare: Double) {
        val f = PayDialogFragment()
        val args = Bundle()
        args.putString(IConstants.ORDER_UUID, orderUuid)
        args.putDouble(IConstants.PRICE, fare)
        args.putBoolean(IConstants.NOTICE_TYPE, true)
        f.arguments = args
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        f.show(fragmentManager, PayDialogFragment::class.java.name)
    }

    /////获取预约单该显示的信息
    private fun getStrRemainTime(vo: OrderVO): String {
        if (vo.departTime == null) return "请准时接驾"
        val time = vo.departTime - System.currentTimeMillis()
        val minutes = (time / (60 * 1000)).toInt() //分钟
        if (minutes <= 0) return "预约时间已到，请尽快接驾"
        val hours = minutes / 60 //小时
        if (hours <= 0) return "距离出发时间还有" + minutes + "分钟，请准时接驾"
        val days = hours / 24 //天
        if (days > 0) return "距离出发时间还有" + days + "天，请准时接驾"
        val str = StringBuilder()
        str.append("距离出发时间还有" + hours + "小时")
        val minute = minutes % 60
        if (minute > 0) str.append(minute.toString() + "分钟")
        str.append("，请准时接驾")
        return str.toString()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tv_complaint -> {
                OrderComplainActivity.actionStart(context, mPresenter.orderUuid)
            }
            R.id.tv_call_service -> {
                SysConfigUtils.get().dialServerPhone(context)
            }
            R.id.btn_for_payment -> {
                mPresenter.rushFare()
            }
            R.id.btn_payment_for_other -> {
                showPayConfirm()
            }
            R.id.order_detail_status -> {
                if (!mIsShowRules) {
                    mIsShowRules = true
                    visible(layoutRules)
                    val drawable = resources.getDrawable(R.drawable.icon_order_detail_up)
                    /// 这一步必须要做,否则不会显示.
                    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                    tvSwitchShowFare.setCompoundDrawables(null, null, drawable, null)
                    tvSwitchShowFare.text = "折叠详情"
                } else {
                    mIsShowRules = false
                    gone(layoutRules)
                    val drawable = resources.getDrawable(R.drawable.icon_order_detail_down)
                    /// 这一步必须要做,否则不会显示.
                    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                    tvSwitchShowFare.setCompoundDrawables(null, null, drawable, null)
                    tvSwitchShowFare.text = "展开详情"
                }
            }
            R.id.tv_rule -> {
                val config = ParseUtils.getInstance().myConfig
                if (config == null || TextUtils.isEmpty(config.priceRules)) {
                    toast("未获取到计价规则")
                    return
                }
                WebActivity.actionStart(
                        requireContext(), config.priceRules +
                        "?appid=" + AppConfig.ANDA_APPKEY +
                        "&busiUuid=" + mPresenter.businessUuid +
                        "&isDriver=2" +
                        if (TextUtils.isEmpty(mPresenter.orderUuid)) "" else "&orderUuid=${mPresenter.orderUuid}",
                        "计价规则"
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String?>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun shouldShowRequestPermissionRationale(permission: String): Boolean {
        return false
    }

    private fun canHarryPay(canHarryPay: Int): Boolean {
        val sysConfigEntity = SysConfigUtils.get().sysConfig
        return canHarryPay == OrderStatus.CAN_HURRY_PAY &&
                sysConfigEntity != null &&
                sysConfigEntity.hintPaymentSwitch == "1"
    }
}


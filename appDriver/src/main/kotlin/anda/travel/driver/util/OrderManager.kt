package anda.travel.driver.util

import anda.travel.driver.config.HxPositionFrom
import anda.travel.driver.config.IConstants
import anda.travel.driver.config.OrderStatus
import anda.travel.driver.data.dispatch.DispatchRepository
import anda.travel.driver.data.duty.DutyRepository
import anda.travel.driver.data.entity.PointEntity
import anda.travel.driver.data.user.UserRepository
import anda.travel.driver.module.amap.assist.CalculateUtils
import anda.travel.driver.module.order.ongoing.calculate.ICalculate
import anda.travel.driver.module.order.ongoing.calculate.LineCalculateImpl
import anda.travel.driver.module.vo.OrderVO
import anda.travel.driver.socket.SocketService
import anda.travel.driver.socket.message.UploadLocationMessage
import anda.travel.driver.socket.utils.InfoUtils
import anda.travel.driver.socket.utils.LocUtils
import anda.travel.driver.baselibrary.utils.BackgroundUtil
import android.content.Context
import com.amap.api.maps.model.LatLng

/**
 * 用于管理订单的单例类
 */
class OrderManager private constructor() {

    private var mOrderVO: OrderVO? = null

    private var mCalculateImpl: ICalculate? = null ////用于计算里程的类

    private var mPointEntity: PointEntity? = null

    private var mLastPoint: LatLng? = null

    private var mMileage = 0.00  /////计算里程

    private var mLastMileage = 0.00  /////上次的里程

    companion object {
        @Volatile
        var instance: OrderManager? = null

        @JvmStatic
        @Synchronized
        fun instance(): OrderManager {
            if (instance == null) {
                instance = OrderManager()
            }
            return instance!!
        }
    }

    ///////像管理类中塞入订单信息
    @Synchronized
    fun setOrderVO(orderVO: OrderVO?) {
        mOrderVO = orderVO
    }

    /////获取存入的订单信息
    @Synchronized
    fun getOrderVO(): OrderVO? {
        return mOrderVO
    }

    fun getUuid(): String? {
        return mOrderVO?.uuid
    }

    //////获取订单状态
    fun getSubStatus(): Int? {
        if (mOrderVO?.subStatus == null) {
            return OrderStatus.WAIT_REPLY
        }
        return mOrderVO?.subStatus
    }

    ////获取地址
    fun getDestAddress(): String? {
        return if (mOrderVO?.destAddress.isNullOrEmpty()) "目的地" else mOrderVO?.destAddress
    }

    /////获得乘客手机号
    fun getPassengerPhone(): String? {
        return mOrderVO?.passengerPhone
    }

    ////订单是否在进行中
    fun isDepart(): Boolean {
        return mOrderVO?.subStatus == OrderStatus.DEPART
    }

    fun create() {
        if (!getUuid().isNullOrEmpty()) {
            mCalculateImpl = LineCalculateImpl()
        }
    }

    fun releaseData() {
        /////重置订单信息，防止影响下一单
        setOrderVO(null)
        setLastMileage(0.0)
        setMileage(0.0)
        setLatLng(null)
        mCalculateImpl = null
        mPointEntity = null
    }

    /////////计算类中加入数据
    fun addPoint(point: PointEntity) {
        mPointEntity?.let {
            ////////////不为空的时候，控制计算的频率
            if ((point.loctime - mPointEntity!!.loctime >= SocketService.getInterval())) {
                mCalculateImpl!!.addPoint(point)
                mPointEntity = point
            }
        } ?: let {
            mCalculateImpl!!.addPoint(point)
            mPointEntity = point
        }
    }

    /**
     * 获取"上传坐标"的报文
     *
     * @return
     */
    fun getUploadLocationMessage(
        context: Context,
        mDutyRepository: DutyRepository,
        mDispatchRepository: DispatchRepository,
        mUserRepository: UserRepository
    ): UploadLocationMessage? {
        val currentPoint: LatLng = LocUtils.get().currentPoint ?: return null
        val clientUuid: String = InfoUtils.get().clientUuid ?: return null
        if (currentPoint.latitude <= 0) return null
        if (currentPoint.longitude <= 0) return null
        val mTimeStamp = System.currentTimeMillis()
        val mAngle = LocUtils.get().angle
        val mAdCode = LocUtils.get().adcode
        val vehicleUuid = InfoUtils.get().vehicleUuid
        val vehLvUuid = InfoUtils.get().vehLvUuid
        val depend = InfoUtils.get().depend
        val vehDepend = InfoUtils.get().vehDepend
        val locationUuid = "$mTimeStamp"
        //上次定位的坐标
        var oldPoint: LatLng? = getLatLng()
        //最新定位的坐标
        var newPoint: LatLng? = null
        var speed = 0.0
        mPointEntity?.let {
            newPoint = LatLng(mPointEntity!!.lat, mPointEntity!!.lon)
            speed = mPointEntity!!.speed + 0.0
        } ?: let {
            newPoint = currentPoint
        }
        if (oldPoint == null) oldPoint = newPoint
        //与上一点的距离
        val distance = CalculateUtils.calculateLineDistance(newPoint, oldPoint) / 1000 + 0.0
        //纬度
        val lat: Double = newPoint!!.latitude
        //经度
        val lng: Double = newPoint!!.longitude
        //角度
        //应用所处状态(1-前台,2-后台,3-锁屏)
        val appStatus = BackgroundUtil.getAppStatus(context)
        var uid = ""
        mUserRepository.let {
            uid = mUserRepository.userUuid
        }
        ////31.639042  118.499443 测试跨城的代码
//        if (!getUuid().isNullOrEmpty()) {
//            if(isDepart()) {
//                lat= 31.639042
//                lng = 118.499443
//            }
//        }
        val remindType = mDutyRepository.getListenerSetting(uid).getRemindType()
        var uploadLocationMessage = UploadLocationMessage(
            locationUuid,
            clientUuid,
            vehicleUuid,
            vehLvUuid,
            distance,
            lat,
            lng,
            mAngle,
            speed,
            mAdCode ?: IConstants.DefaultAdcode, depend, vehDepend, appStatus,
            HxPositionFrom.FROM_LOCATION, //默认使用是定位点
            remindType
        )
        uploadLocationMessage.uploadTime = mTimeStamp
        uploadLocationMessage.isListen = mDutyRepository.isOnDuty
        setLatLng(newPoint)
        uploadLocationMessage.clientUuid = clientUuid
        if (!getUuid().isNullOrEmpty()) {
            //有专车里程信息需要上传
            uploadLocationMessage.orderUuid = getUuid()
            uploadLocationMessage.passengerUuid = mOrderVO!!.passengerUuid
            //////上次的里程加上现在计算的里程
            if (isDepart()) {
                val data = getLastMileage() + mCalculateImpl!!.queryTotalDistance() / 1000.00
                uploadLocationMessage.mileage = data
                setMileage(data)
            } else {
                uploadLocationMessage.mileage = 0.00
            }
            uploadLocationMessage.uploadTime = mTimeStamp
            uploadLocationMessage.orderStatus = mOrderVO!!.subStatus
            uploadLocationMessage.isNavigation = HxPositionFrom.FROM_NAVIGATION

        } else if (mDispatchRepository.dispatch != null) {
            uploadLocationMessage.dispatchUuid = mDispatchRepository.dispatch.getUuid()
            uploadLocationMessage.mileage = mDispatchRepository.queryTotalDistance()
            setMileage(mDispatchRepository.queryTotalDistance())
            val latLng = mDispatchRepository.dispatchCurrentLatLng
            if (latLng != null) {
                uploadLocationMessage.lat = latLng.latitude
                uploadLocationMessage.lng = latLng.longitude
                uploadLocationMessage.isNavigation = HxPositionFrom.FROM_NAVIGATION
            }
        }
        return uploadLocationMessage
    }

    @Synchronized
    fun getLatLng(): LatLng? {
        return mLastPoint
    }

    @Synchronized
    fun setLatLng(lastPoint: LatLng?) {
        mLastPoint = lastPoint
    }

    @Synchronized
    fun getMileage(): Double {
        return mMileage
    }

    @Synchronized
    fun setMileage(mileage: Double) {
        mMileage = mileage
    }

    @Synchronized
    fun setLastMileage(mileage: Double) {
        mLastMileage = mileage
    }

    @Synchronized
    fun getLastMileage(): Double {
        return mLastMileage
    }
}
package anda.travel.driver.module.dispatch.dialog

import anda.travel.driver.R
import anda.travel.driver.baselibrary.network.RequestParams
import anda.travel.driver.baselibrary.utils.RxUtil
import anda.travel.driver.baselibrary.utils.ToastUtil
import anda.travel.driver.common.AppManager
import anda.travel.driver.common.BasePresenter
import anda.travel.driver.data.dispatch.DispatchRepository
import anda.travel.driver.data.user.UserRepository
import anda.travel.driver.module.vo.DispatchVO
import anda.travel.driver.socket.utils.InfoUtils
import anda.travel.driver.util.RouteUtil
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.DrivePath
import javax.inject.Inject

class DispatchDialogPresenter @Inject constructor(
    private val mView: DisPatchDialogContract.View,
    private val mUserRepository: UserRepository,
    private val mDispatchRepository: DispatchRepository
) : DisPatchDialogContract.Presenter, BasePresenter() {

    var mIsSearch: Boolean = false

    override fun replyDispatch(vo: DispatchVO, status: Int) {
        if (status == 1) {
            mDispatchRepository.dispatch = vo
        }
        val routeUtil = RouteUtil()
        routeUtil.driveRouter(
            AppManager.getInstance().currentActivity(),
            LatLonPoint(mUserRepository.currentLocation.lat, mUserRepository.currentLocation.lng),
            LatLonPoint(vo.endLat, vo.endLng),
            null
        )

        routeUtil.setOnRouteResultListener(object : RouteUtil.OnRouteResultListener {
            override fun onRouteResultSuccess(drivePath: DrivePath?) {
                val builder: RequestParams.Builder = RequestParams.Builder()
                builder.apply {
                    putParam("pushId", vo.uuid)
                    putParam("status", "" + status)
                    putParam("vehicleUuid", InfoUtils.get().vehicleUuid)
                    putParam("startCity", mUserRepository.city)
                    putParam("startAddress", mUserRepository.currentLocation.address)
                    putParam("startLng", mUserRepository.currentLocation.lng)
                    putParam("startLat", mUserRepository.currentLocation.lat)
                    putParam("endCity", mUserRepository.city)
                    putParam("endAddress", vo.endAddress)
                    putParam("endLng", vo.endLng)
                    putParam("endLat", vo.endLat)
                    putParam("planTrip", drivePath!!.distance * 1.0 / 1000)
                    putParam("planTime", drivePath.duration * 1000)
                }
                mDisposable.add(mUserRepository.replyDispatch(builder.build())
                    .compose(RxUtil.applySchedulers())
                    .subscribe({ message ->
                        if (!message.isNullOrEmpty()) {
                            ToastUtil.getInstance().toast(message)
                        }
                    },
                        { ex ->
                            showNetworkError(
                                ex,
                                R.string.network_error,
                                mView,
                                mUserRepository
                            )
                        }
                    )
                )
            }

            override fun onRouteResultFailed() {
                if (!mIsSearch) {
                    replyDispatch(vo, status)
                    mIsSearch = true
                }
            }
        })
    }
}

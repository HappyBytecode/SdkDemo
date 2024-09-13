package anda.travel.driver.module.dispatch.dialog

import anda.travel.driver.R
import anda.travel.driver.common.BaseActivityWithoutIconics
import anda.travel.driver.config.IConstants
import anda.travel.driver.data.user.UserRepository
import anda.travel.driver.module.amap.assist.MapUtils
import anda.travel.driver.module.dispatch.DispatchActivity
import anda.travel.driver.module.dispatch.dialog.dagger.DaggerDispatchDialogComponent
import anda.travel.driver.module.dispatch.dialog.dagger.DispatchDialogModule
import anda.travel.driver.module.vo.DispatchVO
import anda.travel.driver.widget.map.AMapRipple
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import kotlinx.android.synthetic.main.hxyc_activity_dispatchdialog.*
import javax.inject.Inject

/**
 * 后台系统推送的弹框页面
 * zy
 */
class DispatchDialogActivity : BaseActivityWithoutIconics(), View.OnClickListener,
    DisPatchDialogContract.View {

    private lateinit var mDisPatchVo: DispatchVO
    private var mAMap: AMap? = null
    private var mapRipple: AMapRipple? = null

    @Inject
    lateinit var mPresenter: DispatchDialogPresenter

    @Inject
    lateinit var mUserRepository: UserRepository

    companion object {
        fun actionStart(context: Context, vo: DispatchVO) {
            val intent = Intent(context, DispatchDialogActivity::class.java)
            intent.putExtra(IConstants.PARAMS, vo)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hxyc_activity_dispatchdialog)
        mDisPatchVo = intent.getSerializableExtra(IConstants.PARAMS) as DispatchVO
        DaggerDispatchDialogComponent.builder().appComponent(appComponent)
            .dispatchDialogModule(DispatchDialogModule(this))
            .build().inject(this)
        initMap(savedInstanceState)
        initContent()
    }

    private fun initContent() {
        dispatch_content.text = mDisPatchVo.content
        dispatch_go2navi.setOnClickListener(this)
        dispatch_ikonw.setOnClickListener(this)
        if (mDisPatchVo.endLat.toInt() == 0 && mDisPatchVo.endLat.toInt() == 0) {
            dispatch_go2navi.visibility = View.GONE
            dispatch_ikonw.visibility = View.GONE
            dispatch_await.visibility = View.VISIBLE
            dispatch_await.setOnClickListener(this)
        }
    }

    private fun initMap(savedInstanceState: Bundle?) {
        dispatch_map_view.onCreate(savedInstanceState)
        val loc: LatLng = if (mDisPatchVo.endLat.toInt() == 0 && mDisPatchVo.endLat.toInt() == 0) {
            LatLng(mUserRepository.currentLocation.lat, mUserRepository.currentLocation.lng)
        } else {
            LatLng(mDisPatchVo.endLat, mDisPatchVo.endLng)
        }
        mAMap = dispatch_map_view.map
        mAMap!!.run {
            setOnMapLoadedListener {
                animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15f))
            }
            uiSettings.isZoomControlsEnabled = false
        }
        MapUtils.addMarker(this, mAMap, R.drawable.circle_blue, loc)
        mapRipple = AMapRipple(mAMap!!, loc, this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.dispatch_go2navi -> {
                mPresenter.replyDispatch(mDisPatchVo, 1)
                DispatchActivity.actionStart(this, mDisPatchVo)
            }
            R.id.dispatch_ikonw -> {
                mPresenter.replyDispatch(mDisPatchVo, 0)
                finish()
            }
            R.id.dispatch_await -> {
                finish()
            }
        }
    }

    override fun onResume() {
        dispatch_map_view.onResume()
        mapRipple!!.run {
            withDistance(1000.00)
            withNumberOfRipples(3)
            withStrokeWidth(0)
            withFillColor(R.color.color_6989F1)
            withRippleDuration(10000)
            withTransparency(0.7f)
        }.startRippleMapAnimation()
        mPresenter.subscribe()
        super.onResume()
    }

    override fun onPause() {
        dispatch_map_view.onPause()
        mPresenter.unsubscribe()
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        if (mapRipple!!.isAnimationRunning) {
            mapRipple!!.stopRippleMapAnimation()
        }
    }

    override fun onDestroy() {
        mAMap = null
        dispatch_map_view.onDestroy()
        super.onDestroy()
    }

    override fun isActive(): Boolean {
        return true
    }
}

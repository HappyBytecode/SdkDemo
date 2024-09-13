package anda.travel.driver.widget.map

import anda.travel.driver.R
import android.animation.IntEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.amap.api.maps.AMap
import com.amap.api.maps.model.*
import kotlin.math.ceil

class AMapRipple(private val mAMap: AMap, private var mLatLng: LatLng?, context: Context) {
    private var mPrevLatLng: LatLng? = null
    private var mBackgroundImageDescriptor: BitmapDescriptor? = null // ripple image.
    private var mTransparency = 0.5f // transparency of image.

    @Volatile
    private var mDistance = 2000.0 // distance to which ripple should be shown in metres
    private var mNumberOfRipples = 1 // number of ripples to show, max = 4
    private var mFillColor = Color.TRANSPARENT // fill color of circle
    private var mStrokeColor = Color.BLACK // border color of circle
    private var mStrokeWidth = 10 // border width of circle
    private var mDurationBetweenTwoRipples: Long = 4000 // in microseconds.
    private var mRippleDuration: Long = 12000 // in microseconds
    private var mAnimators: Array<ValueAnimator?>
    private val mHandlers: Array<Handler?>
    private var mGroundOverlays: Array<GroundOverlay?> = arrayOf()
    private val mBackground: GradientDrawable

    /**
     * @return current state of animation
     */
    var isAnimationRunning = false
        private set

    private val mCircleOneRunnable = Runnable {
        mGroundOverlays[0] = mAMap.addGroundOverlay(
            GroundOverlayOptions()
                .position(mLatLng, mDistance.toInt().toFloat())
                .transparency(mTransparency)
                .image(mBackgroundImageDescriptor)
        )
        startAnimation(0)
    }

    private val mCircleTwoRunnable = Runnable {
        mGroundOverlays[1] = mAMap.addGroundOverlay(
            GroundOverlayOptions()
                .position(mLatLng, mDistance.toInt().toFloat())
                .transparency(mTransparency)
                .image(mBackgroundImageDescriptor)
        )
        startAnimation(1)
    }

    private val mCircleThreeRunnable = Runnable {
        mGroundOverlays[2] = mAMap.addGroundOverlay(
            GroundOverlayOptions()
                .position(mLatLng, mDistance.toInt().toFloat())
                .transparency(mTransparency)
                .image(mBackgroundImageDescriptor)
        )
        startAnimation(2)
    }

    private val mCircleFourRunnable = Runnable {
        mGroundOverlays[3] = mAMap.addGroundOverlay(
            GroundOverlayOptions()
                .position(mLatLng, mDistance.toInt().toFloat())
                .transparency(mTransparency)
                .image(mBackgroundImageDescriptor)
        )
        startAnimation(3)
    }

    init {
        mPrevLatLng = mLatLng
        mBackground =
            ContextCompat.getDrawable(context, R.drawable.map_rippleanim) as GradientDrawable
        mAnimators = arrayOfNulls(4)
        mHandlers = arrayOfNulls(4)
        mGroundOverlays = arrayOfNulls(4)
    }

    /**
     * @param transparency sets transparency for background of circle
     */
    fun withTransparency(transparency: Float): AMapRipple {
        mTransparency = transparency
        return this
    }

    /**
     * @param distance sets radius distance for circle
     */
    fun withDistance(distance: Double): AMapRipple {
        var dis = distance
        if (dis < 200) {
            dis = 200.0
        }
        mDistance = dis
        return this
    }

    /**
     * @param numberOfRipples sets count of ripples
     */
    fun withNumberOfRipples(numberOfRipples: Int): AMapRipple {
        var number = numberOfRipples
        if (number > 4 || number < 1) {
            number = 4
        }
        mNumberOfRipples = number
        return this
    }

    /**
     * @param fillColor sets fill color
     */
    fun withFillColor(fillColor: Int): AMapRipple {
        mFillColor = fillColor
        return this
    }

    @Deprecated("use {@link #withStrokeWidth(int)} instead")
    fun withStrokewidth(strokeWidth: Int) {
        mStrokeWidth = strokeWidth
    }

    /**
     * @param strokeWidth sets stroke width
     */
    fun withStrokeWidth(strokeWidth: Int): AMapRipple {
        mStrokeWidth = strokeWidth
        return this
    }

    /**
     * @param rippleDuration sets duration of ripple animation
     */
    fun withRippleDuration(rippleDuration: Long): AMapRipple {
        mRippleDuration = rippleDuration
        return this
    }

    private fun startAnimation(numberOfRipple: Int) {
        val animator = ValueAnimator.ofInt(0, mDistance.toInt())
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.RESTART
        animator.duration = mRippleDuration
        animator.setEvaluator(IntEvaluator())
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { valueAnimator ->
            val animated = valueAnimator.animatedValue as Int
            mGroundOverlays[numberOfRipple]!!.setDimensions(animated.toFloat())
            if (mDistance - animated <= 10) {
                if (mLatLng !== mPrevLatLng) {
                    mGroundOverlays[numberOfRipple]!!.position = mLatLng
                }
            }
        }
        animator.start()
        mAnimators[numberOfRipple] = animator
    }

    private fun setDrawableAndBitmap() {
        mBackground.setColor(mFillColor)
        mBackground.setStroke(dpToPx(mStrokeWidth.toFloat()), mStrokeColor)
        mBackgroundImageDescriptor = drawableToBitmapDescriptor(mBackground)
    }

    private fun dpToPx(dp: Float): Int {
        val density = Resources.getSystem().displayMetrics.density
        return ceil((dp * density).toDouble()).toInt()
    }

    /**
     * Stops current animation if it running
     */
    fun stopRippleMapAnimation() {
        if (isAnimationRunning) {
            try {
                for (i in 0 until mNumberOfRipples) {
                    if (i == 0) {
                        mHandlers[i]!!.removeCallbacks(mCircleOneRunnable)
                        mAnimators[i]!!.cancel()
                        mGroundOverlays[i]!!.remove()
                    }
                    if (i == 1) {
                        mHandlers[i]!!.removeCallbacks(mCircleTwoRunnable)
                        mAnimators[i]!!.cancel()
                        mGroundOverlays[i]!!.remove()
                    }
                    if (i == 2) {
                        mHandlers[i]!!.removeCallbacks(mCircleThreeRunnable)
                        mAnimators[i]!!.cancel()
                        mGroundOverlays[i]!!.remove()
                    }
                    if (i == 3) {
                        mHandlers[i]!!.removeCallbacks(mCircleFourRunnable)
                        mAnimators[i]!!.cancel()
                        mGroundOverlays[i]!!.remove()
                    }
                }
            } catch (e: Exception) {
                // no need to handle it
            }
        }
        isAnimationRunning = false
    }

    /**
     * Starts animations
     */
    fun startRippleMapAnimation() {
        if (!isAnimationRunning) {
            setDrawableAndBitmap()
            for (i in 0 until mNumberOfRipples) {
                if (i == 0) {
                    mHandlers[i] = Handler()
                    mHandlers[i]!!.postDelayed(mCircleOneRunnable, mDurationBetweenTwoRipples * i)
                }
                if (i == 1) {
                    mHandlers[i] = Handler()
                    mHandlers[i]!!.postDelayed(mCircleTwoRunnable, mDurationBetweenTwoRipples * i)
                }
                if (i == 2) {
                    mHandlers[i] = Handler()
                    mHandlers[i]!!.postDelayed(mCircleThreeRunnable, mDurationBetweenTwoRipples * i)
                }
                if (i == 3) {
                    mHandlers[i] = Handler()
                    mHandlers[i]!!.postDelayed(mCircleFourRunnable, mDurationBetweenTwoRipples * i)
                }
            }
        }
        isAnimationRunning = true
    }

    companion object {

        fun drawableToBitmapDescriptor(drawable: Drawable): BitmapDescriptor? {

            if (drawable is BitmapDrawable) {
                if (drawable.bitmap != null) {
                    return BitmapDescriptorFactory.fromBitmap(drawable.bitmap)
                }
            }
            val bitmap: Bitmap =
                if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
                    Bitmap.createBitmap(
                        1,
                        1,
                        Bitmap.Config.ARGB_8888
                    ) // Single color bitmap will be created of 1x1 pixel
                } else {
                    Bitmap.createBitmap(
                        drawable.intrinsicWidth,
                        drawable.intrinsicHeight,
                        Bitmap.Config.ARGB_8888
                    )
                }

            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }
}

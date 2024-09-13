package anda.travel.driver.widget

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

import java.lang.ref.WeakReference
import java.util.ArrayList
import anda.travel.driver.R
import anda.travel.driver.baselibrary.utils.DisplayUtil
import anda.travel.driver.baselibrary.view.update.utils.DensityUtil
import android.graphics.Typeface
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

/**
 * 对 support Design 包中的TabLayout包装
 * 主要实现功能：更改indicator 的长度
 * Created by zhouwei on 2018/5/18.
 */

class EnhanceTabLayout : FrameLayout {
    companion object {
        private const val DEFAULT_TAB_TEXT_SIZE = 16
        private const val DEFAULT_UNSELECTED_TEXT_COLOR = "#666666"
    }

    /**
     * retrive TabLayout Instance
     *
     * @return
     */
    var tabLayout: TabLayout? = null
        private set
    private var mTabList: MutableList<String>? = null
    private var mCustomViewList: MutableList<View>? = null
    private var mSelectIndicatorColor: Int = 0
    private var mSelectTextColor: Int = 0
    private var mUnSelectTextColor: Int = 0
    private var mIndicatorHeight: Int = 0
    private var mIndicatorWidth: Int = 0
    private var mTabMode: Int = 0
    private var mTabTextSize: Int = 0

    val customViewList: List<View>?
        get() = mCustomViewList

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    private fun readAttr(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.EnhanceTabLayout)
        mSelectIndicatorColor = typedArray.getColor(R.styleable.EnhanceTabLayout_tabIndicatorColor,
                ContextCompat.getColor(context, R.color.color_accent))
        mUnSelectTextColor = typedArray.getColor(R.styleable.EnhanceTabLayout_tabTextColor, Color.parseColor(DEFAULT_UNSELECTED_TEXT_COLOR))
        mSelectTextColor = typedArray.getColor(R.styleable.EnhanceTabLayout_tabSelectTextColor, ContextCompat.getColor(context, R.color.color_accent))
        mIndicatorHeight = typedArray.getDimensionPixelSize(R.styleable.EnhanceTabLayout_tabIndicatorHeight, 1)
        mIndicatorWidth = typedArray.getDimensionPixelSize(R.styleable.EnhanceTabLayout_tabIndicatorWidth, 0)
        mTabTextSize = typedArray.getDimensionPixelSize(R.styleable.EnhanceTabLayout_tabTextSize, DisplayUtil.dp2px(context, DEFAULT_TAB_TEXT_SIZE.toFloat()))
        mTabMode = typedArray.getInt(R.styleable.EnhanceTabLayout_tab_Mode, 2)
        typedArray.recycle()
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        readAttr(context, attrs)

        mTabList = ArrayList()
        mCustomViewList = ArrayList()
        val view = LayoutInflater.from(getContext()).inflate(R.layout.hxyc_enhance_tab_layout, this, true)
        tabLayout = view.findViewById(R.id.enhance_tab_view)

        // 添加属性
        tabLayout!!.tabMode = if (mTabMode == 1) TabLayout.MODE_FIXED else TabLayout.MODE_SCROLLABLE
        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // onTabItemSelected(tab.getPosition());
                // Tab 选中之后，改变各个Tab的状态
                for (i in 0 until tabLayout!!.tabCount) {
                    val customView = tabLayout!!.getTabAt(i)!!.customView ?: return
                    val text = customView.findViewById<View>(R.id.tab_item_text) as TextView
                    val indicator = customView.findViewById<View>(R.id.tab_item_indicator)
                    if (i == tab.position) { // 选中状态
                        text.setTextColor(mSelectTextColor)
                        text.typeface= Typeface.DEFAULT_BOLD
                        indicator.setBackgroundColor(mSelectIndicatorColor)
                        indicator.visibility = View.VISIBLE
                    } else { // 未选中状态
                        text.setTextColor(mUnSelectTextColor)
                        text.typeface= Typeface.DEFAULT
                        indicator.visibility = View.INVISIBLE
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
    }

    fun addOnTabSelectedListener(onTabSelectedListener: TabLayout.OnTabSelectedListener) {
        tabLayout!!.addOnTabSelectedListener(onTabSelectedListener)
    }

    /**
     * 与TabLayout 联动
     *
     * @param viewPager
     */
    fun setupWithViewPager(viewPager: ViewPager?) {
        tabLayout!!.addOnTabSelectedListener(ViewPagerOnTabSelectedListener(viewPager!!, this))
    }

    /**
     * 添加tab
     *
     * @param tab
     */
    fun addTab(tab: String) {
        mTabList!!.add(tab)
        val customView = getTabView(tab, mIndicatorWidth, mIndicatorHeight, mTabTextSize)
        mCustomViewList!!.add(customView)
        tabLayout!!.addTab(tabLayout!!.newTab().setCustomView(customView))
    }

    fun addTab(tab: String, resId: Int) {
        mTabList!!.add(tab)
        val customView = getTabView(tab, resId, mIndicatorWidth, mIndicatorHeight, mTabTextSize)
        mCustomViewList!!.add(customView)
        tabLayout!!.addTab(tabLayout!!.newTab().setCustomView(customView))
    }

    fun setMsgTotal(count: Int, tag: String) {
        val mList: List<View>? = customViewList
        for (i in mList?.indices!!) {
            val view = mList[i]
            val text = view.findViewById<View>(R.id.tab_item_text) as TextView
            if (text.text.equals(tag)) {
                val totalText = view.findViewById<View>(R.id.tv_total_message) as TextView
                if (count == 0) {
                    totalText.visibility = View.INVISIBLE;
                    totalText.text = count.toString()
                } else {
                    totalText.visibility = View.VISIBLE
                    if (count < 100) {
                        totalText.text = count.toString()
                    } else {
                        totalText.text = "99+"
                    }

                }
            }
        }
    }

    fun setUnreadMsgVisible(count: Int, tag: String) {
        val mList: List<View>? = customViewList
        for (i in mList?.indices!!) {
            val view = mList[i]
            val text = view.findViewById<View>(R.id.tab_item_text) as TextView
            if (text.text.equals(tag)) {
                val totalText = view.findViewById<View>(R.id.tv_total_message) as TextView
                if (count == 0) {
                    totalText.visibility = View.INVISIBLE;
                } else {
                    totalText.visibility = View.VISIBLE
                    val layoutParams = totalText.layoutParams
                    layoutParams.height = DensityUtil.dip2px(context, 8f)
                    layoutParams.width = DensityUtil.dip2px(context, 8f)
                    totalText.layoutParams = layoutParams
                }
            }
        }
    }

    class ViewPagerOnTabSelectedListener(private val mViewPager: ViewPager, enhanceTabLayout: EnhanceTabLayout) : TabLayout.OnTabSelectedListener {
        private val mTabLayoutRef: WeakReference<EnhanceTabLayout>?

        init {
            mTabLayoutRef = WeakReference(enhanceTabLayout)
        }

        override fun onTabSelected(tab: TabLayout.Tab) {
            mViewPager.currentItem = tab.position
            val mTabLayout = mTabLayoutRef!!.get()
            val customViewList = mTabLayout!!.customViewList
            if (customViewList == null || customViewList.isEmpty()) {
                return
            }
            for (i in customViewList.indices) {
                val view = customViewList[i]
                val text = view.findViewById<View>(R.id.tab_item_text) as TextView
                val indicator = view.findViewById<View>(R.id.tab_item_indicator)
                if (i == tab.position) { // 选中状态
                    text.setTextColor(mTabLayout.mSelectTextColor)
                    text.typeface= Typeface.DEFAULT_BOLD
                    indicator.setBackgroundColor(mTabLayout.mSelectIndicatorColor)
                    indicator.visibility = View.VISIBLE
                } else { // 未选中状态
                    text.setTextColor(mTabLayout.mUnSelectTextColor)
                    text.typeface= Typeface.DEFAULT
                    indicator.visibility = View.INVISIBLE
                }
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
            // No-op
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
            // No-op
        }
    }

    /**
     * 获取Tab 显示的内容
     *
     * @param
     * @return
     */
    private fun getTabView(text: String, indicatorWidth: Int, indicatorHeight: Int, textSize: Int): View {
        val view = LayoutInflater.from(context).inflate(R.layout.hxyc_tab_item_layout, null)
        val tabText = view.findViewById<View>(R.id.tab_item_text) as TextView
        if (indicatorWidth > 0) {
            val indicator = view.findViewById<View>(R.id.tab_item_indicator)
            val layoutParams = indicator.layoutParams
            layoutParams.width = indicatorWidth
            layoutParams.height = indicatorHeight
            indicator.layoutParams = layoutParams
        }
        tabText.textSize = DisplayUtil.px2sp(context, textSize.toFloat()).toFloat()
        tabText.text = text
        tabText.setTextColor(mUnSelectTextColor)
        return view
    }

    private fun getTabView(text: String, resId: Int, indicatorWidth: Int, indicatorHeight: Int, textSize: Int): View {
        val view = LayoutInflater.from(context).inflate(R.layout.hxyc_tab_item_layout, null)
        val tabText = view.findViewById<View>(R.id.tab_item_text) as TextView
        val tabImg = view.findViewById<ImageView>(R.id.tab_item_ico)
        if (indicatorWidth > 0) {
            val indicator = view.findViewById<View>(R.id.tab_item_indicator)
            val layoutParams = indicator.layoutParams
            layoutParams.width = indicatorWidth
            layoutParams.height = indicatorHeight
            indicator.layoutParams = layoutParams
        }
        tabText.textSize = textSize.toFloat()
        tabText.text = text
        tabImg.setImageResource(resId)
        return view
    }
}

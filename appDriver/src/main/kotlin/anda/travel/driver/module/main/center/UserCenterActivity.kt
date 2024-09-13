package anda.travel.driver.module.main.center

import anda.travel.driver.R
import anda.travel.driver.common.BaseActivity
import anda.travel.driver.databinding.HxycActivityUsercenterBinding
import anda.travel.driver.module.main.mine.MineFragment
import anda.travel.driver.util.Navigate
import android.os.Bundle
import com.gyf.immersionbar.ImmersionBar

/**
 * zy  个人中心
 */
class UserCenterActivity : BaseActivity() {

    private val mMineFragment by lazy { MineFragment() }
    private lateinit var binding: HxycActivityUsercenterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HxycActivityUsercenterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_container, mMineFragment)
        transaction.commit()
        binding.headView.rightView.setOnClickListener {
            Navigate.openSetting(this)
        }
    }

    override fun initImmersionBar() {
        super.initImmersionBar()
        ImmersionBar.with(this)
            .statusBarColor(android.R.color.white)
            .statusBarDarkFont(true)
            .fitsSystemWindows(true)
            .init()
    }
}
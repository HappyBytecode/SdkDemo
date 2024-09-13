package anda.travel.driver.module.main.orderlist

import anda.travel.driver.R
import anda.travel.driver.common.BaseActivity
import anda.travel.driver.module.order.list.OrderListFragment
import android.content.Context
import android.content.Intent
import android.os.Bundle

class OrderListActivity : BaseActivity() {
    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, OrderListActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val mOrderListFragment by lazy { OrderListFragment.newInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hxyc_activity_order_list)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_container, mOrderListFragment)
        transaction.commit()
    }
}

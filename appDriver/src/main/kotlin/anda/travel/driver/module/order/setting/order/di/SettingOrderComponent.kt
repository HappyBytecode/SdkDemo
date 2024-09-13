package anda.travel.driver.module.order.setting.order.di

import anda.travel.driver.baselibrary.annotation.FragmentScope
import anda.travel.driver.common.dagger.AppComponent
import anda.travel.driver.module.order.setting.SettingOrderActivity
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [SettingOrderModule::class])
interface SettingOrderComponent {
    fun inject(settingOrderActivity: SettingOrderActivity)
}

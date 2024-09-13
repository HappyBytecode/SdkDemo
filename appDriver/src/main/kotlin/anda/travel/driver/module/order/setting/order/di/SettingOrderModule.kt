package anda.travel.driver.module.order.setting.order.di

import anda.travel.driver.module.order.setting.order.SettingOrderContract
import dagger.Module
import dagger.Provides

@Module
class SettingOrderModule(private val view: SettingOrderContract.View) {

    @Provides
    fun provideSettingOrderContractView(): SettingOrderContract.View {
        return view
    }
}

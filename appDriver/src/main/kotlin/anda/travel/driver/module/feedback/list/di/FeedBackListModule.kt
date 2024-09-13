package anda.travel.driver.module.feedback.list.di

import anda.travel.driver.module.feedback.list.FeedBackListContract
import dagger.Module
import dagger.Provides

@Module
class FeedBackListModule(private val view: FeedBackListContract.View) {
    @Provides
    fun provideFeedBackListContractView(): FeedBackListContract.View = view
}

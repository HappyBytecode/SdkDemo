package anda.travel.driver.module.feedback.details.di

import anda.travel.driver.module.feedback.details.FeedBackDetailsContract
import dagger.Module
import dagger.Provides

@Module
class FeedBackDetailsModule(private val view: FeedBackDetailsContract.View) {
    @Provides
    fun provideFeedBackDetailsContractView(): FeedBackDetailsContract.View = view
}

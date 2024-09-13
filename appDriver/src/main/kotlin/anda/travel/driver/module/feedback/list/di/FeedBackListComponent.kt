package anda.travel.driver.module.feedback.list.di

import anda.travel.driver.baselibrary.annotation.FragmentScope
import anda.travel.driver.common.dagger.AppComponent
import anda.travel.driver.module.feedback.list.FeedBackListActivity
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [FeedBackListModule::class])
interface FeedBackListComponent {
    fun inject(feedbackActivity: FeedBackListActivity)
}

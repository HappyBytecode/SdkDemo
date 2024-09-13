package anda.travel.driver.module.feedback.details.di

import anda.travel.driver.baselibrary.annotation.FragmentScope
import anda.travel.driver.common.dagger.AppComponent
import anda.travel.driver.module.feedback.details.FeedBackDetailsActivity
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [FeedBackDetailsModule::class])
interface FeedBackDetailsComponent {
    fun inject(feedbackActivity: FeedBackDetailsActivity)
}

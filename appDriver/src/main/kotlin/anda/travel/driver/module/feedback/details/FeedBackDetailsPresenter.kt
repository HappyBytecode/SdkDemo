package anda.travel.driver.module.feedback.details

import anda.travel.driver.common.BasePresenter
import anda.travel.driver.data.user.UserRepository
import javax.inject.Inject

class FeedBackDetailsPresenter @Inject constructor(
    private val mView: FeedBackDetailsContract.View,
    private val mUserRepository: UserRepository
) : BasePresenter(), FeedBackDetailsContract.Presenter

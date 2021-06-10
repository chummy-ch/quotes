package com.example.quotes.onboarding.viewmodels

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.ViewModelContext
import com.example.quotes.notification.NotificationRepository
import com.example.quotes.notification.NotificationUsecase
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class OnboardingNotificationsViewModel(
    private val initialState: OnboardingNotificationState,
    private val notificationRepository: NotificationRepository,
    private val notificationUsecase: NotificationUsecase
) : MavericksViewModel<OnboardingNotificationState>(initialState) {

    init {
        viewModelScope.launch {
            val currentNotification = notificationRepository.loadNotificationPeriod()
            setState { copy(notification = Success(currentNotification)) }
        }
    }

    fun getNotificationsList() = notificationRepository.getNotificationList()

    fun saveNotificationPeriod(id: Int) {
        viewModelScope.launch {
            val newNotification = notificationRepository.saveSelectedNotification(id)
            setState { copy(notification = Success(newNotification)) }
        }
        notificationUsecase.switchBootReceiver(id == 0)
    }

    companion object : MavericksViewModelFactory<OnboardingNotificationsViewModel, OnboardingNotificationState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: OnboardingNotificationState
        ): OnboardingNotificationsViewModel {
            with(viewModelContext.activity) {
                val notificationRepository: NotificationRepository by inject()
                val notificationUsecase: NotificationUsecase by inject()
                return OnboardingNotificationsViewModel(
                    state,
                    notificationRepository,
                    notificationUsecase
                )
            }
        }
    }
}

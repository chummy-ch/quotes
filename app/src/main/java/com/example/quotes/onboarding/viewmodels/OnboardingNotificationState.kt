package com.example.quotes.onboarding.viewmodels

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized

data class OnboardingNotificationState(
    val notification: Async<Int> = Uninitialized
) : MavericksState

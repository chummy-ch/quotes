package com.example.quotes.onboarding.viewmodels

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import com.example.quotes.category.CategoryPresentationModel

data class OnBoardingCategoriesState(
    val categories: Async<List<CategoryPresentationModel>> = Uninitialized
) : MavericksState

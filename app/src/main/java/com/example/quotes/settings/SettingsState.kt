package com.example.quotes.settings

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import com.example.quotes.category.CategoryPresentationModel
import com.example.quotes.category.Event

data class SettingsState(
    val fonts: Async<List<FontModel>> = Uninitialized,
    val categories: Async<List<CategoryPresentationModel>> = Uninitialized,
    val notification: Async<Int> = Uninitialized,
    val alarms: Async<List<Long>> = Uninitialized,
    val hasFavorites: Async<Boolean> = Uninitialized,
    val adResult: Event<CategoriesEvent>? = null
) : MavericksState

sealed class CategoriesEvent
object AdFailToShow : CategoriesEvent()
data class AdShow(val rewardIsEarned: Boolean) : CategoriesEvent()

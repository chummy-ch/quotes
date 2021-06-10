package com.example.quotes.onboarding.viewmodels

import com.airbnb.mvrx.*
import com.example.quotes.category.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class OnboardingCategoriesViewModel(
    private val initialState: OnBoardingCategoriesState,
    private val categoriesRepository: CategoriesRepository
) : MavericksViewModel<OnBoardingCategoriesState>(initialState) {

    init {
        viewModelScope.launch {
            val categories = categoriesRepository.getUnlockedCategories()
            setState { copy(categories = Success(categories)) }
        }
    }

    fun selectCategory(id: Long) {
        viewModelScope.launch {
            val newCategory = categoriesRepository.selectCategory(id)
            updateCategories(newCategory)
        }
    }

    fun unselectCategory(id: Long) {
        viewModelScope.launch {
            val newCategory = categoriesRepository.unselectCategory(id)
            updateCategories(newCategory)
        }
    }

    private fun unlock(id: Long) {
        viewModelScope.launch {
            val newCategory = categoriesRepository.changeCategoryStatus(id, Unlocked(true))
            updateCategories(newCategory)
        }
    }

    private fun updateCategories(category: CategoryPresentationModel) {
        setState {
            val categories = this.categories() ?: return@setState copy()
            val newList = categories.map { if (it.name == category.name) category else it }

            copy(categories = Success(newList))
        }
    }

    companion object : MavericksViewModelFactory<OnboardingCategoriesViewModel, OnBoardingCategoriesState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: OnBoardingCategoriesState
        ): OnboardingCategoriesViewModel {
            with(viewModelContext.activity) {
                val categoriesRepository: CategoriesRepository by inject()
                return OnboardingCategoriesViewModel(
                    state,
                    categoriesRepository
                )
            }
        }
    }
}

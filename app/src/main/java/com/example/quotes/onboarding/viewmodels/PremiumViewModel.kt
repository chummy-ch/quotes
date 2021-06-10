package com.example.quotes.onboarding.viewmodels

import com.airbnb.mvrx.*
import com.example.quotes.onboarding.OnboardingPremiumFragment
import com.example.quotes.onboarding.PremiumRepository
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class PremiumViewModel(
    private val initialState: PremiumState,
    private val premiumRepository: PremiumRepository
) : MavericksViewModel<PremiumState>(initialState) {
    init {
        viewModelScope.launch {
            setState { copy(products = Loading(), selectedProduct = Loading()) }
            val products = premiumRepository.getProducts()
            setState {
                copy(
                    products = Success(products),
                    selectedProduct = Success(products.first { it.vendorProductId == OnboardingPremiumFragment.ANNUAL_SUB_ID })
                )
            }
        }
    }

    fun selectPremiumProduct(productId: String) {
        viewModelScope.launch {
            val product = premiumRepository.setProductById(productId) ?: return@launch
            setState { copy(selectedProduct = Success(product)) }
        }
    }

    companion object : MavericksViewModelFactory<PremiumViewModel, PremiumState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: PremiumState
        ): PremiumViewModel {
            with(viewModelContext.activity) {
                val premiumRepository: PremiumRepository by inject()
                return PremiumViewModel(
                    state,
                    premiumRepository
                )
            }
        }
    }
}

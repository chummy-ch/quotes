package com.example.quotes.onboarding.viewmodels

import com.adapty.api.entity.paywalls.ProductModel
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized

data class PremiumState(
    val products: Async<List<ProductModel>> = Uninitialized,
    val selectedProduct: Async<ProductModel> = Uninitialized
) : MavericksState

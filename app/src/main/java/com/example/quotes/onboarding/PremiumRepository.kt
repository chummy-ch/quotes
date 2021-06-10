package com.example.quotes.onboarding

import android.util.Log
import com.adapty.Adapty
import com.adapty.api.entity.paywalls.ProductModel
import com.example.quotes.Result
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class PremiumRepository {

    private var products: List<ProductModel> = listOf()
    private var selectedProduct: ProductModel? = null

    suspend fun getProducts(): List<ProductModel> {
        if (products.isNullOrEmpty()) {
            when (val res = loadProductsAndGetResult()) {
                is Result.Success -> products = res.data
                is Result.Error -> Log.e(null, res.exception.toString())
            }
        }
        setProductById(OnboardingPremiumFragment.ANNUAL_SUB_ID)
        return products.toList()
    }

    fun getSelectedProduct() = selectedProduct

    suspend fun setProductById(id: String): ProductModel? {
        if (products.isNullOrEmpty()) {
            when (val res = loadProductsAndGetResult()) {
                is Result.Success -> products = res.data
                is Result.Error -> Log.e(null, res.exception.toString())
            }
        }
        val product = products.firstOrNull { it.vendorProductId == id }
        selectedProduct = product
        return product
    }

    private suspend fun loadProductsAndGetResult() = suspendCancellableCoroutine<Result<List<ProductModel>>> { con ->
        Adapty.getPaywalls(true) { paywalls, productList, error ->
            if (con.isCompleted) return@getPaywalls
            if (error == null) con.resume(Result.Success(productList))
            else con.resume(Result.Error(Exception(error.toString())))
        }
    }
}

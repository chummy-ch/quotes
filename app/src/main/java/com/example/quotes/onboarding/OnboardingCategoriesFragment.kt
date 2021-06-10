package com.example.quotes.onboarding

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.airbnb.mvrx.fragmentViewModel
import com.example.quotes.FBAnalytics
import com.example.quotes.R
import com.example.quotes.databinding.FragmentOnboardingBinding
import com.example.quotes.epoxy.MvRxListBaseFragment
import com.example.quotes.epoxy.simpleController
import com.example.quotes.epoxy.viewholders.onboardingviewholders.CategoryViewHolder
import com.example.quotes.epoxy.viewholders.onboardingviewholders.categoryViewHolder
import com.example.quotes.onboarding.viewmodels.OnboardingCategoriesViewModel

class OnboardingCategoriesFragment : MvRxListBaseFragment(R.layout.fragment_onboarding) {

    private val viewModel: OnboardingCategoriesViewModel by fragmentViewModel()
    private val binding: FragmentOnboardingBinding by viewBinding()
    private val categoryListener = object : CategoryViewHolder.CategoryListener {
        override fun selectCategory(id: Long) {
            viewModel.selectCategory(id)
            FBAnalytics.getSetContentAnalytics(id, getString(R.string.category_selected))
        }

        override fun unselectCategory(id: Long) {
            viewModel.unselectCategory(id)
            FBAnalytics.getSetContentAnalytics(id, getString(R.string.category_unselected))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.onboardingTitle.text = getString(R.string.onboarding_categories_title)
        recyclerView.layoutManager = StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        )
    }

    override fun epoxyController() = simpleController(viewModel) { state ->
        val categories = state.categories.invoke()
        categories?.forEach { category ->
            categoryViewHolder {
                id(category.id)
                category(category)
                categoryListener(categoryListener)
            }
        }
    }
}
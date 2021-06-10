package com.example.quotes.quote

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.quotes.R
import com.example.quotes.databinding.FragmentQuotesDetailsBinding

class QuoteDetailsFragment(private val quoteModel: QuoteModel) :
    Fragment(R.layout.fragment_quotes_details) {
    private val binding: FragmentQuotesDetailsBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateView()
        binding.favoriteButton.setOnClickListener {
            favoriteOnClick(it)
        }
    }

    private fun favoriteOnClick(button: View) {
        when (quoteModel.isFavorite) {
            true -> {
                //quoteModel.isFavorite = false
                button.setBackgroundResource(R.drawable.ic_heart_not)
            }
            false -> {
                //quoteModel.isFavorite = false
                button.setBackgroundResource(R.drawable.ic_heart_in)
            }
        }
    }

    private fun updateView() {
        binding.quoteText.text = quoteModel.text
    }
}

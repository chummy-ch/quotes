package com.example.quotes.dialogwindows

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.quotes.R
import com.example.quotes.databinding.ItemDialogWindowFloatingButtonsBinding

class AdDialogFragment(private val showAd: () -> Unit, private val getPremium: () -> Unit) :
    DialogFragment(R.layout.item_dialog_window_floating_buttons) {

    private val binding: ItemDialogWindowFloatingButtonsBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireDialog().apply {
            setCancelable(true)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            with(binding) {
                dialogTextView.text = getString(R.string.theme_dialog_message)
                firstButton.apply {
                    setOnClickListener {
                        getPremium()
                        dismiss()
                    }
                    setImageResource(R.drawable.ic_status_premium)
                }
                secondButton.apply {
                    setOnClickListener {
                        showAd()
                        dismiss()
                    }
                    setImageResource(R.drawable.ic_ad)
                }
            }
        }
    }
}
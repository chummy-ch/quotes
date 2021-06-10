package com.example.quotes.views

import android.content.Context
import android.graphics.Color
import com.example.quotes.R

class MyButton(context: Context) : androidx.appcompat.widget.AppCompatButton(context) {

    fun bindSelectionStatus(isSelected: Boolean) {
        setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_tick_white, 0, 0, 0)
        compoundDrawablePadding = 24
        if (isSelected) {
            setBackgroundResource(R.drawable.button_black)
            setTextColor(Color.WHITE)
        } else {
            setBackgroundResource(R.drawable.button_white)
            setTextColor(Color.BLACK)
        }
    }
}

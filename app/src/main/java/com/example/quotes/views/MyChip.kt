package com.example.quotes.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import com.google.android.material.chip.Chip

class MyChip(context: Context) : Chip(context) {

    private val strokeWidth = 2f

    fun bindSelectionStatus(isSelected: Boolean) {
        chipStrokeWidth = strokeWidth
        if (isSelected) {
            chipBackgroundColor = ColorStateList.valueOf(Color.BLACK)
            setTextColor(Color.WHITE)
        } else {
            chipBackgroundColor = ColorStateList.valueOf(Color.WHITE)
            setTextColor(Color.BLACK)
            chipStrokeColor = ColorStateList.valueOf(Color.BLACK)
        }
    }
}

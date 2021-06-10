package com.example.quotes.epoxy.viewholders.settingsviewholders

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.example.quotes.views.MyChip
import com.example.quotes.R
import com.example.quotes.databinding.ItemChipGroupBinding
import com.example.quotes.epoxy.ViewBindingEpoxyModelWithHolder
import com.google.android.material.chip.Chip

@EpoxyModelClass(layout = R.layout.item_chip_group)
abstract class TimeNotificationChipsViewHolder : ViewBindingEpoxyModelWithHolder<ItemChipGroupBinding>() {

    @EpoxyAttribute
    lateinit var timeList: List<Long>

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var changeAlarm: View.OnClickListener

    override fun ItemChipGroupBinding.bind() {
        root.removeAllViews()
        val context = root.context
        val textChip = Chip(context).apply {
            text = context.getString(R.string.notify_time)
            textSize = 18f
            val states = arrayOf(
                intArrayOf(-android.R.attr.state_enabled), // disabled
                intArrayOf(-android.R.attr.state_enabled), // disabled
                intArrayOf(-android.R.attr.state_checked), // unchecked
                intArrayOf(-android.R.attr.state_pressed)  // unpressed
            )
            val colors = intArrayOf(Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE)
            isClickable = false
            chipBackgroundColor = ColorStateList(states, colors)
        }
        categoryChipGroup.addView(textChip)
        timeList.forEach { time ->
            val chip = MyChip(context).apply {
                text = getTimeString(time)
                textSize = 18f
                bindSelectionStatus(false)
                setOnClickListener(changeAlarm)
            }
            categoryChipGroup.addView(chip)
        }
    }

    private fun getTimeString(m: Long): String {
        var minutes = m
        val hours = minutes / 60
        minutes -= hours * 60
        var minutesString = minutes.toString()
        var hoursString = hours.toString()
        if (hoursString.length == 1) hoursString = "0$hoursString"
        if (minutesString.length == 1) minutesString = "0$minutesString"
        return "$hoursString:$minutesString"
    }
}
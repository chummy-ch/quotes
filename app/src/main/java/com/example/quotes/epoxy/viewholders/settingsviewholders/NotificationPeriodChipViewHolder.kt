package com.example.quotes.epoxy.viewholders.settingsviewholders

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.example.quotes.views.MyChip
import com.example.quotes.R
import com.example.quotes.databinding.ItemChipGroupBinding
import com.example.quotes.epoxy.ViewBindingEpoxyModelWithHolder
import com.example.quotes.notification.Notification

@EpoxyModelClass(layout = R.layout.item_chip_group)
abstract class NotificationPeriodChipViewHolder : ViewBindingEpoxyModelWithHolder<ItemChipGroupBinding>() {

    @EpoxyAttribute
    lateinit var notifications: List<Notification>

    @EpoxyAttribute
    var selectedNotificationId: Int = 0

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var periodListener: PeriodListener

    override fun ItemChipGroupBinding.bind() {
        root.removeAllViews()
        for (notify in notifications) {
            val chip = MyChip(root.context).apply {
                text = notify.name
                textSize = 18f
                val isSelected = selectedNotificationId == notify.id
                bindSelectionStatus(isSelected)
                if (!isSelected) {
                    setOnClickListener { periodListener.changeNotificationPeriod(notify.id) }
                }
            }
            categoryChipGroup.addView(chip)
        }
    }

    interface PeriodListener {
        fun changeNotificationPeriod(id: Int)
    }
}

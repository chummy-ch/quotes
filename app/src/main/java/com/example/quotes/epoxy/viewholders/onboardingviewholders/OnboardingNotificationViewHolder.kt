package com.example.quotes.epoxy.viewholders.onboardingviewholders

import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.example.quotes.R
import com.example.quotes.databinding.ItemEmptyLinearLayoutBinding
import com.example.quotes.epoxy.ViewBindingEpoxyModelWithHolder
import com.example.quotes.epoxy.viewholders.settingsviewholders.NotificationPeriodChipViewHolder
import com.example.quotes.notification.Notification
import com.example.quotes.views.MyButton
import kotlin.math.roundToInt

@EpoxyModelClass(layout = R.layout.item_empty_linear_layout)
abstract class OnboardingNotificationViewHolder : ViewBindingEpoxyModelWithHolder<ItemEmptyLinearLayoutBinding>() {

    @EpoxyAttribute
    lateinit var notifications: List<Notification>

    @EpoxyAttribute
    var selectedNotificationId: Int = 0

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var periodListener: NotificationPeriodChipViewHolder.PeriodListener

    override fun ItemEmptyLinearLayoutBinding.bind() {
        linearLayout.removeAllViews()
        notifications.forEach { notification ->
            val button = MyButton(root.context).apply {
                text = notification.name
                textSize = 18f
                isAllCaps = false
                val weightPX = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 65f, resources.displayMetrics
                ).roundToInt()
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    weightPX
                ).apply {
                    setMargins(0, 0, 0, 14)
                    setPadding(36, 0, 0, 0)
                }
                layoutParams = params
                gravity = Gravity.START or Gravity.CENTER

                val isSelected = selectedNotificationId == notification.id
                bindSelectionStatus(isSelected)
                if (!isSelected) {
                    setOnClickListener { periodListener.changeNotificationPeriod(notification.id) }
                }
            }
            linearLayout.addView(button)
        }
    }
}

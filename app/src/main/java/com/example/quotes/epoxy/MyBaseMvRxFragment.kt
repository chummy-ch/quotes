package com.example.quotes.epoxy

import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.airbnb.mvrx.MavericksView
import com.example.quotes.R

abstract class MyBaseMvRxFragment(layout: Int) : Fragment(layout), MavericksView {
    protected val coordinatorLayout: CoordinatorLayout by lazy {
        requireView().findViewById(R.id.coordinator_layout)
    }
}

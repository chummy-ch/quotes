package com.example.quotes.epoxy

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.example.quotes.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

abstract class MvRxListBaseFragment(layout: Int) : MyBaseMvRxFragment(layout) {
    protected val recyclerView: EpoxyRecyclerView
        get() {
            return requireView().findViewById(R.id.recycler_view)
        }

    private val epoxyController by lazy { epoxyController() }

    protected open fun layoutManager(): RecyclerView.LayoutManager =
        LinearLayoutManager(requireContext())

    abstract fun epoxyController(): EpoxyController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        epoxyController.onRestoreInstanceState(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.setController(epoxyController)
        recyclerView.layoutManager = layoutManager()
    }

    override fun onResume() {
        super.onResume()
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, this::class.java.simpleName)
        }
    }

    override fun invalidate() {
        recyclerView.requestModelBuild()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        epoxyController.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        epoxyController.cancelPendingModelBuild()
        super.onDestroyView()
    }
}

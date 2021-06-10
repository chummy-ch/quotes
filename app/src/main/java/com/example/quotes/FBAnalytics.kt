package com.example.quotes

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

class FBAnalytics {

    companion object {
        fun getSetContentAnalytics(id: Long, type: String) {
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
                param(FirebaseAnalytics.Param.ITEM_ID, id)
                param(FirebaseAnalytics.Param.CONTENT_TYPE, type)
            }
        }
    }
}

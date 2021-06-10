package com.example.quotes.notification

data class NotificationPeriodModel(val notification: Notification)

sealed class Notification(open val id: Int, open val name: String)

data class Daily(override val id: Int = 0, override val name: String = "Daily") : Notification(id, name)
data class Never(override val id: Int = 1, override val name: String = "Never") : Notification(id, name)


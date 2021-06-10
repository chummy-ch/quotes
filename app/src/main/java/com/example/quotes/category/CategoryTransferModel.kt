package com.example.quotes.category

data class CategoryTransferModel(
    var id: Long = 0,
    var name: String = "",
    var icon: Int = 0,
    var status: String = "",
    var groupId: Long = 0
)
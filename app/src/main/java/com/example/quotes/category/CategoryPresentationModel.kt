package com.example.quotes.category

data class CategoryPresentationModel(
    val id: Long,
    val name: String,
    val icon: Int,
    val status: Status,
    val groupId: Long
)

sealed class Status

data class Unlocked(var isSelected: Boolean) : Status()

object Locked : Status()
object Premium : Status()

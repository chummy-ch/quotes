package com.example.quotes.category

import kotlin.random.Random

data class Event<T>(val data: T, val timeStemp: Long = Random.nextLong())

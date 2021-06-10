package com.example.quotes

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


suspend inline fun <T> onIO(crossinline block: suspend CoroutineScope.() -> T): T {
    return withContext(Dispatchers.IO) { block.invoke(this@withContext) }
}

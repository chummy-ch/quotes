package com.example.quotes.quote

import android.net.Uri
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized

data class QuoteState(
    val quotes: Async<List<QuoteModel>> = Uninitialized,
    val selectedQuote: Async<QuoteModel> = Uninitialized,
    val theme: Async<Uri> = Uninitialized,
    val font: Async<Int> = Uninitialized,
    val error: Async<QuoteViewModel.ErrorEvent> = Uninitialized
) : MavericksState

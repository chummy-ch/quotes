package com.example.quotes.theme

import android.net.Uri
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import com.google.firebase.storage.StorageReference

data class ThemeState(
    val themes: Async<List<StorageReference>> = Uninitialized,
    val currentTheme: Async<Uri> = Uninitialized
) : MavericksState

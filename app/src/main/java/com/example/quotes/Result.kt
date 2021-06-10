package com.example.quotes

sealed class Result<out TData : Any> {
    data class Success<out TData : Any>(val data: TData) : Result<TData>()
    data class Error(val exception: Exception) : Result<Nothing>()
}
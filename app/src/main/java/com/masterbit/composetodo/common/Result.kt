package com.masterbit.composetodo.common

sealed class Result<out T> {
    data class Success<R>(val data: R): Result<R>()
    data class Error(val error: Int): Result<Nothing>()
}
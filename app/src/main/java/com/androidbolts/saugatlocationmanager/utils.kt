package com.androidbolts.saugatlocationmanager

inline fun <R> R?.orElse(block: () -> R): R {
    return this ?: block()
}
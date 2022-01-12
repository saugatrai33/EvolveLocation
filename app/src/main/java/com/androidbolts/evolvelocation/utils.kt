package com.androidbolts.evolvelocation

inline fun <R> R?.orElse(block: () -> R): R {
    return this ?: block()
}
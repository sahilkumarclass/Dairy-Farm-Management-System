package com.sahilkumar.dfms.core.network

/** Mirrors Spring Data's Page<T> JSON shape (only the fields we use). */
data class Page<T>(
    val content: List<T> = emptyList(),
    val number: Int = 0,
    val size: Int = 0,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val first: Boolean = true,
    val last: Boolean = true,
)

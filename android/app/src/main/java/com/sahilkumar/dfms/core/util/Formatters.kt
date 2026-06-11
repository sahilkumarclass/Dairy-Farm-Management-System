package com.sahilkumar.dfms.core.util

import java.math.BigDecimal
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val rupeeFormat: NumberFormat = NumberFormat.getNumberInstance(Locale("en", "IN")).apply {
    minimumFractionDigits = 2
    maximumFractionDigits = 2
}

fun formatMoney(value: BigDecimal?): String =
    "₹" + rupeeFormat.format(value ?: BigDecimal.ZERO)

fun formatLiters(value: BigDecimal?): String =
    (value ?: BigDecimal.ZERO).stripTrailingZeros().toPlainString() + " L"

private val isoDate: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
private val prettyDate: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())

/** "2024-08-01" -> "01 Aug 2024"; falls back to the raw string on parse failure. */
fun formatDate(iso: String?): String {
    if (iso.isNullOrBlank()) return "—"
    return runCatching { LocalDate.parse(iso.take(10), isoDate).format(prettyDate) }.getOrDefault(iso)
}

fun todayIso(): String = LocalDate.now().format(isoDate)

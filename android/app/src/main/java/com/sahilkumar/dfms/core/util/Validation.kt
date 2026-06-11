package com.sahilkumar.dfms.core.util

import java.math.BigDecimal

private val TEN_DIGITS = Regex("^\\d{10}$")

fun isValidPhone(phone: String): Boolean = TEN_DIGITS.matches(phone)

fun digitsOnly(input: String, max: Int = 10): String = input.filter { it.isDigit() }.take(max)

fun parseMoneyOrNull(input: String): BigDecimal? =
    input.trim().takeIf { it.isNotBlank() }?.let { runCatching { BigDecimal(it) }.getOrNull() }

package com.sahilkumar.dfms.core.ui

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/** Per-app language switching backed by AndroidX per-app locales (persisted by the system). */
object LocaleManager {
    val supported = listOf("en", "hi", "pa")

    fun current(): String = AppCompatDelegate.getApplicationLocales()
        .takeUnless { it.isEmpty }
        ?.get(0)?.language ?: "en"

    fun set(tag: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
    }
}

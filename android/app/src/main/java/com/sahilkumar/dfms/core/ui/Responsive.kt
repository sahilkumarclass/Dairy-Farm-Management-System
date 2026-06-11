package com.sahilkumar.dfms.core.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Phone-first responsive helpers. Width buckets are derived from the current
 * configuration's [LocalConfiguration.screenWidthDp] so layouts can scale with
 * the device's display size and orientation without any extra dependency.
 */
enum class WidthClass { COMPACT, MEDIUM, EXPANDED }

@Composable
@ReadOnlyComposable
fun widthClass(): WidthClass {
    val widthDp = LocalConfiguration.current.screenWidthDp
    return when {
        widthDp < 360 -> WidthClass.COMPACT
        widthDp < 600 -> WidthClass.MEDIUM
        else -> WidthClass.EXPANDED
    }
}

/** Outer screen padding that scales with available width. */
@Composable
@ReadOnlyComposable
fun screenPadding(): Dp = when (widthClass()) {
    WidthClass.COMPACT -> 12.dp
    WidthClass.MEDIUM -> 16.dp
    WidthClass.EXPANDED -> 24.dp
}

/** Convenience [PaddingValues] wrapper around [screenPadding] for lazy lists/grids. */
@Composable
@ReadOnlyComposable
fun screenContentPadding(): PaddingValues = PaddingValues(screenPadding())

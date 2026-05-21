package com.cyna.app.ui.core.components

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import java.util.Locale

/**
 * shadcn/ui-style DirectionProvider.
 *
 * Wraps a subtree with the given [direction], overriding Compose's
 * [LocalLayoutDirection]. All standard layouts (Row, TextField padding,
 * icon positions, etc.) automatically mirror when [LayoutDirection.Rtl] is active.
 *
 * Usage — app-level:
 * ```
 * AppTheme {
 *     DirectionProvider(direction = DirectionManager.direction) {
 *         NavHost(rememberNavController())
 *     }
 * }
 * ```
 *
 * Usage — screen/preview level:
 * ```
 * DirectionProvider(direction = LayoutDirection.Rtl) {
 *     LoginScreen(navController)
 * }
 * ```
 */
@Composable
fun DirectionProvider(
    direction: LayoutDirection = LayoutDirection.Ltr,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalLayoutDirection provides direction,
        content = content
    )
}

/**
 * Returns the current [LayoutDirection] — mirrors shadcn/ui's `useDirection()` hook.
 *
 * ```
 * val direction = useDirection()
 * val isRtl = direction == LayoutDirection.Rtl
 * ```
 */
@Composable
fun useDirection(): LayoutDirection = LocalLayoutDirection.current

/**
 * Global singleton for runtime LTR ↔ RTL toggling — mirrors shadcn/ui's DirectionManager.
 *
 * Setup at root:
 * ```
 * AppTheme {
 *     DirectionProvider(direction = DirectionManager.direction) {
 *         NavHost(rememberNavController())
 *     }
 * }
 * ```
 *
 * Toggle from anywhere:
 * ```
 * Button(onClick = { DirectionManager.toggle() }) { Text("Toggle RTL") }
 * ```
 */
object DirectionManager {
    var direction by mutableStateOf<LayoutDirection>(LayoutDirection.Ltr)
        private set

    /** Switch between LTR and RTL. */
    fun toggle() {
        direction = if (direction == LayoutDirection.Ltr) LayoutDirection.Rtl else LayoutDirection.Ltr
    }

    /** Explicitly set a direction. */
    fun set(dir: LayoutDirection) { direction = dir }

    /** Auto-detect from locale. */
    fun setFromLocale(locale: Locale = Locale.getDefault()) {
        direction = if (isRtlLocale(locale)) LayoutDirection.Rtl else LayoutDirection.Ltr
    }

    val isRtl: Boolean get() = direction == LayoutDirection.Rtl
    val isLtr: Boolean get() = direction == LayoutDirection.Ltr
}

/**
 * Returns `true` if [locale] uses a right-to-left script.
 * Covers Arabic (ar), Hebrew (he/iw), Persian (fa), Urdu (ur),
 * Pashto (ps), Sindhi (sd), Kurdish (ku), Yiddish (yi), Dhivehi (dv).
 */
fun isRtlLocale(locale: Locale = Locale.getDefault()): Boolean {
    val rtlLanguages = setOf("ar", "he", "iw", "fa", "ur", "ps", "sd", "ku", "yi", "dv")
    return locale.language.lowercase() in rtlLanguages
}
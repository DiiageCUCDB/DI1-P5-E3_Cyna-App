package com.cyna.app.ui.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// ============================================================
// Thème Cyna — mapping 1:1 avec Cyna-Web index.css
// ============================================================

private val LightColorScheme = lightColorScheme(
    background           = LightBackground,       // #F9FAFC
    onBackground         = LightForeground,        // #05050B

    surface              = LightCard,              // #FFFFFF
    onSurface            = LightForeground,
    surfaceVariant       = LightMuted,             // #E9EBF1
    onSurfaceVariant     = LightMutedFg,           // #60636C

    primary              = LightPrimary,           // #562BF5
    onPrimary            = LightPrimaryFg,         // #F8F8FC
    primaryContainer     = LightAccent,            // #E4E6F9
    onPrimaryContainer   = LightAccentFg,          // #262067

    secondary            = LightSecondary,         // #E9EBF2
    onSecondary          = LightSecondaryFg,       // #13151F
    secondaryContainer   = LightMuted,
    onSecondaryContainer = LightSecondaryFg,

    tertiary             = LightAccent,            // #E4E6F9
    onTertiary           = LightAccentFg,          // #262067
    tertiaryContainer    = LightAccent,
    onTertiaryContainer  = LightPrimary,

    outline              = LightBorder,            // #D5D7E0
    outlineVariant       = LightBorder,

    error                = LightDestructive,       // #DF202E
    onError              = White,
    errorContainer       = LightAccent,
    onErrorContainer     = LightDestructive,

    surfaceTint          = LightPrimary,
)

val DarkColorScheme = darkColorScheme(
    background           = DarkBackground,         // #020205
    onBackground         = DarkForeground,         // #F1F1F6

    surface              = DarkCard,               // #08080F
    onSurface            = DarkForeground,
    surfaceVariant       = DarkMuted,              // #101119
    onSurfaceVariant     = DarkMutedFg,            // #787A83

    primary              = DarkPrimary,            // #6D55FF
    onPrimary            = DarkPrimaryFg,          // #F8F8FC
    primaryContainer     = DarkAccent,             // #17182D
    onPrimaryContainer   = DarkAccentFg,           // #CBCDD8

    secondary            = DarkSecondary,          // #101119
    onSecondary          = DarkSecondaryFg,        // #DDDDE3
    secondaryContainer   = DarkMuted,
    onSecondaryContainer = DarkSecondaryFg,

    tertiary             = DarkAccent,             // #17182D
    onTertiary           = DarkAccentFg,           // #CBCDD8
    tertiaryContainer    = DarkAccent,
    onTertiaryContainer  = DarkForeground,

    outline              = DarkBorder,             // #1AFFFFFF (blanc 10%)
    outlineVariant       = DarkInput,              // #24FFFFFF (blanc 14%)

    error                = DarkDestructive,        // #F94144
    onError              = DarkBackground,
    errorContainer       = DarkAccent,
    onErrorContainer     = DarkDestructive,

    surfaceTint          = DarkPrimary,
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val useDarkTheme = ThemeManager.isDarkTheme()

    val colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = CynaTypography,
        content     = content
    )
}
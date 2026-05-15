package com.cyna.app.ui.core.theme

import androidx.compose.ui.graphics.Color

// ============================================================
// Palette Cyna — conversion exacte depuis Cyna-Web index.css
// Valeurs oklch converties en sRGB hex via culori.
// ============================================================

val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)

// ============================================================
// MODE CLAIR
// ============================================================

val LightBackground   = Color(0xFFF9FAFC)   // oklch(0.985 0.003 275)
val LightForeground   = Color(0xFF05050B)   // oklch(0.12  0.015 275)
val LightCard         = Color(0xFFFFFFFF)   // oklch(1 0 0)

val LightPrimary      = Color(0xFF562BF5)   // oklch(0.50 0.27 280) — violet Cyna
val LightPrimaryFg    = Color(0xFFF8F8FC)   // oklch(0.98 0.005 280)

val LightSecondary    = Color(0xFFE9EBF2)   // oklch(0.94 0.01 275)
val LightSecondaryFg  = Color(0xFF13151F)   // oklch(0.20 0.02 275)

val LightMuted        = Color(0xFFE9EBF1)   // oklch(0.94 0.008 275)
val LightMutedFg      = Color(0xFF60636C)   // oklch(0.50 0.015 275)

val LightAccent       = Color(0xFFE4E6F9)   // oklch(0.93 0.025 280)
val LightAccentFg     = Color(0xFF262067)   // oklch(0.30 0.12 280)

val LightBorder       = Color(0xFFD5D7E0)   // oklch(0.88 0.012 275)
val LightDestructive  = Color(0xFFDF202E)   // oklch(0.58 0.22 25)

val LightSidebar       = Color(0xFFF3F5FB)  // oklch(0.97 0.008 275)
val LightSidebarAccent = Color(0xFFE4E6F9)  // oklch(0.93 0.025 280)

// ============================================================
// MODE SOMBRE
// ============================================================

val DarkBackground    = Color(0xFF020205)   // oklch(0.09 0.012 280)
val DarkForeground    = Color(0xFFF1F1F6)   // oklch(0.96 0.006 280)
val DarkCard          = Color(0xFF08080F)   // oklch(0.14 0.015 280)

val DarkPrimary       = Color(0xFF6D55FF)   // oklch(0.60 0.27 280) — violet lumineux
val DarkPrimaryFg     = Color(0xFFF8F8FC)

val DarkSecondary     = Color(0xFF101119)   // oklch(0.18 0.018 280)
val DarkSecondaryFg   = Color(0xFFDDDDE3)   // oklch(0.90 0.008 280)

val DarkMuted         = Color(0xFF101119)   // oklch(0.18 0.018 280)
val DarkMutedFg       = Color(0xFF787A83)   // oklch(0.58 0.015 280)

val DarkAccent        = Color(0xFF17182D)   // oklch(0.22 0.04 280)
val DarkAccentFg      = Color(0xFFCBCDD8)   // oklch(0.85 0.015 280)

val DarkBorder        = Color(0x1AFFFFFF)   // oklch(1 0 0 / 10%)
val DarkInput         = Color(0x24FFFFFF)   // oklch(1 0 0 / 14%)
val DarkDestructive   = Color(0xFFF94144)   // oklch(0.65 0.22 25)

val DarkSidebar       = Color(0xFF08080F)   // oklch(0.14 0.015 280)
val DarkSidebarAccent = Color(0xFF17182D)   // oklch(0.22 0.04 280)

// ============================================================
// GRAPHIQUES
// ============================================================

val Chart1Light = Color(0xFF562BF5)   // oklch(0.50 0.27 280) — violet primaire
val Chart2Light = Color(0xFF9B61EA)   // oklch(0.62 0.20 300) — violet-rose
val Chart3Light = Color(0xFF026FD7)   // oklch(0.55 0.18 255) — bleu-violet
val Chart4Light = Color(0xFF739BF5)   // oklch(0.70 0.14 265) — lavande
val Chart5Light = Color(0xFF3A0EB1)   // oklch(0.38 0.22 280) — violet foncé

val Chart1Dark  = Color(0xFF7A6BFF)   // oklch(0.65 0.26 280)
val Chart2Dark  = Color(0xFFB786FF)   // oklch(0.72 0.18 300)
val Chart3Dark  = Color(0xFF0C84FA)   // oklch(0.62 0.20 255)
val Chart4Dark  = Color(0xFF92B6FF)   // oklch(0.78 0.12 265)
val Chart5Dark  = Color(0xFF5033DE)   // oklch(0.48 0.24 280)
package com.defince.coreuitheme.compose

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.defince.coreuitheme.compose.ext.toColorScheme

internal val LocalSpecificColorScheme = staticCompositionLocalOf { LightSpecificColorScheme }
internal val DarkColorScheme by lazy { DarkSpecificColorScheme.toColorScheme() }
internal val LightColorScheme by lazy { LightSpecificColorScheme.toColorScheme() }

internal val LightSpecificColorScheme = SpecificColorScheme(
    primary = Color(0xFF507844),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF9ABA90),
    onPrimaryContainer = Color(0xFF253022),
    primaryInverse = Color(0xFF8FD17D),

    secondary = Color(0xFFAE4F2F),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFFFFF),
    onSecondaryContainer = Color(0xFF375DFD),

    tertiary = Color(0xFFDBD655),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF4F3D1),
    onTertiaryContainer = Color(0xFF312911),

    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),

    outline = Color(0xFFCECCCC),
    outlineVariant = Color(0xFFCECCCC),
    shimmer = Color(0xFFF4F4F4),
    scrim = Color(0xB3FFFFFF),

    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFFB1B3C0),
    surfaceInverse = Color(0xFF303331),
    onSurfaceInverse = Color(0xFFEFF4F1),

    symbolPrimary = Color(0xFF1C1B1F),
    symbolPrimaryInverse = Color(0xFFFFFFFF),
    symbolSecondary = Color(0x9A1C1B1F),
    symbolSecondaryInverse = Color(0x9AFFFFFF),
    symbolTertiary = Color(0x651C1B1F),
    symbolTertiaryInverse = Color(0x65FFFFFF),

    text = Color(0xFFB1B3C0),
)

internal val DarkSpecificColorScheme = LightSpecificColorScheme

data class SpecificColorScheme(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val primaryInverse: Color,

    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,

    val tertiary: Color,
    val onTertiary: Color,
    val tertiaryContainer: Color,
    val onTertiaryContainer: Color,

    val error: Color,
    val onError: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,

    val outline: Color,
    val outlineVariant: Color,
    val shimmer: Color,
    val scrim: Color,

    val background: Color,
    val onBackground: Color,

    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val surfaceInverse: Color,
    val onSurfaceInverse: Color,
    val surfaceTint: Color = primary,

    val symbolPrimary: Color,
    val symbolPrimaryInverse: Color,
    val symbolSecondary: Color,
    val symbolSecondaryInverse: Color,
    val symbolTertiary: Color,
    val symbolTertiaryInverse: Color,

    val text: Color,

    val white_10: Color = Color(0x33FFFFFF),
    val white_20: Color = Color(0x33FFFFFF),
    val white_30: Color = Color(0x33FFFFFF),
    val white_40: Color = Color(0x33FFFFFF),
    val white_50: Color = Color(0x80FFFFFF),
    val white_60: Color = Color(0x99ffffff),
    val white_70: Color = Color(0x99ffffff),
    val white_80: Color = Color(0xCCFFFFFF),
    val white_90: Color = Color(0xE6FFFFFF),
    val white: Color = Color(0xFFFFFFFF),

    val black_10: Color = Color(0x1A000000),
    val black_20: Color = Color(0x1A000000),
    val black_30: Color = Color(0x4D000000),
    val black_40: Color = Color(0x4D000000),
    val black_50: Color = Color(0x80000000),
    val black_60: Color = Color(0x80000000),
    val black_70: Color = Color(0x80000000),
    val black_80: Color = Color(0xCC000000),
    val black_90: Color = Color(0xCC000000),
    val black: Color = Color(0xFF000000),

    val transparent: Color = Color(0x00000000),
)
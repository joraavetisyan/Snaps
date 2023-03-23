package io.snaps.coreuitheme.compose

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import io.snaps.coreuitheme.compose.ext.toColorScheme

internal val LocalSpecificColorScheme = staticCompositionLocalOf { LightSpecificColorScheme }
internal val DarkColorScheme by lazy { DarkSpecificColorScheme.toColorScheme() }
internal val LightColorScheme by lazy { LightSpecificColorScheme.toColorScheme() }

internal val LightSpecificColorScheme = SpecificColorScheme(
    uiAccent = Color(0xFF4669FF),
    uiContentBg = Color(0xFFFAFAFA),
    uiSystemRed = Color(0xFFE95252),
    uiSystemGreen = Color(0xFFBFFBC5),
    uiSystemYellow = Color(0xFFEEBE14),

    uiDisabledLabel = Color(0xFFB1B3C0),

    textPrimary = Color(0xFF080920),
    textSecondary = Color(0xFFB1B3C0),
    textLink = Color(0xFF4669FF),
    textGreen = Color(0xFF28D039),

    actionLabel = Color(0xFFFFFFFF),
    actionBase = Color(0xFF4669FF),
    actionDisabled = Color(0x802336E8),

    defaultLabel = Color(0xFF080920),
    defaultBase = Color(0xFFF1F4F8),
    defaultDisabled = Color(0xFFF1F2F8),

    outlineLabel = Color(0xFF0B1A12),
    outlineBorderBase = Color(0xFFCED1D0),
    outlineDisabled = Color(0xFFF1F2F8),

    lightLabel = Color(0xFF4669FF),
    lightBase = Color(0xFFEAF4FF),
    lightDisabled = Color(0xFFF1F2F8),

    lightGrey = Color(0xFFF1F2F8),
    grey = Color(0xFFFAFAFA),
    darkGrey = Color(0xFFB1B3C0),
)

internal val DarkSpecificColorScheme = LightSpecificColorScheme

data class SpecificColorScheme(
    val uiAccent: Color,
    val uiSystemRed: Color,
    val uiSystemGreen: Color,
    val uiContentBg: Color,
    val uiDisabledLabel: Color,
    val uiSystemYellow: Color,

    val textPrimary: Color,
    val textSecondary: Color,
    val textLink: Color,
    val textGreen: Color,

    val actionLabel: Color,
    val actionBase: Color,
    val actionDisabled: Color,

    val defaultLabel: Color,
    val defaultBase: Color,
    val defaultDisabled: Color,

    val outlineLabel: Color,
    val outlineBorderBase: Color,
    val outlineDisabled: Color,

    val lightLabel: Color,
    val lightBase: Color,
    val lightDisabled: Color,

    val lightGrey: Color,
    val grey: Color,
    val darkGrey: Color,

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
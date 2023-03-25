package io.snaps.coreuitheme.compose.ext

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import io.snaps.coreuitheme.compose.DarkSpecificColorScheme
import io.snaps.coreuitheme.compose.LightSpecificColorScheme
import io.snaps.coreuitheme.compose.SpecificColorScheme
import io.snaps.coreuitheme.compose.SpecificTypography

fun SpecificColorScheme.toColorScheme() = ColorScheme(
    primary = actionBase,
    onPrimary = actionLabel,
    primaryContainer = actionBase,
    onPrimaryContainer = actionLabel,
    inversePrimary = actionBase,

    secondary = lightBase,
    onSecondary = lightLabel,
    secondaryContainer = lightBase,
    onSecondaryContainer = lightLabel,

    tertiary = lightBase,
    onTertiary = lightLabel,
    tertiaryContainer = lightBase,
    onTertiaryContainer = lightLabel,

    background = uiContentBg,
    onBackground = textPrimary,
    surface = uiContentBg,
    onSurface = textPrimary,
    surfaceVariant = uiContentBg,
    onSurfaceVariant = textPrimary,
    inverseSurface = uiContentBg,
    inverseOnSurface = textPrimary,
    surfaceTint = actionBase,

    error = uiSystemRed,
    onError = lightGrey,
    errorContainer = uiSystemRed,
    onErrorContainer = lightGrey,

    outline = outlineBorderBase,
    outlineVariant = outlineBorderBase,

    scrim = uiContentBg.copy(alpha = .7f),
)

fun ColorScheme.toSpecificColorScheme(isLight: Boolean) = SpecificColorScheme(
    uiAccent = primary,
    uiContentBg = background,
    uiSystemRed = errorContainer,
    uiSystemGreen = if (isLight) LightSpecificColorScheme.uiSystemGreen
    else DarkSpecificColorScheme.uiSystemGreen,
    uiDisabledLabel = if (isLight) LightSpecificColorScheme.uiDisabledLabel
    else DarkSpecificColorScheme.uiDisabledLabel,
    uiSystemYellow = if (isLight) LightSpecificColorScheme.uiSystemYellow
    else DarkSpecificColorScheme.uiSystemYellow,

    textPrimary = onSurface,
    textSecondary = if (isLight) LightSpecificColorScheme.textSecondary
    else DarkSpecificColorScheme.textSecondary,
    textLink = primary,
    textGreen = primary,

    actionLabel = onPrimary,
    actionBase = primary,
    actionDisabled = if (isLight) LightSpecificColorScheme.uiDisabledLabel
    else DarkSpecificColorScheme.uiDisabledLabel,

    defaultLabel = if (isLight) LightSpecificColorScheme.defaultLabel
    else DarkSpecificColorScheme.defaultLabel,
    defaultBase = if (isLight) LightSpecificColorScheme.defaultBase
    else DarkSpecificColorScheme.defaultBase,
    defaultDisabled = if (isLight) LightSpecificColorScheme.defaultDisabled
    else DarkSpecificColorScheme.defaultDisabled,

    outlineLabel = if (isLight) LightSpecificColorScheme.outlineLabel
    else DarkSpecificColorScheme.outlineLabel,
    outlineBorderBase = outline,
    outlineDisabled = if (isLight) LightSpecificColorScheme.outlineDisabled
    else DarkSpecificColorScheme.outlineDisabled,

    lightLabel = onSecondary,
    lightBase = secondary,
    lightDisabled = if (isLight) LightSpecificColorScheme.lightDisabled
    else DarkSpecificColorScheme.lightDisabled,

    lightGrey = onError,
    grey = if (isLight) LightSpecificColorScheme.grey
    else DarkSpecificColorScheme.grey,
    darkGrey = if (isLight) LightSpecificColorScheme.darkGrey
    else DarkSpecificColorScheme.darkGrey,

    pink = if (isLight) LightSpecificColorScheme.pink
    else DarkSpecificColorScheme.pink,
)

fun SpecificTypography.toTypography() = Typography(
    displayLarge = displayLarge,
    displayMedium = displayMedium,
    displaySmall = displaySmall,
    headlineLarge = headlineLarge,
    headlineMedium = headlineMedium,
    headlineSmall = headlineSmall,
    titleLarge = titleLarge,
    titleMedium = titleMedium,
    titleSmall = titleSmall,
    bodyLarge = bodyLarge,
    bodyMedium = bodyMedium,
    bodySmall = bodySmall,
    labelLarge = labelLarge,
    labelMedium = labelMedium,
    labelSmall = labelSmall,
)
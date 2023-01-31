package com.defince.coreuitheme.compose

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

internal val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(AppTheme.specificValues.radius_extra_small),
    small = RoundedCornerShape(AppTheme.specificValues.radius_small),
    medium = RoundedCornerShape(AppTheme.specificValues.radius_medium),
    large = RoundedCornerShape(AppTheme.specificValues.radius_large),
    extraLarge = RoundedCornerShape(AppTheme.specificValues.radius_extra_large),
)

val BottomNavigationBarShape = RoundedCornerShape(100.dp)

val MainHeaderElementShape = RoundedCornerShape(100.dp)
package com.vanced.manager.ui.component.color

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.vanced.manager.preferences.holder.managerAccentColorPref

@Composable
fun contentColorForColor(color: Color) =
    if (color.luminance() > 0.7)
        Color.Black
    else
        Color.White

@Composable
fun managerAccentColor(): Color {
    val accentColor by managerAccentColorPref
    return Color(accentColor)
}

@Composable
fun managerThemedCardColor() = managerAccentColor().copy(alpha = 0.27f)

@Composable
fun managerTextColor(): Color = managerAnimatedColor(color = MaterialTheme.colors.onSurface)

@Composable
fun managerSurfaceColor(): Color = managerAnimatedColor(color = MaterialTheme.colors.surface)

@Composable
fun managerAnimatedColor(
    color: Color
): Color = animateColorAsState(
    targetValue = color,
    animationSpec = tween(500)
).value
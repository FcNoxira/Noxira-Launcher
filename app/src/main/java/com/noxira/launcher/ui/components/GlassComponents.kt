// NOXIRA — Premium Roblox Launcher
// File: ui/components/GlassComponents.kt
// Reusable glassmorphism primitives — cards, buttons, badges, dividers

package com.noxira.launcher.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.noxira.launcher.ui.theme.*

// ── GlassCard ─────────────────────────────────────────────────────────────────

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    radius: Dp = NoxiraRadius.lg,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(radius)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val bgAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.18f else 0.10f,
        animationSpec = tween(120), label = "card_bg"
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = bgAlpha + 0.04f),
                        Color.White.copy(alpha = bgAlpha),
                    )
                )
            )
            .border(
                width = 0.5.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.25f),
                        Color.White.copy(alpha = 0.06f),
                    )
                ),
                shape = shape,
            )
            .then(
                if (onClick != null) Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple(color = NoxiraColors.Ripple),
                    onClick = onClick,
                ) else Modifier
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(NoxiraSpacing.md),
            content = content,
        )
    }
}

// ── NoxiraButton — primary silver action ──────────────────────────────────────

@Composable
fun NoxiraButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null,
) {
    val shape = RoundedCornerShape(NoxiraRadius.md)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val elevation by animateFloatAsState(
        targetValue = if (isPressed) 0f else 4f,
        animationSpec = tween(100), label = "btn_elev"
    )
    val bgColor by animateColorAsState(
        targetValue = when {
            !enabled -> NoxiraColors.BgElevated
            isPressed -> NoxiraColors.Silver.copy(alpha = 0.85f)
            else -> NoxiraColors.Silver
        },
        animationSpec = tween(120), label = "btn_bg"
    )

    Box(
        modifier = modifier
            .shadow(elevation = elevation.dp, shape = shape, ambientColor = NoxiraColors.Silver.copy(alpha = 0.15f))
            .clip(shape)
            .background(bgColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !loading,
                onClick = onClick,
            )
            .padding(horizontal = NoxiraSpacing.lg, vertical = 13.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = NoxiraColors.BgMain,
                strokeWidth = 2.dp,
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = NoxiraColors.BgMain,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                }
                Text(
                    text = text,
                    style = NoxiraType.ButtonText,
                    color = NoxiraColors.BgMain,
                )
            }
        }
    }
}

// ── NoxiraOutlineButton — ghost action ────────────────────────────────────────

@Composable
fun NoxiraOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    val shape = RoundedCornerShape(NoxiraRadius.md)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val borderAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.5f else 0.25f,
        animationSpec = tween(120), label = "outline_border"
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                if (isPressed) NoxiraColors.GlassSurface else Color.Transparent
            )
            .border(
                width = 0.5.dp,
                color = NoxiraColors.Silver.copy(alpha = borderAlpha),
                shape = shape,
            )
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(color = NoxiraColors.Ripple),
                enabled = enabled,
                onClick = onClick,
            )
            .padding(horizontal = NoxiraSpacing.md, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) NoxiraColors.Silver else NoxiraColors.TextMuted,
                    modifier = Modifier.size(15.dp),
                )
                Spacer(Modifier.width(5.dp))
            }
            Text(
                text = text,
                style = NoxiraType.ButtonText,
                color = if (enabled) NoxiraColors.Silver else NoxiraColors.TextMuted,
            )
        }
    }
}

// ── StatusBadge ───────────────────────────────────────────────────────────────

enum class BadgeStatus { SUCCESS, ERROR, WARNING, NEUTRAL }

@Composable
fun StatusBadge(
    text: String,
    status: BadgeStatus,
    modifier: Modifier = Modifier,
) {
    val (bg, fg) = when (status) {
        BadgeStatus.SUCCESS -> NoxiraColors.SuccessDim to NoxiraColors.Success
        BadgeStatus.ERROR   -> NoxiraColors.ErrorDim to NoxiraColors.Error
        BadgeStatus.WARNING -> NoxiraColors.WarningDim to NoxiraColors.Warning
        BadgeStatus.NEUTRAL -> NoxiraColors.GlassSurface to NoxiraColors.TextSecondary
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(NoxiraRadius.pill))
            .background(bg)
            .border(0.5.dp, fg.copy(alpha = 0.35f), RoundedCornerShape(NoxiraRadius.pill))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = text.uppercase(),
            style = NoxiraType.Label,
            color = fg,
        )
    }
}

// ── SectionHeader ─────────────────────────────────────────────────────────────

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title.uppercase(),
        style = NoxiraType.SectionHeading,
        modifier = modifier.padding(
            start = NoxiraSpacing.xs,
            bottom = NoxiraSpacing.sm,
        ),
    )
}

// ── GlassDivider ──────────────────────────────────────────────────────────────

@Composable
fun GlassDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(NoxiraColors.GlassBorder)
    )
}

// ── IconButton (glass) ────────────────────────────────────────────────────────

@Composable
fun GlassIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = NoxiraColors.TextSecondary,
) {
    val shape = CircleShape
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(shape)
            .background(if (isPressed) NoxiraColors.GlassSurface else Color.Transparent)
            .border(0.5.dp, NoxiraColors.GlassBorder, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = false, color = NoxiraColors.Ripple),
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(20.dp),
        )
    }
}

// ── QuickActionChip ───────────────────────────────────────────────────────────

@Composable
fun QuickActionChip(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val shape = RoundedCornerShape(NoxiraRadius.sm)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(shape)
            .background(
                if (isPressed) NoxiraColors.GlassSurface
                else NoxiraColors.GlassHighlight
            )
            .border(0.5.dp, NoxiraColors.GlassBorder, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(color = NoxiraColors.Ripple),
                enabled = enabled,
                onClick = onClick,
            )
            .padding(vertical = NoxiraSpacing.sm, horizontal = NoxiraSpacing.md),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (enabled) NoxiraColors.Silver else NoxiraColors.TextMuted,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = NoxiraType.Label,
            color = if (enabled) NoxiraColors.TextSecondary else NoxiraColors.TextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

// ── LogLine ───────────────────────────────────────────────────────────────────

@Composable
fun LogLine(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = NoxiraColors.TextSecondary,
) {
    Text(
        text = text,
        style = NoxiraType.StatusText,
        color = color,
        modifier = modifier,
    )
}

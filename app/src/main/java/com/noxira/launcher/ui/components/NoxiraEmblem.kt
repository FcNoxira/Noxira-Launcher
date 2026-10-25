// NOXIRA — Premium Roblox Launcher
// File: ui/components/NoxiraEmblem.kt
// Geometric emblem drawn with Compose Canvas — no external assets needed

package com.noxira.launcher.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noxira.launcher.ui.theme.*

// ── Emblem ────────────────────────────────────────────────────────────────────
// Geometric mark: outer hexagonal ring + inner X cross + diamond center dot.
// Drawn in pure Canvas — no PNG/SVG assets.

@Composable
fun NoxiraEmblem(
    size: Dp = 48.dp,
    animate: Boolean = false,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "emblem_spin")
    val rotation by if (animate) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(12000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "emblem_rot",
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    Canvas(modifier = Modifier.size(size)) {
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f
        val r = this.size.minDimension / 2f

        // Outer ring
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    NoxiraColors.Silver.copy(alpha = 0.8f),
                    NoxiraColors.SilverDim.copy(alpha = 0.3f),
                    NoxiraColors.Silver.copy(alpha = 0.8f),
                ),
                center = Offset(cx, cy),
            ),
            radius = r - 2.dp.toPx(),
            style = Stroke(width = 1.5.dp.toPx()),
        )

        rotate(degrees = rotation, pivot = Offset(cx, cy)) {
            // Hexagonal inner ring (6 tick marks)
            for (i in 0 until 6) {
                val angle = Math.toRadians((i * 60).toDouble())
                val x1 = cx + (r * 0.62f) * Math.cos(angle).toFloat()
                val y1 = cy + (r * 0.62f) * Math.sin(angle).toFloat()
                val x2 = cx + (r * 0.80f) * Math.cos(angle).toFloat()
                val y2 = cy + (r * 0.80f) * Math.sin(angle).toFloat()
                drawLine(
                    color = NoxiraColors.Silver.copy(alpha = 0.6f),
                    start = Offset(x1, y1),
                    end = Offset(x2, y2),
                    strokeWidth = 1.2.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
        }

        // X cross — static
        val arm = r * 0.38f
        val crossColor = NoxiraColors.Silver
        val sw = 2.dp.toPx()
        drawLine(crossColor, Offset(cx - arm, cy - arm), Offset(cx + arm, cy + arm), sw, StrokeCap.Round)
        drawLine(crossColor, Offset(cx + arm, cy - arm), Offset(cx - arm, cy + arm), sw, StrokeCap.Round)

        // Center diamond dot
        val dR = r * 0.12f
        val dPath = Path().apply {
            moveTo(cx, cy - dR)
            lineTo(cx + dR, cy)
            lineTo(cx, cy + dR)
            lineTo(cx - dR, cy)
            close()
        }
        drawPath(
            path = dPath,
            brush = Brush.radialGradient(
                colors = listOf(Color.White, NoxiraColors.Silver),
                center = Offset(cx, cy),
                radius = dR * 2,
            ),
        )
    }
}

// ── Full wordmark block (emblem + NOXIRA + tagline) ───────────────────────────

@Composable
fun NoxiraWordmark(
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        NoxiraEmblem(size = if (compact) 32.dp else 40.dp)
        Spacer(Modifier.width(if (compact) 8.dp else 12.dp))
        Column {
            Text(
                text = "NOXIRA",
                style = if (compact) NoxiraType.Wordmark.copy(fontSize = 16.sp) else NoxiraType.Wordmark,
            )
            if (!compact) {
                Text(
                    text = "PLAY • FASTER • SMOOTHER",
                    style = NoxiraType.TagLine,
                )
            }
        }
    }
}

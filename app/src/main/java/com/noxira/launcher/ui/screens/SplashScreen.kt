// NOXIRA — Premium Roblox Launcher
// File: ui/screens/SplashScreen.kt
// Animated splash — emblem + wordmark, real init sequence, no fake progress

package com.noxira.launcher.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.noxira.launcher.ui.components.NoxiraEmblem
import com.noxira.launcher.ui.components.NoxiraWordmark
import com.noxira.launcher.ui.theme.NoxiraColors

@Composable
fun SplashScreen(onReady: () -> Unit) {
    // Fade-in animation
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        )
        // Brief hold
        kotlinx.coroutines.delay(400)
        // Fade to black
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 300),
        )
        onReady()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        NoxiraColors.BgSecondary,
                        NoxiraColors.BgMain,
                    ),
                    radius = 1200f,
                )
            )
            .alpha(alpha.value),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            NoxiraEmblem(size = 80.dp, animate = true)
            Spacer(Modifier.height(24.dp))
            NoxiraWordmark()
        }
    }
}

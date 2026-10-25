// NOXIRA — Premium Roblox Launcher
// File: ui/theme/NoxiraTheme.kt
// Design system: dark glassmorphism, metallic silver, no neon

package com.noxira.launcher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object NoxiraColors {
    val BgMain = Color(0xFF08090B)
    val BgSecondary = Color(0xFF101216)
    val BgElevated = Color(0xFF1A1D22)
    val GlassSurface = Color(0x26FFFFFF)
    val GlassBorder = Color(0x33FFFFFF)
    val GlassHighlight = Color(0x14FFFFFF)
    val TextPrimary = Color(0xFFF5F6F8)
    val TextSecondary = Color(0xFF969CA6)
    val TextMuted = Color(0xFF565C66)
    val Silver = Color(0xFFC8CDD5)
    val SilverDim = Color(0xFF8A9099)
    val SilverGlow = Color(0x1AC8CDD5)
    val Success = Color(0xFF4CAF7C)
    val SuccessDim = Color(0x334CAF7C)
    val Error = Color(0xFFCF6679)
    val ErrorDim = Color(0x33CF6679)
    val Warning = Color(0xFFD4A843)
    val WarningDim = Color(0x33D4A843)
    val Ripple = Color(0x1AC8CDD5)
}

private val NoxiraColorScheme = darkColorScheme(
    primary = NoxiraColors.Silver,
    onPrimary = NoxiraColors.BgMain,
    primaryContainer = NoxiraColors.BgElevated,
    onPrimaryContainer = NoxiraColors.TextPrimary,
    secondary = NoxiraColors.SilverDim,
    onSecondary = NoxiraColors.BgMain,
    background = NoxiraColors.BgMain,
    onBackground = NoxiraColors.TextPrimary,
    surface = NoxiraColors.BgSecondary,
    onSurface = NoxiraColors.TextPrimary,
    surfaceVariant = NoxiraColors.BgElevated,
    onSurfaceVariant = NoxiraColors.TextSecondary,
    error = NoxiraColors.Error,
    onError = NoxiraColors.BgMain,
    outline = NoxiraColors.GlassBorder,
)

object NoxiraType {
    val ScreenTitle = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 22.sp, letterSpacing = 0.5.sp, color = NoxiraColors.TextPrimary)
    val SectionHeading = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, letterSpacing = 1.5.sp, color = NoxiraColors.TextSecondary)
    val CardTitle = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, letterSpacing = 0.2.sp, color = NoxiraColors.TextPrimary)
    val Body = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp, color = NoxiraColors.TextPrimary)
    val BodySmall = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 12.sp, color = NoxiraColors.TextSecondary)
    val Label = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 0.8.sp, color = NoxiraColors.TextSecondary)
    val ButtonText = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, letterSpacing = 1.2.sp)
    val StatusText = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Normal, fontSize = 11.sp, lineHeight = 16.sp, color = NoxiraColors.TextSecondary)
    val Wordmark = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 20.sp, letterSpacing = 4.sp, color = NoxiraColors.Silver)
    val TagLine = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 9.sp, letterSpacing = 3.sp, color = NoxiraColors.TextMuted)
}

object NoxiraSpacing {
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 16.dp
    val lg: Dp = 24.dp
    val xl: Dp = 32.dp
    val xxl: Dp = 48.dp
}

object NoxiraRadius {
    val sm: Dp = 8.dp
    val md: Dp = 14.dp
    val lg: Dp = 20.dp
    val xl: Dp = 28.dp
    val pill: Dp = 50.dp
}

data class NoxiraDesignTokens(val colors: NoxiraColors = NoxiraColors)

val LocalNoxira = staticCompositionLocalOf { NoxiraDesignTokens() }

@Composable
fun NoxiraTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalNoxira provides NoxiraDesignTokens()) {
        MaterialTheme(colorScheme = NoxiraColorScheme, content = content)
    }
}

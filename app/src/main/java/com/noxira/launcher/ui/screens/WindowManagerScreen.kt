// NOXIRA — Premium Roblox Launcher
// File: ui/screens/WindowManagerScreen.kt
// Grid / window tiling controls

package com.noxira.launcher.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.noxira.launcher.MainViewModel
import com.noxira.launcher.core.root.RootStatus
import com.noxira.launcher.ui.components.*
import com.noxira.launcher.ui.theme.*

@Composable
fun WindowManagerScreen(vm: MainViewModel, onBack: () -> Unit) {
    val state by vm.state.collectAsState()
    val s = state.settings
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(NoxiraColors.BgMain, NoxiraColors.BgSecondary))
            )
    ) {
        // App bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = NoxiraSpacing.md, vertical = NoxiraSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GlassIconButton(Icons.Default.ArrowBack, "Back", onBack)
            Spacer(Modifier.width(NoxiraSpacing.md))
            Text("Window Manager", style = NoxiraType.ScreenTitle)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(start = NoxiraSpacing.md, end = NoxiraSpacing.md, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(NoxiraSpacing.md),
        ) {
            // Root warning
            if (state.rootStatus != RootStatus.Available) {
                GlassCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null, tint = NoxiraColors.Warning, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Fitur grid membutuhkan root dan freeform aktif.",
                            style = NoxiraType.Body,
                            color = NoxiraColors.Warning,
                        )
                    }
                }
            }

            // Freeform warning
            if (state.freeformEnabled == false) {
                GlassCard {
                    Text(
                        "Freeform belum aktif. Tekan 'Setup Freeform' lalu REBOOT sekali.",
                        style = NoxiraType.Body,
                        color = NoxiraColors.Warning,
                    )
                    Spacer(Modifier.height(NoxiraSpacing.sm))
                    NoxiraOutlineButton(
                        text = "SETUP FREEFORM",
                        onClick = vm::enableFreeform,
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.Tune,
                    )
                }
            }

            // Layout mode
            SectionHeader("Mode Layout")
            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("Auto Layout", style = NoxiraType.CardTitle)
                        Text(
                            if (s.autoLayout) "Kolom dihitung otomatis"
                            else "Kolom manual (${s.columnCount})",
                            style = NoxiraType.BodySmall,
                        )
                    }
                    Switch(
                        checked = s.autoLayout,
                        onCheckedChange = vm::setAutoLayout,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NoxiraColors.BgMain,
                            checkedTrackColor = NoxiraColors.Silver,
                        )
                    )
                }
            }

            // Manual columns (shown only when auto is off)
            if (!s.autoLayout) {
                SectionHeader("Jumlah Kolom")
                GlassCard {
                    Text("${s.columnCount} kolom", style = NoxiraType.CardTitle)
                    Spacer(Modifier.height(NoxiraSpacing.sm))
                    Slider(
                        value = s.columnCount.toFloat(),
                        onValueChange = { vm.setColumnCount(it.toInt()) },
                        valueRange = 1f..8f,
                        steps = 6,
                        colors = SliderDefaults.colors(
                            thumbColor = NoxiraColors.Silver,
                            activeTrackColor = NoxiraColors.Silver,
                            inactiveTrackColor = NoxiraColors.GlassSurface,
                        )
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("1", style = NoxiraType.Label)
                        Text("8", style = NoxiraType.Label)
                    }
                }
            }

            // Delay
            SectionHeader("Delay Antar Launch")
            GlassCard {
                Text("${s.launchDelaySec}s", style = NoxiraType.CardTitle)
                Spacer(Modifier.height(NoxiraSpacing.sm))
                Slider(
                    value = s.launchDelaySec,
                    onValueChange = { vm.setLaunchDelay(it) },
                    valueRange = 0f..10f,
                    colors = SliderDefaults.colors(
                        thumbColor = NoxiraColors.Silver,
                        activeTrackColor = NoxiraColors.Silver,
                        inactiveTrackColor = NoxiraColors.GlassSurface,
                    )
                )
                Text("Delay antar launch (detik)", style = NoxiraType.BodySmall)
            }

            // Gap
            SectionHeader("Gap Antar Window")
            GlassCard {
                Text("${s.windowGapPx} px", style = NoxiraType.CardTitle)
                Spacer(Modifier.height(NoxiraSpacing.sm))
                Slider(
                    value = s.windowGapPx.toFloat(),
                    onValueChange = { vm.setWindowGap(it.toInt()) },
                    valueRange = 0f..40f,
                    colors = SliderDefaults.colors(
                        thumbColor = NoxiraColors.Silver,
                        activeTrackColor = NoxiraColors.Silver,
                        inactiveTrackColor = NoxiraColors.GlassSurface,
                    )
                )
                Text("Gap px — dipakai saat re-tile", style = NoxiraType.BodySmall)
            }

            // Action buttons
            SectionHeader("Kontrol")
            GlassCard {
                NoxiraButton(
                    text = "LAUNCH + GRID",
                    onClick = vm::launchAllRoblox,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.rootStatus == RootStatus.Available,
                    icon = Icons.Default.PlayArrow,
                )
                Spacer(Modifier.height(NoxiraSpacing.sm))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(NoxiraSpacing.sm),
                ) {
                    NoxiraOutlineButton(
                        text = "RE-TILE",
                        onClick = vm::retileWindows,
                        modifier = Modifier.weight(1f),
                        enabled = state.rootStatus == RootStatus.Available,
                        icon = Icons.Default.GridView,
                    )
                    NoxiraOutlineButton(
                        text = "STOP SEMUA",
                        onClick = vm::stopAll,
                        modifier = Modifier.weight(1f),
                        enabled = state.robloxInstalls.isNotEmpty(),
                        icon = Icons.Default.Stop,
                    )
                }
                Spacer(Modifier.height(NoxiraSpacing.sm))
                NoxiraOutlineButton(
                    text = "LOCK LANDSCAPE",
                    onClick = vm::lockLandscape,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.rootStatus == RootStatus.Available,
                    icon = Icons.Default.ScreenRotation,
                )
            }

            // Log
            if (state.log.isNotEmpty()) {
                SectionHeader("Log")
                GlassCard {
                    state.log.takeLast(20).forEach { LogLine(it) }
                }
            }
        }
    }
}

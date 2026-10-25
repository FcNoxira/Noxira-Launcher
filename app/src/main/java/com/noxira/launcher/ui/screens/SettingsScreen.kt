// NOXIRA — Premium Roblox Launcher
// File: ui/screens/SettingsScreen.kt

package com.noxira.launcher.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.noxira.launcher.MainViewModel
import com.noxira.launcher.ui.components.*
import com.noxira.launcher.ui.theme.*

@Composable
fun SettingsScreen(vm: MainViewModel, onBack: () -> Unit, onAbout: () -> Unit) {
    val state by vm.state.collectAsState()
    val s = state.settings
    val scrollState = rememberScrollState()
    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(NoxiraColors.BgMain, NoxiraColors.BgSecondary)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = NoxiraSpacing.md, vertical = NoxiraSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GlassIconButton(Icons.Default.ArrowBack, "Back", onBack)
            Spacer(Modifier.width(NoxiraSpacing.md))
            Text("Settings", style = NoxiraType.ScreenTitle)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(start = NoxiraSpacing.md, end = NoxiraSpacing.md, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(NoxiraSpacing.md),
        ) {

            // ── Interaction ──────────────────────────────────────────────────
            SectionHeader("Interaksi")
            GlassCard {
                ToggleRow(
                    icon = Icons.Default.Vibration,
                    label = "Haptic Feedback",
                    description = "Getaran pada tap",
                    checked = s.hapticFeedback,
                    onToggle = vm::setHaptic,
                )
                GlassDivider()
                ToggleRow(
                    icon = Icons.Default.Animation,
                    label = "Reduced Motion",
                    description = "Kurangi animasi",
                    checked = s.reducedMotion,
                    onToggle = vm::setReducedMotion,
                )
            }

            // ── Diagnostics ──────────────────────────────────────────────────
            SectionHeader("Diagnostik")
            GlassCard {
                ToggleRow(
                    icon = Icons.Default.BugReport,
                    label = "Tampilkan Diag saat Gagal",
                    description = "Buka layar diagnostik otomatis",
                    checked = s.showDiagOnFail,
                    onToggle = vm::setShowDiagOnFail,
                )
            }

            // ── Reset ────────────────────────────────────────────────────────
            SectionHeader("Data")
            GlassCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showResetDialog = true }
                        .padding(vertical = NoxiraSpacing.sm),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.RestartAlt, null, tint = NoxiraColors.Error, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(NoxiraSpacing.sm))
                        Column {
                            Text("Reset ke Default", style = NoxiraType.CardTitle, color = NoxiraColors.Error)
                            Text("Hapus semua preferensi tersimpan", style = NoxiraType.BodySmall)
                        }
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = NoxiraColors.TextMuted)
                }
            }

            // ── About ────────────────────────────────────────────────────────
            SectionHeader("Tentang")
            GlassCard(onClick = onAbout) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        NoxiraEmblem(size = 32.dp)
                        Spacer(Modifier.width(NoxiraSpacing.sm))
                        Column {
                            Text("NOXIRA", style = NoxiraType.CardTitle)
                            Text("v1.0.0 — Muhammad Nur Fazrullah", style = NoxiraType.BodySmall)
                        }
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = NoxiraColors.TextMuted)
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = NoxiraColors.BgElevated,
            titleContentColor = NoxiraColors.TextPrimary,
            textContentColor = NoxiraColors.TextSecondary,
            title = { Text("Reset Pengaturan") },
            text  = { Text("Semua pengaturan akan dikembalikan ke nilai default. Ini tidak dapat dibatalkan.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.resetSettings()
                    showResetDialog = false
                }) {
                    Text("Reset", color = NoxiraColors.Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Batal", color = NoxiraColors.Silver)
                }
            }
        )
    }
}

// ── Toggle row ────────────────────────────────────────────────────────────────

@Composable
private fun ToggleRow(
    icon: ImageVector,
    label: String,
    description: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = NoxiraSpacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(icon, null, tint = NoxiraColors.SilverDim, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(NoxiraSpacing.sm))
            Column {
                Text(label, style = NoxiraType.CardTitle)
                Text(description, style = NoxiraType.BodySmall)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NoxiraColors.BgMain,
                checkedTrackColor = NoxiraColors.Silver,
            )
        )
    }
}

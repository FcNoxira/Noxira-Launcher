// NOXIRA — Premium Roblox Launcher
// File: ui/screens/DiagnosticsScreen.kt

package com.noxira.launcher.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.noxira.launcher.MainViewModel
import com.noxira.launcher.core.root.RootStatus
import com.noxira.launcher.ui.components.*
import com.noxira.launcher.ui.theme.*

@Composable
fun DiagnosticsScreen(vm: MainViewModel, onBack: () -> Unit) {
    val state by vm.state.collectAsState()
    val ctx = LocalContext.current
    val scrollState = rememberScrollState()

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
            Text("Diagnostics", style = NoxiraType.ScreenTitle)
            Spacer(Modifier.weight(1f))
            GlassIconButton(
                icon = Icons.Default.ContentCopy,
                contentDescription = "Copy report",
                onClick = {
                    val clip = ClipData.newPlainText("NOXIRA Diag", vm.buildDiagReport())
                    (ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = NoxiraSpacing.md, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(NoxiraSpacing.md),
        ) {

            // System
            SectionHeader("Sistem")
            GlassCard {
                DiagRow("Android", "API ${Build.VERSION.SDK_INT} (${Build.VERSION.RELEASE})")
                DiagRow("Device", "${Build.MANUFACTURER} ${Build.MODEL}")
                DiagRow("Build", Build.FINGERPRINT.take(60))
            }

            // Root
            SectionHeader("Root")
            GlassCard {
                val (rootLabel, rootColor) = when (state.rootStatus) {
                    RootStatus.Available   -> "Tersedia (UID 0)" to NoxiraColors.Success
                    RootStatus.Denied      -> "Ditolak" to NoxiraColors.Error
                    RootStatus.Unavailable -> "Tidak Ada" to NoxiraColors.Error
                    null                   -> "Belum Diperiksa" to NoxiraColors.TextSecondary
                }
                DiagRow("Status Root", rootLabel, rootColor)
                Spacer(Modifier.height(NoxiraSpacing.sm))
                NoxiraOutlineButton(
                    text = "PERIKSA ULANG ROOT",
                    onClick = vm::checkRoot,
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.Refresh,
                )
            }

            // Freeform
            SectionHeader("Freeform")
            GlassCard {
                val (ffLabel, ffColor) = when (state.freeformEnabled) {
                    true  -> "Aktif" to NoxiraColors.Success
                    false -> "Tidak Aktif — perlu setup" to NoxiraColors.Warning
                    null  -> "Belum Diperiksa" to NoxiraColors.TextSecondary
                }
                DiagRow("Freeform Support", ffLabel, ffColor)
                if (state.freeformEnabled == false) {
                    Spacer(Modifier.height(NoxiraSpacing.sm))
                    Text(
                        "Jalankan 'Setup Freeform' di Window Manager lalu reboot.",
                        style = NoxiraType.BodySmall,
                        color = NoxiraColors.Warning,
                    )
                }
            }

            // Roblox
            SectionHeader("Roblox")
            GlassCard {
                DiagRow(
                    "Install Terdeteksi",
                    "${state.robloxInstalls.size}",
                    if (state.robloxInstalls.isEmpty()) NoxiraColors.Error else NoxiraColors.Success,
                )
                state.robloxInstalls.forEach { app ->
                    Spacer(Modifier.height(2.dp))
                    Text("• ${app.packageName}", style = NoxiraType.StatusText)
                }
                Spacer(Modifier.height(NoxiraSpacing.sm))
                NoxiraOutlineButton(
                    text = "REFRESH DETEKSI",
                    onClick = vm::refreshRoblox,
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.Refresh,
                )
            }

            // Log
            if (state.log.isNotEmpty()) {
                SectionHeader("Log Terbaru (${state.log.size})")
                GlassCard {
                    state.log.takeLast(30).forEach { LogLine(it) }
                }
            }
        }
    }
}

@Composable
private fun DiagRow(label: String, value: String, valueColor: Color = NoxiraColors.TextPrimary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = NoxiraType.BodySmall, modifier = Modifier.weight(1f))
        Text(value, style = NoxiraType.BodySmall, color = valueColor, modifier = Modifier.weight(1f))
    }
}

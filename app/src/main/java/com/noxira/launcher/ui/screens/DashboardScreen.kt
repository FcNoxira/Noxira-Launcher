// NOXIRA — Premium Roblox Launcher
// File: ui/screens/DashboardScreen.kt
// Main home screen — Roblox card, quick actions, status, log tail

package com.noxira.launcher.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.noxira.launcher.MainViewModel
import com.noxira.launcher.OperationState
import com.noxira.launcher.core.root.RootStatus
import com.noxira.launcher.ui.components.*
import com.noxira.launcher.ui.theme.*

@Composable
fun DashboardScreen(
    vm: MainViewModel,
    onNavigateSettings: () -> Unit,
    onNavigateDiag: () -> Unit,
    onNavigateWindowMgr: () -> Unit,
) {
    val state by vm.state.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(NoxiraColors.BgMain, NoxiraColors.BgSecondary)
                )
            )
            .verticalScroll(scrollState)
            .padding(bottom = 32.dp)
    ) {
        // Top bar
        DashboardTopBar(onSettings = onNavigateSettings)

        val hPad = Modifier.padding(horizontal = NoxiraSpacing.md)

        Spacer(Modifier.height(NoxiraSpacing.md))

        // Root + freeform status row
        StatusRow(
            rootStatus = state.rootStatus,
            rootChecking = state.rootChecking,
            freeformEnabled = state.freeformEnabled,
            modifier = hPad,
            onCheckRoot = vm::checkRoot,
        )

        Spacer(Modifier.height(NoxiraSpacing.md))

        // Roblox launcher card
        SectionHeader("Launcher", modifier = hPad.then(Modifier))
        RobloxCard(
            installs = state.robloxInstalls,
            launchState = state.launchState,
            loading = state.robloxRefreshing,
            selectedPackages = state.selectedRobloxPackages,
            modifier = hPad,
            onToggleSelection = vm::toggleRobloxSelection,
            onSelectCount = vm::selectRobloxCount,
            onLaunch = vm::launchAllRoblox,
            onRefresh = vm::refreshRoblox,
        )

        Spacer(Modifier.height(NoxiraSpacing.md))

        // Quick actions
        SectionHeader("Quick Actions", modifier = hPad.then(Modifier))
        QuickActionsRow(
            modifier = hPad,
            gridEnabled = state.rootStatus == RootStatus.Available,
            stopEnabled = state.robloxInstalls.isNotEmpty(),
            onLaunch    = vm::launchAllRoblox,
            onRefresh   = vm::refreshRoblox,
            onRetile    = { vm.retileWindows(); onNavigateWindowMgr() },
            onStop      = vm::stopAll,
            onSettings  = onNavigateSettings,
            onDiag      = onNavigateDiag,
        )

        Spacer(Modifier.height(NoxiraSpacing.md))

        // Log tail
        if (state.log.isNotEmpty()) {
            SectionHeader("Activity Log", modifier = hPad.then(Modifier))
            LogCard(log = state.log, onClear = vm::clearLog, modifier = hPad)
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun DashboardTopBar(onSettings: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = NoxiraSpacing.md, vertical = NoxiraSpacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NoxiraWordmark()
        GlassIconButton(
            icon = Icons.Default.Settings,
            contentDescription = "Settings",
            onClick = onSettings,
        )
    }
}

// ── Status row ────────────────────────────────────────────────────────────────

@Composable
private fun StatusRow(
    rootStatus: RootStatus?,
    rootChecking: Boolean,
    freeformEnabled: Boolean?,
    modifier: Modifier,
    onCheckRoot: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(NoxiraSpacing.sm),
    ) {
        // Root badge
        val (rootText, rootBadge) = when {
            rootChecking -> "Memeriksa…" to BadgeStatus.NEUTRAL
            rootStatus == RootStatus.Available -> "Root OK" to BadgeStatus.SUCCESS
            rootStatus == RootStatus.Denied -> "Root Ditolak" to BadgeStatus.ERROR
            rootStatus == RootStatus.Unavailable -> "Tidak Ada Root" to BadgeStatus.ERROR
            else -> "Root?" to BadgeStatus.NEUTRAL
        }
        StatusBadge(rootText, rootBadge)

        // Freeform badge
        val (ffText, ffBadge) = when (freeformEnabled) {
            true  -> "Freeform Aktif" to BadgeStatus.SUCCESS
            false -> "Freeform Off" to BadgeStatus.WARNING
            null  -> "Freeform?" to BadgeStatus.NEUTRAL
        }
        StatusBadge(ffText, ffBadge)

        Spacer(Modifier.weight(1f))

        if (!rootChecking) {
            GlassIconButton(
                icon = Icons.Default.Refresh,
                contentDescription = "Check root",
                onClick = onCheckRoot,
            )
        }
    }
}

// ── Roblox card ───────────────────────────────────────────────────────────────

@Composable
private fun RobloxCard(
    installs: List<com.noxira.launcher.core.launcher.InstalledApp>,
    launchState: OperationState,
    loading: Boolean,
    selectedPackages: Set<String>,
    modifier: Modifier,
    onToggleSelection: (String, Boolean) -> Unit,
    onSelectCount: (Int) -> Unit,
    onLaunch: () -> Unit,
    onRefresh: () -> Unit,
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Roblox icon placeholder — geometric R
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(NoxiraRadius.md))
                    .background(Color(0xFF1A1D22))
                    .border(0.5.dp, NoxiraColors.GlassBorder, RoundedCornerShape(NoxiraRadius.md)),
                contentAlignment = Alignment.Center,
            ) {
                Text("R", style = NoxiraType.Wordmark, color = NoxiraColors.Silver)
            }

            Spacer(Modifier.width(NoxiraSpacing.md))

            Column(Modifier.weight(1f)) {
                Text("Roblox", style = NoxiraType.CardTitle)
                val sub = when {
                    loading -> "Memuat…"
                    installs.isEmpty() -> "Tidak terinstal"
                    installs.size == 1 -> "1 install terdeteksi"
                    else -> "${installs.size} install terdeteksi"
                }
                Text(sub, style = NoxiraType.BodySmall)
            }

            val (badgeText, badgeStatus) = when {
                loading -> "—" to BadgeStatus.NEUTRAL
                installs.isEmpty() -> "TIDAK ADA" to BadgeStatus.ERROR
                else -> "SIAP" to BadgeStatus.SUCCESS
            }
            StatusBadge(badgeText, badgeStatus)
        }

        Spacer(Modifier.height(NoxiraSpacing.md))
        GlassDivider()
        Spacer(Modifier.height(NoxiraSpacing.md))

        // Choose which Roblox instances should be launched.
        if (installs.isNotEmpty()) {
            Text("Pilih Roblox yang ingin dibuka", style = NoxiraType.CardTitle)
            installs.forEach { app ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = app.packageName in selectedPackages,
                        onCheckedChange = { checked -> onToggleSelection(app.packageName, checked) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = NoxiraColors.Silver,
                            checkmarkColor = NoxiraColors.BgMain,
                        ),
                    )
                    Column(Modifier.weight(1f)) {
                        Text(app.label, style = NoxiraType.Body)
                        Text(app.packageName, style = NoxiraType.BodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(NoxiraSpacing.sm)) {
                NoxiraOutlineButton(text = "PILIH 1", onClick = { onSelectCount(1) }, modifier = Modifier.weight(1f))
                NoxiraOutlineButton(text = "PILIH 2", onClick = { onSelectCount(2) }, modifier = Modifier.weight(1f))
                NoxiraOutlineButton(text = "PILIH SEMUA", onClick = { onSelectCount(installs.size) }, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(NoxiraSpacing.sm))
            Text("${selectedPackages.size} dipilih • Freeform otomatis menata jendela jika root aktif", style = NoxiraType.BodySmall)
            Spacer(Modifier.height(NoxiraSpacing.sm))
        }

        // Launch button
        NoxiraButton(
            text = when (launchState) {
                OperationState.RUNNING -> "MELUNCURKAN…"
                OperationState.SUCCESS -> "BERHASIL"
                OperationState.ERROR   -> "GAGAL — COBA LAGI"
                OperationState.IDLE    -> "LAUNCH ROBLOX"
            },
            onClick = onLaunch,
            modifier = Modifier.fillMaxWidth(),
            enabled = installs.isNotEmpty() && launchState != OperationState.RUNNING,
            loading = launchState == OperationState.RUNNING,
            icon = Icons.Default.PlayArrow,
        )

        Spacer(Modifier.height(NoxiraSpacing.sm))

        NoxiraOutlineButton(
            text = "REFRESH DETEKSI",
            onClick = onRefresh,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Default.Refresh,
        )
    }
}

// ── Quick actions row ─────────────────────────────────────────────────────────

@Composable
private fun QuickActionsRow(
    modifier: Modifier,
    gridEnabled: Boolean,
    stopEnabled: Boolean,
    onLaunch: () -> Unit,
    onRefresh: () -> Unit,
    onRetile: () -> Unit,
    onStop: () -> Unit,
    onSettings: () -> Unit,
    onDiag: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(NoxiraSpacing.sm),
    ) {
        QuickActionChip("Launch",    Icons.Default.PlayArrow,     onLaunch,   Modifier.weight(1f))
        QuickActionChip("Refresh",   Icons.Default.Refresh,       onRefresh,  Modifier.weight(1f))
        QuickActionChip("Re-tile",   Icons.Default.GridView,      onRetile,   Modifier.weight(1f), gridEnabled)
        QuickActionChip("Stop",      Icons.Default.Stop,          onStop,     Modifier.weight(1f), stopEnabled)
        QuickActionChip("Diag",      Icons.Default.BugReport,     onDiag,     Modifier.weight(1f))
    }
}

// ── Log card ──────────────────────────────────────────────────────────────────

@Composable
private fun LogCard(
    log: List<String>,
    onClear: () -> Unit,
    modifier: Modifier,
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Log", style = NoxiraType.CardTitle)
            GlassIconButton(
                icon = Icons.Default.ClearAll,
                contentDescription = "Clear log",
                onClick = onClear,
            )
        }
        Spacer(Modifier.height(NoxiraSpacing.sm))
        log.takeLast(12).forEach { line ->
            LogLine(line)
        }
    }
}

// NOXIRA — Premium Roblox Launcher
// File: MainViewModel.kt
// Single ViewModel shared across all screens via activity scope.

package com.noxira.launcher

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.noxira.launcher.core.launcher.InstalledApp
import com.noxira.launcher.core.launcher.LauncherEngine
import com.noxira.launcher.core.prefs.NoxiraPrefsRepository
import com.noxira.launcher.core.prefs.NoxiraSettings
import com.noxira.launcher.core.root.RootManager
import com.noxira.launcher.core.root.RootStatus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ── UI state types ────────────────────────────────────────────────────────────

enum class OperationState { IDLE, RUNNING, SUCCESS, ERROR }

data class NoxiraUiState(
    // Root
    val rootStatus: RootStatus? = null,
    val rootChecking: Boolean = false,

    // Freeform
    val freeformEnabled: Boolean? = null,       // null = unknown
    val freeformCheckDone: Boolean = false,

    // Roblox
    val robloxInstalls: List<InstalledApp> = emptyList(),
    val robloxRefreshing: Boolean = false,

    // Operations
    val launchState: OperationState = OperationState.IDLE,
    val gridState: OperationState = OperationState.IDLE,
    val stopState: OperationState = OperationState.IDLE,

    // Log
    val log: List<String> = emptyList(),

    // Settings (live mirror)
    val settings: NoxiraSettings = NoxiraSettings(),
)

// ── ViewModel ─────────────────────────────────────────────────────────────────

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val engine = LauncherEngine(app)
    private val prefs  = NoxiraPrefsRepository(app)

    private val _state = MutableStateFlow(NoxiraUiState())
    val state: StateFlow<NoxiraUiState> = _state.asStateFlow()

    init {
        // Mirror settings from DataStore
        viewModelScope.launch {
            prefs.settingsFlow.collect { settings ->
                _state.update { it.copy(settings = settings) }
            }
        }
        // Initial checks on launch
        refreshRoblox()
        checkRoot()
    }

    // ── Root ──────────────────────────────────────────────────────────────────

    fun checkRoot() {
        viewModelScope.launch {
            _state.update { it.copy(rootChecking = true) }
            val status = RootManager.checkRoot()
            _state.update { it.copy(rootStatus = status, rootChecking = false) }
            if (status == RootStatus.Available) checkFreeform()
        }
    }

    fun checkFreeform() {
        viewModelScope.launch {
            val settings = RootManager.readFreeformSettings()
            val freeEnabled = settings["enable_freeform_support"] == "1"
                           && settings["force_resizable_activities"] == "1"
            _state.update { it.copy(freeformEnabled = freeEnabled, freeformCheckDone = true) }
        }
    }

    fun enableFreeform() {
        viewModelScope.launch {
            appendLog("Mengaktifkan freeform…")
            val result = RootManager.enableFreeform()
            appendLog(result.toString())
            appendLog("REBOOT diperlukan agar aktif.")
            checkFreeform()
        }
    }

    // ── Roblox ───────────────────────────────────────────────────────────────

    fun refreshRoblox() {
        _state.update { it.copy(robloxRefreshing = true) }
        viewModelScope.launch {
            val installs = engine.detectRobloxInstalls()
            _state.update { it.copy(robloxInstalls = installs, robloxRefreshing = false) }
        }
    }

    // ── Launch all ────────────────────────────────────────────────────────────

    fun launchAllRoblox() {
        if (_state.value.launchState == OperationState.RUNNING) return
        viewModelScope.launch {
            _state.update { it.copy(launchState = OperationState.RUNNING) }
            clearLog()
            val s = _state.value.settings
            try {
                if (_state.value.rootStatus == RootStatus.Available) {
                    engine.launchAllFreeform(
                        delaySeconds = s.launchDelaySec,
                        gapPx = s.windowGapPx,
                        columnCount = s.columnCount,
                        autoColumns = s.autoLayout,
                        log = ::appendLog,
                    )
                } else {
                    // Fallback: standard intent for base Roblox package
                    appendLog("Root tidak tersedia — launch via intent standar.")
                    val installs = _state.value.robloxInstalls
                    if (installs.isEmpty()) {
                        appendLog("Roblox tidak terinstal.")
                    } else {
                        val result = engine.launchViaIntent(installs.first().packageName)
                        appendLog(result.message.ifBlank { "Berhasil." })
                    }
                }
                _state.update { it.copy(launchState = OperationState.SUCCESS) }
            } catch (e: Exception) {
                appendLog("Error: ${e.message}")
                _state.update { it.copy(launchState = OperationState.ERROR) }
            }
        }
    }

    // ── Grid only ─────────────────────────────────────────────────────────────

    fun retileWindows() {
        if (_state.value.gridState == OperationState.RUNNING) return
        viewModelScope.launch {
            _state.update { it.copy(gridState = OperationState.RUNNING) }
            val s = _state.value.settings
            engine.tileWindows(s.windowGapPx, s.columnCount, s.autoLayout, ::appendLog)
            _state.update { it.copy(gridState = OperationState.SUCCESS) }
        }
    }

    // ── Stop all ──────────────────────────────────────────────────────────────

    fun stopAll() {
        if (_state.value.stopState == OperationState.RUNNING) return
        viewModelScope.launch {
            _state.update { it.copy(stopState = OperationState.RUNNING) }
            engine.stopAllRoblox(::appendLog)
            refreshRoblox()
            _state.update { it.copy(stopState = OperationState.SUCCESS) }
        }
    }

    // ── Lock landscape ────────────────────────────────────────────────────────

    fun lockLandscape() {
        viewModelScope.launch {
            appendLog("Lock landscape…")
            val r = RootManager.lockLandscape()
            appendLog(r.toString())
        }
    }

    // ── Settings ──────────────────────────────────────────────────────────────

    fun setLaunchDelay(v: Float)  { viewModelScope.launch { prefs.updateLaunchDelay(v) } }
    fun setWindowGap(v: Int)      { viewModelScope.launch { prefs.updateWindowGap(v) } }
    fun setColumnCount(v: Int)    { viewModelScope.launch { prefs.updateColumnCount(v) } }
    fun setAutoLayout(v: Boolean) { viewModelScope.launch { prefs.updateAutoLayout(v) } }
    fun setHaptic(v: Boolean)     { viewModelScope.launch { prefs.updateHaptic(v) } }
    fun setReducedMotion(v: Boolean) { viewModelScope.launch { prefs.updateReducedMotion(v) } }
    fun setShowDiagOnFail(v: Boolean) { viewModelScope.launch { prefs.updateShowDiagOnFail(v) } }
    fun resetSettings()           { viewModelScope.launch { prefs.resetToDefaults() } }

    // ── Log helpers ───────────────────────────────────────────────────────────

    private fun appendLog(msg: String) {
        _state.update { it.copy(log = (it.log + msg).takeLast(200)) }
    }

    fun clearLog() {
        _state.update { it.copy(log = emptyList()) }
    }

    // ── Diagnostics ───────────────────────────────────────────────────────────

    fun buildDiagReport(): String {
        val s = _state.value
        return buildString {
            appendLine("=== NOXIRA Diagnostics ===")
            appendLine("Root: ${s.rootStatus}")
            appendLine("Freeform aktif: ${s.freeformEnabled}")
            appendLine("Roblox terdeteksi: ${s.robloxInstalls.size}")
            s.robloxInstalls.forEach { appendLine("  - ${it.packageName}") }
            appendLine("Log terbaru:")
            s.log.takeLast(20).forEach { appendLine("  $it") }
        }
    }
}

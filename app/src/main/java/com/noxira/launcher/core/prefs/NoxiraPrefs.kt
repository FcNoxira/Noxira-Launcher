// NOXIRA — Premium Roblox Launcher
// File: core/prefs/NoxiraPrefs.kt
// DataStore-backed persistent preferences

package com.noxira.launcher.core.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property — one DataStore instance per process
val Context.noxiraDataStore: DataStore<Preferences> by preferencesDataStore(name = "noxira_prefs")

// ── Preference keys ───────────────────────────────────────────────────────────

object PrefKeys {
    val LAUNCH_DELAY_SEC  = floatPreferencesKey("launch_delay_sec")
    val WINDOW_GAP_PX     = intPreferencesKey("window_gap_px")
    val COLUMN_COUNT      = intPreferencesKey("column_count")
    val AUTO_LAYOUT       = booleanPreferencesKey("auto_layout")
    val HAPTIC_FEEDBACK   = booleanPreferencesKey("haptic_feedback")
    val REDUCED_MOTION    = booleanPreferencesKey("reduced_motion")
    val SHOW_DIAG_ON_FAIL = booleanPreferencesKey("show_diag_on_fail")
}

// ── Defaults ──────────────────────────────────────────────────────────────────

object PrefDefaults {
    const val LAUNCH_DELAY_SEC  = 1.5f
    const val WINDOW_GAP_PX     = 5
    const val COLUMN_COUNT      = 2
    const val AUTO_LAYOUT       = true
    const val HAPTIC_FEEDBACK   = true
    const val REDUCED_MOTION    = false
    const val SHOW_DIAG_ON_FAIL = true
}

// ── Snapshot ──────────────────────────────────────────────────────────────────

data class NoxiraSettings(
    val launchDelaySec: Float  = PrefDefaults.LAUNCH_DELAY_SEC,
    val windowGapPx: Int       = PrefDefaults.WINDOW_GAP_PX,
    val columnCount: Int       = PrefDefaults.COLUMN_COUNT,
    val autoLayout: Boolean    = PrefDefaults.AUTO_LAYOUT,
    val hapticFeedback: Boolean= PrefDefaults.HAPTIC_FEEDBACK,
    val reducedMotion: Boolean = PrefDefaults.REDUCED_MOTION,
    val showDiagOnFail: Boolean= PrefDefaults.SHOW_DIAG_ON_FAIL,
)

// ── Repository ────────────────────────────────────────────────────────────────

class NoxiraPrefsRepository(private val context: Context) {

    val settingsFlow: Flow<NoxiraSettings> = context.noxiraDataStore.data.map { prefs ->
        NoxiraSettings(
            launchDelaySec  = prefs[PrefKeys.LAUNCH_DELAY_SEC]  ?: PrefDefaults.LAUNCH_DELAY_SEC,
            windowGapPx     = prefs[PrefKeys.WINDOW_GAP_PX]     ?: PrefDefaults.WINDOW_GAP_PX,
            columnCount     = prefs[PrefKeys.COLUMN_COUNT]       ?: PrefDefaults.COLUMN_COUNT,
            autoLayout      = prefs[PrefKeys.AUTO_LAYOUT]        ?: PrefDefaults.AUTO_LAYOUT,
            hapticFeedback  = prefs[PrefKeys.HAPTIC_FEEDBACK]    ?: PrefDefaults.HAPTIC_FEEDBACK,
            reducedMotion   = prefs[PrefKeys.REDUCED_MOTION]     ?: PrefDefaults.REDUCED_MOTION,
            showDiagOnFail  = prefs[PrefKeys.SHOW_DIAG_ON_FAIL] ?: PrefDefaults.SHOW_DIAG_ON_FAIL,
        )
    }

    suspend fun updateLaunchDelay(v: Float) {
        val clamped = v.coerceIn(0f, 30f)
        context.noxiraDataStore.edit { it[PrefKeys.LAUNCH_DELAY_SEC] = clamped }
    }

    suspend fun updateWindowGap(v: Int) {
        val clamped = v.coerceIn(0, 200)
        context.noxiraDataStore.edit { it[PrefKeys.WINDOW_GAP_PX] = clamped }
    }

    suspend fun updateColumnCount(v: Int) {
        val clamped = v.coerceIn(1, 8)
        context.noxiraDataStore.edit { it[PrefKeys.COLUMN_COUNT] = clamped }
    }

    suspend fun updateAutoLayout(v: Boolean) {
        context.noxiraDataStore.edit { it[PrefKeys.AUTO_LAYOUT] = v }
    }

    suspend fun updateHaptic(v: Boolean) {
        context.noxiraDataStore.edit { it[PrefKeys.HAPTIC_FEEDBACK] = v }
    }

    suspend fun updateReducedMotion(v: Boolean) {
        context.noxiraDataStore.edit { it[PrefKeys.REDUCED_MOTION] = v }
    }

    suspend fun updateShowDiagOnFail(v: Boolean) {
        context.noxiraDataStore.edit { it[PrefKeys.SHOW_DIAG_ON_FAIL] = v }
    }

    suspend fun resetToDefaults() {
        context.noxiraDataStore.edit { it.clear() }
    }
}

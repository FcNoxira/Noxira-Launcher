// NOXIRA — Premium Roblox Launcher
// File: core/root/RootManager.kt
// Root capability: detection, scoped su execution, result types.
// All shell commands are narrowly scoped to launcher functionality.

package com.noxira.launcher.core.root

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

// ── Result type ───────────────────────────────────────────────────────────────

sealed class ShellResult {
    data class Ok(val stdout: String) : ShellResult()
    data class Err(val stderr: String, val exitCode: Int) : ShellResult()
    object NoSu : ShellResult()
}

sealed class RootStatus {
    object Available : RootStatus()
    object Denied    : RootStatus()
    object Unavailable : RootStatus()
}

// ── RootManager ───────────────────────────────────────────────────────────────

object RootManager {

    /**
     * Test root access.
     * Runs `echo __ROOT_UID_$(id -u); exit` via su, exactly matching the
     * original ZetsuLauncher probe. Returns RootStatus.
     */
    suspend fun checkRoot(): RootStatus = withContext(Dispatchers.IO) {
        try {
            val proc = Runtime.getRuntime().exec(arrayOf("su"))
            val writer = OutputStreamWriter(proc.outputStream)
            writer.write("\necho __ROOT_UID_\$(id -u)\nexit\n")
            writer.flush()
            writer.close()

            val reader = BufferedReader(InputStreamReader(proc.inputStream))
            val out = buildString { reader.lines().forEach { appendLine(it) } }
            val exit = proc.waitFor()

            when {
                out.contains("__ROOT_UID_0") -> RootStatus.Available
                exit != 0 -> RootStatus.Denied
                else -> RootStatus.Denied
            }
        } catch (e: Exception) {
            RootStatus.Unavailable
        }
    }

    /**
     * Execute a shell command via su.
     * Only call this for documented launcher commands:
     *   - am start --windowingMode 5
     *   - am stack resize
     *   - am task resize
     *   - am force-stop
     *   - am stack list
     *   - settings put global [freeform keys]
     *   - settings get global [freeform keys]
     *   - cmd appops set [package] SYSTEM_ALERT_WINDOW allow
     *
     * Do not call for arbitrary user input.
     */
    suspend fun runSu(command: String): ShellResult = withContext(Dispatchers.IO) {
        try {
            val proc = Runtime.getRuntime().exec(arrayOf("su"))
            val writer = OutputStreamWriter(proc.outputStream)
            writer.write("$command\nexit\n")
            writer.flush()
            writer.close()

            val stdout = BufferedReader(InputStreamReader(proc.inputStream))
                .readText().trim()
            val stderr = BufferedReader(InputStreamReader(proc.errorStream))
                .readText().trim()
            val exit = proc.waitFor()

            if (exit == 0) ShellResult.Ok(stdout)
            else ShellResult.Err(stderr.ifBlank { "exit $exit" }, exit)
        } catch (e: Exception) {
            ShellResult.NoSu
        }
    }

    /** Read freeform and force-resizable settings in one call. */
    suspend fun readFreeformSettings(): Map<String, String> {
        val result = runSu(
            "settings get global enable_freeform_support; " +
            "settings get global force_resizable_activities"
        )
        if (result !is ShellResult.Ok) return emptyMap()
        val lines = result.stdout.lines()
        return mapOf(
            "enable_freeform_support"    to (lines.getOrNull(0) ?: "null"),
            "force_resizable_activities" to (lines.getOrNull(1) ?: "null"),
        )
    }

    /**
     * Enable Android freeform windowing via developer settings.
     * Requires root. User must REBOOT after this.
     */
    suspend fun enableFreeform(): ShellResult = runSu(
        "settings put global enable_freeform_support 1; " +
        "settings put global force_resizable_activities 1; " +
        "settings put global enable_non_resizable_multi_window 1"
    )

    /**
     * Lock landscape orientation via root.
     * Original ZetsuLauncher uses this before tiling.
     */
    suspend fun lockLandscape(): ShellResult = runSu(
        "settings put system accelerometer_rotation 0; " +
        "settings put system user_rotation 1"
    )

    /**
     * List active task stacks.
     */
    suspend fun listStacks(): ShellResult = runSu("am stack list")

    /**
     * Start Roblox in freeform windowing mode via root am start.
     * windowing mode 5 = WINDOWING_MODE_FREEFORM
     */
    suspend fun amStartFreeform(pkg: String, activity: String): ShellResult = runSu(
        "am start --windowingMode 5 -n $pkg/$activity"
    )

    /**
     * Resize a task stack to given bounds.
     * Used for grid layout.
     */
    suspend fun resizeStack(stackId: Int, left: Int, top: Int, right: Int, bottom: Int): ShellResult =
        runSu("am stack resize $stackId $left $top $right $bottom")

    /**
     * Resize a task to given bounds.
     */
    suspend fun resizeTask(taskId: Int, left: Int, top: Int, right: Int, bottom: Int): ShellResult =
        runSu("am task resize $taskId $left $top $right $bottom")

    /**
     * Force-stop a package.
     */
    suspend fun forceStop(pkg: String): ShellResult = runSu("am force-stop $pkg")

    /**
     * Grant SYSTEM_ALERT_WINDOW to a package via appops.
     */
    suspend fun grantOverlayPermission(pkg: String): ShellResult =
        runSu("cmd appops set $pkg SYSTEM_ALERT_WINDOW allow")
}

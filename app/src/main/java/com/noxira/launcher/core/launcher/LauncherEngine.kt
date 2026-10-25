// NOXIRA — Premium Roblox Launcher
// File: core/launcher/LauncherEngine.kt
// Roblox detection, standard-intent launch, grid tiling, App Cloner XML patch.
// Preserves all verified ZetsuLauncher functionality.

package com.noxira.launcher.core.launcher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import com.noxira.launcher.core.root.RootManager
import com.noxira.launcher.core.root.RootStatus
import com.noxira.launcher.core.root.ShellResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.ceil
import kotlin.math.sqrt

// ── Constants ─────────────────────────────────────────────────────────────────

const val PKG_ROBLOX        = "com.roblox.client"
const val PKG_ROBLOX_TYPO  = "com.roblox.clienu"  // original ZetsuLauncher had this variant
const val ACTIVITY_ROBLOX   = "com.roblox.client.ActivitySplash"
const val WINDOWING_FREEFORM = 5

// App Cloner uses package prefix "com.roblox" and increments
// e.g. com.roblox.client, com.roblox.client1, com.roblox.client2 …
const val PKG_ROBLOX_PREFIX  = "com.roblox"

// ── Data types ────────────────────────────────────────────────────────────────

data class InstalledApp(
    val packageName: String,
    val label: String,
)

enum class LaunchResultStatus { OK, NOT_INSTALLED, NO_ACTIVITY, FAILED }
data class LaunchResult(val status: LaunchResultStatus, val message: String = "")

data class GridTask(
    val taskId: Int,
    val stackId: Int,
    val pkg: String,
    val bounds: Rect,
)

// ── LauncherEngine ────────────────────────────────────────────────────────────

class LauncherEngine(private val context: Context) {

    private val pm: PackageManager get() = context.packageManager

    // ── Detection ────────────────────────────────────────────────────────────

    /** All installed apps whose package starts with com.roblox */
    fun detectRobloxInstalls(): List<InstalledApp> {
        return pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.packageName.startsWith(PKG_ROBLOX_PREFIX) }
            .map { InstalledApp(it.packageName, pm.getApplicationLabel(it).toString()) }
    }

    fun isRobloxInstalled(): Boolean =
        pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .any { it.packageName.startsWith(PKG_ROBLOX_PREFIX) }

    // ── Standard intent launch ────────────────────────────────────────────────

    /** Launch a package via standard getLaunchIntentForPackage. No root needed. */
    fun launchViaIntent(packageName: String): LaunchResult {
        val intent = pm.getLaunchIntentForPackage(packageName)
            ?: return LaunchResult(LaunchResultStatus.NO_ACTIVITY, "No launcher activity for $packageName")
        return try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            context.startActivity(intent)
            LaunchResult(LaunchResultStatus.OK)
        } catch (e: Exception) {
            LaunchResult(LaunchResultStatus.FAILED, e.message ?: "startActivity failed")
        }
    }

    // ── Root freeform launch ──────────────────────────────────────────────────

    /**
     * Launch all detected Roblox installs in freeform mode with a configurable
     * delay between launches, then tile them into a grid.
     * Requires root + freeform enabled.
     */
    suspend fun launchAllFreeform(
        delaySeconds: Float,
        gapPx: Int,
        columnCount: Int,
        autoColumns: Boolean,
        log: (String) -> Unit,
    ) = withContext(Dispatchers.IO) {
        val installs = detectRobloxInstalls()
        if (installs.isEmpty()) {
            log("Tidak ada Roblox terdeteksi.")
            return@withContext
        }
        log("Terdeteksi ${installs.size} install.")

        for ((i, app) in installs.withIndex()) {
            log("Launch ${i + 1}/${installs.size}: ${app.packageName}")
            val r = RootManager.amStartFreeform(app.packageName, ACTIVITY_ROBLOX)
            when (r) {
                is ShellResult.Ok  -> log("  -> OK")
                is ShellResult.Err -> log("  -> gagal: ${r.stderr}")
                ShellResult.NoSu   -> { log("  -> tidak ada su"); return@withContext }
            }
            if (i < installs.size - 1) {
                delay((delaySeconds * 1000L).toLong())
            }
        }

        log("Semua launched. Menunggu ${(delaySeconds * 1000).toInt()}ms lalu grid…")
        delay((delaySeconds * 1000L).toLong())

        tileWindows(gapPx, columnCount, autoColumns, log)
    }

    // ── Window tiling ─────────────────────────────────────────────────────────

    suspend fun tileWindows(
        gapPx: Int,
        columnCount: Int,
        autoColumns: Boolean,
        log: (String) -> Unit,
    ) = withContext(Dispatchers.IO) {
        val stacksResult = RootManager.listStacks()
        if (stacksResult !is ShellResult.Ok) {
            log("am stack list gagal.")
            return@withContext
        }
        val tasks = parseTasksFromStackList(stacksResult.stdout)
            .filter { it.pkg.startsWith(PKG_ROBLOX_PREFIX) }

        if (tasks.isEmpty()) {
            log("Tidak ada Roblox task aktif.")
            return@withContext
        }

        val screen = getScreenSize()
        val statusBarH = getStatusBarHeight()
        val usableH = screen.y - statusBarH

        val n = tasks.size
        val cols = if (autoColumns) bestColumns(n) else columnCount.coerceAtLeast(1)
        val rows = ceil(n.toDouble() / cols).toInt()

        val cellW = (screen.x - gapPx * (cols + 1)) / cols
        val cellH = (usableH - gapPx * (rows + 1)) / rows

        log("Grid ${cols}x${rows} — cell ${cellW}x${cellH}px — gap ${gapPx}px")

        tasks.forEachIndexed { i, task ->
            val col = i % cols
            val row = i / cols
            val l = gapPx + col * (cellW + gapPx)
            val t = statusBarH + gapPx + row * (cellH + gapPx)
            val r = l + cellW
            val b = t + cellH

            log("  taskId=${task.taskId} -> [$l,$t][$r,$b]")
            RootManager.resizeTask(task.taskId, l, t, r, b)
        }

        log("Selesai. Koreksi ukuran: ${tasks.size} window.")
    }

    // ── Stop all ──────────────────────────────────────────────────────────────

    suspend fun stopAllRoblox(log: (String) -> Unit) = withContext(Dispatchers.IO) {
        val installs = detectRobloxInstalls()
        for (app in installs) {
            log("Stop ${app.packageName}…")
            RootManager.forceStop(app.packageName)
        }
        log("Stop semua.")
    }

    // ── App Cloner XML bounds patch ───────────────────────────────────────────
    // Original ZetsuLauncher patches App Cloner's shared_prefs XML to set
    // initial window position, preventing "app runs off screen" on first launch.

    suspend fun patchAppClonerBounds(
        gap: Int,
        gapRight: Int,
        log: (String) -> Unit,
    ) = withContext(Dispatchers.IO) {
        val installs = detectRobloxInstalls().filter { it.packageName != PKG_ROBLOX }
        if (installs.isEmpty()) { log("Tidak ada App Cloner install."); return@withContext }

        val screen = getScreenSize()
        val statusH = getStatusBarHeight()

        // Map: window number -> Rect(left,top,right,bottom)
        // Uses the "app_cloner_current_window_" key pattern
        val xmlTemplate = """<map>
<int name="app_cloner_current_window_left" value="$gap" />
<int name="app_cloner_current_window_top" value="${statusH + gap}" />
<int name="app_cloner_current_window_right" value="${screen.x / 2 - gapRight}" />
<int name="app_cloner_current_window_bottom" value="${screen.y - gap}" />
</map>"""

        for (app in installs) {
            val prefsDir = "/data/data/${app.packageName}/shared_prefs"
            val xmlFile  = "$prefsDir/${app.packageName}.rbxgrid.bak"

            val writeCmd = buildString {
                append("mkdir -p '$prefsDir'; ")
                append("[ -f '$xmlFile' ] || cp -p '$prefsDir/${app.packageName}.xml' '$xmlFile'; ")
                append("cat '$xmlFile' > /dev/null 2>/dev/null; ")
                // Write XML via echo — avoids need for text editor in shell
                append("printf '%s' '${xmlTemplate.replace("'", "'\\''")}' > '$prefsDir/${app.packageName}.xml'; ")
                append("sync")
            }

            val result = RootManager.runSu(writeCmd)
            when (result) {
                is ShellResult.Ok  -> log("XML patch OK: ${app.packageName}")
                is ShellResult.Err -> log("XML patch gagal (${app.packageName}): ${result.stderr}")
                ShellResult.NoSu   -> log("Tidak ada su untuk patch.")
            }
        }
    }

    // ── Freeform compatibility check ──────────────────────────────────────────

    fun isMultiWindowSupported(): Boolean =
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_ACTIVITIES_ON_SECONDARY_DISPLAYS) ||
        Build.VERSION.SDK_INT >= 26  // freeform available as developer setting since API 24

    /** Check current freeform settings. Requires root. */
    suspend fun checkFreeformStatus(): Map<String, String> =
        RootManager.readFreeformSettings()

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun getScreenSize(): Point {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return if (Build.VERSION.SDK_INT >= 30) {
            val bounds = wm.currentWindowMetrics.bounds
            Point(bounds.width(), bounds.height())
        } else {
            val metrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            wm.defaultDisplay.getRealMetrics(metrics)
            Point(metrics.widthPixels, metrics.heightPixels)
        }
    }

    private fun getStatusBarHeight(): Int {
        val resId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resId > 0) context.resources.getDimensionPixelSize(resId) else 0
    }

    /** Best column count for n windows (minimises empty cells, prefers landscape). */
    private fun bestColumns(n: Int): Int = when {
        n <= 1 -> 1
        n <= 2 -> 2
        n <= 4 -> 2
        n <= 6 -> 3
        else   -> ceil(sqrt(n.toDouble())).toInt()
    }

    /** Parse `am stack list` output into task descriptors. */
    private fun parseTasksFromStackList(output: String): List<GridTask> {
        val tasks = mutableListOf<GridTask>()
        var currentStack = -1
        val stackRegex = Regex("""^\s*(?:Stack|RootTask) id=(\d+)""")
        val taskRegex  = Regex("""^\s*taskId=(\d+):\s*(\S+)/""")
        val boundsRegex= Regex("""bounds=\[(-?\d+),(-?\d+)]\[(-?\d+),(-?\d+)]""")

        for (line in output.lines()) {
            stackRegex.find(line)?.let { currentStack = it.groupValues[1].toInt() }
            taskRegex.find(line)?.let { tm ->
                val taskId = tm.groupValues[1].toInt()
                val pkg    = tm.groupValues[2]
                val bounds = boundsRegex.find(line)?.let { bm ->
                    Rect(
                        bm.groupValues[1].toInt(), bm.groupValues[2].toInt(),
                        bm.groupValues[3].toInt(), bm.groupValues[4].toInt(),
                    )
                } ?: Rect(0, 0, 0, 0)
                tasks.add(GridTask(taskId, currentStack, pkg, bounds))
            }
        }
        return tasks
    }
}

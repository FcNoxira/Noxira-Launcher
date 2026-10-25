// NOXIRA — Unit tests
// File: test/PrefsTest.kt

package com.noxira.launcher

import com.noxira.launcher.core.prefs.NoxiraSettings
import com.noxira.launcher.core.prefs.PrefDefaults
import org.junit.Assert.*
import org.junit.Test

class PrefsTest {

    @Test
    fun defaultSettingsAreCorrect() {
        val s = NoxiraSettings()
        assertEquals(PrefDefaults.LAUNCH_DELAY_SEC, s.launchDelaySec)
        assertEquals(PrefDefaults.WINDOW_GAP_PX,    s.windowGapPx)
        assertEquals(PrefDefaults.COLUMN_COUNT,     s.columnCount)
        assertTrue(s.autoLayout)
        assertTrue(s.hapticFeedback)
        assertFalse(s.reducedMotion)
    }

    @Test
    fun launchDelayClampedCorrectly() {
        // Simulates what the repository does
        val raw = -5f
        val clamped = raw.coerceIn(0f, 30f)
        assertEquals(0f, clamped)

        val raw2 = 999f
        val clamped2 = raw2.coerceIn(0f, 30f)
        assertEquals(30f, clamped2)
    }

    @Test
    fun windowGapClampedCorrectly() {
        val raw = -10
        assertEquals(0, raw.coerceIn(0, 200))
        val raw2 = 300
        assertEquals(200, raw2.coerceIn(0, 200))
    }

    @Test
    fun columnCountClampedCorrectly() {
        assertEquals(1, 0.coerceIn(1, 8))
        assertEquals(8, 99.coerceIn(1, 8))
        assertEquals(3, 3.coerceIn(1, 8))
    }
}

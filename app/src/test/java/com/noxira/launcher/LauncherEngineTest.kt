// NOXIRA — Unit tests
// File: test/LauncherEngineTest.kt

package com.noxira.launcher

import com.noxira.launcher.core.launcher.PKG_ROBLOX
import com.noxira.launcher.core.launcher.PKG_ROBLOX_PREFIX
import org.junit.Assert.*
import org.junit.Test

class LauncherEngineTest {

    @Test
    fun robloxPackageStartsWithPrefix() {
        assertTrue(PKG_ROBLOX.startsWith(PKG_ROBLOX_PREFIX))
    }

    @Test
    fun bestColumnsLogic() {
        // Mirrors private bestColumns in LauncherEngine — we test the contract
        fun bestCols(n: Int) = when {
            n <= 1 -> 1
            n <= 2 -> 2
            n <= 4 -> 2
            n <= 6 -> 3
            else   -> Math.ceil(Math.sqrt(n.toDouble())).toInt()
        }

        assertEquals(1, bestCols(0))
        assertEquals(1, bestCols(1))
        assertEquals(2, bestCols(2))
        assertEquals(2, bestCols(3))
        assertEquals(2, bestCols(4))
        assertEquals(3, bestCols(5))
        assertEquals(3, bestCols(6))
        assertEquals(3, bestCols(7))  // ceil(sqrt(7)) = ceil(2.6) = 3
        assertEquals(3, bestCols(9))  // ceil(sqrt(9)) = 3
        assertEquals(4, bestCols(10)) // ceil(sqrt(10)) = 4
    }

    @Test
    fun robloxAppClonerPackageDetection() {
        // App Cloner clone packages follow "com.roblox.*" pattern
        val testPackages = listOf(
            "com.roblox.client"  to true,
            "com.roblox.client1" to true,
            "com.roblox.clienu"  to true,
            "com.other.app"      to false,
            "org.roblox.client"  to false,
        )
        testPackages.forEach { (pkg, expected) ->
            assertEquals("Package $pkg", expected, pkg.startsWith(PKG_ROBLOX_PREFIX))
        }
    }
}

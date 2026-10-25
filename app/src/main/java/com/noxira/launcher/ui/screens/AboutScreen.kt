// NOXIRA — Premium Roblox Launcher
// File: ui/screens/AboutScreen.kt

package com.noxira.launcher.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.noxira.launcher.ui.components.*
import com.noxira.launcher.ui.theme.*

@Composable
fun AboutScreen(onBack: () -> Unit) {
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
            Text("About NOXIRA", style = NoxiraType.ScreenTitle)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = NoxiraSpacing.md, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(NoxiraSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(NoxiraSpacing.lg))

            NoxiraEmblem(size = 72.dp, animate = true)

            Spacer(Modifier.height(NoxiraSpacing.md))

            Text("NOXIRA", style = NoxiraType.Wordmark)
            Text("PLAY • FASTER • SMOOTHER", style = NoxiraType.TagLine)

            Spacer(Modifier.height(NoxiraSpacing.sm))
            GlassDivider(Modifier.fillMaxWidth(0.4f))
            Spacer(Modifier.height(NoxiraSpacing.sm))

            SectionHeader("Informasi Aplikasi", Modifier.fillMaxWidth())
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                InfoRow("Versi", "1.0.0")
                InfoRow("Pengembang", "Muhammad Nur Fazrullah")
                InfoRow("Target SDK", "Android 35 (API 35)")
                InfoRow("Min SDK", "Android 10 (API 29)")
                InfoRow("Lisensi", "Proprietary")
            }

            SectionHeader("Fitur Utama", Modifier.fillMaxWidth())
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                listOf(
                    "Deteksi otomatis Roblox & App Cloner",
                    "Launch freeform via root am start",
                    "Grid tiling otomatis & manual",
                    "Setup freeform via settings put global",
                    "Stop semua task Roblox",
                    "Patch XML App Cloner bounds",
                    "Diagnostik lengkap",
                    "Preferensi persisten via DataStore",
                ).forEach {
                    Text("• $it", style = NoxiraType.Body, modifier = Modifier.padding(vertical = 2.dp))
                }
            }

            SectionHeader("Privasi", Modifier.fillMaxWidth())
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "NOXIRA tidak mengumpulkan, menyimpan, atau mengirimkan data pengguna ke " +
                    "server eksternal. Tidak ada kredensial Roblox yang diminta atau disimpan. " +
                    "Semua operasi shell dijalankan secara lokal dan terbatas pada fungsi " +
                    "launcher yang terdokumentasi.",
                    style = NoxiraType.Body,
                )
            }

            SectionHeader("Keterbatasan", Modifier.fillMaxWidth())
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                listOf(
                    "Membutuhkan root untuk freeform grid.",
                    "Freeform membutuhkan reboot setelah setup.",
                    "Tidak semua device mendukung windowing mode 5.",
                    "Offset task ID bervariasi per build Android.",
                    "App Cloner XML patch spesifik untuk versi tertentu.",
                ).forEach {
                    Text("• $it", style = NoxiraType.BodySmall, modifier = Modifier.padding(vertical = 2.dp))
                }
            }

            SectionHeader("Acknowledgment", Modifier.fillMaxWidth())
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Diinspirasi oleh ZetsuLauncher (com.zetsu.rbxgrid). " +
                    "NOXIRA merupakan reimplementasi independen dengan arsitektur Kotlin/Compose " +
                    "yang baru dan desain visual yang sepenuhnya berbeda.",
                    style = NoxiraType.Body,
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = NoxiraType.BodySmall)
        Text(value, style = NoxiraType.Body, color = NoxiraColors.Silver)
    }
}

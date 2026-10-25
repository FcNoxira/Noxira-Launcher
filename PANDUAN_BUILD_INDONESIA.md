# NOXIRA — Panduan Membuat APK di Windows

Proyek ini adalah aplikasi Android native (Kotlin + Jetpack Compose) untuk membantu membuka dan mengatur beberapa jendela Roblox. File APK belum disertakan karena lingkungan pembuatan saat ini tidak memiliki Android SDK/Gradle yang diperlukan.

## Membuat APK

1. Instal **Android Studio** versi stabil dari situs resmi: https://developer.android.com/studio
2. Ekstrak folder `NOXIRA` dari ZIP ini ke lokasi yang mudah ditemukan.
3. Buka Android Studio → **Open** → pilih folder `NOXIRA` (folder yang berisi `settings.gradle.kts`).
4. Tunggu Gradle Sync selesai. Jika diminta memasang Android SDK Platform 35 atau Build Tools, pilih **Install**.
5. Untuk membuat APK: menu **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
6. Setelah selesai, tekan **locate** pada notifikasi Android Studio. Biasanya APK debug ada di `app\build\outputs\apk\debug\app-debug.apk`.
7. Pindahkan APK ke ponsel Android dan buka untuk memasang. Jika diminta, izinkan instalasi aplikasi dari sumber tersebut.

## Syarat dan batasan

- Aplikasi memerlukan Android 10 (API 29) atau lebih baru.
- Membuka Roblox biasa tidak memerlukan root; fitur grid/freeform dan beberapa tindakan pengelolaan jendela memerlukan perangkat yang mendukung freeform dan akses root.
- Aktifkan **Developer options → Freeform windows** jika tersedia; beberapa perangkat memerlukan reboot. Dukungan tergantung merek/versi Android.
- Fitur root belum bisa diverifikasi tanpa uji di perangkat Android yang sesuai. Jangan memberikan akses root jika tidak memahami perintah yang dijalankan.

## Paket aplikasi

- Nama: NOXIRA
- Application ID: `com.noxira.launcher`
- Versi: `1.0.0`
- Minimum Android: Android 10 / API 29

Ini adalah paket sumber proyek untuk dibangun di Android Studio; bukan APK yang sudah terpasang atau ditandatangani untuk rilis.

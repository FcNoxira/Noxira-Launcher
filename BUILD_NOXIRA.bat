@echo off
setlocal
cd /d "%~dp0"
where gradle >nul 2>nul
if errorlevel 1 (
  echo Gradle tidak ditemukan di PATH.
  echo Buka folder ini melalui Android Studio, tunggu Gradle Sync,
  echo lalu pilih Build ^> Build Bundle(s) / APK(s) ^> Build APK(s).
  pause
  exit /b 1
)
gradle assembleDebug
if errorlevel 1 (
  echo Build gagal. Periksa pesan error di atas dan pastikan Android SDK sudah terpasang.
  pause
  exit /b 1
)
echo.
echo APK tersedia di app\build\outputs\apk\debug\app-debug.apk
pause

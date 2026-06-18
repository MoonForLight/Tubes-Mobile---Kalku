# Kalku — Penentu Harga Jual Produk untuk UMKM

Kalku adalah aplikasi Android lokal untuk membantu pelaku UMKM mencatat produk, menghitung modal, menentukan target keuntungan, memperoleh rekomendasi harga jual, dan memantau riwayat perhitungan.

## Fitur

- Splash screen dan onboarding.
- Register, login, session, logout, dan reset password dengan verifikasi nama usaha.
- Password disimpan menggunakan PBKDF2; akun lama dengan password teks biasa dimigrasikan saat login.
- Dashboard: produk aktif, stok rendah, estimasi profit, grafik profit 7 hari, dan perhitungan terbaru.
- Produk: tambah, edit, hapus, foto produk, kategori, status aktif, stok rendah, pencarian, filter, dan pengurutan.
- Kalkulator: biaya produksi, biaya operasional, jumlah, margin 0–100%, ringkasan langsung, hasil, simpan, dan bagikan.
- Riwayat: pencarian, filter tanggal, rentang tanggal khusus, detail, hitung ulang, hapus, dan ekspor CSV.
- Profil bisnis: foto, nama, usaha, email, telepon, alamat, deskripsi, statistik, pengaturan, dan hapus akun.
- Mode terang/gelap.
- Room Database dengan migrasi tanpa menghapus data lama.
- Unit test rumus dan hashing password.
- GitHub Actions untuk unit test dan debug build.

## Teknologi

- Kotlin
- Android XML + View Binding
- Room Database
- Coroutines
- Material Components

## Menjalankan project

1. Buka project melalui Android Studio.
2. Pilih **Embedded JDK** sebagai Gradle JDK.
3. Jalankan **Sync Project with Gradle Files**.
4. Jalankan emulator atau perangkat Android API 24 ke atas.

Pengujian terminal:

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
```

APK debug dihasilkan di:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Rumus

```text
Total modal = biaya produksi + biaya operasional
Modal per produk = total modal / jumlah produk
Harga jual = modal per produk × (1 + persentase keuntungan)
Total profit = total pendapatan - total modal
```

Pembulatan harga dilakukan ke atas agar seluruh modal tetap tertutup.

## Struktur package

```text
com.example.kalku
├── calculator
├── data/local
├── history
├── home
├── login
├── onboarding
├── product
├── profile
├── register
├── splash
└── utils
```

## Catatan

- Data disimpan lokal pada perangkat.
- Login Google tidak disertakan karena memerlukan project Firebase dan OAuth Client ID milik kelompok.
- Sebelum menghapus aplikasi/data, ekspor riwayat ke CSV sebagai cadangan.

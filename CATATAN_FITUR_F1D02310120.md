# Fitur branch F1D02310120

Bagian yang ditambahkan:

1. Splash Screen
2. Onboarding tiga halaman
3. Kalkulator harga jual
4. Halaman hasil perhitungan
5. Format mata uang Rupiah
6. Simpan hasil ke Room Database
7. Bagikan hasil melalui aplikasi lain
8. Session pengguna sederhana untuk menghubungkan login dengan perhitungan dan riwayat

## Alur aplikasi

Splash → Onboarding (hanya pertama kali) → Login/Register → Main sementara → Kalkulator → Hasil.

`MainActivity` saat ini hanya menjadi halaman penghubung sementara. Ketika Home milik Abdi digabungkan, pertahankan navigasi menuju:

```kotlin
startActivity(Intent(this, CalculatorActivity::class.java))
```

## Kontrak data untuk fitur Riwayat

Hasil disimpan pada tabel Room `calculations` melalui:

- `CalculationEntity.kt`
- `CalculationDao.kt`
- `AppDatabase.calculationDao()`

Riwayat milik Zahra dapat membaca data dengan:

```kotlin
val userId = SessionManager(context).getUserId()
val data = AppDatabase.getDatabase(context)
    .calculationDao()
    .getCalculationsByUser(userId)
```

## Cara melihat onboarding lagi

Hapus data aplikasi dari emulator atau uninstall lalu jalankan ulang aplikasi.

## Contoh hasil rumus

Biaya produksi Rp75.000 + operasional Rp25.000, jumlah 10, keuntungan 25%:

- Total biaya: Rp100.000
- Modal per produk: Rp10.000
- Profit per produk: Rp2.500
- Harga jual: Rp12.500
- Total profit: Rp25.000

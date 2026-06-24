# Bagian Zahra - Kalkulator dan Riwayat

Sumber kode diambil dari branch `origin/F1D02310120`, sesuai instruksi pada `percakapan_kalku_chatgpt.pdf`.

## Bagian yang dikerjakan Zahra

Zahra mengerjakan fitur kalkulator dan riwayat:

- Pilih produk.
- Masukkan biaya produksi.
- Masukkan biaya operasional.
- Masukkan jumlah produk.
- Masukkan target keuntungan.
- Hitung harga jual.
- Tampilkan hasil perhitungan.
- Simpan hasil ke riwayat.
- Lihat detail riwayat.
- Filter riwayat.
- Ekspor riwayat.

## File Kotlin

```text
app/src/main/java/com/example/kalku/calculator/
app/src/main/java/com/example/kalku/history/
```

Isi penting:

```text
CalculationHelper.kt
CalculatorActivity.kt
CalculationResultActivity.kt
HistoryFragment.kt
HistoryDetailActivity.kt
```

## Database Perhitungan

```text
app/src/main/java/com/example/kalku/data/local/CalculationEntity.kt
app/src/main/java/com/example/kalku/data/local/CalculationDao.kt
```

Catatan: sesuai instruksi, `AppDatabase.kt` tidak dimasukkan karena bagian itu menjadi tanggung jawab integrasi Aldi.

## Layout

```text
app/src/main/res/layout/activity_calculator.xml
app/src/main/res/layout/activity_calculation_result.xml
app/src/main/res/layout/fragment_history.xml
app/src/main/res/layout/activity_history_detail.xml
app/src/main/res/layout/item_history.xml
```

## Drawable

```text
app/src/main/res/drawable/ic_export.xml
```

## Test

```text
app/src/test/java/com/example/kalku/calculator/CalculationHelperTest.kt
```

## Tidak termasuk bagian Zahra

File berikut tidak dimasukkan karena di instruksi PDF termasuk bagian Aldi atau resource bersama:

- `AppDatabase.kt`
- `MainActivity.kt`
- `KalkuApplication.kt`
- `AndroidManifest.xml`
- `activity_main.xml`
- `menu_bottom_navigation.xml`
- `build.gradle.kts`
- `gradle.properties`
- resource `values/`

Folder ini dibuat hanya untuk menunjukkan bagian kerja Zahra. Agar aplikasi bisa berjalan, file ini tetap perlu digabung ke project utama oleh bagian integrasi.

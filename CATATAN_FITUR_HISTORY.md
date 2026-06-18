# Fitur History Kalku

Fitur History dibuat di atas progress `F1D02310120` karena progress tersebut sudah memiliki fitur login, calculator, result, SessionManager, Room Database, dan tabel `calculations`.

## Struktur file yang ditambahkan

```text
app/src/main/java/com/example/kalku/history/
├── HistoryActivity.kt
└── HistoryAdapter.kt

app/src/main/res/layout/
├── activity_history.xml
└── item_history.xml

app/src/main/res/drawable/
├── bg_chip_history_selected.xml
├── bg_chip_history_unselected.xml
├── bg_search_history.xml
├── bg_history_detail_box.xml
└── bg_empty_history.xml
```

## File yang diubah

```text
app/src/main/java/com/example/kalku/data/local/CalculationDao.kt
app/src/main/java/com/example/kalku/MainActivity.kt
app/src/main/res/layout/activity_main.xml
app/src/main/AndroidManifest.xml
app/build.gradle.kts
```

## Alur fitur

1. User menghitung harga jual di Calculator.
2. User melihat hasil di Calculation Result.
3. User menekan tombol Save Result.
4. Data tersimpan ke tabel `calculations` berdasarkan `userId` yang sedang login.
5. User membuka tombol `Lihat Riwayat Perhitungan` dari MainActivity.
6. HistoryActivity mengambil data dari `CalculationDao` lalu menampilkannya melalui RecyclerView.
7. User dapat mencari riwayat berdasarkan nama produk dan memfilter berdasarkan All Time, This Month, atau Last 3 Months.
8. User juga bisa menghapus item riwayat tertentu.

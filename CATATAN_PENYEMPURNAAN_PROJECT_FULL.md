# Penyempurnaan Project Full — F1D02310120

Revisi ini memperlakukan branch F1D02310120 sebagai basis aplikasi utama.

## Penyempurnaan yang ditambahkan

1. Password PBKDF2 dan migrasi otomatis password lama ketika login.
2. Verifikasi email + nama usaha pada reset password.
3. Database versi 4:
   - profil bisnis lengkap;
   - foto produk dan foto profil;
   - status produk dan batas stok rendah;
   - relasi logis `productId` pada riwayat;
   - indeks email, user, kategori, dan tanggal.
4. Dashboard dengan grafik profit 7 hari dan statistik stok rendah.
5. Produk dengan foto, status aktif/habis/rendah/nonaktif, filter, dan sorting.
6. Kalkulator dengan margin maksimal 100%, pembulatan modal yang aman, dan preview langsung.
7. Riwayat dengan filter 7/30/90 hari, rentang tanggal, dan ekspor CSV.
8. Profil dengan foto, telepon, alamat, deskripsi, mode gelap, dan hapus akun.
9. GitHub Actions untuk test serta build debug.
10. Unit test untuk rumus dan hashing password.

## Yang memerlukan konfigurasi eksternal

Google Sign-In tidak bisa diaktifkan hanya dari source code karena membutuhkan Firebase project, SHA-1 aplikasi, OAuth Client ID, dan file `google-services.json` milik kelompok.

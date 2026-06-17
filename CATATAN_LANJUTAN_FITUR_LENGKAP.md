# Penyelesaian Fitur Branch F1D02310120

Fitur yang ditambahkan pada revisi ini:

- Home dashboard dengan jumlah produk, estimasi profit, dan perhitungan terbaru.
- Bottom navigation: Home, Produk, Riwayat, dan Profil.
- Kelola produk berbasis Room: tambah, cari, edit, hapus, dan hitung ulang.
- Riwayat perhitungan: pencarian, detail, hapus, dan hitung ulang.
- Profil: tampil data pengguna, edit profil, statistik, bantuan, dan logout.
- Reset password lokal berdasarkan email terdaftar.
- Integrasi kalkulator dengan data produk dan riwayat.
- Database Room versi 3 dengan tabel `products` dan migrasi 2 ke 3.
- Splash langsung membuka MainActivity ketika pengguna masih login.

Catatan integrasi:

- Package tetap `com.example.kalku`.
- Data disimpan lokal melalui Room Database.
- Password masih disimpan sebagai teks biasa karena mengikuti implementasi awal tugas. Untuk aplikasi produksi harus menggunakan autentikasi dan hashing yang aman.

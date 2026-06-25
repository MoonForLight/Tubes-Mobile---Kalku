package com.example.kalku.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.kalku.R
import com.example.kalku.data.local.AppDatabase
import com.example.kalku.databinding.FragmentProfileBinding
import com.example.kalku.login.LoginActivity
import com.example.kalku.utils.CurrencyUtils
import com.example.kalku.utils.SessionManager
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnEditProfile.setOnClickListener { startActivity(Intent(requireContext(), EditProfileActivity::class.java)) }
        binding.btnAppSettings.setOnClickListener { startActivity(Intent(requireContext(), AppSettingsActivity::class.java)) }
        binding.btnHelp.setOnClickListener { showHelp() }
        binding.btnLogout.setOnClickListener { confirmLogout() }
        binding.btnDeleteAccount.setOnClickListener { confirmDeleteAccount() }
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
    }

    private fun loadProfile() {
        val session = SessionManager(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            val database = AppDatabase.getDatabase(requireContext())
            val user = database.userDao().getUserById(session.getUserId())
            if (user != null) session.updateProfile(user)

            binding.tvName.text = session.getFullName()
            binding.tvBusinessName.text = session.getBusinessName()
            binding.tvEmail.text = session.getEmail()
            binding.tvPhone.text = session.getPhone().ifBlank { "Nomor telepon belum diisi" }
            binding.tvAddress.text = session.getAddress().ifBlank { "Alamat usaha belum diisi" }
            binding.tvInitials.text = initials(session.getFullName())

            if (session.getPhotoUri().isNotBlank()) {
                binding.ivProfilePhoto.visibility = View.VISIBLE
                binding.tvInitials.visibility = View.GONE
                runCatching { binding.ivProfilePhoto.setImageURI(Uri.parse(session.getPhotoUri())) }
                    .onFailure { binding.ivProfilePhoto.setImageResource(R.drawable.ic_person) }
            } else {
                binding.ivProfilePhoto.visibility = View.GONE
                binding.tvInitials.visibility = View.VISIBLE
            }

            val userId = session.getUserId()
            binding.tvTotalProfit.text = CurrencyUtils.formatRupiah(
                database.calculationDao().getTotalProfitByUser(userId)
            )
//            binding.tvProductCount.text = database.calculationDao().countActiveProducts(userId).toString()
        }
    }

    private fun showHelp() {
        AlertDialog.Builder(requireContext())
            .setTitle("Pusat Bantuan")
            .setMessage(
                "1. Tambahkan produk dan biaya melalui menu Produk.\n" +
                    "2. Gunakan Kalkulator untuk menentukan harga jual.\n" +
                    "3. Simpan hasil agar muncul di Riwayat dan grafik Home.\n" +
                    "4. Riwayat dapat difilter serta diekspor ke CSV."
            )
            .setPositiveButton("Mengerti", null)
            .show()
    }

    private fun initials(name: String): String = name.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "U" }

    private fun confirmLogout() {
        AlertDialog.Builder(requireContext())
            .setTitle("Keluar dari akun")
            .setMessage("Anda yakin ingin logout?")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Logout") { _, _ -> logout() }
            .show()
    }

    private fun confirmDeleteAccount() {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus akun dan data")
            .setMessage("Semua produk dan riwayat akun ini akan dihapus permanen. Tindakan ini tidak dapat dibatalkan.")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Lanjutkan") { _, _ ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Konfirmasi terakhir")
                    .setMessage("Hapus akun sekarang?")
                    .setNegativeButton("Tidak", null)
                    .setPositiveButton("Hapus Permanen") { _, _ -> deleteAccount() }
                    .show()
            }
            .show()
    }

    private fun deleteAccount() {
        viewLifecycleOwner.lifecycleScope.launch {
            val session = SessionManager(requireContext())
            val database = AppDatabase.getDatabase(requireContext())
            val user = database.userDao().getUserById(session.getUserId())
            if (user == null) {
                Toast.makeText(requireContext(), "Akun tidak ditemukan", Toast.LENGTH_SHORT).show()
                return@launch
            }
            database.calculationDao().deleteByUser(user.id)
            database.userDao().deleteUser(user)
            logout()
        }
    }

    private fun logout() {
        SessionManager(requireContext()).logout()
        startActivity(Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

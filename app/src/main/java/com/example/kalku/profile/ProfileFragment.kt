package com.example.kalku.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.kalku.data.local.AppDatabase
import com.example.kalku.databinding.FragmentProfileBinding
import com.example.kalku.login.LoginActivity
import com.example.kalku.utils.CurrencyUtils
import com.example.kalku.utils.SessionManager
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }
        binding.btnAppSettings.setOnClickListener {
            Toast.makeText(requireContext(), "Pengaturan aplikasi menggunakan pengaturan perangkat", Toast.LENGTH_SHORT).show()
        }
        binding.btnHelp.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Pusat Bantuan")
                .setMessage("Kalku membantu menghitung harga jual, menyimpan produk, dan mencatat riwayat perhitungan UMKM.")
                .setPositiveButton("Mengerti", null)
                .show()
        }
        binding.btnLogout.setOnClickListener { confirmLogout() }
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
    }

    private fun loadProfile() {
        val session = SessionManager(requireContext())
        binding.tvName.text = session.getFullName()
        binding.tvBusinessName.text = session.getBusinessName()
        binding.tvEmail.text = session.getEmail()
        binding.tvInitials.text = initials(session.getFullName())

        viewLifecycleOwner.lifecycleScope.launch {
            val database = AppDatabase.getDatabase(requireContext())
            val userId = session.getUserId()
            binding.tvTotalProfit.text = CurrencyUtils.formatRupiah(
                database.calculationDao().getTotalProfitByUser(userId)
            )
            binding.tvProductCount.text = database.productDao().countProducts(userId).toString()
        }
    }

    private fun initials(name: String): String {
        return name.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
            .ifBlank { "U" }
    }

    private fun confirmLogout() {
        AlertDialog.Builder(requireContext())
            .setTitle("Keluar dari akun")
            .setMessage("Anda yakin ingin logout?")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Logout") { _, _ ->
                SessionManager(requireContext()).logout()
                startActivity(Intent(requireContext(), LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

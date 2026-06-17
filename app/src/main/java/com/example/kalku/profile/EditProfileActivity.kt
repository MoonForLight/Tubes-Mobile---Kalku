package com.example.kalku.profile

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kalku.data.local.AppDatabase
import com.example.kalku.databinding.ActivityEditProfileBinding
import com.example.kalku.utils.SessionManager
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnSave.setOnClickListener { saveProfile() }
        loadProfile()
    }

    private fun loadProfile() {
        val session = SessionManager(this)
        binding.etFullName.setText(session.getFullName())
        binding.etBusinessName.setText(session.getBusinessName())
        binding.etEmail.setText(session.getEmail())
    }

    private fun saveProfile() {
        val fullName = binding.etFullName.text.toString().trim()
        val businessName = binding.etBusinessName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val session = SessionManager(this)

        when {
            fullName.isBlank() -> binding.etFullName.error = "Nama harus diisi"
            businessName.isBlank() -> binding.etBusinessName.error = "Nama usaha harus diisi"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> binding.etEmail.error = "Email tidak valid"
            else -> lifecycleScope.launch {
                val dao = AppDatabase.getDatabase(this@EditProfileActivity).userDao()
                if (dao.getOtherUserByEmail(email, session.getUserId()) != null) {
                    binding.etEmail.error = "Email sudah digunakan"
                    return@launch
                }

                val user = dao.getUserById(session.getUserId())
                if (user == null) {
                    Toast.makeText(this@EditProfileActivity, "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                dao.updateUser(user.copy(
                    fullName = fullName,
                    businessName = businessName,
                    email = email
                ))
                session.updateProfile(fullName, businessName, email)
                Toast.makeText(this@EditProfileActivity, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}

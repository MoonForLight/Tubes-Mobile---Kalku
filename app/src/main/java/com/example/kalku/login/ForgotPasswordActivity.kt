package com.example.kalku.login

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kalku.data.local.AppDatabase
import com.example.kalku.databinding.ActivityForgotPasswordBinding
import com.example.kalku.utils.PasswordUtils
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnResetPassword.setOnClickListener { resetPassword() }
    }

    private fun resetPassword() {
        val email = binding.etEmail.text.toString().trim().lowercase()
        val businessName = binding.etBusinessName.text.toString().trim()
        val password = binding.etNewPassword.text.toString()
        val confirmation = binding.etConfirmPassword.text.toString()

        when {
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> binding.etEmail.error = "Format email tidak valid"
            businessName.isBlank() -> binding.etBusinessName.error = "Masukkan nama usaha sebagai verifikasi"
            password.length < 8 || !password.any(Char::isLetter) || !password.any(Char::isDigit) -> {
                binding.etNewPassword.error = "Minimal 8 karakter, gunakan huruf dan angka"
            }
            password != confirmation -> binding.etConfirmPassword.error = "Konfirmasi password tidak cocok"
            else -> lifecycleScope.launch {
                val userDao = AppDatabase.getDatabase(this@ForgotPasswordActivity).userDao()
                val user = userDao.getUserByEmail(email)
                if (user == null || !user.businessName.equals(businessName, ignoreCase = true)) {
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Email atau nama usaha tidak sesuai",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                userDao.updatePassword(email, PasswordUtils.hash(password))
                Toast.makeText(this@ForgotPasswordActivity, "Password berhasil diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}

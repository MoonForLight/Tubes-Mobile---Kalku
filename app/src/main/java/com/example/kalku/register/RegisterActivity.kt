package com.example.kalku.register

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kalku.data.local.AppDatabase
import com.example.kalku.data.local.UserEntity
import com.example.kalku.databinding.ActivityRegisterBinding
import com.example.kalku.login.LoginActivity
import com.example.kalku.utils.PasswordUtils
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var passwordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivClose.setOnClickListener { finish() }
        binding.tvSignIn.setOnClickListener { openLogin() }
        binding.ivTogglePassword.setOnClickListener { togglePassword() }
        binding.btnCreateAccount.setOnClickListener { register() }
    }

    private fun togglePassword() {
        passwordVisible = !passwordVisible
        val method = if (passwordVisible) {
            HideReturnsTransformationMethod.getInstance()
        } else {
            PasswordTransformationMethod.getInstance()
        }
        binding.etPassword.transformationMethod = method
        binding.etConfirmPassword.transformationMethod = method
        binding.etPassword.setSelection(binding.etPassword.text.length)
        binding.etConfirmPassword.setSelection(binding.etConfirmPassword.text.length)
    }

    private fun register() {
        val fullName = binding.etFullName.text.toString().trim()
        val businessName = binding.etBusinessName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim().lowercase()
        val password = binding.etPassword.text.toString()
        val confirmation = binding.etConfirmPassword.text.toString()

        when {
            fullName.length < 3 -> binding.etFullName.error = "Nama lengkap minimal 3 karakter"
            businessName.length < 2 -> binding.etBusinessName.error = "Nama usaha harus diisi"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> binding.etEmail.error = "Format email tidak valid"
            !isStrongPassword(password) -> binding.etPassword.error = "Minimal 8 karakter, gunakan huruf dan angka"
            password != confirmation -> binding.etConfirmPassword.error = "Konfirmasi password tidak cocok"
            !binding.cbTerms.isChecked -> Toast.makeText(this, "Setujui syarat dan ketentuan terlebih dahulu", Toast.LENGTH_SHORT).show()
            else -> lifecycleScope.launch {
                val userDao = AppDatabase.getDatabase(this@RegisterActivity).userDao()
                if (userDao.getUserByEmail(email) != null) {
                    binding.etEmail.error = "Email sudah terdaftar"
                    return@launch
                }

                userDao.registerUser(
                    UserEntity(
                        fullName = fullName,
                        businessName = businessName,
                        email = email,
                        password = PasswordUtils.hash(password)
                    )
                )
                Toast.makeText(this@RegisterActivity, "Registrasi berhasil. Silakan login.", Toast.LENGTH_LONG).show()
                openLogin()
            }
        }
    }

    private fun isStrongPassword(password: String): Boolean {
        return password.length >= 8 && password.any(Char::isLetter) && password.any(Char::isDigit)
    }

    private fun openLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}

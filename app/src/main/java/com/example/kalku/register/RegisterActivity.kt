package com.example.kalku.register

import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kalku.R
import com.example.kalku.data.local.AppDatabase
import com.example.kalku.data.local.UserEntity
import com.example.kalku.login.LoginActivity
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    // Deklarasi database Room
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inisialisasi Database Room
        database = AppDatabase.getDatabase(this)

        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etBusinessName = findViewById<EditText>(R.id.etBusinessName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val cbTerms = findViewById<CheckBox>(R.id.cbTerms)
        val btnCreateAccount = findViewById<RelativeLayout>(R.id.btnCreateAccount)
        val tvSignIn = findViewById<TextView>(R.id.tvSignIn)

        // Logika ketika Tombol "Create Account" diklik
        btnCreateAccount.setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val businessName = etBusinessName?.text.toString().trim()
            val email = etEmail?.text.toString().trim()
            val password = etPassword?.text.toString()
            val confirmPassword = etConfirmPassword?.text.toString()

            if (fullName.isEmpty() || businessName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Harap semua data diisi terlebih dahulu!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(
                    this,
                    "Password dan Konfirmasi Password tidak cocok!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (cbTerms == null || !cbTerms.isChecked) {
                Toast.makeText(
                    this,
                    "Anda harus menyetujui Syarat dan Ketentuan!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val userDao = database.userDao()
                val existingUser = userDao.getUserByEmail(email)
                if (existingUser != null) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Email sudah terdaftar! Gunakan email lain.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                val newUser = UserEntity(
                    fullName = fullName,
                    businessName = businessName,
                    email = email,
                    password = password
                )

                userDao.registerUser(newUser)
                Toast.makeText(
                    this@RegisterActivity,
                    "Registrasi Akun Bisnis Berhasil!",
                    Toast.LENGTH_LONG
                ).show()

                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // Logika ketika teks "Sign In" diklik
        tvSignIn?.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}



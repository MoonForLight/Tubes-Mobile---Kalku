package com.example.kalku.login

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kalku.MainActivity
import com.example.kalku.R
import com.example.kalku.databinding.ActivityLoginBinding
import com.example.kalku.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        // Logika ketika Tombol Login diklik
        binding.btnLinearLogin.setOnClickListener {
            val email = binding.etEmailLogin.text.toString().trim()
            val password = binding.etPasswordLogin.text.toString()
            viewModel.login(email, password)
        }

        // Toggle Password Visibility
        binding.ivTogglePasswordLogin.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.etPasswordLogin.transformationMethod = HideReturnsTransformationMethod.getInstance()
                // Gunakan ic_eye jika sudah ada, sementara tetap ic_eye_off atau tidak ganti gambar
                // binding.ivTogglePasswordLogin.setImageResource(R.drawable.ic_eye)
            } else {
                binding.etPasswordLogin.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.ivTogglePasswordLogin.setImageResource(R.drawable.ic_eye_off)
            }
            binding.etPasswordLogin.setSelection(binding.etPasswordLogin.text.length)
        }

        // Logika pindah ke halaman Register ketika teks Register diklik
        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Logika ketika Teks "Forgot?" diklik
        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Fitur Reset Password akan dikembangkan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is LoginResult.Success -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is LoginResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

// TEST PUSH



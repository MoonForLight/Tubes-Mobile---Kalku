package com.example.kalku

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kalku.calculator.CalculatorActivity
import com.example.kalku.databinding.ActivityMainBinding
import com.example.kalku.utils.SessionManager

/**
 * Halaman penghubung sementara setelah login.
 * Saat modul Home milik anggota lain digabung, tombol ini cukup diarahkan ke CalculatorActivity.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        binding.tvWelcome.text = "Halo, ${session.getFullName()}!"

        binding.btnOpenCalculator.setOnClickListener {
            startActivity(Intent(this, CalculatorActivity::class.java))
        }
    }
}

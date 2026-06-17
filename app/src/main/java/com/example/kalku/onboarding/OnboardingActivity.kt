package com.example.kalku.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kalku.R
import com.example.kalku.databinding.ActivityOnboardingBinding
import com.example.kalku.login.LoginActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private var currentPage = 0

    private val pages = listOf(
        OnboardingPage(
            icon = R.drawable.ic_storefront,
            title = "Hitung harga jual dengan mudah",
            description = "Masukkan seluruh biaya produksi, jumlah barang, dan target keuntungan Anda."
        ),
        OnboardingPage(
            icon = R.drawable.ic_wallet,
            title = "Keuangan usaha lebih terarah",
            description = "Kalku membantu memisahkan modal, keuntungan, dan rekomendasi harga per produk."
        ),
        OnboardingPage(
            icon = R.drawable.ic_chart_up,
            title = "Catatan bisnis lebih rapi",
            description = "Simpan hasil perhitungan agar dapat dipantau kembali melalui riwayat usaha."
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvSkip.setOnClickListener { finishOnboarding() }
        binding.btnNext.setOnClickListener {
            if (currentPage == pages.lastIndex) {
                finishOnboarding()
            } else {
                currentPage++
                renderPage()
            }
        }

        renderPage()
    }

    private fun renderPage() {
        val page = pages[currentPage]
        binding.ivOnboarding.setImageResource(page.icon)
        binding.tvOnboardingTitle.text = page.title
        binding.tvOnboardingDescription.text = page.description
        binding.btnNext.text = if (currentPage == pages.lastIndex) "Mulai Sekarang" else "Selanjutnya"

        val active = R.drawable.bg_dot_active
        val inactive = R.drawable.bg_dot_inactive
        binding.dotOne.setBackgroundResource(if (currentPage == 0) active else inactive)
        binding.dotTwo.setBackgroundResource(if (currentPage == 1) active else inactive)
        binding.dotThree.setBackgroundResource(if (currentPage == 2) active else inactive)
    }

    private fun finishOnboarding() {
        getSharedPreferences(PREF_NAME, MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ONBOARDING_DONE, true)
            .apply()

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private data class OnboardingPage(
        val icon: Int,
        val title: String,
        val description: String
    )

    companion object {
        const val PREF_NAME = "kalku_onboarding"
        const val KEY_ONBOARDING_DONE = "onboarding_done"
    }
}

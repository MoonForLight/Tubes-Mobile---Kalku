package com.example.kalku.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kalku.databinding.ActivityAppSettingsBinding
import com.example.kalku.utils.AppSettingsManager

class AppSettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAppSettingsBinding
    private lateinit var settings: AppSettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        settings = AppSettingsManager(this)

        binding.btnBack.setOnClickListener { finish() }
        binding.switchDarkMode.isChecked = settings.isDarkMode()
        binding.switchReminder.isChecked = settings.isReminderEnabled()

        binding.switchDarkMode.setOnCheckedChangeListener { _, enabled ->
            if (enabled != settings.isDarkMode()) settings.setDarkMode(enabled)
        }
        binding.switchReminder.setOnCheckedChangeListener { _, enabled ->
            settings.setReminderEnabled(enabled)
        }
    }
}

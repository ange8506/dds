package com.dds.assistant

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.dds.assistant.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        binding.btnOpenAccessibility.setOnClickListener {
            openAccessibilitySettings()
        }

        binding.btnCheckStatus.setOnClickListener {
            updateServiceStatus()
        }

        updateServiceStatus()
    }

    private fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }

    private fun updateServiceStatus() {
        val isEnabled = isAccessibilityServiceEnabled()
        binding.tvServiceStatus.text = if (isEnabled) {
            "✓ 无障碍服务已启用"
        } else {
            "✗ 无障碍服务未启用"
        }
        
        binding.tvServiceStatus.setTextColor(
            if (isEnabled) getColor(android.R.color.holo_green_dark)
            else getColor(android.R.color.holo_red_dark)
        )
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val service = "${packageName}/${WhackMoleAccessibilityService::class.java.name}"
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return enabledServices?.contains(service) == true
    }

    override fun onResume() {
        super.onResume()
        updateServiceStatus()
    }
}


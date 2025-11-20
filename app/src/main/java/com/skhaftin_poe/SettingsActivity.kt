package com.skhaftin_poe

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.skhaftin_poe.databinding.ActivitySettingsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var toneGenerator: ToneGenerator
    private lateinit var vibrator: Vibrator
    private var notificationPreferences = NotificationPreferences()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize tone generator for system beep
        toneGenerator = ToneGenerator(AudioManager.STREAM_SYSTEM, 100)

        // Initialize vibrator with API check
        vibrator = getSystemService(Vibrator::class.java)

        setupClickListeners()
        loadCurrentSettings()
        updateLanguageUI()

        binding.backButton.setOnClickListener {
            navigateToMainActivity()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh UI when returning to settings
        updateLanguageUI()
        loadCurrentSettings()
    }

    private fun updateLanguageUI() {
        // Update the current language display with proper formatting
        val currentLanguageName = LocaleHelper.getCurrentLanguageName(this)
        binding.currentLanguageText.text = getString(R.string.current_language, currentLanguageName)

        // Update biometric status with proper string resources
        updateBiometricStatusText()

        // Update all text views to reflect current language
        updateAllTexts()
    }

    private fun updateAllTexts() {
        // Update button texts
        binding.languageButton.text = getString(R.string.change_language)
        binding.saveButton.text = getString(R.string.save_settings)
        binding.testNotificationButton.text = getString(R.string.send_test_notification)

        // Update section titles
        binding.root.findViewById<android.widget.TextView>(R.id.textView4)?.text = getString(R.string.language_settings)
        binding.root.findViewById<android.widget.TextView>(R.id.textView5)?.text = getString(R.string.sound_settings)
        binding.root.findViewById<android.widget.TextView>(R.id.textView6)?.text = getString(R.string.security)
        binding.root.findViewById<android.widget.TextView>(R.id.textView7)?.text = getString(R.string.notification_settings)

        // Update switch labels
        binding.root.findViewById<android.widget.TextView>(R.id.textView8)?.text = getString(R.string.sound_effects)
        binding.root.findViewById<android.widget.TextView>(R.id.textView9)?.text = getString(R.string.vibration)
        binding.root.findViewById<android.widget.TextView>(R.id.textView10)?.text = getString(R.string.biometric_login)
        binding.root.findViewById<android.widget.TextView>(R.id.textView11)?.text = getString(R.string.daily_reminders)
        binding.root.findViewById<android.widget.TextView>(R.id.textView12)?.text = getString(R.string.food_updates)
        binding.root.findViewById<android.widget.TextView>(R.id.textView13)?.text = getString(R.string.test_notifications)
    }

    private fun updateBiometricStatusText() {
        val isBiometricAvailable = BiometricLogin.isBiometricAvailable(this)
        val isBiometricEnabled = SharedPrefs.isBiometricEnabled()

        val statusText = when {
            !isBiometricAvailable -> getString(R.string.biometric_hardware_unavailable)
            isBiometricEnabled -> getString(R.string.biometric_enabled)
            else -> getString(R.string.biometric_disabled)
        }

        binding.biometricStatusText.text = statusText
    }

    private fun loadCurrentSettings() {
        val settings = SharedPrefs.getSettings()
        notificationPreferences = SharedPrefs.getNotificationPreferences()

        binding.soundSwitch.isChecked = settings.soundEnabled
        binding.vibrationSwitch.isChecked = settings.vibrationEnabled

        // Load notification settings
        binding.notificationDailyRemindersSwitch.isChecked = notificationPreferences.dailyReminders
        binding.notificationFoodUpdatesSwitch.isChecked = notificationPreferences.foodUpdates
        binding.notificationTestSwitch.isChecked = notificationPreferences.testNotifications

        // Load biometric setting
        binding.biometricSwitch.isChecked = SharedPrefs.isBiometricEnabled()

        // Check biometric status
        checkBiometricStatus()
    }

    private fun setupClickListeners() {
        // Language change button
        binding.languageButton.setOnClickListener {
            showLanguageSelectionDialog()
        }

        binding.saveButton.setOnClickListener {
            saveSettings()
            playFeedback()
        }

        // Test sound when sound switch is toggled
        binding.soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                playSound()
            }
        }

        // Test vibration when vibration switch is toggled
        binding.vibrationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                vibrate()
            }
        }

        // Handle notification switches
        binding.notificationDailyRemindersSwitch.setOnCheckedChangeListener { _, isChecked ->
            notificationPreferences = notificationPreferences.copy(dailyReminders = isChecked)
        }

        binding.notificationFoodUpdatesSwitch.setOnCheckedChangeListener { _, isChecked ->
            notificationPreferences = notificationPreferences.copy(foodUpdates = isChecked)
        }

        binding.notificationTestSwitch.setOnCheckedChangeListener { _, isChecked ->
            notificationPreferences = notificationPreferences.copy(testNotifications = isChecked)
        }

        // Test notification button
        binding.testNotificationButton.setOnClickListener {
            sendTestNotification()
        }

        // Handle biometric switch changes
        binding.biometricSwitch.setOnCheckedChangeListener { _, isChecked ->
            onBiometricSettingChanged(isChecked)
        }
    }

    private fun onBiometricSettingChanged(isEnabled: Boolean) {
        if (isEnabled) {
            // User wants to enable biometrics - check if available
            if (BiometricLogin.isBiometricAvailable(this)) {
                SharedPrefs.setBiometricEnabled(true)
                updateBiometricStatusText()
                Toast.makeText(this, getString(R.string.biometric_enabled), Toast.LENGTH_SHORT).show()
            } else {
                // Biometrics not available - revert the switch
                binding.biometricSwitch.isChecked = false
                updateBiometricStatusText()
                Toast.makeText(this, getString(R.string.biometric_hardware_unavailable), Toast.LENGTH_LONG).show()
            }
        } else {
            // User wants to disable biometrics
            SharedPrefs.setBiometricEnabled(false)
            updateBiometricStatusText()
            Toast.makeText(this, getString(R.string.biometric_disabled), Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkBiometricStatus() {
        val isBiometricAvailable = BiometricLogin.isBiometricAvailable(this)
        val isBiometricEnabled = SharedPrefs.isBiometricEnabled()

        updateBiometricStatusText()

        // Disable the switch if biometrics aren't available
        binding.biometricSwitch.isEnabled = isBiometricAvailable

        // Update switch state based on availability
        if (!isBiometricAvailable) {
            binding.biometricSwitch.isChecked = false
        }
    }

    private fun saveSettings() {
        // Save general settings
        val settings = Settings(
            soundEnabled = binding.soundSwitch.isChecked,
            vibrationEnabled = binding.vibrationSwitch.isChecked,
        )
        SharedPrefs.saveSettings(settings)

        // Save notification preferences
        SharedPrefs.saveNotificationPreferences(notificationPreferences)

        // Sync notification preferences with server
        syncNotificationPreferencesWithServer()

        Toast.makeText(this, getString(R.string.settings_saved_message), Toast.LENGTH_SHORT).show()
    }

    private fun syncNotificationPreferencesWithServer() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = UpdateNotificationPreferencesRequest(
                    dailyReminders = notificationPreferences.dailyReminders,
                    foodUpdates = notificationPreferences.foodUpdates,
                    testNotifications = notificationPreferences.testNotifications
                )
                val response = RetrofitClient.instance.updateNotificationPreferences(request)

                if (response.isSuccessful) {
                    Log.d("Settings", "Notification preferences synced with server")
                } else {
                    Log.e("Settings", "Failed to sync notification preferences: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("Settings", "Network error syncing notification preferences", e)
            }
        }
    }

    private fun sendTestNotification() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Use localized title and message
                val request = TestNotificationRequest(
                    title = getString(R.string.test_notifications),
                    message = getString(R.string.send_test_notification)
                )
                val response = RetrofitClient.instance.sendTestNotification(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@SettingsActivity, getString(R.string.send_test_notification), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@SettingsActivity, "Failed to send test notification", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SettingsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun playFeedback() {
        playSound()
        vibrate()
    }

    private fun playSound() {
        if (binding.soundSwitch.isChecked) {
            toneGenerator.startTone(ToneGenerator.TONE_DTMF_1, 200)
        }
    }

    private fun showLanguageSelectionDialog() {
        val languages = arrayOf(
            getString(R.string.english),
            getString(R.string.afrikaans)
        )

        val languageCodes = arrayOf("en", "af")
        val currentLanguage = LocaleHelper.getPersistedLanguage(this)
        val currentIndex = languageCodes.indexOf(currentLanguage)

        AlertDialog.Builder(this)
            .setTitle(R.string.select_language)
            .setSingleChoiceItems(languages, currentIndex) { dialog, which ->
                val selectedLanguageCode = languageCodes[which]
                changeLanguage(selectedLanguageCode)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun changeLanguage(languageCode: String) {
        // Update the locale
        LocaleHelper.setLocale(this, languageCode)

        // Show confirmation message
        val languageName = when (languageCode) {
            "en" -> getString(R.string.english)
            "af" -> getString(R.string.afrikaans)
            else -> getString(R.string.english)
        }

        val message = getString(R.string.language_changed_message, languageName)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        // Completely restart the activity to apply language changes
        val intent = Intent(this, SettingsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@SettingsActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun vibrate() {
        if (binding.vibrationSwitch.isChecked && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // API 26+ - use VibrationEffect
                val vibrationEffect = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(vibrationEffect)
            } else {
                // API 25 and below
                @Suppress("DEPRECATION")
                vibrator.vibrate(100)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        toneGenerator.release()
    }
}
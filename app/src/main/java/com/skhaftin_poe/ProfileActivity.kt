package com.skhaftin_poe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.skhaftin_poe.databinding.ActivityProfileBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        setupUserInfo()
        setupEditButtons()
        setupLogoutButton()
        setupNavigation()

        // Load user profile from backend
        loadUserProfile()
    }

    private fun setupUserInfo() {
        // Get user data from SharedPrefs
        val userEmail = SharedPrefs.getUserEmail() ?: "Not available"
        binding.tvUserEmail.text = userEmail
    }

    private fun setupEditButtons() {
        // Check if these buttons exist in your layout
        binding.btnEditUsername?.setOnClickListener {
            showEditDialog("Username", binding.tvUsername.text.toString()) { newValue ->
                updateUsername(newValue)
            }
        }

        binding.btnEditLocation?.setOnClickListener {
            showEditDialog("Location", binding.tvLocation.text.toString()) { newValue ->
                updateLocation(newValue)
            }
        }
    }

    private fun showEditDialog(title: String, currentValue: String, onSave: (String) -> Unit) {
        val input = EditText(this)
        input.setText(currentValue)
        input.hint = "Enter $title"

        MaterialAlertDialogBuilder(this)
            .setTitle("Edit $title")
            .setView(input)
            .setPositiveButton("Save") { dialog, which ->
                val newValue = input.text.toString().trim()
                if (newValue.isNotEmpty()) {
                    onSave(newValue)
                } else {
                    Toast.makeText(this, "$title cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateUsername(newUsername: String) {
        if (newUsername.length > 50) {
            Toast.makeText(this, "Username too long (max 50 characters)", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val request = UpdateProfileRequest(username = newUsername, location = null)
                val response = RetrofitClient.instance.updateUserProfile(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    binding.tvUsername.text = newUsername
                    Toast.makeText(this@ProfileActivity, "Username updated successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ProfileActivity, "Failed to update username", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun updateLocation(newLocation: String) {
        if (newLocation.length > 100) {
            Toast.makeText(this, "Location too long (max 100 characters)", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val request = UpdateProfileRequest(username = null, location = newLocation)
                val response = RetrofitClient.instance.updateUserProfile(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    binding.tvLocation.text = newLocation
                    Toast.makeText(this@ProfileActivity, "Location updated successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ProfileActivity, "Failed to update location", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun loadUserProfile() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitClient.instance.getUserProfile()

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.success == true) {
                        val user = apiResponse.data?.user
                        user?.let {
                            // Update UI with user data
                            binding.tvUsername?.text = it.username ?: "Not set"
                            binding.tvLocation?.text = it.location ?: "Not set"

                            // Also update the email if it's different
                            it.email?.let { email ->
                                if (email != binding.tvUserEmail.text.toString()) {
                                    binding.tvUserEmail.text = email
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this@ProfileActivity, "Failed to load profile: ${apiResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // If the new endpoint fails, try the old one for compatibility
                    tryOldProfileEndpoint()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    // Fallback method for old endpoint
    private fun tryOldProfileEndpoint() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitClient.instance.getProfile()

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.success == true) {
                        val userData = apiResponse.data
                        val user = userData?.user

                        user?.let {
                            binding.tvUsername?.text = it.username ?: "Not set"
                            binding.tvLocation?.text = it.location ?: "Not set"
                        }
                    }
                } else {
                    Toast.makeText(this@ProfileActivity, "Error loading profile: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Failed to load profile from both endpoints", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this@ProfileActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_chat -> {
                    //val intent = Intent(this@ProfileActivity, ChatActivity::class.java)
                    //startActivity(intent)
                    true
                }
                R.id.nav_settings -> {
                    val intent = Intent(this@ProfileActivity, SettingsActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    // Already on profile page
                    true
                }
                else -> false
            }
        }
    }

    private fun setupLogoutButton() {
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes, Logout") { dialog, which ->
                performLogout()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun performLogout() {
        try {
            // Clear SharedPrefs data
            SharedPrefs.clearUserData()

            // Sign out from Firebase
            firebaseAuth.signOut()

            // Sign out from Google
            GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()

            // Navigate to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(this, "Logout failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
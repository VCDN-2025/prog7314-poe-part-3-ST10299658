package com.skhaftin_poe

import android.content.Intent
import androidx.biometric.BiometricPrompt
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.skhaftin_poe.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



class LoginActivity: AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPrefs
        SharedPrefs.init(this)

        // Enables an instance of FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Check if we should show biometric prompt
        checkAutoLoginAndBiometrics()

        // Navigates to RegisterActivity
        binding.textView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Allows users to login using Google
        binding.googleSignInBtn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        // Allows user to login using Email and Password
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                loginWithEmailPassword(email, pass)
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkAutoLoginAndBiometrics() {
        val currentUser = firebaseAuth.currentUser
        val isBiometricEnabled = SharedPrefs.isBiometricEnabled()
        val isBiometricAvailable = BiometricLogin.isBiometricAvailable(this)

        Log.d("AutoLogin", "User: $currentUser, BiometricEnabled: $isBiometricEnabled")

        if (currentUser != null) {
            if (isBiometricEnabled && isBiometricAvailable) {
                // User has biometrics enabled
                Log.d("AutoLogin", "User has biometrics enabled")
                showBiometricUI()
                // Small delay to ensure UI is updated before showing prompt
                binding.root.postDelayed({
                    authenticateWithBiometrics()
                }, 300)
            } else {
                // User doesn't have biometrics enabled
                Log.d("AutoLogin", "User has no biometrics")
                showBiometricUI()
                binding.biometricsText.visibility = View.GONE
                fetchTokenAndCallBackend()
            }
        } else {
            // No user logged in
            Log.d("AutoLogin", "No user logged in - showing login form")
            showNormalLoginUI()
        }
    }

    private fun loginWithEmailPassword(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fetchTokenAndCallBackend()
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Handles the result of Google Login
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Sign in to Firebase with Google token
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    fetchTokenAndCallBackend()
                } else {
                    Toast.makeText(this, "Google sign-in failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Authenticates with biometric login
    private fun authenticateWithBiometrics() {
        BiometricLogin.showBiometricPrompt(
            activity = this,
            title = "Welcome Back to Skhaftin",
            subtitle = "Use your fingerprint to access your account",
            negativeButtonText = "Use Password Instead",
            callback = object : BiometricLogin.BiometricCallback {

                // Navigate to MainActivity if biometric login is successful
                override fun onBiometricSuccess() {
                    Log.d("Biometric", "Biometric success - navigating to MainActivity")
                    navigateToMainActivity()
                }

                // Show login form if biometric login fails
                override fun onBiometricError(errorCode: Int, errString: CharSequence) {
                    Log.d("Biometric", "Biometric error: $errorCode - $errString")
                    when (errorCode) {
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                            // User chose "Use Password" - show login form
                            Log.d("Biometric", "User selected 'Use Password'")
                            showNormalLoginUI()
                        }
                        BiometricPrompt.ERROR_USER_CANCELED -> {
                            // User canceled - show login form so they can try other methods
                            Log.d("Biometric", "User canceled biometric prompt")
                            showNormalLoginUI()
                        }
                        else -> {
                            Toast.makeText(this@LoginActivity, "Biometric unavailable: $errString", Toast.LENGTH_SHORT).show()
                            showNormalLoginUI()
                        }
                    }
                }

                override fun onBiometricFailed() {
                    Toast.makeText(this@LoginActivity, "Biometric not recognized. Please try again.", Toast.LENGTH_SHORT).show()
                    // Don't show login form here - let user try biometric again
                }
            }
        )
    }

    // Shows biometric-focused UI (centered logo only)
    private fun showBiometricUI() {
        binding.biometricLayout.visibility = View.VISIBLE
        binding.normalLoginLayout.visibility = View.GONE
    }

    // Shows normal login UI with form elements
    private fun showNormalLoginUI() {
        binding.biometricLayout.visibility = View.GONE
        binding.normalLoginLayout.visibility = View.VISIBLE
    }


    // Fetches Firebase Token
    private fun fetchTokenAndCallBackend() {
        val user = firebaseAuth.currentUser
        user?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val result = task.result
                val idToken = result.token

                if (idToken != null) {
                    callBackendWithToken(idToken)
                } else {
                    Toast.makeText(this, "Failed to get authentication token", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Token fetch failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun callBackendWithToken(idToken: String) {
        // Save token first
        SharedPrefs.saveAuthToken(idToken)

        // Use coroutines to call the suspend function
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitClient.instance.getProfile()

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.success == true) {
                        // CORRECTED: Access the user through the proper structure
                        val userData = apiResponse.data
                        val user = userData?.user

                        user?.let {
                            val userId = it.userId ?: firebaseAuth.currentUser?.uid ?: ""
                            val userEmail = it.email ?: firebaseAuth.currentUser?.email ?: ""
                            SharedPrefs.saveUserInfo(userEmail, userId)
                        }

                        sendFCMTokenToBackend()
                        navigateToMainActivity()

                    } else {
                        // Handle case where user might not exist yet (new registration)
                        if (apiResponse?.message?.contains("not found", true) == true) {
                            createUserProfile(idToken)
                        } else {
                            Toast.makeText(this@LoginActivity, "API error: ${apiResponse?.message}", Toast.LENGTH_SHORT).show()
                            saveEmailFromFirebase()
                            navigateToMainActivity()
                        }
                    }
                } else {
                    when (response.code()) {
                        404 -> {
                            createUserProfile(idToken)
                        }
                        else -> {
                            Toast.makeText(this@LoginActivity, "HTTP error: ${response.code()}", Toast.LENGTH_SHORT).show()
                            saveEmailFromFirebase()
                            navigateToMainActivity()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
                saveEmailFromFirebase()
                navigateToMainActivity()
            }
        }
    }

    private fun sendFCMTokenToBackend() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM", "FCM token retrieved: $token")

                // Save to SharedPrefs immediately
                SharedPrefs.saveFCMToken(token)

                // Send to backend with retry logic
                sendTokenToBackendWithRetry(token, maxRetries = 3)
            } else {
                Log.e("FCM", "FCM token fetch failed", task.exception)
                // Schedule retry for token fetch
                scheduleTokenRetry()
            }
        }
    }

    private fun sendTokenToBackendWithRetry(token: String, maxRetries: Int, currentRetry: Int = 0) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("FCM", "Attempting to send FCM token to backend (attempt ${currentRetry + 1}/$maxRetries)")

                val response = RetrofitClient.instance.updateFCMToken(UpdateFCMTokenRequest(token))

                if (response.isSuccessful) {
                    Log.d("FCM", "FCM token successfully saved to backend")
                    // Also load notification preferences after successful token update
                    loadNotificationPreferencesFromServer()
                } else {
                    Log.e("FCM", "Failed to save FCM token: ${response.code()} - ${response.message()}")

                    if (currentRetry < maxRetries - 1) {
                        // Retry after delay
                        kotlinx.coroutines.delay(2000L * (currentRetry + 1)) // Exponential backoff
                        sendTokenToBackendWithRetry(token, maxRetries, currentRetry + 1)
                    } else {
                        Log.e("FCM", "Max retries reached for FCM token update")
                        // Token will be retried in MyFirebaseMessagingService.onNewToken
                    }
                }
            } catch (e: Exception) {
                Log.e("FCM", "Network error sending FCM token: ${e.message}")

                if (currentRetry < maxRetries - 1) {
                    kotlinx.coroutines.delay(2000L * (currentRetry + 1))
                    sendTokenToBackendWithRetry(token, maxRetries, currentRetry + 1)
                }
            }
        }
    }

    private fun scheduleTokenRetry() {
        // Retry token fetch after 5 seconds
        binding.root.postDelayed({
            if (firebaseAuth.currentUser != null) {
                sendFCMTokenToBackend()
            }
        }, 5000)
    }

    private fun loadNotificationPreferencesFromServer() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getNotificationPreferences()
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let { serverPrefs ->
                        // Save server preferences to local storage
                        SharedPrefs.saveNotificationPreferences(serverPrefs)
                        Log.d("Notifications", "Loaded notification preferences from server: $serverPrefs")
                    }
                }
            } catch (e: Exception) {
                Log.e("Notifications", "Failed to load notification preferences from server", e)
            }
        }
    }

    private fun createUserProfile(idToken: String) {
        saveEmailFromFirebase()
        Toast.makeText(this@LoginActivity, "Setting up your profile...", Toast.LENGTH_SHORT).show()
        navigateToMainActivity()
    }

    // Save email from Firebase
    private fun saveEmailFromFirebase() {
        val currentUser = firebaseAuth.currentUser
        currentUser?.email?.let { email ->
            val userId = currentUser.uid ?: ""
            SharedPrefs.saveUserInfo(email, userId)
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)

        val username = when {
            SharedPrefs.getUserEmail() != null -> SharedPrefs.getUserEmail()!!
            firebaseAuth.currentUser?.email != null -> firebaseAuth.currentUser?.email!!
            else -> "User"
        }

        intent.putExtra("username", username)
        startActivity(intent)
        finish()
    }
}
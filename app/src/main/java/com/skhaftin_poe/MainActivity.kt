package com.skhaftin_poe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.skhaftin_poe.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var foodItemsAdapter: FoodItemsAdapter
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enables an instance of FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        setupRecyclerView()
        setupAddFoodItemButton()
        loadFoodItems()

        setupNavigation()
        logAuthToken()
    }

    private fun logAuthToken() {
        FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result?.token
                Log.d("AUTH_TOKEN", "Firebase ID Token: $token")
            }
        }
    }

    private fun setupRecyclerView() {
        foodItemsAdapter = FoodItemsAdapter()
        binding.rvFoodItems.layoutManager = LinearLayoutManager(this)
        binding.rvFoodItems.adapter = foodItemsAdapter
    }

    private fun setupAddFoodItemButton() {
        binding.fabAddFoodItem.setOnClickListener {
            val intent = Intent(this, CreateFoodItemActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadFoodItems() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitClient.instance.getAllFoodItems()

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.success == true) {
                        val foodItems = apiResponse.data?.foodItems ?: emptyList()
                        foodItemsAdapter.submitList(foodItems)
                    } else {
                        Toast.makeText(this@MainActivity, "Failed to load food items", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error loading food items: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun setupNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Already on home
                    true
                }
                R.id.nav_chat -> {
                    // val intent = Intent(this@MainActivity, ChatActivity::class.java)
                    // startActivity(intent)
                    true
                }
                R.id.nav_settings -> {
                    val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadFoodItems()
    }
}
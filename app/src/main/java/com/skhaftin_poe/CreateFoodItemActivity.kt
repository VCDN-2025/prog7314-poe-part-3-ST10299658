package com.skhaftin_poe

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.skhaftin_poe.databinding.ActivityCreateFoodItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CreateFoodItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateFoodItemBinding
    private var imageUri: Uri? = null

    // Categories and urgency levels
    private val categories = arrayOf("Fruits", "Vegetables", "Dairy", "Meat", "Grains", "Other")
    private val urgencyLevels = arrayOf("Low", "Medium", "High")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateFoodItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpinners()
        setupDatePicker()
        setupImagePicker()
        setupSubmitButton()
    }

    private fun setupSpinners() {
        // Category spinner
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCategory.adapter = categoryAdapter

        // Urgency spinner
        val urgencyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, urgencyLevels)
        urgencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spUrgency.adapter = urgencyAdapter
    }

    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateLabel(calendar)
        }

        binding.etExpiryDate.setOnClickListener {
            DatePickerDialog(
                this, datePicker,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateDateLabel(calendar: Calendar) {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.etExpiryDate.setText(sdf.format(calendar.time))
    }

    private fun setupImagePicker() {
        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = it
                binding.ivFoodImage.setImageURI(it)
            }
        }

        binding.btnPickImage.setOnClickListener {
            getContent.launch("image/*")
        }
    }

    private fun setupSubmitButton() {
        binding.btnSubmit.setOnClickListener {
            if (validateForm()) {
                createFoodItem()
            }
        }
    }

    private fun validateForm(): Boolean {
        if (binding.etFoodName.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter food name", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.etDescription.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.etExpiryDate.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select expiry date", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.etQuantity.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter quantity", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun createFoodItem() {
        val foodName = binding.etFoodName.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val category = binding.spCategory.selectedItem.toString()
        val expiryDate = binding.etExpiryDate.text.toString().trim()
        val urgency = binding.spUrgency.selectedItem.toString()
        val quantity = binding.etQuantity.text.toString().trim()
        val imageUrl = imageUri?.toString()

        val request = CreateFoodItemRequest(
            foodName = foodName,
            description = description,
            category = category,
            expiryDate = expiryDate,
            urgency = urgency,
            quantity = quantity,
            imageUrl = imageUrl
        )

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitClient.instance.createFoodItem(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@CreateFoodItemActivity, "Food item created successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@CreateFoodItemActivity, "Failed to create food item", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CreateFoodItemActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }
}
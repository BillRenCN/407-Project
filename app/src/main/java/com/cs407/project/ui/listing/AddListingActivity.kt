package com.cs407.project.ui.listing

import android.net.Uri
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cs407.project.data.AppDatabase
import com.cs407.project.data.Item
import com.cs407.project.data.SharedPreferences
import com.cs407.project.data.UsersDatabase
import com.cs407.project.databinding.ActivityPostItemBinding
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class AddListingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostItemBinding
    private lateinit var database: AppDatabase
    private lateinit var userDB: UsersDatabase
    private var selectedImageUri: Uri? = null

    // Define the ActivityResultLauncher
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.itemPrice.addDecimalLimiter(2)

        // Initialize the database
        database = AppDatabase.getDatabase(this)
        userDB = UsersDatabase.getDatabase(this)

        // Register the ActivityResultLauncher
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                handleImageSelection(uri)
            } else {
                Toast.makeText(this, "Image selection canceled", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up image upload button
        binding.btnUploadImage.setOnClickListener {
            openImagePicker()
        }

        binding.btnListItem.setOnClickListener {
            if (binding.checkboxAgree.isChecked) {
                addItemToDatabase()
            } else {
                Toast.makeText(this, "Please agree to the terms", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Open the image picker using the new API
    private fun openImagePicker() {
        imagePickerLauncher.launch("image/*")
    }

    // Handle the image selection result
    private fun handleImageSelection(uri: Uri) {
        selectedImageUri = saveImageLocally(uri)?.let { Uri.fromFile(File(it)) }
        if (selectedImageUri != null) {
            binding.previewImage.setImageURI(selectedImageUri)
            binding.previewImage.visibility = View.VISIBLE
            binding.btnUploadImage.text = "Change Image"
        } else {
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageLocally(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val fileName = "listing_image_${System.currentTimeMillis()}.jpg"
            val file = File(filesDir, fileName)
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            file.absolutePath // Return the local file path
        } catch (e: Exception) {
            Log.e("AddListingActivity", "Failed to save image: ${e.message}")
            null
        }
    }

    private fun addItemToDatabase() {
        val title = binding.itemTitle.text.toString()
        val description = binding.itemDescription.text.toString()
        val price = binding.itemPrice.text.toString().toDoubleOrNull() ?: 0.0
        val imageUrl = selectedImageUri?.toString() // Retrieve selected image URI

        if (title.isNotEmpty() && description.isNotEmpty()) {
            val sharedPrefs = SharedPreferences(this)
            val username = sharedPrefs.getLogin().username.toString()

            // Insert the item into the database
            lifecycleScope.launch {
                val userId = userDB.userDao().getIdByUsername(username)
                val newItem = Item(
                    title = title,
                    description = description,
                    price = price,
                    userId = userId,
                    imageUrl = imageUrl
                )
                database.itemDao().insertItem(newItem)
                Toast.makeText(
                    this@AddListingActivity,
                    "Item posted successfully",
                    Toast.LENGTH_SHORT
                ).show()
                finish() // Close the activity
            }
        } else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }

    // Decimal Limiter Helper
    fun EditText.addDecimalLimiter(maxLimit: Int = 2) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val str = this@addDecimalLimiter.text!!.toString()
                if (str.isEmpty()) return
                val str2 = decimalLimiter(str, maxLimit)

                if (str2 != str) {
                    this@addDecimalLimiter.setText(str2)
                    val pos = this@addDecimalLimiter.text!!.length
                    this@addDecimalLimiter.setSelection(pos)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    fun EditText.decimalLimiter(string: String, MAX_DECIMAL: Int): String {
        var str = string
        if (str[0] == '.') str = "0$str"
        val max = str.length

        var rFinal = ""
        var after = false
        var i = 0
        var up = 0
        var decimal = 0
        var t: Char

        val decimalCount = str.count { ".".contains(it) }

        if (decimalCount > 1) return str.dropLast(1)

        while (i < max) {
            t = str[i]
            if (t != '.' && !after) {
                up++
            } else if (t == '.') {
                after = true
            } else {
                decimal++
                if (decimal > MAX_DECIMAL) return rFinal
            }
            rFinal += t
            i++
        }
        return rFinal
    }
}

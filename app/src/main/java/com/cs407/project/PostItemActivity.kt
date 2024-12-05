package com.cs407.project

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.cs407.project.data.AppDatabase
import com.cs407.project.data.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostItemActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private lateinit var previewImage: ImageView
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_item)

        val titleInput: EditText = findViewById(R.id.item_title)
        val descriptionInput: EditText = findViewById(R.id.item_description)
        val priceInput: EditText = findViewById(R.id.item_price)
        val uploadImageButton: Button = findViewById(R.id.btn_upload_image)
        val listItemButton: Button = findViewById(R.id.btn_list_item)
        previewImage = findViewById(R.id.preview_image)

        // Handle Image Upload
        uploadImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        listItemButton.setOnClickListener {
            val title = titleInput.text.toString()
            val description = descriptionInput.text.toString()
            val price = priceInput.text.toString().toDoubleOrNull()

            if (title.isNotEmpty() && description.isNotEmpty() && price != null && selectedImageUri != null) {
                val newItem = Item(
                    title = title,
                    description = description,
                    price = price,
                    imageUrl = selectedImageUri.toString() // Store the image URI as a string
                )

                val database = AppDatabase.getDatabase(applicationContext)
                CoroutineScope(Dispatchers.IO).launch {
                    database.itemDao().insertItem(newItem)
                }
                Toast.makeText(this, "Item Posted", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "All fields and image are required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            previewImage.setImageURI(selectedImageUri)
            previewImage.visibility = ImageView.VISIBLE
        }
    }
}

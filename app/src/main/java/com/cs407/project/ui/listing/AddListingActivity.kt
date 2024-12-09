package com.cs407.project.ui.listing

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cs407.project.data.AppDatabase
import com.cs407.project.data.Item
import com.cs407.project.data.SharedPreferences
import com.cs407.project.data.UsersDatabase
import com.cs407.project.databinding.ActivityPostItemBinding
import kotlinx.coroutines.launch

class AddListingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostItemBinding
    private lateinit var database: AppDatabase
    private lateinit var userDB: UsersDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.itemPrice.addDecimalLimiter(2)

        // Initialize the database
        database = AppDatabase.getDatabase(this)
        userDB = UsersDatabase.getDatabase(this)
        binding.btnListItem.setOnClickListener {
            if (binding.checkboxAgree.isChecked) {
                addItemToDatabase()
            } else {
                Toast.makeText(this, "Please agree to the terms", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addItemToDatabase() {
        val title = binding.itemTitle.text.toString()
        val description = binding.itemDescription.text.toString()
        val price = binding.itemPrice.text.toString().toDoubleOrNull() ?: 0.0
        //val userId = 1 // Replace with the actual user ID in a real app

        if (title.isNotEmpty() && description.isNotEmpty()) {
            val sharedPrefs = SharedPreferences()
            val username = sharedPrefs.getLogin(this).username.toString()
            // Insert the item into the database
            lifecycleScope.launch {
                val userId = userDB.userDao().getIdByUsername(username)
                val newItem = Item(
                    title = title, description = description, price = price, userId = userId
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

    // https://stackoverflow.com/a/58260463/15503136
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

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

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

package com.cs407.project.ui.trade_feedback

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.cs407.project.R
import com.cs407.project.data.AppDatabase
import com.cs407.project.data.Message
import com.cs407.project.data.MessagesDatabase
import com.cs407.project.data.UsersDatabase
import com.cs407.project.ui.messages.MessagesActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class ScheduleTradeActivity : AppCompatActivity() {
    private lateinit var mMap: GoogleMap
    private lateinit var locationEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var mDestinationLatLng: LatLng
    private var isLocationConfirmed: Boolean = false

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_schedule_trade)

        // Initialize mDestinationLatLng with a default value
        mDestinationLatLng = LatLng(43.077321, -89.424316) // Default location

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        val messagesDao = MessagesDatabase.getDatabase(this).messagesDao()
        val appDatabase = AppDatabase.getDatabase(this)
        val itemDao = appDatabase.itemDao()

        locationEditText = findViewById(R.id.editTextText2)
        dateEditText = findViewById(R.id.editTextText)

        dateEditText.setOnClickListener {
            showDateTimePicker()
        }

        mapFragment?.getMapAsync { googleMap ->
            runOnUiThread {
                mMap = googleMap
                setLocationMarker(mDestinationLatLng, "Bascom Hall") // Initial marker with default location
            }
        }

        findViewById<Button>(R.id.search_button).setOnClickListener {
            runOnUiThread {
                val dateTime = dateEditText.text.toString()
                val locationName = locationEditText.text.toString()
                if (dateTime.isNotEmpty()) {
                    if (locationName.isNotEmpty()) {
                        searchLocation(locationName)
                    } else {
                        Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Please enter a date and time", Toast.LENGTH_SHORT).show()
                }
            }
        }

        findViewById<Button>(R.id.confirm_button).setOnClickListener {
            runOnUiThread {
                if (isLocationConfirmed) {
                    val intent = intent
                    val itemId = intent.getIntExtra("ITEM_ID", -1)
                    val buyerId = intent.getIntExtra("MY_USER_ID", -1)

                    CoroutineScope(Dispatchers.IO).launch {
                        val sellerId = itemDao.getItemById(itemId)!!.userId
                        val itemName = itemDao.getItemById(itemId)!!.title

                        val message = Message(buyerId,
                            sellerId,
                            "Hi! I'd like to schedule a trade with you for '$itemName' at ${locationEditText.text} on ${dateEditText.text}.",
                            System.currentTimeMillis(),
                        )

                        messagesDao.insertItem(message)

                        val seller = UsersDatabase.getDatabase(this@ScheduleTradeActivity).userDao().getById(sellerId)

                        val intent = Intent(this@ScheduleTradeActivity, MessagesActivity::class.java)
                        intent.putExtra("USER_ID", sellerId)
                        intent.putExtra("MY_USER_ID", buyerId)
                        intent.putExtra("USER_NAME", seller.username)
                        runOnUiThread {
                            Toast.makeText(this@ScheduleTradeActivity, "Trade confirmed, message sent", Toast.LENGTH_LONG).show()
                            finish()
                            startActivity(intent)
                        }
                    }

                } else {
                    Toast.makeText(this, "Please search and confirm the location first", Toast.LENGTH_SHORT).show()
                }
            }
        }

        supportActionBar?.hide()
    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val timePickerDialog = TimePickerDialog(this, { _, hourOfDay, minute ->
                val selectedDateTime = "$dayOfMonth/${month + 1}/$year $hourOfDay:$minute"
                dateEditText.setText(selectedDateTime)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
            timePickerDialog.show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun searchLocation(locationName: String) {
        Log.d("debug", "1")
        val geocoder = Geocoder(this, Locale.getDefault())
        geocoder.getFromLocationName(locationName, 1, object : Geocoder.GeocodeListener {
            override fun onGeocode(addressList: List<android.location.Address>) {
                runOnUiThread {
                    if (addressList.isNotEmpty()) {
                        Log.d("debug", "2")
                        val address = addressList[0]
                        val latLng = LatLng(address.latitude, address.longitude)
                        Log.d("MainActivity", "Location found: ${address.latitude}, ${address.longitude}")

                        setLocationMarker(latLng, locationName)
                        mDestinationLatLng = latLng // Update the destination latLng here
                        isLocationConfirmed = true
                    } else {
                        Toast.makeText(this@ScheduleTradeActivity, "Location not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onError(errorMessage: String?) {
                runOnUiThread {
                    Toast.makeText(this@ScheduleTradeActivity, "Unable to get location", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setLocationMarker(destination: LatLng, destinationName: String) {
        runOnUiThread {
            // Create a marker with the specified position and title
            val markerOptions = MarkerOptions().position(destination).title(destinationName)
            mMap.addMarker(markerOptions)

            // Move the camera to the marker's location with a zoom of 15x
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 15f))
        }
    }
}

package com.cs407.project

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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var mMap: GoogleMap
    private lateinit var locationEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var mDestinationLatLng: LatLng

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment

        locationEditText = findViewById(R.id.editTextText2)
        dateEditText = findViewById(R.id.editTextText)

        mapFragment?.getMapAsync { googleMap ->
            runOnUiThread {
                mMap = googleMap
                mDestinationLatLng = LatLng(43.0753, -89.4034)
                setLocationMarker(mDestinationLatLng, "Bascom Hall")
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

                        setLocationMarker(latLng, "locationName")
                    } else {
                        Toast.makeText(this@MainActivity, "Location not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onError(errorMessage: String?) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Unable to get location", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setLocationMarker(destination: LatLng, destinationName: String) {
        runOnUiThread {
            // Create a marker with the specified position and title
            val markerOptions = MarkerOptions().position(destination).title(destinationName)
            mMap.addMarker(markerOptions)

            // Move the camera to the marker's location with a zoom of  15x
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 15f))
        }
    }
}

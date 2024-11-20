package com.cs407.project

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val fragmentManager=supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.startfragment, StartFragment::class.java, null)
            .setReorderingAllowed(true)
            .addToBackStack("loading start fragment")
            .commit()
    }

}
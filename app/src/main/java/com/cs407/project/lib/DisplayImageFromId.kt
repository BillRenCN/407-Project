package com.cs407.project.lib

import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.util.TypedValue
import android.widget.ImageView
import com.cs407.project.R
import java.io.File

/**
 * Display an image for a given item ID.
 * Or, if the image does not exist, display an error image.
 * @param url The URL of the image.
 * @param imageView The ImageView to display the image in.
 */
fun displayImage(imageView: ImageView, url: String?) {
    imageView.setBackgroundColor(Color.TRANSPARENT)
    if (url.isNullOrEmpty() || Uri.parse(url).path.isNullOrEmpty()) {
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        imageView.setImageResource(R.drawable.error_image)
        return
    }

    val itemUri = Uri.parse(url)
    val context = imageView.context
    val imageFile = File(itemUri.path!!)

    if (imageFile.exists()) {
        val requiredWidthPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 100f, context.resources.displayMetrics
        ).toInt()

        // Set BitmapFactory options to scale down the image
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true  // First decode to get dimensions
            BitmapFactory.decodeFile(imageFile.absolutePath, this)
            inSampleSize = calculateInSampleSize(this, requiredWidthPx)
            inJustDecodeBounds = false // Decode again for the actual bitmap
        }

        // Decode bitmap with inSampleSize set
        val scaledBitmap = BitmapFactory.decodeFile(imageFile.absolutePath, options)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.setImageBitmap(scaledBitmap)
    } else {
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        imageView.setImageResource(R.drawable.error_image)
        Log.e("ListingAdapter", "Image does not exist at $url")
    }
}

/**
 * Helper function to calculate the inSampleSize for BitmapFactory.
 * Basically, calculates the largest number that can be used to scale down the image without
 * losing visual quality.
 */
private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int): Int {
    val width: Int = options.outWidth
    var inSampleSize = 1

    if (width > reqWidth) {
        val widthRatio = width / reqWidth
        inSampleSize = widthRatio
    }
    return inSampleSize.coerceAtLeast(1)  // Ensure it's at least 1
}

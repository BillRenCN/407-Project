package com.cs407.project.ui.listing

import android.graphics.BitmapFactory
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cs407.project.R
import java.io.File
import java.text.NumberFormat
import java.util.Currency

class ListingAdapter(
    private val listingModelArrayList: ArrayList<ListingModel>
) : RecyclerView.Adapter<ListingAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_listing_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: ListingModel = listingModelArrayList[position]
        holder.itemName.text = model.name
        holder.itemDescription.text = model.description
        val priceFormat = NumberFormat.getCurrencyInstance()
        priceFormat.currency = Currency.getInstance("USD")
        holder.itemPrice.text = priceFormat.format(model.price)

        val filesDir = holder.itemView.context.filesDir
        val imagesDir = "${filesDir.absolutePath}/images"
        val imagesDirFile = File(imagesDir)
        if (!imagesDirFile.exists()) {
            imagesDirFile.mkdirs()
        }
        val imageFile = File("${imagesDir}/${model.itemId}")

        if (imageFile.exists()) {
            val requiredWidthPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 100f, holder.itemView.context.resources.displayMetrics
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
            holder.imageView.scaleType = ImageView.ScaleType.FIT_XY
            holder.imageView.setImageBitmap(scaledBitmap)
        } else {
            holder.imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            holder.imageView.setImageResource(R.drawable.error_image)
            Log.e("ListingAdapter", "Image does not exist for item ${model.itemId}")
        }
    }

    override fun getItemCount(): Int {
        return listingModelArrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val itemDescription: TextView = itemView.findViewById(R.id.itemDescription)
        val itemPrice: TextView = itemView.findViewById(R.id.itemPrice)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int): Int {
        val width: Int = options.outWidth
        var inSampleSize = 1

        if (width > reqWidth) {
            val widthRatio = width / reqWidth
            inSampleSize = widthRatio
        }
        return inSampleSize.coerceAtLeast(1)  // Ensure it's at least 1
    }
}

package com.cs407.project.ui.listing

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivities
import androidx.recyclerview.widget.RecyclerView
import com.cs407.project.ItemDetailsActivity
import com.cs407.project.R
import com.cs407.project.lib.displayImage
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

        displayImage(model.itemId, holder.imageView)

        holder.itemView.setOnClickListener {
            // launch item details activity here
            Toast.makeText(
                holder.itemView.context,
                "Item #$position (id ${model.itemId}) clicked",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(holder.itemView.context, ItemDetailsActivity::class.java)
            intent.putExtra("ITEM_ID", model.itemId)
            startActivities(holder.itemView.context, arrayOf(intent))
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
}
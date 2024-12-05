package com.cs407.project.ui.listing

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.cs407.project.R
import com.cs407.project.data.Item
import com.cs407.project.lib.displayImage
import java.text.NumberFormat
import java.util.Currency

@SuppressLint("NotifyDataSetChanged")
class ListingAdapter(
    private val allItems: LiveData<List<Item>>, lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<ListingAdapter.ViewHolder>() {

    init {
        allItems.observe(lifecycleOwner) {
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_listing_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = allItems.value?.get(position)?: return

        holder.itemName.text = model.title
        holder.itemDescription.text = model.description

        val priceFormat = NumberFormat.getCurrencyInstance()
        priceFormat.currency = Currency.getInstance("USD")
        holder.itemPrice.text = priceFormat.format(model.price)
        displayImage(model.id, holder.imageView, "listing")

        holder.itemView.setOnClickListener {
            // launch item details activity here
            Toast.makeText(
                holder.itemView.context,
                "Item #$position (id ${model.id}) clicked",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(holder.itemView.context, ListingDetailsActivity::class.java)
            intent.putExtra("ITEM_ID", model.id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return allItems.value?.size ?: 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val itemDescription: TextView = itemView.findViewById(R.id.itemDescription)
        val itemPrice: TextView = itemView.findViewById(R.id.itemPrice)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }
}

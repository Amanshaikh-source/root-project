package com.example.finalpro.cart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.finalpro.R
import com.example.finalpro.dataModel.DataModel
import com.squareup.picasso.Picasso

class CartAdaptor(
    private val context: Context,
    private val cartItems: MutableList<DataModel>, // Use MutableList to allow modifications
    private val onDeleteClickListener: (Int) -> Unit,
    private val onQuantityChangeListener: (Int, Int) -> Unit
) : RecyclerView.Adapter<CartAdaptor.CartViewHolder>() {

    private val maxQuantity = 5 // Maximum quantity limit for items

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.cartName)
        val priceTextView: TextView = itemView.findViewById(R.id.cartPrice)
        val descriptionTextView: TextView = itemView.findViewById(R.id.cartDescription)
        val imageView: ImageView = itemView.findViewById(R.id.cartImage)
        val btnRemove: Button = itemView.findViewById(R.id.btnRemove)
        val cartQuantity: TextView = itemView.findViewById(R.id.cartQuantity)
        val quantitySpinner: Spinner = itemView.findViewById(R.id.quantitySpinner)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val inflate = LayoutInflater.from(context).inflate(R.layout.cart_items_list, parent, false)
        return CartViewHolder(inflate)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentItem = cartItems[position]

        holder.nameTextView.text = currentItem.Name
        holder.priceTextView.text = currentItem.Price.toString()
        holder.descriptionTextView.text = currentItem.Description
        holder.cartQuantity.text = currentItem.quantity.toString()

        Picasso.get()
            .load(currentItem.Image)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error)
            .into(holder.imageView)

        holder.btnRemove.setOnClickListener {
            onDeleteClickListener.invoke(position)
        }

        // Set up the spinner with quantities
        val quantityList = (1..maxQuantity).map { "Qty : $it" } // List of quantities
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, quantityList)
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        holder.quantitySpinner.adapter = adapter

        // Set the initial selected item in the spinner
        val initialSelection = (currentItem.quantity - 1).coerceIn(0, maxQuantity - 1)
        holder.quantitySpinner.setSelection(initialSelection)

        // Handle spinner item selection changes
        holder.quantitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, pos: Int, id: Long) {
                val newQuantity = pos + 1
                if (newQuantity != currentItem.quantity) {
                    if (newQuantity <= maxQuantity) {
                        // Update the item quantity in the data model
                        currentItem.quantity = newQuantity

                        // Notify the adapter that the data has changed for this item
                        notifyItemChanged(position)

                        // Update the cartQuantity TextView
                        holder.cartQuantity.text = newQuantity.toString()

                        // Calculate and update the new price
                        val newPrice = currentItem.Price * newQuantity
                        holder.priceTextView.text = newPrice.toString()

                        // Notify the listener to handle quantity change
                        onQuantityChangeListener.invoke(currentItem.id, newQuantity)
                    } else {
                        Toast.makeText(context, "Max quantity is $maxQuantity", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No action needed
            }
        }
    }
}


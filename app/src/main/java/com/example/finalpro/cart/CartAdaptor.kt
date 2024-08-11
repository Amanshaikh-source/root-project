package com.example.finalpro.cart

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
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
    private val cartItems: MutableList<DataModel>,
    private val onDeleteClickListener: (Int) -> Unit,
    private val onQuantityChangeListener: (Int, Int) -> Unit
) : RecyclerView.Adapter<CartAdaptor.CartViewHolder>() {

    private val maxQuantity = 5 // You can adjust this as needed

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

    override fun onBindViewHolder(holder: CartViewHolder, @SuppressLint("RecyclerView") position: Int) {
        if (position >= cartItems.size) {
            Log.e("CartAdaptor", "Index out of bounds: $position")
            return
        }
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
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                onDeleteClickListener.invoke(pos)
            }
        }

        // Set up the spinner with quantities
        val quantityList = (1..maxQuantity).map { "Qty : $it" }
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, quantityList)
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        holder.quantitySpinner.adapter = adapter

        // Set the initial selected item in the spinner
        val initialSelection = (currentItem.quantity - 1).coerceIn(0, maxQuantity - 1)
        holder.quantitySpinner.setSelection(initialSelection)

        holder.quantitySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    pos: Int,
                    id: Long
                ) {
                    val newQuantity = pos + 1
                    if (position >= cartItems.size) {
                        Log.e(
                            "CartAdaptor",
                            "Index out of bounds during spinner selection: $position"
                        )
                        return
                    }

                    val item = cartItems[position]
                    if (newQuantity != item.quantity) {
                        onQuantityChangeListener.invoke(holder.adapterPosition, newQuantity)
                        holder.cartQuantity.text = newQuantity.toString()

                        val newPrice = item.Price * newQuantity
                        holder.priceTextView.text = newPrice.toString()

                        notifyItemChanged(holder.adapterPosition)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle case when nothing is selected

                }
            }
    }
}

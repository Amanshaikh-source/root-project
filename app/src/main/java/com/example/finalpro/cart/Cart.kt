package com.example.finalpro.cart

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalpro.R
import com.example.finalpro.dataModel.DataModel
import com.example.finalpro.dbHelper.DbHelper
import com.example.finalpro.payment.PaymentActivity
import java.util.Locale

class Cart : AppCompatActivity() {

    private val dbHelper = DbHelper(this)
    private lateinit var toolbar: Toolbar
    private lateinit var recyclerView: RecyclerView
    private var cartItems = ArrayList<DataModel>()
    private lateinit var cartAdaptor: CartAdaptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        setupToolbar()
        setupRecyclerView()
        setupPlaceOrderButton()
        loadCartItems()
    }

    private fun setupPlaceOrderButton() {
        val placeOrderButton: Button = findViewById(R.id.PlaceOrder)
        placeOrderButton.setOnClickListener { placeOrder() }
    }

    private fun placeOrder() {
        val totalAmountTextView: TextView = findViewById(R.id.TotalAmount)
        val totalAmountString = totalAmountTextView.text.toString()

        val totalAmount = parseAmount(totalAmountString)

        if (totalAmount < 100) {
            Toast.makeText(this, "Amount should be at least ₹1", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, PaymentActivity::class.java).apply {
            putExtra("TOTAL_AMOUNT", totalAmount)
        }
        startActivity(intent)
    }

    private fun parseAmount(amountString: String): Int {
        val numericString = amountString.replace(Regex("[^0-9]"), "")
        return (numericString.toIntOrNull() ?: 0) * 100
    }

    private fun setupToolbar() {
        toolbar = findViewById(R.id.cartToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "CART"
    }

    private fun setupRecyclerView() {
        val emptyCartMessage = findViewById<TextView>(R.id.emptyCartMessage)

        recyclerView = findViewById(R.id.cartRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        cartAdaptor = CartAdaptor(
            this,
            cartItems,
            { position ->
                val item = cartItems[position]
                if (dbHelper.removeItemFromCart(item.id)) {
                    refreshCart()
                } else {
                    Toast.makeText(this, "Failed to remove item", Toast.LENGTH_SHORT).show()
                }
            },
            { position, newQuantity ->
                val item = cartItems[position]
                if (dbHelper.updateProductQuantity(item.id, newQuantity)) {
                    item.quantity = newQuantity
                    cartAdaptor.notifyItemChanged(position)
                    updateTotalPrice()
                    updateEmptyCartMessage()
                } else {
                    Toast.makeText(this, "Failed to update quantity", Toast.LENGTH_SHORT).show()
                }
            }
        )
        recyclerView.adapter = cartAdaptor
        updateEmptyCartMessage()
    }

    private fun refreshCart() {
        loadCartItems() // Refresh cart items and update the UI
    }

    private fun updateEmptyCartMessage() {
        val emptyCartMessage = findViewById<TextView>(R.id.emptyCartMessage)
        emptyCartMessage.visibility = if (cartItems.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun loadCartItems() {
        lifecycleScope.launch {
            try {
                cartItems.clear()
                cartItems.addAll(dbHelper.getAllProduct())
                cartAdaptor.notifyDataSetChanged()
                updateTotalPrice()
                updateEmptyCartMessage()
            } catch (e: Exception) {
                Log.e("CartActivity", "Error loading cart items", e)
                Toast.makeText(this@Cart, "Error loading cart items", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun updateTotalPrice() {
        val totalAmountTextView: TextView = findViewById(R.id.TotalAmount)
        val totalPrice = cartItems.sumOf { it.Price * it.quantity }
        totalAmountTextView.text = String.format(Locale.getDefault(), "₹%d", totalPrice)
    }
}

package com.example.finalpro.cart;

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalpro.R
import com.example.finalpro.dataModel.DataModel
import com.example.finalpro.dbHelper.DbHelper
import com.example.finalpro.payment.PaymentActivity
import kotlinx.coroutines.launch
import java.util.Locale


class Cart : AppCompatActivity() {

    private val dbHelper = DbHelper(this)
    private lateinit var toolbar: Toolbar
    private lateinit var recyclerView: RecyclerView
    private val dbList : MutableList<DataModel> = mutableListOf()
    private lateinit var adaptor : CartAdaptor


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
        placeOrderButton.setOnClickListener {
            placeOrder()
        }
    }

    private fun placeOrder() {
           val totalAmountTextView = findViewById<TextView>(R.id.TotalAmount)
        val totaAmountString = totalAmountTextView.text.toString()

        val totalAmount = parseAmount(totaAmountString)

        // Ensure the amount is at least ₹1
        if (totalAmount < 100){
                Toast.makeText(this, "Amount should be at least ₹1", Toast.LENGTH_SHORT).show()
            return
        }

        // Create an Intent to start PaymentActivity
        val intent = Intent(this, PaymentActivity::class.java).apply {
            putExtra("TOTAL_AMOUNT", totalAmount)
        }
        startActivity(intent)
        }
    private fun parseAmount(amountString : String): Int{
        // Remove any non-numeric characters (e.g., currency symbols, commas)
        val numericString = amountString.replace(Regex("[^0-9]"), "")

        // Convert the numeric string to integer paise (if the amount is in rupees, multiply by 100)
        return (numericString.toIntOrNull() ?: 0) * 100
    }

    private fun setupToolbar() {
        toolbar = findViewById(R.id.cartToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "CART"
    }

    private fun setupRecyclerView() {

        recyclerView = findViewById(R.id.cartRecycler)
        val layoutManager = LinearLayoutManager(applicationContext,LinearLayoutManager.VERTICAL,false)
        recyclerView.layoutManager = layoutManager

        adaptor = CartAdaptor(this, dbList, onDeleteClickListener = { position ->
            if (position >= 0 && position < dbList.size) {
                val productDelete = dbList[position]
                lifecycleScope.launch {
                    if (dbHelper.deleteProductByName(productDelete.id)) {
                        // Successfully deleted from the database
                        dbList.removeAt(position)
                        adaptor.notifyItemRemoved(position)
                        updateTotalPrice()
                        updateUI()
                    } else {
                        // Failed to delete
                        Toast.makeText(this@Cart, "Failed to remove item", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }, onQuantityChangeListener = { position, newQuantity ->
            if (position >= 0 && position < dbList.size) {
                lifecycleScope.launch {
                    // Update the database with the new quantity
                    val productId = dbList[position].id
                    dbHelper.updateProductQuantity(productId, newQuantity)

                    // Update the local list and notify the adapter
                    dbList[position].quantity = newQuantity
                    adaptor.notifyItemChanged(position)
                    updateTotalPrice()
                    updateUI()
                }
            }
        })
        recyclerView.adapter = adaptor
    }
    private fun loadCartItems() {
        lifecycleScope.launch {
            try {
                dbList.clear()
                dbList.addAll(dbHelper.getAllProduct())
                adaptor.notifyDataSetChanged()
                updateTotalPrice()
                updateUI()
            } catch (e: Exception) {
                Log.e("CartActivity", "Error loading cart items", e)
                Toast.makeText(this@Cart, "Error loading cart items", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(){

        val totalQuantityTextView = findViewById<TextView>(R.id.cartQuantity)
        val totalPriceTextView = findViewById<TextView>(R.id.cartPrice)

        if (totalQuantityTextView == null || totalPriceTextView == null) {
            Log.e("Cart", "Error: TextViews not found in layout")
            return
        }

        // Calculate total quantity and price from the updated data in the database
        var totalQuantity = 0
        var totalPrice = 0.0

        for (item in dbList) {
            totalQuantity += item.quantity
            totalPrice += item.Price * item.quantity
        }
        // Update the TextViews in your layout
        totalQuantityTextView.text = String.format(Locale.getDefault(),"Quantity: %d",totalQuantity).trim()
        totalPriceTextView.text = String.format(Locale.getDefault(),"Total: $%.2f",totalPrice).trim()
    }

    private fun updateTotalPrice() {
        val totalAmountTextView = findViewById<TextView>(R.id.TotalAmount)

        // Calculate the total price from the updated data in the database
        var totalPrice = 0.0

        for (item in dbList){
            totalPrice += item.Price * item.quantity
        }
        // Update the TextView in your layout with formatted price
        totalAmountTextView.text = String.format(Locale.getDefault(),"%.2f", totalPrice).trim()
    }
}



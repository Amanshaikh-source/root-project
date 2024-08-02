package com.example.finalpro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.finalpro.R.*
import com.example.finalpro.dataModel.DataModel
import com.example.finalpro.dbHelper.DbHelper
import androidx.lifecycle.lifecycleScope
import com.example.finalpro.Utils.IdUtils
import kotlinx.coroutines.launch


import com.squareup.picasso.Picasso

class ProductDetail : AppCompatActivity() {

    lateinit var bundle: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_product_detail)

        val imgDetail = findViewById<ImageView>(id.imgdetails)
        val nameDetail = findViewById<TextView>(id.TVproname)
        val priceDetail = findViewById<TextView>(id.TVproprice)
        val descDetail = findViewById<TextView>(id.Tvprodesc)

//      val addToCart = findViewById<Button>(R.id.addToCart)

        bundle = intent.extras ?: return
        val nameEx : String? = bundle.getString("Title")
        val priceEx : Int  = bundle.getInt("Price")
        val imageEx : String? = bundle.getString("Image")
        val descEx : String?= bundle.getString("Desc")

      //  imgDetail.setImageResource(imageEx)
        Picasso.get()
            .load(imageEx)
            .placeholder(drawable.example)
            .error(drawable.error)
            .into(imgDetail)

        nameDetail.text = nameEx
        priceDetail.text = priceEx.toString()
        descDetail.text = descEx

    }
    fun addToCart(view: View) {
        val dbHelper = DbHelper(this)

        val name: String = bundle.getString("Title") ?: ""
        val description: String = bundle.getString("Desc") ?: ""
        val price: Int = bundle.getInt("Price", 0)
        val image: String = bundle.getString("Image") ?: ""
        val productId = IdUtils.generateUniqueProductId(dbHelper) // Implement this based on your needs

        // Check if item is already in the cart
        val existingItem = dbHelper.getProductById(productId)

        if (existingItem !=null){
            // Update quantity if item already exists
            existingItem.quantity += 1
                lifecycleScope.launch {
                    dbHelper.updateProduct(existingItem)
                    Log.d("ProductDetail", "Product updated: ${existingItem.Name}")
                    Toast.makeText(this@ProductDetail,"Updated in Cart",Toast.LENGTH_SHORT).show()
                }
        }else{
            // Add new item to cart
        val product = DataModel(-1,name, image, price, description,1)
        lifecycleScope.launch {
            try {
            dbHelper.addProduct(product)
            Log.d("ProductDetail", "Product added: ${product.Name}")
                Toast.makeText(this@ProductDetail,"Added to Cart", Toast.LENGTH_LONG).show()
            }catch (e: Exception){
                Log.e("ProductDetail", "Error adding product to cart: ${e.message}")
                Toast.makeText(this@ProductDetail, "Error adding to Cart", Toast.LENGTH_LONG).show()}
            }
        }
    }
}
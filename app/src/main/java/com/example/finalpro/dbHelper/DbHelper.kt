package com.example.finalpro.dbHelper

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.ContactsContract.Data
import android.util.Log
import com.example.finalpro.dataModel.DataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DbHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "cart.db"
        internal const val DATABASE_TABLE = "AddToCart"
        private const val DATABASE_VERSION = 5
        const val ID = "ID"
        const val IMAGE = "IMAGE"
        const val NAME = "NAME"
        const val DESCRIPTION = "DESCRIPTION"
        const val PRICE = "PRICE"
        const val QUANTITY = "QUANTITY"
        const val ORIGINAL_PRICE = "ORIGINAL_PRICE"
    }

    private val CREATE_TABLE_QUERY =
        "CREATE TABLE $DATABASE_TABLE (" +
                "$ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$NAME TEXT, " +
                "$IMAGE TEXT, " +
                "$PRICE INTEGER, " +
                "$DESCRIPTION TEXT," +
                "$QUANTITY INTEGER," +
                "$ORIGINAL_PRICE INTEGER" +
                ")"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_QUERY)
        Log.d("DbHelper", "Table created with schema: $CREATE_TABLE_QUERY")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        try {

            db.execSQL("DROP TABLE IF EXISTS $DATABASE_TABLE")
            onCreate(db)
        } catch (e: Exception) {
            Log.e("DbHelper", "Error upgrading database: ${e.message}")
        }
    }

    @SuppressLint("Range")
    fun addProduct(data: DataModel) {
        writableDatabase.use { db ->
            try {
                // Check if the product already exists
                val cursor = db.query(
                    DATABASE_TABLE,
                    null,
                    "$ID = ?",
                    arrayOf(data.id.toString()),
                    null,
                    null,
                    null
                )
                if (cursor.moveToFirst()) {
                    val currentQuantity = cursor.getInt(cursor.getColumnIndex(QUANTITY))
                    val newQuantity = currentQuantity + data.quantity

                    val values = ContentValues().apply {
                        put(QUANTITY, newQuantity)
                        put(PRICE, data.Price * newQuantity)
                    }

                    val whereClause = "$ID = ?"
                    val whereArgs = arrayOf(data.id.toString())

                    db.update(DATABASE_TABLE, values, whereClause, whereArgs)
                    Log.d("AddToCart", "Quantity updated for ${data.Name}")

                } else {
                    val values = ContentValues().apply {
                        put(IMAGE, data.Image)
                        put(NAME, data.Name)
                        put(DESCRIPTION, data.Description)
                        put(PRICE, data.Price)
                        put(QUANTITY, data.quantity)

                        val originalPrice = data.Price * data.quantity
                        put(ORIGINAL_PRICE, originalPrice)
                    }
                    db.insert(DATABASE_TABLE, null, values)
                    Log.d("AddToCart", "New item added to cart: ${data.Name}")
                }
                cursor.close()
            } catch (e: Exception) {
                Log.e("AddToCart", "Error adding item to cart: ${e.message}")
            }
        }
    }


    @SuppressLint("Range")
    suspend fun getAllProduct(): List<DataModel> = withContext(Dispatchers.IO) {

        val cartModel: ArrayList<DataModel> = ArrayList()
        val selectQuery = "SELECT * FROM $DATABASE_TABLE"
        val db = this@DbHelper.readableDatabase

        var cursor: Cursor? = null // Declare cursor outside the try block
        try {
            cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndex(ID)) // ID field
                    val name = cursor.getString(cursor.getColumnIndex(NAME)) // Name
                    val image = cursor.getString(cursor.getColumnIndex(IMAGE)) // Image
                    val price = cursor.getInt(cursor.getColumnIndex(PRICE))  // Price
                    val description = cursor.getString(cursor.getColumnIndex(DESCRIPTION)) // Description
                    val quantity = cursor.getInt(cursor.getColumnIndex(QUANTITY)) // Quantity

                    val cartItem = DataModel(id, name, image, price, description, quantity)
                    cartModel.add(cartItem)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e("DbHelper", "Error retrieving products: ${e.message}")
        } finally {
            cursor?.close()// Close the cursor in the finally block
            db.close() // Close the database connection
        }
        cartModel
    }

    fun deleteProductById(id: Int): Boolean {

        val db = this.writableDatabase
        val whereClause = "$ID =?"
        val whereArgs = arrayOf(id.toString())
        return try {
            db.delete(DATABASE_TABLE, whereClause, whereArgs) > 0
        } catch (e: Exception) {
            Log.e("DbHelper", "Error deleting product: ${e.message}")
            false
        } finally {
            db.close()
        }
    }

    fun updateProductQuantity(id: Int, newQuantity: Int): Boolean {

        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(QUANTITY, newQuantity)
        }
        val whereClause = "$ID = ?"  // Assuming ID is the column name for the unique identifier
        val whereArgs = arrayOf(id.toString())

        return try {
            val rowAffected = db.update(DATABASE_TABLE, values, whereClause, whereArgs)
            rowAffected > 0
        } catch (e: Exception) {
            // Handle the exception (e.g., log it or show an error message)
            Log.e("DbHelper", "Error updating product quantity: ${e.message}")
            false
        }
    }

     fun getProductById(id: Int): DataModel? {

        val db = this.readableDatabase
        val cursor = db.query(
            DATABASE_TABLE,
            arrayOf(ID, NAME, IMAGE, PRICE, DESCRIPTION, QUANTITY, ORIGINAL_PRICE),
            "$ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        try {
            if (cursor.moveToFirst()) {
                val idIndex = cursor.getInt(cursor.getColumnIndexOrThrow(ID))
                val nameIndex = cursor.getString(cursor.getColumnIndexOrThrow(NAME))
                val imageIndex = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE))
                val priceIndex = cursor.getInt(cursor.getColumnIndexOrThrow(PRICE))
                val indexDescription = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION))
                val quantityIndex = cursor.getInt(cursor.getColumnIndexOrThrow(QUANTITY))

                return DataModel(
                    idIndex,
                    nameIndex,
                    imageIndex,
                    priceIndex,
                    indexDescription,
                    quantityIndex
                )
            }
            return null
        } catch (e: Exception) {
            Log.e("DbHelper", "Error retrieving product by ID: ${e.message}")
            return null
        } finally {
            cursor.close()
        }
    }
    // Method to update product details
    fun updateProduct(product : DataModel) :Boolean{
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(NAME, product.Name)
            put(IMAGE, product.Image)
            put(PRICE, product.Price)
            put(DESCRIPTION, product.Description)
            put(QUANTITY, product.quantity)

            val originalPrice = product.Price * product.quantity
            put(ORIGINAL_PRICE, originalPrice)
        }
        val result = db.update(DATABASE_TABLE, contentValues,"$ID = ?", arrayOf(product.id.toString().trim()))
        return result > 0
    }
    // New method to remove an item from the cart
    fun removeItemFromCart(id: Int): Boolean {
        val db = this.writableDatabase
        return try {
            val whereClause = "$ID = ?"
            val whereArgs = arrayOf(id.toString())
            val rowsDeleted = db.delete(DATABASE_TABLE, whereClause, whereArgs)
            rowsDeleted > 0
        } catch (e: Exception) {
            Log.e("DbHelper", "Error removing item from cart: ${e.message}")
            false
        }
    }
    // New method to get all items from the cart
    fun getAllCartItems(): List<DataModel> {
        val cartItems = mutableListOf<DataModel>()
        val db = this.readableDatabase

        db.rawQuery("SELECT * FROM $DATABASE_TABLE", null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val item = DataModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(IMAGE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(QUANTITY))
                    )
                    cartItems.add(item)
                } while (cursor.moveToNext())
            }
        }

        db.close()
        return cartItems
    }

    // New method to clear the cart
    fun clearCart() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $DATABASE_TABLE")
        db.close()
    }
}
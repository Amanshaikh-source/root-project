package com.example.finalpro.payment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finalpro.R
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class PaymentActivity : AppCompatActivity(), PaymentResultListener {

    private var totalAmount : Int = 0 // Amounts are in paise
    private lateinit var autoReadOtpHelperReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        autoReadOtpHelperReceiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {

            }
        }
        // Register receiver, if needed
        registerReceiver(autoReadOtpHelperReceiver, IntentFilter("YOUR_ACTION"))

        // Retrieve the amount from the intent
        totalAmount = intent.getIntExtra("TOTAL_AMOUNT", 0)

        // Display the total amount in a non-editable TextView
        val amountTextView = findViewById<TextView>(R.id.totalAmountTextView)
        amountTextView.text = "Total Amount ₹${totalAmount / 100}"   // Converting paise to INR

       val btnpay =  findViewById<Button>(R.id.btnPay)
        btnpay.setOnClickListener {
            startPayment()
        }

        // Initialize Razorpay
        Checkout.preload(applicationContext)
    }

    private fun startPayment() {
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_mqAEmZ0gVVxFUC")

        try {
            val options = JSONObject().apply {
                put("name", "KIN CART")
                put("description", "Order #123456")
                put("currency", "INR")
                put("amount", totalAmount) // amount in paise
            }

            checkout.open(this, options)
        } catch (e: Exception) {
            Toast.makeText(this, "Error in payment: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String) {
        Toast.makeText(this, "Payment Successful: $razorpayPaymentID", Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment Failed: $response", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister receiver to prevent leaks
        try {
            unregisterReceiver(autoReadOtpHelperReceiver)
        } catch (e: IllegalArgumentException) {
            // Receiver was not registered
        }
    }
}

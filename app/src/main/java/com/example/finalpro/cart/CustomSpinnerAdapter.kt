package com.example.finalpro.cart

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CustomSpinnerAdapter(context: Context, resource: Int,objects: MutableList<String>) :
    ArrayAdapter<String>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        // Show only the number in the selected item
        val item = getItem(position)
        (view as TextView).text = item?.substring(5) // Remove "Qty: " prefix
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        // Show "Qty: " followed by the number in the dropdown list
        val item = getItem(position)
        (view as TextView).text = item // Keep "Qty: " prefix
        return view
    }
}
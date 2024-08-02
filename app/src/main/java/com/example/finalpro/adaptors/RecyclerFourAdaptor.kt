package com.example.finalpro.adaptors

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalpro.ProductDetail
import com.example.finalpro.R
import com.example.finalpro.dataModel.DataModel
import com.squareup.picasso.Picasso

class RecyclerFourAdaptor (private val context: Context, private var List: List <DataModel>) :
    RecyclerView.Adapter<RecyclerFourAdaptor.ViewHolder>() {

   fun filteredListfour(filteredList: List<DataModel>) {
        List = filteredList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgFour: ImageView = itemView.findViewById(R.id.imgFour)
        val nameFour: TextView = itemView.findViewById(R.id.nameFour)
        val priceFour: TextView = itemView.findViewById(R.id.priceFour)
        // val discount: TextView = itemView.findViewById(R.id.Discount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate = LayoutInflater.from(context).inflate(R.layout.recycler_list_four, parent,false)
        return ViewHolder(inflate)
    }

    override fun getItemCount(): Int {
        return List.size
    }

    override fun onBindViewHolder(holder: RecyclerFourAdaptor.ViewHolder, position: Int) {

        if (position < List.size) {
            val data: DataModel = List[position]
            holder.nameFour.text = data.Name
            holder.priceFour.text = data.Price.toString()
            // holder.discount.text = data.discount.toString()

            Picasso.get()
                .load(data.Image)
                .placeholder(R.drawable.example)
                .error(R.drawable.error)
                .into(holder.imgFour)

            holder.itemView.setOnClickListener {
                val intent = Intent(context, ProductDetail::class.java).apply {
                    putExtra("Title", data.Name)
                    putExtra("Price", data.Price)
                    putExtra("Image", data.Image)
                    putExtra("Desc", data.Description)
                }
                context.startActivity(intent)
            }
        } else {
            // Assuming you have a method to load a placeholder image
            Picasso.get().load(R.drawable.placeholder_image).into(holder.imgFour)
        }
    }
}

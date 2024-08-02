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
import com.example.finalpro.dataModel.DataModel
import com.example.finalpro.R
import com.squareup.picasso.Picasso


class RecyclerThreeAdaptor(private val context: Context,private var List: List <DataModel> ) :
    RecyclerView.Adapter<RecyclerThreeAdaptor.ViewHolder>() {

        fun filteredList(filteredList: List<DataModel>) {
            List = filteredList
            notifyDataSetChanged()
        }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgThree: ImageView = itemView.findViewById(R.id.imgThree)
        val nameThree: TextView = itemView.findViewById(R.id.nameThree)

        //   val descriptionThree: TextView = itemView.findViewById(R.id.descriptionThree)
        val priceThree: TextView = itemView.findViewById(R.id.priceThree)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate = LayoutInflater.from(context).inflate(R.layout.recycler_list_three, parent, false)
        return ViewHolder(inflate)
    }

    override fun getItemCount(): Int {
        return List.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (position < List.size) {
            val data: DataModel = List[position]
            //   holder.imgThree.setImageResource(data.Image)
            holder.nameThree.text = data.Name
            //    holder.descriptionThree.text = data.Description
            holder.priceThree.text = data.Price.toString()
            Picasso.get()
                .load(data.Image)
                .placeholder(R.drawable.example)
                .error(R.drawable.error)
                .into(holder.imgThree)

            holder.itemView.setOnClickListener {
                val list = List[position]
                val getTitle: String = list.Name
                val getPrice: Int = list.Price
                val getDes: String = list.Description
                val getImg = list.Image

                val intent = Intent(this.context, ProductDetail::class.java)
                intent.putExtra("Title", getTitle)
                intent.putExtra("Price", getPrice)
                intent.putExtra("Image", getImg)
                intent.putExtra("Desc", getDes)
                context.startActivity(intent)
            }
        } else {
            // Assuming you have a method to load a placeholder image
            Picasso.get().load(R.drawable.placeholder_image).into(holder.imgThree)
        }
    }
}

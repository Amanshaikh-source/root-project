package com.example.finalpro.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalpro.R
import com.example.finalpro.dataModel.DataModel
import com.squareup.picasso.Picasso

class RecyclerTwoAdaptor (private var context: Context,private var List : List<DataModel>)  : RecyclerView.Adapter<RecyclerTwoAdaptor.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val imageTwo: ImageView = itemView.findViewById(R.id.imgTwo)
        val nameTwo: TextView = itemView.findViewById(R.id.nameTwo)
        val description: TextView = itemView.findViewById(R.id.descriptionTwo)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate = LayoutInflater.from(context).inflate(R.layout.recycler_list_two,parent , false)
        return ViewHolder(inflate)
    }

    override fun getItemCount(): Int {
        return List.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data : DataModel = List[position]
    //    holder.imageTwo.setImageResource(data.Image)
        holder.nameTwo.text = data.Name
        holder.description.text = data.Description

        Picasso.get()
            .load(data.Image)
            .placeholder(R.drawable.example)
            .error(R.drawable.error)
            .into(holder.imageTwo)

    }

}
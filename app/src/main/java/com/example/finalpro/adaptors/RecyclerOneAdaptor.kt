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

class RecyclerOneAdaptor (val context: Context, val List: List <DataModel>) :
    RecyclerView.Adapter<RecyclerOneAdaptor.ViewHolder>() {

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val img =itemView.findViewById<ImageView>(R.id.img_one)
        val name =itemView.findViewById<TextView>(R.id.name_one)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate = LayoutInflater.from(context).inflate(R.layout.recycler_list_one , parent , false)
        return ViewHolder(inflate)
    }
    override fun getItemCount(): Int {
        return List.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data : DataModel = List[position]
       // holder.img.setImageResource(data.Image)
        holder.name.text = data.Name
        Picasso.get()
            .load(data.Image)
            .placeholder(R.drawable.example)
            .error(R.drawable.error)
            .into(holder.img)
    }
}
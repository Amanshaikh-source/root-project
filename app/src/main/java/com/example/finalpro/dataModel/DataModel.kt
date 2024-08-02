package com.example.finalpro.dataModel


class DataModel(
    val id : Int,
    val Name: String,
    val Image: String,
    var Price: Int,
    val Description: String,
    var quantity: Int = 0,// List to store quantities for each RecyclerView item
    )


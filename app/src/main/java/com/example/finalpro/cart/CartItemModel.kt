package com.example.finalpro.cart

class CartItemModel
    ( val CDMName : String,
      val CDMImage : String,
      val CDMPrice : Int,
      val CDMDescription : String,
      val CDMquantities: MutableList<Int> = mutableListOf() )// Default quantity is 1
package com.example.finalpro.ApiService

import com.example.finalpro.dataModel.DataModel
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("products")
    fun getProducts(): Call<List<DataModel>>

    @GET("products/{id}")
    fun getProduct(@Path("id") id: Int): Call<DataModel>

    @POST("products")
    fun addProduct(@Body product: DataModel): Call<DataModel>

    @PUT("products/{id}")
    fun updateProduct(@Path("id") id: Int, @Body product: DataModel): Call<DataModel>

    @DELETE("products/{id}")
    fun deleteProduct(@Path("id") id: Int): Call<Void>
}


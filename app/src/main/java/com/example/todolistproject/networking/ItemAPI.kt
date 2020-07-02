package com.example.todolistproject.networking

import com.example.todolistproject.model.ApiItem
import com.example.todolistproject.model.ItemApi
import com.example.todolistproject.model.ItemRoom
import retrofit2.Call
import retrofit2.http.*

interface ItemApi {
    @POST("items")
    fun postItemApi(@Header("token") token: String?,
                 @Body body: ApiItem
    ): Call<List<ItemRoom>>

    @PUT("items/{item_id}")
    fun updateItemApi(@Header("token") token: String?,
                      @Path("item_id") item_id: Int?,
                      @Body()itemRoom: ItemRoom
    ): Call<ItemRoom>

    @GET("items/{item_id}")
    fun getItemApi(@Header("token") token: String?,
                   @Path("item_id") item_id: Int?
    ): Call<ItemRoom>

    @GET("items/")
    fun getItemsApi(@Header("token") token: String?,
                   @Query("list_id") list_id: Int?
    ): Call<List<ItemApi>>

    @DELETE("items/{item_id}")
    fun deleteItemApi(@Header("token") token: String?,
                      @Path("item_id") item_id: Int?
    ): Call<ItemRoom>
}

package com.nicomahnic.dadm.enerfi.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nicomahnic.dadm.enerfi.data.database.AppDatabase
import com.nicomahnic.dadm.enerfi.data.entities.Book
import com.nicomahnic.dadm.enerfi.data.entities.OrderEntity
import com.nicomahnic.dadm.enerfi.utils.getJsonDataFromAsset

class DataSource(
    private val context: Context,
    private val appDatabase: AppDatabase
) {
    fun getBooks(): List<Book>? {
        val jsonFileString = getJsonDataFromAsset(context, "libros.json")
        jsonFileString?.let {
//            Log.d("NM", "BookDataSource: $jsonFileString")
            val gson = Gson()
            val listPersonType = object : TypeToken<List<Book>>() {}.type

            return gson.fromJson(jsonFileString, listPersonType)
        }
        return null
    }

    suspend fun insertOrderIntoRoom(order: OrderEntity){
        appDatabase.orderDao().insertOrder(order)
    }

    suspend fun getOrdersIntoRoom(): List<OrderEntity>? {
        return appDatabase.orderDao().getOrders()
    }

    suspend fun getOrderByOrderNumIntoRoom(orderNum: Long): OrderEntity? {
        return appDatabase.orderDao().getOrderByOrderNum(orderNum)
    }

    suspend fun updateOrderIntoRoom(order: OrderEntity){
        appDatabase.orderDao().updateOrder(order)
    }

    suspend fun deleteOrderIntoRoom(order: OrderEntity){
        appDatabase.orderDao().deleteOrder(order)
    }
}
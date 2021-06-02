package com.nicomahnic.dadm.enerfi.repository

import com.nicomahnic.dadm.enerfi.data.entities.Book
import com.nicomahnic.dadm.enerfi.data.entities.OrderEntity

interface Repository {
    fun getBooks(): List<Book>?

    suspend fun insertOrder(order: OrderEntity)
    suspend fun getOrders(): List<OrderEntity>?
    suspend fun getOrdersByOrderNum(orderNum: Long): OrderEntity?
    suspend fun updateOrder(order: OrderEntity)
    suspend fun deleteOrder(order: OrderEntity)
}
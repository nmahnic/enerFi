package com.nicomahnic.dadm.enerfi.repository

import com.nicomahnic.dadm.enerfi.data.DataSource
import com.nicomahnic.dadm.enerfi.data.entities.Book
import com.nicomahnic.dadm.enerfi.data.entities.OrderEntity

class RepositoryImpl(private val dataSource: DataSource): Repository {
    override fun getBooks(): List<Book>? = dataSource.getBooks()

    override suspend fun insertOrder(order: OrderEntity) {
        dataSource.insertOrderIntoRoom(order)
    }

    override suspend fun getOrders(): List<OrderEntity>? {
        return dataSource.getOrdersIntoRoom()
    }

    override suspend fun getOrdersByOrderNum(orderNum: Long): OrderEntity? {
        return dataSource.getOrderByOrderNumIntoRoom(orderNum)
    }

    override suspend fun updateOrder(order: OrderEntity){
        dataSource.updateOrderIntoRoom(order)
    }

    override suspend fun deleteOrder(order: OrderEntity){
        dataSource.deleteOrderIntoRoom(order)
    }
}
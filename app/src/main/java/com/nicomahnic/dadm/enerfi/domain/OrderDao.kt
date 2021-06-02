package com.nicomahnic.dadm.enerfi.domain

import androidx.room.*
import com.nicomahnic.dadm.enerfi.data.entities.OrderEntity
import com.nicomahnic.dadm.enerfi.data.entities.UserEntity

@Dao
interface OrderDao {

    @Query("SELECT * FROM orders ORDER BY id")
    suspend fun getOrders(): List<OrderEntity>?

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getOrderByOrderNum(id: Long): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity?)

    @Update
    suspend fun updateOrder(order: OrderEntity?)

    @Delete
    suspend fun deleteOrder(order: OrderEntity?)
}
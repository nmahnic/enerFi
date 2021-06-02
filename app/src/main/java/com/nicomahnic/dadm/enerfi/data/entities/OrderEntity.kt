package com.nicomahnic.dadm.enerfi.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "orders")
data class OrderEntity(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    @ColumnInfo(name = "client_name")
    var name: String,

    @ColumnInfo(name = "list_of_books")
    val books: String

)
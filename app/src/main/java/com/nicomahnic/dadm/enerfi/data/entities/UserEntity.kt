package com.nicomahnic.dadm.enerfi.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "users")
data class UserEntity(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    @ColumnInfo(name = "user_name")
    val name: String,

    @ColumnInfo(name = "password")
    val password: String,

    @ColumnInfo(name = "img_uri")
    val img: String,

    var checked: Boolean = false
)
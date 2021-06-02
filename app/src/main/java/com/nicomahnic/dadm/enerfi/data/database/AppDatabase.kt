package com.nicomahnic.dadm.enerfi.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nicomahnic.dadm.enerfi.data.entities.OrderEntity
import com.nicomahnic.dadm.enerfi.domain.UserDao

import com.nicomahnic.dadm.enerfi.data.entities.UserEntity
import com.nicomahnic.dadm.enerfi.domain.OrderDao

@Database(entities = [UserEntity::class, OrderEntity::class], version = 1, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun orderDao(): OrderDao

    companion object {
        var INSTANCE: AppDatabase? = null

        fun getAppDataBase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "myDB"
                    ).allowMainThreadQueries().build() // No es lo mas recomendable que se ejecute en el mainthread
                }
            }
            return INSTANCE!!
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }
}
package com.nicomahnic.dadm.enerfi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.nicomahnic.dadm.enerfi.core.Resource
import com.nicomahnic.dadm.enerfi.repository.Repository
import kotlinx.coroutines.Dispatchers

class RvOrdersViewModel(private val repo: Repository) : ViewModel() {
    fun fetchOrderList() = liveData(Dispatchers.IO){
        emit(Resource.Loading())
        try{
            emit(Resource.Success(repo.getOrders()))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }
}
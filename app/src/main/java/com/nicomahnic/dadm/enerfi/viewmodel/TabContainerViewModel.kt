
package com.nicomahnic.dadm.enerfi.viewmodel

import androidx.lifecycle.*
import com.google.gson.Gson
import com.nicomahnic.dadm.enerfi.core.Resource
import com.nicomahnic.dadm.enerfi.data.entities.OrderEntity
import com.nicomahnic.dadm.enerfi.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TabContainerViewModel(private val repo: Repository) : ViewModel() {
    val orderNum = MutableLiveData<Long>()
    var currentOrder = MutableLiveData<OrderEntity>()

    fun fetchOrder() = liveData(Dispatchers.IO) {
        emit(Resource.Loading())

        try {
            emit(Resource.Success(repo.getOrdersByOrderNum(orderNum.value!!)))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }

    fun updateOrder(newName: String) {
        currentOrder.value?.name = newName
        viewModelScope.launch {
            repo.updateOrder(currentOrder.value!!)
        }
    }

    fun deleteOrder() {
        viewModelScope.launch {
            repo.deleteOrder(currentOrder.value!!)
        }
    }

    fun loadOrder(order: Long) {
        orderNum.value = order
    }

    fun loadCurrentOrder(order: OrderEntity){
        currentOrder.value = order
    }



}
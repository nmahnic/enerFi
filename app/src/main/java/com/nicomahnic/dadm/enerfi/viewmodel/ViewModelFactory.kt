package com.nicomahnic.dadm.enerfi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nicomahnic.dadm.enerfi.repository.Repository


class ViewModelFactory(private val repo: Repository): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Repository::class.java).newInstance(repo)
    }
}
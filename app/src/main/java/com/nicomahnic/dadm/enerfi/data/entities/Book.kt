package com.nicomahnic.dadm.enerfi.data.entities

data class Book(
    val title: String,
    val author: String,
    val year: Int,
    val editorial: String,
    val isbn: String,
    val description: String,
    val imgUrl: String
)
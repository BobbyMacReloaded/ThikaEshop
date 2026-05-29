package com.example.thikaeshop.data.models

data class Order(
    val id: String,
    val productName: String,
    val productIcon: String,
    val quantity: Int,
    val totalAmount: Int,
    val status: String,
    val location: String,
    val date: String,
    val isEscrowHeld: Boolean = false,
    val orderNumber: String
)

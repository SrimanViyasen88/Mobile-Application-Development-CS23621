package com.example.madecommerce.data

data class Product(
    val id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val rating: Double,
    val description: String,
    val inStock: Int,
)

data class CartItem(
    val product: Product,
    val quantity: Int,
)

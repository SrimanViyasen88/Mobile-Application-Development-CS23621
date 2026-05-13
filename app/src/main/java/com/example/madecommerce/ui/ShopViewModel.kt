package com.example.madecommerce.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.madecommerce.data.CartItem
import com.example.madecommerce.data.Product
import com.example.madecommerce.data.ProductRepository

class ShopViewModel : ViewModel() {
    val products = ProductRepository.products

    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> = _cartItems

    val cartCount: Int
        get() = _cartItems.sumOf { it.quantity }

    val cartTotal: Double
        get() = _cartItems.sumOf { it.product.price * it.quantity }

    fun findProduct(productId: Int): Product? = products.firstOrNull { it.id == productId }

    fun addToCart(product: Product) {
        val index = _cartItems.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            val existing = _cartItems[index]
            _cartItems[index] = existing.copy(quantity = existing.quantity + 1)
        } else {
            _cartItems.add(CartItem(product = product, quantity = 1))
        }
    }

    fun increaseQuantity(productId: Int) {
        val index = _cartItems.indexOfFirst { it.product.id == productId }
        if (index >= 0) {
            val existing = _cartItems[index]
            _cartItems[index] = existing.copy(quantity = existing.quantity + 1)
        }
    }

    fun decreaseQuantity(productId: Int) {
        val index = _cartItems.indexOfFirst { it.product.id == productId }
        if (index >= 0) {
            val existing = _cartItems[index]
            if (existing.quantity > 1) {
                _cartItems[index] = existing.copy(quantity = existing.quantity - 1)
            } else {
                _cartItems.removeAt(index)
            }
        }
    }

    fun removeFromCart(productId: Int) {
        _cartItems.removeAll { it.product.id == productId }
    }
}

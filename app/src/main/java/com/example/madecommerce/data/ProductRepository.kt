package com.example.madecommerce.data

object ProductRepository {
    val products = listOf(
        Product(
            id = 1,
            name = "AirFlex Runner",
            category = "Shoes",
            price = 89.99,
            rating = 4.7,
            description = "Lightweight running shoes with breathable knit fabric and all-day comfort.",
            inStock = 12,
        ),
        Product(
            id = 2,
            name = "Urban Carry Pack",
            category = "Bags",
            price = 54.49,
            rating = 4.5,
            description = "A compact backpack designed for commuting, travel, and everyday essentials.",
            inStock = 9,
        ),
        Product(
            id = 3,
            name = "Pulse Smartwatch",
            category = "Wearables",
            price = 129.00,
            rating = 4.6,
            description = "Track workouts, sleep, and notifications with a clean AMOLED display.",
            inStock = 7,
        ),
        Product(
            id = 4,
            name = "CloudSound Mini",
            category = "Audio",
            price = 39.95,
            rating = 4.3,
            description = "Portable Bluetooth speaker with punchy bass and a durable matte finish.",
            inStock = 15,
        ),
        Product(
            id = 5,
            name = "DeskGlow Lamp",
            category = "Home",
            price = 24.99,
            rating = 4.4,
            description = "Minimal LED desk lamp with touch controls and adjustable brightness levels.",
            inStock = 18,
        ),
        Product(
            id = 6,
            name = "CoreFit Bottle",
            category = "Fitness",
            price = 19.50,
            rating = 4.2,
            description = "Insulated stainless steel bottle that keeps drinks cold through long workouts.",
            inStock = 20,
        ),
    )
}

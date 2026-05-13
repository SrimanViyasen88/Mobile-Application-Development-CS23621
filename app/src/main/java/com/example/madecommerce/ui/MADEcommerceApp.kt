package com.example.madecommerce.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.madecommerce.data.CartItem
import com.example.madecommerce.data.Product
import kotlinx.coroutines.launch

private enum class BottomDestination(val route: String, val label: String) {
    Home("home", "Home"),
    Cart("cart", "Cart"),
}

private object AppRoute {
    const val home = "home"
    const val cart = "cart"
    const val detail = "detail/{productId}"

    fun detail(productId: Int): String = "detail/$productId"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MADEcommerceApp(viewModel: ShopViewModel = viewModel()) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = currentDestination?.route in setOf(AppRoute.home, AppRoute.cart)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = when (currentDestination?.route) {
                            AppRoute.cart -> "Your Cart"
                            AppRoute.home -> "MAD Store"
                            else -> "Product Details"
                        },
                    )
                },
                navigationIcon = {
                    if (currentDestination?.route == AppRoute.detail) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Go back")
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (currentDestination?.route != AppRoute.cart) {
                                navController.navigate(AppRoute.cart) {
                                    launchSingleTop = true
                                }
                            }
                        },
                    ) {
                        BadgedBox(
                            badge = {
                                if (viewModel.cartCount > 0) {
                                    Badge { Text(viewModel.cartCount.toString()) }
                                }
                            },
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Open cart")
                        }
                    }
                },
            )
        },
        bottomBar = {
            if (showBottomBar) {
                BottomAppBar {
                    BottomDestination.entries.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        TextButton(
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            modifier = Modifier.weight(1f),
                        ) {
                            val icon = if (item == BottomDestination.Home) {
                                Icons.Default.Home
                            } else {
                                Icons.Default.ShoppingCart
                            }
                            Icon(icon, contentDescription = item.label)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(item.label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.home,
            modifier = Modifier.padding(padding),
        ) {
            composable(AppRoute.home) {
                HomeScreen(
                    products = viewModel.products,
                    onProductClick = { product -> navController.navigate(AppRoute.detail(product.id)) },
                    onAddToCart = { product ->
                        viewModel.addToCart(product)
                        scope.launch {
                            snackbarHostState.showSnackbar("${product.name} added to cart")
                        }
                    },
                )
            }
            composable(AppRoute.cart) {
                CartScreen(
                    cartItems = viewModel.cartItems,
                    cartTotal = viewModel.cartTotal,
                    onIncrease = viewModel::increaseQuantity,
                    onDecrease = viewModel::decreaseQuantity,
                    onRemove = viewModel::removeFromCart,
                    onContinueShopping = {
                        navController.navigate(AppRoute.home) {
                            popUpTo(AppRoute.home) { inclusive = true }
                        }
                    },
                    onCheckout = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Checkout flow is not connected yet")
                        }
                    },
                )
            }
            composable(AppRoute.detail) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
                val product = productId?.let(viewModel::findProduct)
                if (product == null) {
                    MissingProductScreen(onBack = { navController.popBackStack() })
                } else {
                    ProductDetailScreen(
                        product = product,
                        onAddToCart = {
                            viewModel.addToCart(product)
                            scope.launch {
                                snackbarHostState.showSnackbar("${product.name} added to cart")
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(
    products: List<Product>,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val categories = remember(products) { listOf("All") + products.map { it.category }.distinct() }
    var selectedCategory by rememberSaveable { mutableStateOf("All") }

    val filteredProducts = products.filter { product ->
        val matchesQuery = query.isBlank() || product.name.contains(query, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || product.category == selectedCategory
        matchesQuery && matchesCategory
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            StoreHeroCard()
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search products") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                singleLine = true,
            )
        }
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                    )
                }
            }
        }
        items(filteredProducts, key = { it.id }) { product ->
            ProductCard(
                product = product,
                onClick = { onProductClick(product) },
                onAddToCart = { onAddToCart(product) },
            )
        }
    }
}

@Composable
private fun StoreHeroCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Fresh picks for everyday shopping",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Browse a lightweight mobile storefront built with Kotlin and Jetpack Compose.",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = product.category,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = product.description,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                PricePill(price = product.price)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AssistChip(
                    onClick = { },
                    label = { Text("${product.rating} rating") },
                    leadingIcon = {
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp))
                    },
                )
                Button(onClick = onAddToCart) {
                    Text("Add to cart")
                }
            }
        }
    }
}

@Composable
private fun ProductDetailScreen(
    product: Product,
    onAddToCart: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = product.category,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PricePill(price = product.price)
                    Spacer(modifier = Modifier.width(12.dp))
                    AssistChip(
                        onClick = { },
                        label = { Text("${product.rating} rating") },
                        leadingIcon = {
                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp))
                        },
                    )
                }
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text("Why customers like it", fontWeight = FontWeight.Bold)
                    Text("Reliable quality for everyday use")
                    Text("Fast add-to-cart flow for demo purposes")
                    Text("Stock available: ${product.inStock} units")
                }
            }
        }
        item {
            Button(
                onClick = onAddToCart,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Add to cart")
            }
        }
    }
}

@Composable
private fun CartScreen(
    cartItems: List<CartItem>,
    cartTotal: Double,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit,
    onRemove: (Int) -> Unit,
    onContinueShopping: () -> Unit,
    onCheckout: () -> Unit,
) {
    if (cartItems.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Your cart is empty",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Add a few items from the store to see them here.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(18.dp))
                FilledTonalButton(onClick = onContinueShopping) {
                    Text("Browse products")
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        items(cartItems, key = { it.product.id }) { item ->
            CartItemCard(
                cartItem = item,
                onIncrease = { onIncrease(item.product.id) },
                onDecrease = { onDecrease(item.product.id) },
                onRemove = { onRemove(item.product.id) },
            )
        }
        item {
            OrderSummaryCard(
                itemCount = cartItems.sumOf { it.quantity },
                total = cartTotal,
                onCheckout = onCheckout,
            )
        }
    }
}

@Composable
private fun CartItemCard(
    cartItem: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
) {
    Card(shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(cartItem.product.name, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(cartItem.product.category, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "$${"%.2f".format(cartItem.product.price * cartItem.quantity)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Remove item")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                QuantityButton(icon = Icons.Default.Remove, onClick = onDecrease)
                Text(
                    text = cartItem.quantity.toString(),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                QuantityButton(icon = Icons.Default.Add, onClick = onIncrease)
            }
        }
    }
}

@Composable
private fun OrderSummaryCard(
    itemCount: Int,
    total: Double,
    onCheckout: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text("Order summary", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            SummaryRow(label = "Items", value = itemCount.toString())
            SummaryRow(label = "Shipping", value = "Free")
            HorizontalDivider()
            SummaryRow(label = "Total", value = "$${"%.2f".format(total)}", emphasized = true)
            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = onCheckout,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Proceed to checkout")
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    emphasized: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            fontWeight = if (emphasized) FontWeight.Bold else FontWeight.Normal,
        )
        Text(
            text = value,
            fontWeight = if (emphasized) FontWeight.Bold else FontWeight.Medium,
        )
    }
}

@Composable
private fun QuantityButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null)
    }
}

@Composable
private fun PricePill(price: Double) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        Text(
            text = "$${"%.2f".format(price)}",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun MissingProductScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Product not found",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("The selected product could not be loaded.")
            Spacer(modifier = Modifier.height(16.dp))
            FilledTonalButton(onClick = onBack) {
                Text("Go back")
            }
        }
    }
}

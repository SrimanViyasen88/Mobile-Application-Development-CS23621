package com.example.madecommerce

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.madecommerce.ui.MADEcommerceApp
import com.example.madecommerce.ui.theme.MADEcommerceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MADEcommerceTheme {
                MADEcommerceApp()
            }
        }
    }
}

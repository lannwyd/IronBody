package com.ironlog.gym

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.ironlog.gym.ui.navigation.IronLogNavHost
import com.ironlog.gym.ui.theme.IronLogTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IronLogTheme {
                val navController = rememberNavController()
                IronLogNavHost(navController = navController)
            }
        }
    }
}

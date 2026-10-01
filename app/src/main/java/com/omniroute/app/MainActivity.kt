package com.omniroute.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.omniroute.app.ui.navigation.OmniBottomNavBar
import com.omniroute.app.ui.navigation.OmniNavGraph
import com.omniroute.app.ui.theme.OmniBackground
import com.omniroute.app.ui.theme.OmniRouteTheme
import com.omniroute.app.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OmniRouteTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = OmniBackground,
                    bottomBar = {
                        OmniBottomNavBar(navController = navController)
                    }
                ) { paddingValues ->
                    OmniNavGraph(
                        navController = navController,
                        viewModel = viewModel,
                        paddingValues = paddingValues
                    )
                }
            }
        }
    }
}

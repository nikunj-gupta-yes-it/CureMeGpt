package com.bussiness.curemegptapp.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.di.SessionEventBus
import com.bussiness.curemegptapp.navigation.AppNavGraph
import com.bussiness.curemegptapp.ui.component.LoaderOverlay
import com.bussiness.curemegptapp.ui.component.SetStatusBarColor
import com.bussiness.curemegptapp.ui.dialog.AlertErrorDialog
import com.bussiness.curemegptapp.viewmodel.GlobalLoaderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                val useDarkIcons = MaterialTheme.colorScheme.background.luminance() > 0.5f
                SetStatusBarColor(color = Color.Transparent, darkIcons = useDarkIcons)
                val mainNavController = rememberNavController()
                val loaderViewModel: GlobalLoaderViewModel = hiltViewModel()
                val isLoading by loaderViewModel.isLoading.collectAsState()
                var showSessionDialog by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    SessionEventBus.sessionExpiredFlow.collect {
                        showSessionDialog = true
                    }
                }
                Box(Modifier.fillMaxSize()) {
                    AppNavGraph(navController = mainNavController)
                    LoaderOverlay(isVisible = isLoading)
                    // Session Expired Dialog
                    if (showSessionDialog) {
                        AlertErrorDialog(
                            message = "Your session has expired. Please log in again to continue.",
                            onDismiss = {},
                            onConfirm = {
                                showSessionDialog = false
                            }
                        )
                    }
                }
            }
        }
    }
}
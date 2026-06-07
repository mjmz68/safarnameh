package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.ui.SierViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Wrap in CompositionLocalProvider to enforce native RTL support
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val viewModel: SierViewModel = viewModel()
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route ?: "dashboard"

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            if (currentRoute in listOf("dashboard", "history", "entertainment")) {
                                NavigationBar(
                                    modifier = Modifier.testTag("bottom_nav")
                                ) {
                                    NavigationBarItem(
                                        selected = currentRoute == "dashboard",
                                        onClick = { 
                                            navController.navigate("dashboard") { 
                                                popUpTo("dashboard") { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true 
                                            } 
                                        },
                                        label = { Text("پیشخوان") },
                                        icon = { 
                                            Icon(
                                                imageVector = if (currentRoute == "dashboard") Icons.Default.Dashboard else Icons.Outlined.Dashboard, 
                                                contentDescription = "Dashboard"
                                            ) 
                                        }
                                    )
                                    NavigationBarItem(
                                        selected = currentRoute == "history",
                                        onClick = { 
                                            navController.navigate("history") { 
                                                popUpTo("dashboard") { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true 
                                            } 
                                        },
                                        label = { Text("بایگانی سیرها") },
                                        icon = { 
                                            Icon(
                                                imageVector = if (currentRoute == "history") Icons.Default.History else Icons.Outlined.History, 
                                                contentDescription = "History"
                                            ) 
                                        }
                                    )
                                    NavigationBarItem(
                                        selected = currentRoute == "entertainment",
                                        onClick = { 
                                            navController.navigate("entertainment") { 
                                                popUpTo("dashboard") { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true 
                                            } 
                                        },
                                        label = { Text("کافه ریلی") },
                                        icon = { 
                                            Icon(
                                                imageVector = if (currentRoute == "entertainment") Icons.Default.LocalLibrary else Icons.Outlined.LocalLibrary, 
                                                contentDescription = "Entertainment"
                                            ) 
                                        }
                                    )
                                }
                            }
                        },
                        floatingActionButton = {
                            if (currentRoute in listOf("dashboard", "history")) {
                                FloatingActionButton(
                                    onClick = {
                                        viewModel.selectSier(null)
                                        navController.navigate("add_edit_sier")
                                    },
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.testTag("add_sier_fab").padding(bottom = 16.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Trip")
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "dashboard",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("dashboard") {
                                DashboardScreen(
                                    viewModel = viewModel,
                                    onNavigateToAddSier = {
                                        viewModel.selectSier(null)
                                        navController.navigate("add_edit_sier")
                                    },
                                    onNavigateToDetails = { sier ->
                                        viewModel.selectSier(sier)
                                        navController.navigate("sier_details")
                                    },
                                    onNavigateToEdit = { sier ->
                                        viewModel.selectSier(sier)
                                        navController.navigate("add_edit_sier")
                                    }
                                )
                            }
                            composable("history") {
                                HistoryScreen(
                                    viewModel = viewModel,
                                    onNavigateToDetails = { sier ->
                                        viewModel.selectSier(sier)
                                        navController.navigate("sier_details")
                                    },
                                    onNavigateToEdit = { sier ->
                                        viewModel.selectSier(sier)
                                        navController.navigate("add_edit_sier")
                                    }
                                )
                            }
                            composable("entertainment") {
                                EntertainmentScreen(viewModel = viewModel)
                            }
                            composable("add_edit_sier") {
                                val selectedSier by viewModel.selectedSier.collectAsState()
                                SierFormScreen(
                                    viewModel = viewModel,
                                    editingSier = selectedSier,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable("sier_details") {
                                val selectedSier by viewModel.selectedSier.collectAsState()
                                selectedSier?.let { sier ->
                                    SierDetailsScreen(
                                        viewModel = viewModel,
                                        sier = sier,
                                        onNavigateBack = { navController.popBackStack() },
                                        onNavigateToEdit = { navController.navigate("add_edit_sier") }
                                    )
                                } ?: run {
                                    LaunchedEffect(Unit) {
                                        navController.popBackStack()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.example.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.remember
import com.example.data.AppDatabase
import com.example.data.SyncRepository
import com.example.data.SettingsRepository
import com.example.data.ThemePreference
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriveSyncApp() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { SyncRepository(database.folderPairDao(), database.syncLogDao()) }
    val settingsRepository = remember { SettingsRepository(context) }
    val driveAuthRepository = remember { com.example.data.DriveAuthRepository(context) }
    val viewModel: DriveSyncViewModel = viewModel(
        factory = DriveSyncViewModelFactory(repository, settingsRepository, driveAuthRepository)
    )

    val themePreference by viewModel.themePreference.collectAsStateWithLifecycle()
    val isDarkTheme = when(themePreference) {
        ThemePreference.DARK -> true
        ThemePreference.LIGHT -> false
        ThemePreference.SYSTEM -> isSystemInDarkTheme()
    }

    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    MyApplicationTheme(darkTheme = isDarkTheme) {
        Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(text = getTitleForRoute(currentRoute), fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: drawer or menu */ }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                actions = {
                    if (currentRoute == "home") {
                        IconButton(onClick = { /* TODO: profile or account */ }) {
                            Icon(Icons.Filled.List, contentDescription = "More", tint = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            if (currentRoute in listOf("home", "logs", "settings")) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        icon = { Icon(if (currentRoute == "home") Icons.Filled.TaskAlt else Icons.Outlined.TaskAlt, contentDescription = "Tasks") },
                        label = { Text("Tasks") },
                        selected = currentRoute == "home",
                        onClick = {
                            navController.navigate("home") {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onBackground,
                            selectedTextColor = MaterialTheme.colorScheme.onBackground,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(if (currentRoute == "logs") Icons.Filled.History else Icons.Outlined.History, contentDescription = "Logs") },
                        label = { Text("Logs") },
                        selected = currentRoute == "logs",
                        onClick = {
                            navController.navigate("logs") {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onBackground,
                            selectedTextColor = MaterialTheme.colorScheme.onBackground,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(if (currentRoute == "settings") Icons.Filled.Settings else Icons.Outlined.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") },
                        selected = currentRoute == "settings",
                        onClick = {
                            navController.navigate("settings") {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onBackground,
                            selectedTextColor = MaterialTheme.colorScheme.onBackground,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentRoute == "home") {
                FloatingActionButton(
                    onClick = { navController.navigate("addEdit/-1") },
                    containerColor = SyncingBlue,
                    contentColor = OnSyncingBlue,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Folder Pair")
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                HomeScreen(viewModel, onNavigateToEdit = { id ->
                    navController.navigate("addEdit/$id")
                })
            }
            composable(
                "addEdit/{pairId}",
                arguments = listOf(navArgument("pairId") { type = NavType.IntType })
            ) { backStackEntry ->
                val pairId = backStackEntry.arguments?.getInt("pairId") ?: -1
                AddEditTaskScreen(viewModel, pairId, onNavigateBack = { navController.popBackStack() })
            }
            composable("settings") {
                SettingsScreen(viewModel, onNavigateBack = { navController.popBackStack() })
            }
            composable("logs") {
                LogScreen(viewModel, onNavigateBack = { navController.popBackStack() })
            }
        }
    }
    }
}

fun getTitleForRoute(route: String?): String {
    return when {
        route?.startsWith("addEdit") == true -> "Edit Folder Pair"
        route == "settings" -> "Settings"
        route == "logs" -> "Sync Logs"
        else -> "DriveSync"
    }
}

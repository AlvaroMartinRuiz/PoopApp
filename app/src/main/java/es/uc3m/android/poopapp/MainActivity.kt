package es.uc3m.android.poopapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import es.uc3m.android.poopapp.firebase.AuthManager
import es.uc3m.android.poopapp.screens.AuthScreen
import es.uc3m.android.poopapp.screens.MapScreen
import es.uc3m.android.poopapp.screens.SettingsScreen
import es.uc3m.android.poopapp.screens.ShitShareScreen
import es.uc3m.android.poopapp.screens.TrackerScreen
import es.uc3m.android.poopapp.ui.theme.DarkBrown
import es.uc3m.android.poopapp.ui.theme.LightBrown
import es.uc3m.android.poopapp.ui.theme.MediumBrown
import es.uc3m.android.poopapp.ui.theme.PoopAppTheme
import es.uc3m.android.poopapp.ui.theme.White
import es.uc3m.android.poopapp.firebase.FirebaseManager
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PoopAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()

                    // Authentication dialog
                    AuthScreen(
                        showDialog = AuthManager.showAuthDialog,
                        onDismiss = { AuthManager.dismissAuthDialog() },
                        onLoginSuccess = { AuthManager.onLoginSuccess() },
                        firebaseManager = AuthManager.getFirebaseManager()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Map,
        Screen.Tracker,
        Screen.ShitShare,
        Screen.Settings
    )
    val firebaseManager = FirebaseManager()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = LightBrown
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = when (screen) {
                                    Screen.Map -> Icons.Filled.LocationOn
                                    Screen.Tracker -> Icons.Filled.List
                                    Screen.ShitShare -> Icons.Filled.Share
                                    Screen.Settings -> Icons.Filled.Settings
                                },
                                contentDescription = stringResource(screen.resourceId)
                            )
                        },
                        label = { Text(stringResource(screen.resourceId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkBrown,
                            selectedTextColor = DarkBrown,
                            indicatorColor = MediumBrown,
                            unselectedIconColor = DarkBrown,
                            unselectedTextColor = DarkBrown
                        ),
                        onClick = {
                            // For screens that require login, check authentication first
                            when (screen) {
                                Screen.ShitShare, Screen.Tracker -> {
                                    // These screens require login
                                    AuthManager.requireLogin {
                                        // This will be called after successful login
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                                else -> {
                                    // Other screens don't require login
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Map.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Map.route) { MapScreen() }
            composable(Screen.Tracker.route) { TrackerScreen() }
            composable(Screen.ShitShare.route) { ShitShareScreen(firebaseManager,userId) }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    isAuthenticated = AuthManager.isAuthenticated,
                    username = AuthManager.currentUserDisplayName,
                    onSignInClick = { AuthManager.requireLogin() },
                    onSignOutClick = { AuthManager.signOut() }
                )
            }
        }
    }
}

sealed class Screen(val route: String, val resourceId: Int) {
    object Map : Screen("map", R.string.title_map)
    object Tracker : Screen("tracker", R.string.title_tracker)
    object ShitShare : Screen("shitshare", R.string.title_shitshare)
    object Settings : Screen("settings", R.string.title_settings)
}
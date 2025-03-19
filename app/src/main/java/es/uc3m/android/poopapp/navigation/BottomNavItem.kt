package es.uc3m.android.poopapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Map : BottomNavItem(
        route = "map",
        title = "Map",
        icon = Icons.Default.LocationOn
    )
    
    object Tracker : BottomNavItem(
        route = "tracker",
        title = "Tracker",
        icon = Icons.Default.Timeline
    )
    
    object ShitShare : BottomNavItem(
        route = "shitshare",
        title = "ShitShare",
        icon = Icons.Default.Share
    )
    
    object Settings : BottomNavItem(
        route = "settings",
        title = "Settings",
        icon = Icons.Default.Settings
    )
} 
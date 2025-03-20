package es.uc3m.android.poopapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import es.uc3m.android.poopapp.ui.theme.*

@Composable
fun SettingsScreen(
    isAuthenticated: Boolean,
    username: String?,
    onSignInClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    // A state for the LazyColumn (optional: used for remembering scroll position)
    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
            .background(Teal)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)  // spacing between items
    ) {
        // Screen Title
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = DarkBrown
            )
        }

        // Profile Section
        item {
            if (isAuthenticated && username != null) {
                ProfileSection(username = username, onSignOutClick = onSignOutClick)
            } else {
                SignInPrompt(onSignInClick = onSignInClick)
            }
        }

        // General Settings header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "General Settings",
                style = MaterialTheme.typography.titleLarge,
                color = DarkBrown
            )
        }

        // General Settings card
        item {
            SettingsCard {
                // Notifications
                SettingItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = "Manage notification preferences"
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Dark Mode
                var darkModeEnabled by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dark Mode",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )

                    Switch(
                        checked = darkModeEnabled,
                        onCheckedChange = { darkModeEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DarkBrown,
                            checkedTrackColor = LightBrown,
                            uncheckedThumbColor = Gray,
                            uncheckedTrackColor = LightGray
                        )
                    )
                }
            }
        }

        // Account Settings (only if authenticated)
        if (isAuthenticated) {
            // Account Settings header
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Account Settings",
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkBrown
                )
            }

            // Account Settings card
            item {
                SettingsCard {
                    SettingItem(
                        icon = Icons.Default.Person,
                        title = "Profile",
                        subtitle = "Edit your profile information"
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingItem(
                        icon = Icons.Default.Lock,
                        title = "Privacy",
                        subtitle = "Manage your privacy settings"
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingItem(
                        icon = Icons.Default.Delete,
                        title = "Delete Account",
                        subtitle = "Permanently delete your account",
                        iconTint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = White,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
private fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: androidx.compose.ui.graphics.Color = DarkBrown
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = DarkBrown
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ProfileSection(
    username: String,
    onSignOutClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = White,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Picture
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = DarkBrown
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = username,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DarkBrown
                    )
                    Text(
                        text = "View Profile",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSignOutClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightBrown,
                    contentColor = DarkBrown
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out")
            }
        }
    }
}

@Composable
private fun SignInPrompt(
    onSignInClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = White,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = DarkBrown,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Sign in to access all features",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = DarkBrown
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Track your progress, sync across devices, and more",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSignInClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightBrown,
                    contentColor = DarkBrown
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Login,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign In")
            }
        }
    }
}
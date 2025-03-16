package es.uc3m.android.poopapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.uc3m.android.poopapp.firebase.AuthManager

@Composable
fun SettingsScreen(
    isAuthenticated: Boolean = false,
    username: String = "Guest",
    onSignInClick: () -> Unit = {},
    onSignOutClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Section
        ProfileSection(
            isAuthenticated = isAuthenticated,
            username = username,
            onSignInClick = onSignInClick,
            onSignOutClick = onSignOutClick
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // General Settings
        Text(
            text = "General Settings",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5C4033),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        
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
                        checkedThumbColor = Color(0xFF5C4033),
                        checkedTrackColor = Color(0xFFD4BEA5),
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.LightGray
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Account Settings (only if authenticated)
        if (isAuthenticated) {
            Text(
                text = "Account Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5C4033),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            
            SettingsCard {
                // Edit Profile
                SettingItem(
                    icon = Icons.Default.Person,
                    title = "Edit Profile",
                    subtitle = "Update your personal information"
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                // Privacy
                SettingItem(
                    icon = Icons.Default.PrivacyTip,
                    title = "Privacy",
                    subtitle = "Manage your data and privacy settings"
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Sign Out Button
            Button(
                onClick = onSignOutClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5C4033)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Sign Out",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Sign Out")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // App Info
        Text(
            text = "App Info",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5C4033),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        
        SettingsCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "PoopApp",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5C4033)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Version 1.0.0",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(onClick = { /* Open privacy policy */ }) {
                    Text(
                        text = "Privacy Policy",
                        color = Color(0xFF5C4033)
                    )
                }
                
                TextButton(onClick = { /* Open terms of service */ }) {
                    Text(
                        text = "Terms of Service",
                        color = Color(0xFF5C4033)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Troubleshooting Section
        Text(
            text = "Troubleshooting",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5C4033),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        
        SettingsCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Having issues with authentication?",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Button(
                    onClick = { 
                        // Reset authentication state
                        AuthManager.resetAuthState()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5C4033)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Authentication",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Reset Authentication Cache",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Text(
                    text = "Use this if you've changed Firebase projects or are experiencing sign-in issues",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ProfileSection(
    isAuthenticated: Boolean,
    username: String,
    onSignInClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F0E8)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF5C4033)),
                contentAlignment = Alignment.Center
            ) {
                if (isAuthenticated) {
                    // Display first letter of username or default icon
                    Text(
                        text = username.firstOrNull()?.toString() ?: "U",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Guest",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Username or Guest
            Text(
                text = if (isAuthenticated) username else "Guest User",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5C4033)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Status text
            Text(
                text = if (isAuthenticated) "Signed In" else "Not Signed In",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Sign In/Out button
            if (isAuthenticated) {
                // Add a small sign out button here instead of the main one
                TextButton(
                    onClick = onSignOutClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF5C4033)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Sign Out",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sign Out",
                        fontSize = 14.sp
                    )
                }
            } else {
                Button(
                    onClick = onSignInClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5C4033)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "Sign In",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFF5C4033),
            modifier = Modifier
                .padding(end = 16.dp)
                .size(24.dp)
        )
        
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
} 
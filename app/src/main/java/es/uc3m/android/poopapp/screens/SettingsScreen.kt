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
import es.uc3m.android.poopapp.ui.theme.poopAppColors
import es.uc3m.android.poopapp.firebase.AuthManager
import es.uc3m.android.poopapp.firebase.FirebaseManager
import es.uc3m.android.poopapp.data.model.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import androidx.compose.foundation.clickable

@Composable
fun SettingsScreen(
    isAuthenticated: Boolean,
    username: String?,
    onSignInClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    val lazyListState = rememberLazyListState()
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            if (isAuthenticated && username != null) {
                ProfileSection(
                    username = username,
                    onSignOutClick = onSignOutClick,
                    onEditProfileClick = { showEditProfileDialog = true }
                )
            } else {
                SignInPrompt(onSignInClick = onSignInClick)
            }
        }

        if (isAuthenticated) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Account Settings",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                SettingsCard {
                    SettingItem(
                        icon = Icons.Default.Person,
                        title = "Edit Profile",
                        subtitle = "Change your username",
                        onClick = { showEditProfileDialog = true }
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingItem(
                        icon = Icons.Default.Lock,
                        title = "Change Password",
                        subtitle = "Update your password",
                        onClick = { showChangePasswordDialog = true }
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingItem(
                        icon = Icons.Default.Delete,
                        title = "Delete Account",
                        subtitle = "Permanently delete your account",
                        iconTint = MaterialTheme.colorScheme.error,
                        onClick = { showDeleteAccountDialog = true }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "General Settings",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            SettingsCard {
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
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.poopAppColors.textPrimary
                    )

                    Switch(
                        checked = darkModeEnabled,
                        onCheckedChange = { darkModeEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.poopAppColors.buttonBackground,
                            checkedTrackColor = MaterialTheme.poopAppColors.buttonBackground.copy(alpha = 0.7f),
                            uncheckedThumbColor = MaterialTheme.colorScheme.surfaceVariant,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        )
                    )
                }
            }
        }
    }

    // Dialogs
    if (showEditProfileDialog) {
        EditProfileDialog(
            onDismiss = { showEditProfileDialog = false },
            onSave = { newUsername ->
                updateProfile(newUsername)
                showEditProfileDialog = false
            }
        )
    }

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onSave = { newPassword ->
                updatePassword(newPassword)
                showChangePasswordDialog = false
            }
        )
    }

    if (showDeleteAccountDialog) {
        DeleteAccountDialog(
            onDismiss = { showDeleteAccountDialog = false },
            onConfirm = {
                deleteAccount()
                showDeleteAccountDialog = false
            }
        )
    }
}

@Composable
private fun SettingsCard(
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.poopAppColors.staticCardBackground,
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
    iconTint: androidx.compose.ui.graphics.Color = MaterialTheme.poopAppColors.textPrimary,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
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
                color = MaterialTheme.poopAppColors.textPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.poopAppColors.textSecondary
            )
        }
    }
}

@Composable
private fun ProfileSection(
    username: String,
    onSignOutClick: () -> Unit,
    onEditProfileClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.poopAppColors.staticCardBackground,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.poopAppColors.buttonBackground
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.poopAppColors.buttonContent,
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
                        color = MaterialTheme.poopAppColors.textPrimary
                    )
                    Text(
                        text = "Edit Profile",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.poopAppColors.buttonBackground,
                        modifier = Modifier.clickable { onEditProfileClick() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSignOutClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
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
        color = MaterialTheme.poopAppColors.staticCardBackground,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = MaterialTheme.poopAppColors.textPrimary,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Sign in to access all features",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.poopAppColors.textPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Track your progress, sync across devices, and more",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.poopAppColors.textSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSignInClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.poopAppColors.buttonBackground,
                    contentColor = MaterialTheme.poopAppColors.buttonContent
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

@Composable
private fun EditProfileDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var username by remember { mutableStateOf(AuthManager.currentUserDisplayName) }
    var currentPassword by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.poopAppColors.popupCardBackground,
        title = { Text("Edit Profile", color = MaterialTheme.poopAppColors.textPrimary) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username", color = MaterialTheme.poopAppColors.textPrimary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.poopAppColors.textPrimary,
                        unfocusedTextColor = MaterialTheme.poopAppColors.textPrimary,
                        focusedContainerColor = MaterialTheme.poopAppColors.textFieldBackground,
                        unfocusedContainerColor = MaterialTheme.poopAppColors.textFieldBackground,
                        cursorColor = MaterialTheme.poopAppColors.textPrimary,
                        focusedBorderColor = MaterialTheme.poopAppColors.buttonBackground,
                        unfocusedBorderColor = MaterialTheme.poopAppColors.buttonBackground
                    )
                )
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Current Password", color = MaterialTheme.poopAppColors.textPrimary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.poopAppColors.textPrimary,
                        unfocusedTextColor = MaterialTheme.poopAppColors.textPrimary,
                        focusedContainerColor = MaterialTheme.poopAppColors.textFieldBackground,
                        unfocusedContainerColor = MaterialTheme.poopAppColors.textFieldBackground,
                        cursorColor = MaterialTheme.poopAppColors.textPrimary,
                        focusedBorderColor = MaterialTheme.poopAppColors.buttonBackground,
                        unfocusedBorderColor = MaterialTheme.poopAppColors.buttonBackground
                    )
                )
                if (showError) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (username.isBlank() || currentPassword.isBlank()) {
                        showError = true
                        errorMessage = "All fields are required"
                    } else {
                        onSave(username)
                    }
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.poopAppColors.textPrimary
                )
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.poopAppColors.textPrimary
                )
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.poopAppColors.popupCardBackground,
        title = { Text("Change Password", color = MaterialTheme.poopAppColors.textPrimary) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Current Password", color = MaterialTheme.poopAppColors.textPrimary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.poopAppColors.textPrimary,
                        unfocusedTextColor = MaterialTheme.poopAppColors.textPrimary,
                        focusedContainerColor = MaterialTheme.poopAppColors.textFieldBackground,
                        unfocusedContainerColor = MaterialTheme.poopAppColors.textFieldBackground,
                        cursorColor = MaterialTheme.poopAppColors.textPrimary,
                        focusedBorderColor = MaterialTheme.poopAppColors.buttonBackground,
                        unfocusedBorderColor = MaterialTheme.poopAppColors.buttonBackground
                    )
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password", color = MaterialTheme.poopAppColors.textPrimary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.poopAppColors.textPrimary,
                        unfocusedTextColor = MaterialTheme.poopAppColors.textPrimary,
                        focusedContainerColor = MaterialTheme.poopAppColors.textFieldBackground,
                        unfocusedContainerColor = MaterialTheme.poopAppColors.textFieldBackground,
                        cursorColor = MaterialTheme.poopAppColors.textPrimary,
                        focusedBorderColor = MaterialTheme.poopAppColors.buttonBackground,
                        unfocusedBorderColor = MaterialTheme.poopAppColors.buttonBackground
                    )
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm New Password", color = MaterialTheme.poopAppColors.textPrimary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.poopAppColors.textPrimary,
                        unfocusedTextColor = MaterialTheme.poopAppColors.textPrimary,
                        focusedContainerColor = MaterialTheme.poopAppColors.textFieldBackground,
                        unfocusedContainerColor = MaterialTheme.poopAppColors.textFieldBackground,
                        cursorColor = MaterialTheme.poopAppColors.textPrimary,
                        focusedBorderColor = MaterialTheme.poopAppColors.buttonBackground,
                        unfocusedBorderColor = MaterialTheme.poopAppColors.buttonBackground
                    )
                )
                if (showError) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
                        showError = true
                        errorMessage = "All fields are required"
                    } else if (newPassword != confirmPassword) {
                        showError = true
                        errorMessage = "New passwords do not match"
                    } else {
                        onSave(newPassword)
                    }
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.poopAppColors.textPrimary
                )
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.poopAppColors.textPrimary
                )
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun DeleteAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.poopAppColors.popupCardBackground,
        title = { Text("Delete Account", color = MaterialTheme.poopAppColors.textPrimary) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Are you sure you want to delete your account? This action cannot be undone.",
                    color = MaterialTheme.poopAppColors.textPrimary
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Enter your password to confirm", color = MaterialTheme.poopAppColors.textPrimary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.poopAppColors.textPrimary,
                        unfocusedTextColor = MaterialTheme.poopAppColors.textPrimary,
                        focusedContainerColor = MaterialTheme.poopAppColors.textFieldBackground,
                        unfocusedContainerColor = MaterialTheme.poopAppColors.textFieldBackground,
                        cursorColor = MaterialTheme.poopAppColors.textPrimary,
                        focusedBorderColor = MaterialTheme.poopAppColors.buttonBackground,
                        unfocusedBorderColor = MaterialTheme.poopAppColors.buttonBackground
                    )
                )
                if (showError) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (password.isBlank()) {
                        showError = true
                        errorMessage = "Password is required"
                    } else {
                        onConfirm()
                    }
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete Account")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.poopAppColors.textPrimary
                )
            ) {
                Text("Cancel")
            }
        }
    )
}

private fun updateProfile(newUsername: String) {
    val firebaseManager = AuthManager.getFirebaseManager()
    val currentUser = AuthManager.currentUser

    if (currentUser != null) {
        // Update Firebase Auth profile
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newUsername)
            .build()

        currentUser.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Update Firestore
                    updateFirestoreProfile(currentUser, newUsername)
                }
            }
    }
}

private fun updatePassword(newPassword: String) {
    val currentUser = AuthManager.currentUser
    if (currentUser != null) {
        currentUser.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Password updated successfully
                }
            }
    }
}

private fun deleteAccount() {
    val currentUser = AuthManager.currentUser
    if (currentUser != null) {
        currentUser.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Account deleted successfully
                    AuthManager.signOut()
                }
            }
    }
}

private fun updateFirestoreProfile(user: FirebaseUser, newUsername: String) {
    val firebaseManager = AuthManager.getFirebaseManager()
    val updatedUser = User(
        uid = user.uid,
        email = user.email ?: "",
        displayName = newUsername,
        createdAt = com.google.firebase.Timestamp.now()
    )

    firebaseManager.updateUserProfile(
        updatedUser,
        onSuccess = {
            // Profile updated successfully
            AuthManager.resetAuthState()
        },
        onError = { error ->
            // Handle error
        }
    )
}
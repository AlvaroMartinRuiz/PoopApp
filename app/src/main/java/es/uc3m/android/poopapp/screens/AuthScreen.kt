package es.uc3m.android.poopapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import es.uc3m.android.poopapp.R
import es.uc3m.android.poopapp.firebase.FirebaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onLoginSuccess: () -> Unit,
    firebaseManager: FirebaseManager = FirebaseManager(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    if (!showDialog) return
    
    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    
    // Force dialog close after timeout (15 seconds)
    LaunchedEffect(isLoading) {
        if (isLoading) {
            println("📱 PoopApp: Starting loading timeout timer")
            delay(15000) // 15 seconds timeout
            if (isLoading) {
                println("📱 PoopApp: Loading timeout reached! Force closing dialog.")
                isLoading = false
                errorMessage = "Request timed out. Please try again."
            }
        }
    }
    
    val focusManager = LocalFocusManager.current
    
    // Show the loading dialog when isLoading is true
    LoadingDialog(
        message = if (isLogin) "Signing in..." else "Creating your account...",
        isLoading = isLoading,
        onDismiss = {
            println("📱 PoopApp: Loading dialog manually dismissed")
            isLoading = false
        }
    )
    
    Dialog(onDismissRequest = { 
        if (!isLoading) onDismiss() 
    }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // App Logo/Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(40.dp))
                        .background(Color(0xFF5C4033)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "💩",
                        fontSize = 40.sp,
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Title
                Text(
                    text = if (isLogin) "Welcome Back!" else "Join PoopApp",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5C4033)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Subtitle
                Text(
                    text = if (isLogin) "Sign in to continue" else "Create your account",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Error message
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                
                // Username field (only for registration)
                if (!isLogin) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Username") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Down)
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF5C4033),
                            focusedLabelColor = Color(0xFF5C4033),
                            unfocusedBorderColor = Color(0xFFD4BEA5)
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF5C4033),
                        focusedLabelColor = Color(0xFF5C4033),
                        unfocusedBorderColor = Color(0xFFD4BEA5)
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            // Attempt login/register
                            if (isLogin) {
                                if (validateLoginInputs(email, password)) {
                                    handleLogin(
                                        email, password, 
                                        firebaseManager, coroutineScope,
                                        onSuccess = {
                                            isLoading = false
                                            onLoginSuccess()
                                            onDismiss()
                                        },
                                        onError = {
                                            isLoading = false
                                            errorMessage = it
                                        },
                                        onLoading = {
                                            isLoading = true
                                            errorMessage = null
                                        }
                                    )
                                } else {
                                    errorMessage = "Please enter valid email and password"
                                }
                            } else {
                                if (validateRegisterInputs(email, password, username)) {
                                    println("📱 PoopApp: Sign Up button clicked, starting registration")
                                    handleRegister(
                                        email, password, username,
                                        firebaseManager, coroutineScope,
                                        onSuccess = {
                                            println("📱 PoopApp: Registration success callback received")
                                            isLoading = false
                                            // Using AuthManager's onLoginSuccess to manage authentication state
                                            es.uc3m.android.poopapp.firebase.AuthManager.onLoginSuccess()
                                            println("📱 PoopApp: AuthManager.onLoginSuccess called")
                                            onLoginSuccess()
                                            println("📱 PoopApp: Component onLoginSuccess called")
                                            onDismiss()
                                            println("📱 PoopApp: Dialog dismissed")
                                        },
                                        onError = { errorMsg ->
                                            println("📱 PoopApp: Registration error callback received: $errorMsg")
                                            isLoading = false
                                            errorMessage = errorMsg
                                        },
                                        onLoading = {
                                            println("📱 PoopApp: Setting loading state to true")
                                            isLoading = true
                                            errorMessage = null
                                        }
                                    )
                                } else {
                                    errorMessage = "Please fill all fields correctly"
                                }
                            }
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF5C4033),
                        focusedLabelColor = Color(0xFF5C4033),
                        unfocusedBorderColor = Color(0xFFD4BEA5)
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Forgot password (only for login)
                if (isLogin) {
                    TextButton(
                        onClick = { /* Handle forgot password */ },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = "Forgot Password?",
                            color = Color(0xFF5C4033)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Login/Register button
                Button(
                    onClick = {
                        errorMessage = null
                        isLoading = true // Set loading to true immediately
                        
                        if (isLogin) {
                            // Handle login
                            if (email.isBlank() || password.isBlank()) {
                                errorMessage = "Please fill in all fields"
                                isLoading = false // Reset loading state
                                return@Button
                            }
                            
                            println("📱 PoopApp: Login button clicked")
                            
                            coroutineScope.launch {
                                try {
                                    firebaseManager.signInWithEmailPassword(
                                        email = email,
                                        password = password,
                                        onSuccess = { user ->
                                            try {
                                                println("📱 PoopApp: Login successful for ${user.email}")
                                                isLoading = false // Reset loading state
                                                onLoginSuccess()
                                                onDismiss()
                                            } catch (e: Exception) {
                                                println("📱 PoopApp: Exception in login success callback: ${e.message}")
                                                isLoading = false // Reset loading state
                                                onLoginSuccess()
                                                onDismiss()
                                            }
                                        },
                                        onError = { errorMsg ->
                                            try {
                                                println("📱 PoopApp: Login error: $errorMsg")
                                                errorMessage = errorMsg
                                                isLoading = false // Reset loading state
                                            } catch (e: Exception) {
                                                println("📱 PoopApp: Exception in login error callback: ${e.message}")
                                                isLoading = false // Reset loading state
                                            }
                                        }
                                    )
                                } catch (e: Exception) {
                                    println("📱 PoopApp: Login exception: ${e.message}")
                                    errorMessage = "Login failed: ${e.message}"
                                    isLoading = false // Reset loading state
                                }
                            }
                        } else {
                            // Handle registration
                            if (email.isBlank() || password.isBlank() || username.isBlank()) {
                                errorMessage = "Please fill in all fields"
                                isLoading = false // Reset loading state
                                return@Button
                            }
                            println("📱 PoopApp: Register button clicked")
                            
                            handleRegister(
                                email = email,
                                password = password,
                                username = username,
                                firebaseManager = firebaseManager,
                                coroutineScope = coroutineScope,
                                onSuccess = {
                                    try {
                                        println("📱 PoopApp: Registration success callback received")
                                        isLoading = false // Ensure loading is turned off
                                        onLoginSuccess()
                                        onDismiss()
                                    } catch (e: Exception) {
                                        println("📱 PoopApp: Exception in registration success callback: ${e.message}")
                                        isLoading = false // Ensure loading is turned off
                                        onLoginSuccess()
                                        onDismiss()
                                    }
                                },
                                onError = { error ->
                                    try {
                                        println("📱 PoopApp: Registration error callback received: $error")
                                        errorMessage = error
                                        isLoading = false // Ensure loading is turned off
                                    } catch (e: Exception) {
                                        println("📱 PoopApp: Exception in registration error callback: ${e.message}")
                                        isLoading = false // Ensure loading is turned off
                                    }
                                },
                                onLoading = {
                                    // Loading already set to true above
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5C4033)
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = if (isLogin) "Sign In" else "Sign Up",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Switch between login and register
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isLogin) "Don't have an account?" else "Already have an account?",
                        color = Color.Gray
                    )
                    TextButton(onClick = { 
                        isLogin = !isLogin
                        errorMessage = null
                    }) {
                        Text(
                            text = if (isLogin) "Sign Up" else "Sign In",
                            color = Color(0xFF5C4033),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Add a cancel button when loading takes too long
                if (isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        onClick = { 
                            println("📱 PoopApp: User manually canceled operation")
                            isLoading = false 
                            onDismiss()
                        }
                    ) {
                        Text(
                            text = "Cancel",
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

private fun validateLoginInputs(email: String, password: String): Boolean {
    return email.isNotBlank() && password.isNotBlank() && email.contains("@")
}

private fun validateRegisterInputs(email: String, password: String, username: String): Boolean {
    return email.isNotBlank() && password.isNotBlank() && username.isNotBlank() 
        && email.contains("@") && password.length >= 6
}

private fun handleLogin(
    email: String,
    password: String,
    firebaseManager: FirebaseManager,
    coroutineScope: CoroutineScope,
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
    onLoading: () -> Unit
) {
    onLoading()
    coroutineScope.launch {
        firebaseManager.signInWithEmailPassword(
            email = email,
            password = password,
            onSuccess = { onSuccess() },
            onError = { onError(it) }
        )
    }
}

private fun handleRegister(
    email: String,
    password: String,
    username: String,
    firebaseManager: FirebaseManager,
    coroutineScope: CoroutineScope,
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
    onLoading: () -> Unit
) {
    onLoading()
    
    // Create a job we can cancel if needed
    val registrationJob = coroutineScope.launch {
        try {
            println("📱 PoopApp: Starting registration for $email")
            
            // Add a backup timeout at this level too
            val registrationTimeout = launch {
                delay(10000) // 10 second timeout as backup
                println("📱 PoopApp: Registration coroutine timeout reached!")
                // We don't need to do anything here, the LaunchedEffect will handle UI
            }
            
            firebaseManager.registerWithEmailPassword(
                email = email,
                password = password,
                username = username,
                onSuccess = { user -> 
                    try {
                        println("📱 PoopApp: Registration successful for ${user.email}")
                        // Cancel the backup timeout
                        registrationTimeout.cancel()
                        // Make sure to call onSuccess on the main thread
                        onSuccess()
                    } catch (e: Exception) {
                        println("📱 PoopApp: Exception in onSuccess callback: ${e.message}")
                        onSuccess() // Try again in case the first call failed
                    }
                },
                onError = { errorMsg -> 
                    try {
                        println("📱 PoopApp: Registration error: $errorMsg")
                        // Cancel the backup timeout
                        registrationTimeout.cancel()
                        onError(errorMsg)
                    } catch (e: Exception) {
                        println("📱 PoopApp: Exception in onError callback: ${e.message}")
                        onError("An error occurred: ${e.message}")
                    }
                }
            )
        } catch (e: Exception) {
            // Catch any uncaught exceptions
            println("📱 PoopApp: Uncaught exception in registration: ${e.message}")
            try {
                onError("An unexpected error occurred: ${e.message}")
            } catch (callbackException: Exception) {
                println("📱 PoopApp: Exception calling onError: ${callbackException.message}")
                // Nothing more we can do here
            }
        }
    }
    
    // Safety mechanism: if coroutine is cancelled, ensure we're not stuck in loading state
    registrationJob.invokeOnCompletion { throwable ->
        if (throwable != null) {
            println("📱 PoopApp: Registration job cancelled with error: ${throwable.message}")
            try {
                onError("Registration was interrupted")
            } catch (e: Exception) {
                // Last resort, can't do anything else
                println("📱 PoopApp: Failed to report job cancellation: ${e.message}")
            }
        }
    }
}

@Composable
fun LoadingDialog(
    message: String,
    isLoading: Boolean,
    onDismiss: () -> Unit
) {
    // Add a timeout effect to automatically dismiss after 15 seconds
    LaunchedEffect(isLoading) {
        if (isLoading) {
            println("📱 PoopApp: Loading dialog shown with timeout")
            delay(15000) // 15 second timeout
            println("📱 PoopApp: Loading dialog timeout reached - forcing dismiss")
            onDismiss()
        }
    }
    
    if (isLoading) {
        AlertDialog(
            onDismissRequest = { /* Prevent dismiss on outside click */ },
            title = { Text(text = "Please Wait") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = message)
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(
                        color = Color(0xFF8D6E63),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        onClick = {
                            println("📱 PoopApp: Cancel button clicked - manually dismissing")
                            onDismiss()
                        }
                    ) {
                        Text("Cancel", color = Color(0xFF8D6E63))
                    }
                }
            },
            confirmButton = { }
        )
    }
} 
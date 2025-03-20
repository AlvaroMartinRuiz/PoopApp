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
import es.uc3m.android.poopapp.ui.theme.*
import kotlinx.coroutines.CoroutineScope
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

    val focusManager = LocalFocusManager.current

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
                        .background(DarkBrown),
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
                    color = DarkBrown
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle
                Text(
                    text = if (isLogin) "Sign in to continue" else "Create your account",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Gray,
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
                            focusedBorderColor = DarkBrown,
                            focusedLabelColor = DarkBrown,
                            unfocusedBorderColor = LightBrown
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
                        focusedBorderColor = DarkBrown,
                        focusedLabelColor = DarkBrown,
                        unfocusedBorderColor = LightBrown
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
                                    handleRegister(
                                        email, password, username,
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
                                    errorMessage = "Please fill all fields correctly"
                                }
                            }
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkBrown,
                        focusedLabelColor = DarkBrown,
                        unfocusedBorderColor = LightBrown
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
                            color = DarkBrown
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Login/Register button
                Button(
                    onClick = {
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
                                handleRegister(
                                    email, password, username,
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
                                errorMessage = "Please fill all fields correctly"
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightBrown,
                        contentColor = DarkBrown
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = DarkBrown,
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
                        color = Gray
                    )
                    TextButton(onClick = {
                        isLogin = !isLogin
                        errorMessage = null
                    }) {
                        Text(
                            text = if (isLogin) "Sign Up" else "Sign In",
                            color = DarkBrown,
                            fontWeight = FontWeight.Bold
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
    coroutineScope.launch {
        firebaseManager.registerWithEmailPassword(
            email = email,
            password = password,
            username = username,
            onSuccess = { onSuccess() },
            onError = { onError(it) }
        )
    }
}
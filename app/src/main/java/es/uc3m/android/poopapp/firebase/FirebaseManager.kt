package es.uc3m.android.poopapp.firebase

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseManager {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    // Check if user is already logged in
    fun getCurrentUser(): FirebaseUser? = auth.currentUser
    
    // Sign in with email and password
    fun signInWithEmailPassword(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        println("📱 PoopApp: Firebase - Starting sign-in process for $email")
        
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            println("📱 PoopApp: Firebase - User signed in successfully: ${user.uid}")
                            onSuccess(user)
                        } else {
                            println("📱 PoopApp: Firebase - User is null after successful sign-in")
                            onError("Sign-in appeared successful but no user was returned")
                        }
                    } else {
                        val errorMsg = task.exception?.message ?: "Unknown error during sign-in"
                        println("📱 PoopApp: Firebase - Sign-in failed: $errorMsg")
                        onError(errorMsg)
                    }
                }
                .addOnFailureListener { exception ->
                    println("📱 PoopApp: Firebase - Sign-in failure listener: ${exception.message}")
                    onError(exception.message ?: "Sign-in failed")
                }
        } catch (e: Exception) {
            println("📱 PoopApp: Firebase - Exception during sign-in: ${e.message}")
            onError("Exception during sign-in: ${e.message}")
        }
    }
    
    // Register new user with email and password
    fun registerWithEmailPassword(
        email: String,
        password: String,
        username: String,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        println("📱 PoopApp: Firebase - Starting registration process for $email")
        
        try {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        println("📱 PoopApp: Firebase - User created successfully: ${user?.uid}")
                        
                        if (user != null) {
                            // Create a ProfileUpdates object to set the display name
                            try {
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build()

                                // Update the user's profile with the display name
                                user.updateProfile(profileUpdates)
                                    .addOnCompleteListener { profileTask ->
                                        if (profileTask.isSuccessful) {
                                            println("📱 PoopApp: Firebase - Display name set successfully to $username")
                                            // Even if setting profile fails, we've created the account, so consider it a success
                                            onSuccess(user)
                                        } else {
                                            println("📱 PoopApp: Firebase - Failed to set display name: ${profileTask.exception?.message}")
                                            // Even if setting profile fails, we've created the account, so consider it a success
                                            onSuccess(user)
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        println("📱 PoopApp: Firebase - Profile update failure: ${exception.message}")
                                        // Even if profile update fails, the registration succeeded
                                        onSuccess(user)
                                    }
                            } catch (e: Exception) {
                                println("📱 PoopApp: Firebase - Exception during profile update: ${e.message}")
                                // Even if we couldn't set the profile, the account was created
                                onSuccess(user)
                            }
                        } else {
                            println("📱 PoopApp: Firebase - User is null after successful registration")
                            onError("Registration appeared successful but no user was returned")
                        }
                    } else {
                        val errorMsg = task.exception?.message ?: "Unknown error during registration"
                        println("📱 PoopApp: Firebase - Registration failed: $errorMsg")
                        
                        // Check if the error is because the email is already in use
                        if (errorMsg.contains("email address is already in use", ignoreCase = true)) {
                            // Check if we should try to sign in instead
                            println("📱 PoopApp: Firebase - Email already in use, attempting to sign in")
                            signInWithEmailPassword(email, password, onSuccess, onError)
                        } else {
                            onError(errorMsg)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    println("📱 PoopApp: Firebase - Registration failure listener: ${exception.message}")
                    onError(exception.message ?: "Registration failed")
                }
        } catch (e: Exception) {
            println("📱 PoopApp: Firebase - Exception during registration: ${e.message}")
            onError("Exception during registration: ${e.message}")
        }
    }
    
    // Sign out
    fun signOut() {
        auth.signOut()
    }
    
    // Reset password
    suspend fun resetPassword(
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            auth.sendPasswordResetEmail(email).await()
            onSuccess()
        } catch (e: Exception) {
            onError(e.message ?: "Failed to send reset email")
        }
    }
    
    // Clear Firebase Auth cache completely
    fun clearAuthCache() {
        // Sign out current user first
        auth.signOut()
        
        // Get a new instance of FirebaseAuth to ensure cache is cleared
        FirebaseAuth.getInstance().apply { 
            signOut()
        }
    }
} 
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
    suspend fun signInWithEmailPassword(
        email: String, 
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                onSuccess(user)
            } ?: onError("Login failed, please try again")
        } catch (e: Exception) {
            onError(e.message ?: "Login failed, please try again")
        }
    }
    
    // Register new user with email and password
    suspend fun registerWithEmailPassword(
        email: String,
        password: String,
        username: String,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                // Set display name in Firebase Auth
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()
                
                user.updateProfile(profileUpdates).await()
                
                // Save user details to Firestore
                val userMap = hashMapOf(
                    "uid" to user.uid,
                    "email" to email,
                    "username" to username,
                    "createdAt" to System.currentTimeMillis()
                )
                
                firestore.collection("users").document(user.uid)
                    .set(userMap).await()
                
                // Reload user to get updated profile
                user.reload().await()
                
                // Get fresh user instance with updated profile
                auth.currentUser?.let { freshUser ->
                    onSuccess(freshUser)
                } ?: onSuccess(user) // Fallback to original user if refresh fails
                
            } ?: onError("Registration failed, please try again")
        } catch (e: Exception) {
            onError(e.message ?: "Registration failed, please try again")
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
} 
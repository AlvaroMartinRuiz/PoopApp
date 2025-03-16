package es.uc3m.android.poopapp.firebase

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseUser

/**
 * Singleton class to manage authentication state across the app
 */
object AuthManager {
    private val firebaseManager = FirebaseManager()
    
    // Authentication state
    var currentUser by mutableStateOf<FirebaseUser?>(null)
        private set
    
    var isAuthenticated by mutableStateOf(false)
        private set
        
    // User display name for UI
    var currentUserDisplayName by mutableStateOf("Guest")
        private set
    
    // Dialog visibility state
    var showAuthDialog by mutableStateOf(false)
        private set
    
    // Callback to execute after successful login
    private var pendingAction: (() -> Unit)? = null
    
    init {
        // Initialize with current Firebase user
        resetAuthState()
    }
    
    /**
     * Show authentication dialog with optional callback to execute after successful login
     */
    fun requireLogin(onLoginSuccess: (() -> Unit)? = null) {
        if (isAuthenticated) {
            // User is already logged in, execute callback immediately
            onLoginSuccess?.invoke()
        } else {
            // Store callback for later execution
            pendingAction = onLoginSuccess
            showAuthDialog = true
        }
    }
    
    /**
     * Called after successful login/registration
     */
    fun onLoginSuccess() {
        println("📱 PoopApp: AuthManager - onLoginSuccess called")
        currentUser = firebaseManager.getCurrentUser()
        println("📱 PoopApp: AuthManager - Current user: ${currentUser?.email}")
        
        isAuthenticated = currentUser != null
        if (isAuthenticated) {
            println("📱 PoopApp: AuthManager - User is authenticated")
            updateDisplayName()
            println("📱 PoopApp: AuthManager - DisplayName updated to: $currentUserDisplayName")
        } else {
            println("📱 PoopApp: AuthManager - Warning: onLoginSuccess called but user is null")
        }
        
        // Execute pending action even if something went wrong, 
        // the UI will handle displaying appropriate state
        pendingAction?.let { action ->
            println("📱 PoopApp: AuthManager - Executing pending action")
            action()
        }
        pendingAction = null
    }
    
    /**
     * Update the display name from the current user
     */
    private fun updateDisplayName() {
        currentUserDisplayName = currentUser?.displayName ?: 
                               currentUser?.email?.substringBefore('@') ?: 
                               "User"
    }
    
    /**
     * Dismiss the authentication dialog
     */
    fun dismissAuthDialog() {
        showAuthDialog = false
        pendingAction = null
    }
    
    /**
     * Sign out the current user
     */
    fun signOut() {
        firebaseManager.signOut()
        currentUser = null
        isAuthenticated = false
        currentUserDisplayName = "Guest"
    }
    
    /**
     * Completely reset authentication state and clear all cached data
     * Use this when switching Firebase projects or troubleshooting auth issues
     */
    fun resetAuthState() {
        // Clear Firebase Auth cache
        firebaseManager.clearAuthCache()
        
        // Reset local state
        currentUser = firebaseManager.getCurrentUser()
        isAuthenticated = currentUser != null
        
        // Initialize display name if user is already logged in
        if (isAuthenticated) {
            updateDisplayName()
        } else {
            currentUserDisplayName = "Guest"
        }
    }
    
    /**
     * Get Firebase manager for direct Firebase operations
     */
    fun getFirebaseManager(): FirebaseManager {
        return firebaseManager
    }
} 
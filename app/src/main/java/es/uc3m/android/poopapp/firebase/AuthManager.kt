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
        currentUser = firebaseManager.getCurrentUser()
        isAuthenticated = currentUser != null
        // Initialize display name if user is already logged in
        if (isAuthenticated) {
            updateDisplayName()
        }
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
        currentUser = firebaseManager.getCurrentUser()
        isAuthenticated = true
        updateDisplayName()
        pendingAction?.invoke()
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
     * Get Firebase manager for direct Firebase operations
     */
    fun getFirebaseManager(): FirebaseManager {
        return firebaseManager
    }
} 
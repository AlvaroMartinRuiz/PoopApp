package es.uc3m.android.poopapp.firebase

import android.content.Context
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import es.uc3m.android.poopapp.data.model.PoopLog
import es.uc3m.android.poopapp.data.model.User
import es.uc3m.android.poopapp.data.model.League
import es.uc3m.android.poopapp.data.model.LeaderboardEntry


import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
                                        saveUserToFirestore(user)
                                    }
                                    .addOnFailureListener { exception ->
                                        println("📱 PoopApp: Firebase - Profile update failure: ${exception.message}")
                                        // Even if profile update fails, the registration succeeded
                                        onSuccess(user)
                                        saveUserToFirestore(user)
                                    }
                            } catch (e: Exception) {
                                println("📱 PoopApp: Firebase - Exception during profile update: ${e.message}")
                                // Even if we couldn't set the profile, the account was created
                                onSuccess(user)
                                saveUserToFirestore(user)
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

    private fun saveUserToFirestore(user: FirebaseUser) {
        // Use User data class

        val userData = User(
            uid = user.uid,
            email = user.email ?: "",
            displayName = user.displayName ?: "Anonymous",
            createdAt = Timestamp.now()
        )

        val userRef = firestore.collection("users").document(user.uid)

        userRef.set(userData) // Now saving as a structured User object
            .addOnSuccessListener {
                println("User saved to Firestore!")
            }
            .addOnFailureListener { e ->
                println(" Error saving user: ${e.message}")
            }
    }

    fun savePoopLog(userId: String, poopLog: PoopLog, onSuccess: () -> Unit, onError: (String) -> Unit) {
        firestore.collection("users").document(userId)
            .collection("poop_logs") //  Subcollection created dynamically
            .add(poopLog) // Auto-generates document ID
            .addOnSuccessListener {
                println(" Poop log added successfully!")
                onSuccess()
            }
            .addOnFailureListener { e ->
                println(" Error saving poop log: ${e.message}")
                onError(e.message ?: "Unknown error")
            }
    }


    fun createLeague(
        name: String,
        createdBy: String,
        onSuccess: (League) -> Unit, // 🔥 Now returns the created League
        onError: (String) -> Unit
    ) {
        val leagueData = hashMapOf(
            "name" to name,
            "createdBy" to createdBy,
            "members" to listOf(createdBy),
            "createdAt" to Timestamp.now(),
            "isPrivate" to true
        )

        firestore.collection("leagues")
            .add(leagueData)
            .addOnSuccessListener { documentRef ->
                val newLeague = League(
                    id = documentRef.id, // ✅ Get Firestore-generated ID
                    name = name,
                    createdBy = createdBy,
                    members = listOf(createdBy),
                    createdAt = Timestamp.now(),
                    isPrivate = true
                )
                onSuccess(newLeague) // ✅ Pass the League object
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Unknown error")
            }
    }
    suspend fun getLeaderboardEntries(leagueId: String): List<LeaderboardEntry> {
        return try {
            firestore.collection("leaderboard_entries")
                .whereEqualTo("leagueId", leagueId)
                .get()
                .await()
                .toObjects(LeaderboardEntry::class.java)
        } catch (e: Exception) {
            println("❌ Error fetching leaderboard entries: ${e.message}")
            emptyList()
        }
    }

     suspend fun getLeagues(): List<League> {
        return try {
            firestore.collection("leagues")
                .get()
                .await()
                .toObjects(League::class.java) // Ensure correct type conversion
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun createLeague(name: String, createdBy: String) {
        val league = League(
            id = "", // Let Firestore auto-generate
            name = name,
            createdBy = createdBy,
            members = listOf(createdBy)
        )
        firestore.collection("leagues").add(league)
    }

    suspend fun getLeagueByInviteCode(code: String): League? {
        return try {
            val result = firestore.collection("leagues")
                .whereEqualTo("inviteCode", code)
                .get()
                .await()

            if (!result.isEmpty) {
                result.documents[0].toObject(League::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun joinLeague(userId: String, league: League) {
        val updatedMembers = league.members + userId
        firestore.collection("leagues").document(league.id)
            .update("members", updatedMembers)
    }
    fun updateUserProfile(
        user: User,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            // Reference to the user's document in Firestore
            val userRef = firestore.collection("users").document(user.uid)

            // Write the updated User object to Firestore
            // If you need to preserve certain fields (like createdAt), you can do partial updates or use merge()
            userRef.set(user)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onError(e.message ?: "Failed to update user profile")
                }

        } catch (e: Exception) {
            onError("Exception during profile update: ${e.message}")
        }
    }
}


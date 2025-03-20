package es.uc3m.android.poopapp.firebase
// Used for pooplogs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import es.uc3m.android.poopapp.data.model.PoopLog
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    // User collection reference
    private fun getUserRef(userId: String) = db.collection("users").document(userId)
    
    // Pooplog collection reference for a user
    private fun getPoopLogsRef(userId: String) = getUserRef(userId).collection("pooplogs")
    fun savePoopLog(userId: String, poopLog: PoopLog, onSuccess: () -> Unit, onError: (String) -> Unit) {
        db.collection("users").document(userId)
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
    // Add a new pooplog with proper ID generation
    suspend fun addPoopLog(poopLog: PoopLog): String {
        val currentUserId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
        
        // Create document reference first to get the ID
        val poopLogRef = getPoopLogsRef(currentUserId).document()
        
        // Update the poopLog with the generated ID
        val updatedPoopLog = poopLog.copy(
            id = poopLogRef.id,
            userId = currentUserId
        )
        
        // Save to Firestore
        return suspendCoroutine { continuation ->
            poopLogRef.set(updatedPoopLog.toMap())
                .addOnSuccessListener {
                    // Update user streaks after adding a log
                    updateUserStreaks(currentUserId)
                    continuation.resume(poopLogRef.id)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }
    
    // Get all pooplogs for the current user
    suspend fun getUserPoopLogs(): List<PoopLog> {
        val currentUserId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
        
        return suspendCoroutine { continuation ->
            getPoopLogsRef(currentUserId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val poopLogs = querySnapshot.documents.map { document ->
                        PoopLog.fromMap(document.data ?: mapOf(), document.id)
                    }
                    continuation.resume(poopLogs)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }
    
    // Calculate and update user streaks
    private fun updateUserStreaks(userId: String) {
        getPoopLogsRef(userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val poopLogs = querySnapshot.documents.mapNotNull { document ->
                    val data = document.data ?: return@mapNotNull null
                    PoopLog.fromMap(data, document.id)
                }
                
                // Calculate streaks
                val (currentStreak, longestStreak) = calculateStreaks(poopLogs)
                
                // Update user document with streaks
                getUserRef(userId).update(
                    mapOf(
                        "currentStreak" to currentStreak,
                        "longestStreak" to longestStreak
                    )
                )
            }
    }
    
    // Calculate current and longest streaks from pooplogs
    fun calculateStreaks(poopLogs: List<PoopLog>): Pair<Int, Int> {
        if (poopLogs.isEmpty()) return Pair(0, 0)
        
        // Sort logs by timestamp (newest first)
        val sortedLogs = poopLogs.sortedByDescending { it.timestamp }
        
        // Get unique dates (day precision)
        val dates = sortedLogs.map { log ->
            val calendar = Calendar.getInstance()
            calendar.time = log.timestamp
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.time
        }.distinct()
        
        // Check if there's an entry for today
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
        
        // Calculate current streak
        var currentStreak = 0
        var checkDate = today
        var streakBroken = false
        
        while (!streakBroken) {
            if (dates.contains(checkDate)) {
                currentStreak++
                
                // Move to previous day
                val calendar = Calendar.getInstance()
                calendar.time = checkDate
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                checkDate = calendar.time
            } else {
                streakBroken = true
            }
        }
        
        // If no entry for today, check if streak can continue from yesterday
        if (!dates.contains(today) && currentStreak == 0) {
            val yesterday = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
            
            if (dates.contains(yesterday)) {
                checkDate = yesterday
                streakBroken = false
                
                while (!streakBroken) {
                    if (dates.contains(checkDate)) {
                        currentStreak++
                        
                        // Move to previous day
                        val calendar = Calendar.getInstance()
                        calendar.time = checkDate
                        calendar.add(Calendar.DAY_OF_YEAR, -1)
                        checkDate = calendar.time
                    } else {
                        streakBroken = true
                    }
                }
            }
        }
        
        // Calculate longest streak (looking at all sequences)
        var longestStreak = currentStreak
        var tempStreak = 0
        
        for (i in 0 until dates.size - 1) {
            val currentDate = dates[i]
            val nextDate = dates[i + 1]
            
            val diffInMillis = currentDate.time - nextDate.time
            val diffInDays = diffInMillis / (1000 * 60 * 60 * 24)
            
            if (diffInDays == 1L) {
                // Consecutive days
                tempStreak++
            } else {
                // Streak broken
                tempStreak = 0
            }
            
            if (tempStreak > longestStreak) {
                longestStreak = tempStreak
            }
        }
        
        // Add 1 to longest streak to account for the first day
        if (dates.isNotEmpty()) {
            longestStreak++
        }
        
        return Pair(currentStreak, longestStreak)
    }
    
    // Get monthly visit days (days when user visited bathroom)
    fun getMonthlyVisitDays(poopLogs: List<PoopLog>): List<Int> {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        
        return poopLogs
            .filter { log ->
                val logCalendar = Calendar.getInstance().apply {
                    time = log.timestamp
                }
                logCalendar.get(Calendar.MONTH) == currentMonth && 
                logCalendar.get(Calendar.YEAR) == currentYear
            }
            .map { log ->
                Calendar.getInstance().apply {
                    time = log.timestamp
                }.get(Calendar.DAY_OF_MONTH)
            }
            .distinct()
    }
    
    // Get user streaks from Firebase
    suspend fun getUserStreaks(): Pair<Int, Int> {
        val currentUserId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
        
        return suspendCoroutine { continuation ->
            getUserRef(currentUserId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val data = documentSnapshot.data
                    if (data != null) {
                        val currentStreak = (data["currentStreak"] as? Number)?.toInt() ?: 0
                        val longestStreak = (data["longestStreak"] as? Number)?.toInt() ?: 0
                        continuation.resume(Pair(currentStreak, longestStreak))
                    } else {
                        // Initialize streaks if not present
                        getUserRef(currentUserId).update(
                            mapOf(
                                "currentStreak" to 0,
                                "longestStreak" to 0
                            )
                        )
                        continuation.resume(Pair(0, 0))
                    }
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }
} 
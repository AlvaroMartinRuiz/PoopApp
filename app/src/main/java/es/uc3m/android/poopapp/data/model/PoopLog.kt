package es.uc3m.android.poopapp.data.model

import com.google.firebase.Timestamp
import java.util.Date

data class PoopLog(
    val id: String = "", // We'll fix this to generate proper IDs
    val bristolScale: Int = 1,
    val completeness: Int = 3,
    val duration: Int = 0,
    val notes: String = "",
    val strain: Int = 3,
    val timestamp: Date = Date(),
    val userId: String = "" // This is redundant but keeping for data integrity
) {
    // Convert to HashMap for Firebase
    fun toMap(): Map<String, Any> {
        return hashMapOf(
            "id" to id,
            "bristolScale" to bristolScale,
            "completeness" to completeness,
            "duration" to duration,
            "notes" to notes,
            "strain" to strain,
            "timestamp" to timestamp,
            "userId" to userId
        )
    }
    
    companion object {
        // Convert from Firebase to PoopLog
        fun fromMap(data: Map<String, Any>, documentId: String): PoopLog {
            return PoopLog(
                id = documentId, // Use the document ID as the poop log ID
                bristolScale = (data["bristolScale"] as? Number)?.toInt() ?: 1,
                completeness = (data["completeness"] as? Number)?.toInt() ?: 3,
                duration = (data["duration"] as? Number)?.toInt() ?: 0,
                notes = data["notes"] as? String ?: "",
                strain = (data["strain"] as? Number)?.toInt() ?: 3,
                timestamp = (data["timestamp"] as? Timestamp)?.toDate() ?: Date(),
                userId = data["userId"] as? String ?: ""
            )
        }
    }
}

// Bristol Stool Scale descriptions
enum class BristolType(val description: String) {
    TYPE1("Separate hard lumps, like nuts"),
    TYPE2("Sausage-shaped but lumpy"),
    TYPE3("Like a sausage but with cracks"),
    TYPE4("Like a sausage or snake, smooth and soft"),
    TYPE5("Soft blobs with clear-cut edges"),
    TYPE6("Fluffy pieces with ragged edges, mushy"),
    TYPE7("Watery, no solid pieces")
} 
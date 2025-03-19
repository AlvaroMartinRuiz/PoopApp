package es.uc3m.android.poopapp.data.model

import java.util.Date

data class PoopLog(
    val id: String = "",
    val userId: String = "",
    val timestamp: Date = Date(),
    val bristolScale: Int = 4, // 1-7 Bristol Stool Scale
    val duration: Int = 0, // Duration in minutes
    val strain: Int = 0, // 1-5 scale
    val completeness: Int = 0, // 1-5 scale
    val notes: String = ""
)

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
package es.uc3m.android.poopapp.data.model

import com.google.firebase.Timestamp
import java.sql.Time

data class League(
    val id: String = "",
    val name: String = "",
    val createdBy: String = "", // User ID of the creator
    val members: List<String> = listOf(), // List of user IDs
    val createdAt: Timestamp = Timestamp.now(),
    val isPrivate: Boolean = true // Always true as per requirements
)

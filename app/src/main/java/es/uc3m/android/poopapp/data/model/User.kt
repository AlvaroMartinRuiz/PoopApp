package es.uc3m.android.poopapp.data.model
import com.google.firebase.Timestamp


// User model
data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "Anonymous",
    val createdAt: Timestamp = Timestamp.now()
)
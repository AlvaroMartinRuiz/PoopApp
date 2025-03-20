package es.uc3m.android.poopapp.data.model

import java.util.UUID
import com.google.firebase.Timestamp

data class League(
    val id: String = "",
    val name: String = "",
    val createdBy: String = "",
    val members: List<String> = listOf(),
    val createdAt: Timestamp = Timestamp.now(),
    val isPrivate: Boolean = true,
    val inviteCode: String = generateInviteCode()
) {
    companion object {
        fun generateInviteCode(): String {
            return UUID.randomUUID().toString().take(6).uppercase()
        }
    }
}

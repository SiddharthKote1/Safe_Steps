package SessionDataclass

data class SessionStartResponse(
    val session_id: String,
    val tracking_id: String,
    val created_at: String
)

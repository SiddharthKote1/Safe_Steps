package ConversationDataClass

data class ConversationResponse(
    val guidance: String,
    val questions: List<String>,
    val recommendations: List<String>
)

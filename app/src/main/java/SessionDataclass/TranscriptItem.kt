package SessionDataclass

data class TranscriptItem(
    val time: String,
    val speaker: String,
    val text: String,
    val language: String,
    val confidence: Int
)

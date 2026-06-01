package ReportDataClass

data class ReportResponse(
    val threatLevel: String,
    val incidentType: String,
    val summary: String,
    val actionsTaken: List<String>,
    val recommendations: List<String>
)
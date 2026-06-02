package com.Siddharth.SafeSteps.sessiondataclass

data class EmergencyIncident(
    val id: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val duration: String,
    val severity: String,
    val incidentType: String,
    val summary: String,
    val actionsPerformed: List<String>,
    val timeline: List<TimelineItem>,
    val transcript: List<TranscriptItem>
)
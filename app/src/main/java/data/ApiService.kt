package data

import AnalyticsDataClass.AnalyticsResponse
import retrofit2.http.Body
import retrofit2.http.POST
import AuthDataClass.LoginRequest
import AuthDataClass.LoginResponse
import AuthDataClass.RegisterRequest
import AuthDataClass.RegisterResponse
import AuthDataClass.UserX
import ConversationDataClass.CallStatusRequest
import ContactDataClass.AddContactRequest
import ContactDataClass.Contact
import ConversationDataClass.ConversationResponse
import ConversionDataClass.ConversationRequest
import LocationDataClass.LocationResponse
import LocationDataClass.LocationUpdateRequest
import NotificationDataClass.NotificationItem
import NotificationDataClass.RegisterDeviceRequest
import NotificationDataClass.SendNotificationRequest
import PermissionsDataClass.PermissionsResponse
import ProfilesDataclass.LanguageUpdateRequest
import ProfilesDataclass.ProfileUpdateRequest
import ProfilesDataclass.SettingsResponse
import ProfilesDataclass.SettingsUpdateRequest
import ProfilesDataclass.SimUpdateRequest
import ReportDataClass.ReportResponse
import SessionDataclass.EmergencyIncident
import SessionDataclass.SessionStartResponse
import TimelineDataClass.CreateTimelineEventRequest
import TimelineDataClass.TimelineEvent
import TtsDataClass.TtsRequest
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @POST("auth/logout")
    suspend fun logout(): String

    @GET("auth/me")
    suspend fun getMe(): UserX

    @GET("profile")
    suspend fun getProfile(): UserX

    @PUT("profile")
    suspend fun updateProfile(
        @Body request: ProfileUpdateRequest
    ): UserX

    @PUT("profile/language")
    suspend fun updateLanguage(
        @Body request: LanguageUpdateRequest
    ): UserX

    @PUT("profile/sim")
    suspend fun updateSim(
        @Body request: SimUpdateRequest
    ): UserX

    @GET("profile/settings")
    suspend fun getSettings(): SettingsResponse

    @PUT("profile/settings")
    suspend fun updateSettings(
        @Body request: SettingsUpdateRequest
    ): SettingsResponse

    @GET("contacts")
    suspend fun getContacts(): List<Contact>

    @POST("contacts")
    suspend fun addContact(
        @Body request: AddContactRequest
    ): Contact

    @PUT("contacts/{contact_id}")
    suspend fun updateContact(
        @Path("contact_id") contactId: String,
        @Body request: AddContactRequest
    ): Contact

    @DELETE("contacts/{contact_id}")
    suspend fun deleteContact(
        @Path("contact_id") contactId: String
    ): String

    @GET("permissions")
    suspend fun getPermissions(): PermissionsResponse

    @POST("emergency/start")
    suspend fun startSession(): SessionStartResponse

    @POST("emergency/end")
    suspend fun endSession(): EmergencyIncident

    @GET("emergency/current")
    suspend fun getCurrentSession(): SessionStartResponse

    @GET("emergency/incidents")
    suspend fun getIncidents(): List<EmergencyIncident>

    @GET("emergency/history")
    suspend fun getHistory(): List<EmergencyIncident>

    @POST("emergency/trigger")
    suspend fun triggerSos(): EmergencyIncident

    @GET("emergency/{session_id}")
    suspend fun getSessionById(
        @Path("session_id") sessionId: String
    ): EmergencyIncident

    @POST("location/update")
    suspend fun updateLocation(
        @Body request: LocationUpdateRequest
    ): LocationDataClass.LocationUpdateResponse

    @GET("location/{session_id}")
    suspend fun getLatestLocation(
        @Path("session_id") sessionId: String
    ): LocationResponse

    @GET("location/history/{session_id}")
    suspend fun getLocationHistory(
        @Path("session_id") sessionId: String
    ): List<LocationResponse>

    @POST("report/generate")
    suspend fun generateReport(
        @Query("session_id") sessionId: String
    ): ReportResponse

    @GET("report/{session_id}")
    suspend fun getReport(
        @Path("session_id") sessionId: String
    ): ReportResponse

    @POST("timeline/event")
    suspend fun createTimelineEvent(
        @Body request: CreateTimelineEventRequest
    ): TimelineEvent

    @GET("timeline/{session_id}")
    suspend fun getTimeline(
        @Path("session_id") sessionId: String
    ): List<TimelineEvent>

    @POST("call/status")
    suspend fun updateCallStatus(
        @Body request: CallStatusRequest
    ): String

    @GET("call/{session_id}")
    suspend fun getCallStatus(
        @Path("session_id") sessionId: String
    ): String

    @POST("conversation/message")
    suspend fun sendMessage(
        @Body request: ConversationRequest
    ): ConversationResponse

    @POST("notifications/register")
    suspend fun registerDevice(
        @Body request: RegisterDeviceRequest
    ): String

    @POST("notifications/send")
    suspend fun sendNotification(
        @Body request: SendNotificationRequest
    ): String

    @GET("notifications")
    suspend fun getNotificationHistory(): List<NotificationItem>

    @GET("analytics/overview")
    suspend fun getAnalyticsOverview(): AnalyticsResponse

    @GET("analytics")
    suspend fun getAnalytics(): AnalyticsResponse

    @GET("analytics/incidents")
    suspend fun getIncidentTypes(): String

    @GET("analytics/severity")
    suspend fun getSeverity(): String

    @GET("analytics/monthly")
    suspend fun getMonthly(): String

    @GET("analytics/trends")
    suspend fun getTrends(): String

    @GET("health")
    suspend fun getHealth(): String

    @HEAD("health")
    suspend fun checkHealth()

    @GET("metrics")
    suspend fun getMetrics(): String

    @GET("version")
    suspend fun getVersion(): String


    @POST("tts/synthesize")
    suspend fun synthesizeSpeech(
        @Body request: TtsRequest
    ): String

    @GET("/")
    suspend fun getRoot(): String

}
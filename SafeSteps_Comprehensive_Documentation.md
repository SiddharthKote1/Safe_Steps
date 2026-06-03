# SafeSteps — Comprehensive Application Documentation

> Derived directly from source code analysis. All findings are evidence-based and reference the specific files, classes, and methods responsible for each behaviour.

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Feature Breakdown](#2-feature-breakdown)
3. [Screen-by-Screen Analysis](#3-screen-by-screen-analysis)
4. [Architecture & Code Structure](#4-architecture--code-structure)
5. [Data Management](#5-data-management)
6. [Contact & User Information Storage](#6-contact--user-information-storage)
7. [Authentication & User Management](#7-authentication--user-management)
8. [API & Backend Analysis](#8-api--backend-analysis)
9. [Location, Safety & Emergency Features](#9-location-safety--emergency-features)
10. [Database & Storage Schema](#10-database--storage-schema)
11. [Permissions Analysis](#11-permissions-analysis)
12. [Security Analysis](#12-security-analysis)
13. [Dependencies & Libraries](#13-dependencies--libraries)
14. [Application Flow](#14-application-flow)
15. [Detailed Technical Findings](#15-detailed-technical-findings)
16. [Conclusion](#16-conclusion)

---

## 1. Project Overview

### Purpose

SafeSteps is a personal-safety emergency-response Android application. Its primary goal is to allow a user in danger to trigger an SOS alert instantly, notify up to two pre-configured emergency contacts via SMS and phone call, stream real-time audio from the device microphone to a backend server, and continuously broadcast the user's GPS location — all in one tap or through a covert hardware shortcut.

### Problem It Solves

In emergency or dangerous situations, a user may not have time to dial a number, open an app, or speak clearly. SafeSteps solves this by:

- Providing a large, one-tap SOS button on the main screen.
- Enabling a **covert, hands-free SOS trigger**: holding the Volume-Down button for 3 seconds activates the emergency sequence without opening the app.
- Continuously sharing GPS location with emergency contacts via repeated SMS updates every 20 seconds until the session is manually ended.
- Streaming microphone audio to the backend where AI analyses the threat level and returns real-time assessment.
- Providing an AI-powered safety chat assistant during an active SOS session.

### Target Users

Individuals concerned about personal safety — particularly people who travel alone, women, elderly users, or anyone who wants a fast, silent way to call for help.

### Core Workflow

```
App Launch
  └─► Splash Screen (routing decision)
        ├─ New user          → Onboarding (3 slides) → Register → Permission Setup → Location Setup → Emergency Contacts Setup
        └─ Returning user    → Main Screen (NeeScreen)
                                  └─ Tap SOS / Hold Volume-Down
                                        ├─ POST /emergency/start
                                        ├─ Audio streaming via WebSocket
                                        ├─ Location captured and sent via SMS + POST /location/update
                                        ├─ Phone call placed to Contact 1
                                        ├─ Periodic SMS every 20 seconds
                                        └─ End SOS → POST /emergency/end → Incident Report
```

---

## 2. Feature Breakdown

### 2.1 SOS Button

**Description**: The central feature — a large, animated circular button on the main screen (`NeeScreen.kt`). Tapping it when no session is active calls `POST /emergency/start`, starts the `AudioStreamingService`, and begins the emergency sequence via `EmergencyHelper.sendSmsAndCall()`.

**User Flow**: Tap button → session starts → SMS sent → call placed → button colour changes to red with active pulsation → "End SOS" and "AI Safety Chat" options appear.

**Permissions Required**: `ACCESS_FINE_LOCATION`, `SEND_SMS`, `CALL_PHONE`, `RECORD_AUDIO` (runtime).

---

### 2.2 Volume-Button SOS Trigger (Covert / Hands-Free)

**Description**: `VolumeButtonAccessibilityService.kt` intercepts hardware key events system-wide. When the **Volume-Down** key is held for exactly **3,000 ms**, `EmergencyHelper.sendSmsAndCall()` is invoked automatically — without any screen interaction.

**Implementation**: A `CoroutineScope(Dispatchers.Default).launch { delay(3000) }` job starts on `ACTION_DOWN` and is cancelled on `ACTION_UP`. If the key is released before 3 seconds the job is cancelled harmlessly.

**Permissions Required**: `BIND_ACCESSIBILITY_SERVICE`, Accessibility Service must be manually enabled by the user in system settings.

---

### 2.3 Real-Time Audio Streaming

**Description**: `AudioStreamingService.kt` is a foreground service that captures microphone audio at **16 kHz, 16-bit PCM mono** and streams it over a **WebSocket** connection to `ws://safesteps-backend-douj.onrender.com/ws/audio/{sessionId}`. The backend responds with JSON containing a `threat_level` field (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`) which is published through `ThreatLevelManager` and reflected in the UI.

**Chunk size**: 4,096 samples per frame (≈256 ms of audio at 16 kHz).

**Permissions Required**: `RECORD_AUDIO` (runtime), `FOREGROUND_SERVICE`.

---

### 2.4 Continuous Location Tracking & Periodic SMS

**Description**: `LocationService.kt` is a foreground location service that requests GPS updates every **10 seconds** using `FusedLocationProviderClient` with `Priority.PRIORITY_HIGH_ACCURACY`. Every location update:
1. Stores coordinates in the global singleton `LocationHolder`.
2. Calls `POST /location/update` to sync with the backend.
3. During an active SOS session (`ThreatLevelManager.activeSessionId != null`), sends an SMS to both emergency contacts every **20 seconds**: `"UPDATE: I am moving! My current location is: https://maps.google.com/?q={lat},{lng}"`.
4. Updates the foreground notification with the current address (resolved via Android `Geocoder`).

**Permissions Required**: `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`, `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_LOCATION`, `SEND_SMS`, `POST_NOTIFICATIONS`.

---

### 2.5 Emergency SMS & Phone Call

**Description**: On SOS trigger, `EmergencyHelper.sendSmsAndCall()` (`EmergencyHelper.kt`):
1. Starts `LocationService` as a foreground service.
2. Calls `POST /emergency/start` to create a session on the backend.
3. Starts `AudioStreamingService` with the new session ID.
4. Waits up to **5 seconds** for `LocationHolder` to populate.
5. Calls `POST /location/update` to record the initial position.
6. Sends the same SMS to **both** contacts: `"EMERGENCY! I am {name}. My location: {maps_url}"`.
7. Calls the first contact using `Intent.ACTION_CALL`.

---

### 2.6 AI Safety Chat

**Description**: `ChatScreen.kt` provides a full chat UI. Each user message is sent to `POST /conversation/message` with the message text and language (`"en-IN"`). The backend returns a `ConversationResponse` with `guidance`, `questions`, and `recommendations` fields. The `guidance` text is also passed to `POST /tts/synthesize`, the returned audio bytes are saved to a temp WAV file in `context.cacheDir`, and played via `MediaPlayer`.

**Permissions Required**: None additional (network only).

---

### 2.7 Incident History

**Description**: `HistoryScreen.kt` calls `GET /emergency/history` and displays a list of `EmergencyIncident` cards showing date, severity (colour-coded), incident type, and duration. Tapping a card navigates to `IncidentReportScreen` passing the session ID.

---

### 2.8 Incident Report

**Description**: `IncidentReportScreen.kt` calls `GET /report/{sessionId}` and renders a multi-card report containing: threat level, incident type, summary, actions taken (with checkmark icons), and recommendations.

---

### 2.9 Analytics Dashboard

**Description**: `AnalyticsScreen.kt` calls `GET /analytics` and renders metric cards displaying total incidents, average response time, and a severity breakdown (Critical / High / Medium / Low) with colour-coded counts.

---

### 2.10 User Profile

**Description**: `ProfileScreen.kt` reads data from `PreferencesHelper` (SharedPreferences) and displays the user's name, own phone number, age, blood group (hardcoded as "O+ Default"), and both emergency contacts. A **Log Out** button clears the access token, resets the `setup_done` flag, and navigates back to `LoginScreen` clearing the entire back stack.

---

### 2.11 Onboarding

**Description**: `IntroScreen.kt` renders a 3-page horizontal pager with background images and feature highlights (One-Tap SOS, AI Assistant, Live Tracking). On the last page the "Get Started" button sets `onboarding_seen = true` in `PreferencesHelper` and navigates to `LoginScreen`.

---

### 2.12 Permission Setup (3-Step Onboarding Flow)

**Description**: Steps 1–3 (indicated by a `StepProgressIndicator`) walk the user through:
- **Step 1** (`PermissionScreen.kt`): Accessibility Service, SMS, Phone, Notification permissions.
- **Step 2** (`LocationScreen.kt` / `LocationPermission`): Fine and coarse location.
- **Step 3** (`SetupContactsScreen.kt`): Primary (required) and secondary (optional) emergency contacts.

---

## 3. Screen-by-Screen Analysis

### 3.1 CustomSplashScreen (`screens/CustomSplashScreen.kt`)

| Attribute | Detail |
|-----------|--------|
| **Purpose** | Animated logo screen + routing decision point |
| **Duration** | 2,500 ms delay before navigation |
| **Routing Logic** | `isOnboardingSeen()` → `getAccessToken()` → `isAppSetupDone()` → `getUserData()` |
| **Navigation targets** | `IntroScreen`, `LoginScreen`, `PermissionScreen`, `SetupContactsScreen`, `NeeScreen` |
| **User interaction** | None (fully automatic) |

The routing decision is a cascading `when` block:
1. If onboarding not seen → `IntroScreen`
2. If no access token → `LoginScreen`
3. If setup not done → `PermissionScreen`
4. If user data incomplete → `SetupContactsScreen`
5. Otherwise → `NeeScreen` (main screen) with user data passed as nav arguments

---

### 3.2 IntroScreen (`screens/IntroScreen.kt`)

| Attribute | Detail |
|-----------|--------|
| **Purpose** | First-run onboarding, 3 feature-highlight slides |
| **Pages** | "One-Tap SOS", "AI Assistant", "Live Tracking" |
| **Background** | `intro_bg_1`, `intro_bg_2`, `intro_bg_3` images with gradient overlay |
| **Navigation** | "Next" advances pages; "Get Started" on last page sets `onboarding_seen = true` → `LoginScreen` |

---

### 3.3 LoginScreen (`screens/LoginScreen.kt`)

| Attribute | Detail |
|-----------|--------|
| **Purpose** | Sign in to existing account |
| **Inputs** | Phone number (with country code picker), Password |
| **Action** | Calls `AuthViewModel.login(countryCode + phone, password)` → navigates to `PermissionScreen` |
| **Navigation** | "Create one" link → `RegisterScreen` |
| **ViewModel** | `AuthViewModel` (Koin injected) |

Note: Navigation to `PermissionScreen` happens immediately after calling login without awaiting the API response. There is no loading state or error feedback displayed to the user.

---

### 3.4 RegisterScreen (`screens/RegisterScreen.kt`)

| Attribute | Detail |
|-----------|--------|
| **Purpose** | Create a new account |
| **Inputs** | Full Name, Phone number (country code picker), Password |
| **Fixed values** | `age="20"`, `blood_group="O+"`, `date_of_birth="01-01-2000"`, `gender="Not Specified"`, `medical_notes="None"`, `preferred_language="en"` (hardcoded at `RegisterScreen.kt:142–151`) |
| **Action** | Calls `AuthViewModel.register(request)` → navigates to `PermissionScreen` |

---

### 3.5 PermissionScreen (`screens/PermissionScreen.kt`) — Setup Step 1

| Attribute | Detail |
|-----------|--------|
| **Purpose** | Request non-location runtime permissions + Accessibility Service |
| **Permissions requested** | `SEND_SMS`, `CALL_PHONE`, `POST_NOTIFICATIONS` (Android 13+) |
| **Accessibility** | Checks `Settings.Secure.ACCESSIBILITY_ENABLED`; shows dialog to open system settings |
| **Progress indicator** | Step 1 of 3 |
| **Continue condition** | `allPermissionsGranted && accessibilityEnabled` |
| **Next screen** | `LocationPermission` (Step 2) |

---

### 3.6 LocationPermission / LocationScreen (`LocationScreen.kt`) — Setup Step 2

| Attribute | Detail |
|-----------|--------|
| **Purpose** | Request location permissions and start `LocationService` |
| **Permissions requested** | `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION` |
| **On grant** | Calls `preferencesHelper.setAppSetupDone(true)`, starts `LocationService` |
| **Progress indicator** | Step 2 of 3 |
| **Next screen** | `SetupContactsScreen` (Step 3) |

---

### 3.7 SetupContactsScreen (`screens/SetupContactsScreen.kt`) — Setup Step 3

| Attribute | Detail |
|-----------|--------|
| **Purpose** | Collect emergency contact phone numbers |
| **Inputs** | Primary contact phone (required), Secondary contact phone (optional) |
| **On complete** | Saves to `PreferencesHelper` + uploads both contacts via `POST /contacts`; navigates to `NeeScreen` |
| **Progress indicator** | Step 3 of 3 |

---

### 3.8 NeeScreen — Main Home Screen (`NeeScreen.kt`)

| Attribute | Detail |
|-----------|--------|
| **Purpose** | Central dashboard; primary SOS trigger surface |
| **Top bar** | Hamburger menu (Accessibility Settings / Incident History / Analytics) + "SafeSteps" title + Profile avatar button |
| **SOS Banner** | Visible only when `ThreatLevelManager.activeSessionId != null`; shows threat level with colour coding, "End SOS" and "AI Safety Chat" buttons |
| **SOS Button** | Animated `PulsatingSOSButton` (160dp circle); changes colour and animation when SOS is active |
| **Emergency Info Card** | Shows Contact 1, Contact 2, Accessibility status, Location Tracking, Emergency SMS readiness |
| **On SOS tap** | Calls `POST /emergency/start`, starts `AudioStreamingService` |
| **On End SOS** | Calls `POST /emergency/end`, stops `AudioStreamingService`, navigates to `ReportScreen/{sessionId}` |
| **Permissions requested at runtime** | `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`, `SEND_SMS`, `CALL_PHONE`, `RECORD_AUDIO`, `POST_NOTIFICATIONS` |

---

### 3.9 ChatScreen (`screens/ChatScreen.kt`)

| Attribute | Detail |
|-----------|--------|
| **Purpose** | AI-powered safety guidance chat during an active SOS session |
| **Input** | Free-text message field + Send button |
| **API call** | `POST /conversation/message` with `{message, language: "en-IN"}` |
| **Response** | `guidance` field displayed as AI chat bubble |
| **TTS** | `POST /tts/synthesize` called after each response; audio played via `MediaPlayer` from `context.cacheDir/ai_audio.wav` |
| **Navigation** | Back button returns to `NeeScreen` |

---

### 3.10 HistoryScreen (`screens/HistoryScreen.kt`)

| Attribute | Detail |
|-----------|--------|
| **Purpose** | List of all past emergency incidents |
| **API call** | `GET /emergency/history` |
| **Data displayed** | Date, severity (colour-coded), incident type, duration |
| **On card tap** | Navigate to `ReportScreen/{incident.id}` |

---

### 3.11 IncidentReportScreen (`screens/IncidentReportScreen.kt`)

| Attribute | Detail |
|-----------|--------|
| **Purpose** | Detailed report for a specific emergency session |
| **API call** | `GET /report/{sessionId}` |
| **Data displayed** | Threat level (colour-coded), incident type, AI-generated summary, actions taken (checklist), recommendations |

---

### 3.12 AnalyticsScreen (`screens/AnalyticsScreen.kt`)

| Attribute | Detail |
|-----------|--------|
| **Purpose** | User's incident statistics dashboard |
| **API call** | `GET /analytics` |
| **Data displayed** | Total encounters, average response time, Critical / High / Medium / Low severity counts |

---

### 3.13 ProfileScreen (`screens/ProfileScreen.kt`)

| Attribute | Detail |
|-----------|--------|
| **Purpose** | View personal info and emergency contacts; logout |
| **Data source** | `PreferencesHelper` (SharedPreferences only — no API call) |
| **Data displayed** | Name, own phone, age, blood group (hardcoded "O+ Default"), both emergency contacts |
| **Logout action** | Clears `access_token`, sets `setup_done = false`, navigates to `LoginScreen` clearing back stack |

---

## 4. Architecture & Code Structure

### Architecture Pattern

SafeSteps follows **MVVM (Model-View-ViewModel) + Repository Pattern** with **Koin** for dependency injection.

```
UI Layer (Jetpack Compose Screens)
    │
    ▼
ViewModels (androidx.lifecycle.ViewModel)
    │
    ▼
Repositories (plain Kotlin classes)
    │
    ▼
ApiService (Retrofit interface)
    │
    ▼
OkHttpClient (with AuthInterceptor)
    │
    ▼
Backend: https://safesteps-backend-douj.onrender.com/
```

### Project Package Structure

```
com.Siddharth.SafeSteps/
├── MainActivity.kt                    — Entry point, sets up NavGraph
├── BaseApplication.kt                 — Application class; Koin init; notification channel
├── EmergencyHelper.kt                 — Singleton; orchestrates full SOS sequence
├── LocationHolder.kt                  — Global singleton holding last-known lat/lng
├── ThreatLevelManager.kt              — StateFlow-based holder for active session ID and threat level
├── LocationService.kt                 — Foreground service; GPS polling; periodic SMS
├── AudioStreamingService.kt           — Foreground service; mic capture; WebSocket streaming
├── VolumeButtonAccessibilityService.kt — Accessibility service; detects 3s volume-down hold
├── PreferenceHelper.kt                — SharedPreferences wrapper; all local storage
├── Auth2.0.kt                         — Google Sign-In helper (utility only, not wired to flow)
│
├── screens/                           — All Compose UI screens
│   ├── NavGraph.kt                    — Navigation host and all composable routes
│   ├── Routes.kt                      — Route string constants
│   ├── CustomSplashScreen.kt
│   ├── IntroScreen.kt
│   ├── LoginScreen.kt
│   ├── RegisterScreen.kt
│   ├── PermissionScreen.kt
│   ├── SetupContactsScreen.kt
│   ├── SetupUIComponents.kt           — Shared UI: StepProgressIndicator, PremiumCardModifier, getPremiumBackgroundBrush
│   ├── PhoneInputFieldWithCountry.kt  — Country-code picker + phone input field
│   ├── ProfileScreen.kt
│   ├── ChatScreen.kt
│   ├── HistoryScreen.kt
│   ├── IncidentReportScreen.kt
│   └── AnalyticsScreen.kt
│
├── NeeScreen.kt                       — Main home screen (SOS dashboard)
├── LocationScreen.kt                  — Location permission screen (Step 2)
│
├── data/
│   ├── ApiService.kt                  — Retrofit interface (all endpoints)
│   ├── RetrofitClient.kt              — Singleton Retrofit + OkHttp setup
│   └── AuthInterceptor.kt             — OkHttp interceptor; attaches Bearer token
│
├── di/
│   └── AppModule.kt                   — Koin module; wires all dependencies
│
├── repository/                        — One repository per domain
│   ├── AuthRepository.kt
│   ├── ContactRepository.kt
│   ├── EmergencyRepository.kt
│   ├── LocationRepository.kt
│   ├── ConversationRepository.kt
│   ├── NotificationRepository.kt
│   ├── ProfileRepository.kt
│   ├── ReportRepository.kt
│   ├── AnalyticsRepository.kt
│   ├── PermissionRepository.kt
│   ├── AdminRepository.kt
│   └── TtsRepository.kt
│
├── viewmodel/                         — One ViewModel per domain
│   ├── AuthViewModel.kt
│   ├── ContactsViewModel.kt
│   ├── EmergencyViewModel.kt
│   ├── ConversationViewModel.kt
│   ├── NotificationViewModel.kt
│   ├── ProfileViewModel.kt
│   └── AnalyticsViewModel.kt
│
├── authdataclass/                     — Auth request/response models
├── contactdataclass/                  — Contact models
├── locationdataclass/                 — Location models
├── sessiondataclass/                  — Session/incident models
├── conversationdataclass/             — Chat response models
├── conversiondataclass/               — Chat request models  [sic — package typo]
├── reportdataclass/                   — Report response models
├── analyticsdataclass/                — Analytics response models
├── timelinedataclass/                 — Timeline event models
├── notificationdataclass/             — Notification models
├── permissionsdataclass/              — Permissions response models
├── profilesdataclass/                 — Profile and settings models
├── ttsdataclass/                      — TTS request model
└── ui/theme/
    ├── Color.kt
    ├── Theme.kt
    └── Type.kt
```

### Key Classes and Their Roles

| Class | File | Role |
|-------|------|------|
| `MainActivity` | `MainActivity.kt` | Single Activity; installs splash screen; sets `NavGraph` as content |
| `BaseApplication` | `BaseApplication.kt` | Application subclass; initialises Koin DI; creates app-wide notification channel |
| `EmergencyHelper` | `EmergencyHelper.kt` | `object` singleton; full SOS orchestration (session start → audio → location → SMS → call) |
| `LocationHolder` | `LocationHolder.kt` | `object` singleton; shared mutable state for last-known GPS coordinates |
| `ThreatLevelManager` | `ThreatLevelManager.kt` | `object` singleton; exposes `StateFlow<String?>` for threat level and session ID |
| `LocationService` | `LocationService.kt` | `Service`; foreground location service with Koin; 10-second GPS polling; 20-second SOS SMS |
| `AudioStreamingService` | `AudioStreamingService.kt` | `Service`; captures mic audio; streams PCM over WebSocket |
| `VolumeButtonAccessibilityService` | `VolumeButtonAccessibilityService.kt` | `AccessibilityService`; key event interceptor; 3s hold triggers SOS |
| `PreferencesHelper` | `PreferenceHelper.kt` | SharedPreferences wrapper; stores all user data, tokens, session IDs |
| `RetrofitClient` | `data/RetrofitClient.kt` | `object` singleton; Retrofit instance with `AuthInterceptor` |
| `AuthInterceptor` | `data/AuthInterceptor.kt` | OkHttp `Interceptor`; reads token from `PreferencesHelper` via Koin; adds `Authorization: Bearer` header |
| `NavGraph` | `screens/NavGraph.kt` | Compose navigation host; defines all routes and composable destinations |

---

## 5. Data Management

### Local Storage

All local data is stored in a single **SharedPreferences** file named `"user_preferences"` (private mode), managed entirely by `PreferencesHelper` (`PreferenceHelper.kt`).

There is **no Room database in use** despite Room being declared as a dependency in `app/build.gradle.kts`. No `@Entity`, `@Dao`, or `@Database` annotations exist anywhere in the source tree.

### Remote Storage

All persistent data — user accounts, emergency contacts, sessions, locations, reports, and analytics — is stored on the backend server at `https://safesteps-backend-douj.onrender.com/`. The app interacts with it exclusively through the Retrofit-based `ApiService`.

### Data Flow

```
User action (tap/hold)
    └─► Compose screen (UI state)
            └─► ViewModel (viewModelScope.launch)
                    └─► Repository (suspend fun)
                            └─► ApiService (Retrofit @POST/@GET)
                                    └─► Backend API (JSON over HTTPS)
                                            └─► Response object (Kotlin data class)
                                                    └─► Repository return value
                                                            └─► ViewModel updates StateFlow/LiveData (where implemented)
                                                                    └─► Compose screen re-renders
```

For emergency operations (SMS, audio, location), the flow bypasses the ViewModel layer and is called directly from `EmergencyHelper` and the two services using `CoroutineScope(Dispatchers.IO)`.

---

## 6. Contact & User Information Storage

### Where Contacts Are Stored

Emergency contacts are stored in **two places simultaneously**:

1. **Locally** in `SharedPreferences` file `"user_preferences"` (keys: `phone1`, `phone2`, `country_code1`, `country_code2`) — managed by `PreferencesHelper`.
2. **Remotely** on the backend via `POST /contacts` — called from `SetupContactsScreen.completeSetup()` (`SetupContactsScreen.kt:182–200`).

### How Contacts Are Added

`SetupContactsScreen.kt` → `completeSetup()` function:
1. Calls `preferencesHelper.saveUserData(...)` to persist locally.
2. Calls `RetrofitClient.apiService.addContact(AddContactRequest(...))` for the primary contact.
3. If `phone2` is not blank, calls `addContact()` again for the secondary contact.
4. Sets `setup_done = true` and navigates to `NeeScreen`.

### How Contacts Are Retrieved

- **For SMS and calling** during SOS: `EmergencyHelper` reads `contact1` / `contact2` static vars (set by `NeeScreen.kt:99–104`) or falls back to `PreferencesHelper(context).getUserData()?.let { ... }`.
- **For display**: `NeeScreen.kt` reads from its constructor arguments (passed by `NavGraph`) or re-reads `PreferencesHelper` in a `LaunchedEffect`.
- **For profile view**: `ProfileScreen.kt` reads directly from `PreferencesHelper`.

### How Contacts Are Updated

No dedicated "edit contacts" screen exists. Contacts can only be replaced by going through the setup flow again. The backend supports `PUT /contacts/{contact_id}` and `DELETE /contacts/{contact_id}` (defined in `ApiService.kt:93–101`) but these are not called from any UI screen.

### Data Structure for Contact Storage

**Local (SharedPreferences)**:
```
Key: "phone1"         Value: e.g. "9876543210"
Key: "country_code1"  Value: e.g. "+91"
Key: "phone2"         Value: e.g. "9123456789"
Key: "country_code2"  Value: e.g. "+91"
```

**Remote API model** (`contactdataclass/AddContactRequest.kt`):
```kotlin
data class AddContactRequest(
    val name: String,         // "Primary Contact" or "Secondary Contact"
    val relationship: String, // "Family"
    val phoneNumber: String,  // countryCode + phone, e.g. "+919876543210"
    val priority: String      // "Primary" or "Secondary"
)
```

**Remote response model** (`contactdataclass/Contact.kt`):
```kotlin
data class Contact(
    val id: String,
    val name: String,
    val relationship: String,
    val phoneNumber: String,
    val priority: String
)
```

### Files and Classes Responsible

| Operation | File | Method |
|-----------|------|--------|
| Save locally | `PreferenceHelper.kt` | `PreferencesHelper.saveUserData()` |
| Read locally | `PreferenceHelper.kt` | `PreferencesHelper.getUserData()` |
| Upload to backend | `SetupContactsScreen.kt:182–200` | `completeSetup()` |
| Set in EmergencyHelper | `NeeScreen.kt:99–104` | `LaunchedEffect` block |
| API interface | `data/ApiService.kt:84–101` | `getContacts()`, `addContact()`, `updateContact()`, `deleteContact()` |

---

## 7. Authentication & User Management

### Login / Signup Flow

**Registration** (`RegisterScreen.kt`):
- User fills: Full Name, Phone (with country code), Password.
- `AuthViewModel.register(RegisterRequest(...))` is called.
- `POST /auth/register` is invoked via `AuthRepository.register()`.
- Age, blood group, DOB, gender, medical notes, and preferred language are hardcoded in `RegisterScreen.kt:142–151`.

**Login** (`LoginScreen.kt`):
- User fills: Phone (with country code), Password.
- `AuthViewModel.login(countryCode + phone, password)` is called.
- `POST /auth/login` is invoked via `AuthRepository.login()`.
- Returns `LoginResponse(success, token, user)`.

### Authentication Provider

The app uses a **custom backend authentication system** — phone number + password. A `LoginResponse` contains a `token` field (JWT Bearer token).

`Auth2.0.kt` (Google Sign-In helper class) exists in the source tree but is **not wired into any navigation flow or called from any screen**. It is dead code from an earlier development iteration.

### Token Storage and Session Management

The access token is stored locally in `SharedPreferences` under key `"access_token"` via `PreferencesHelper.saveAccessToken()`. It is read by `AuthInterceptor.kt` on every HTTP request and injected as `Authorization: Bearer {token}`.

**Note**: `AuthViewModel.kt:14–28` shows that after a successful login call, the token from `LoginResponse` is NOT saved to `PreferencesHelper`. The `login()` method calls `repository.login()` but discards the response. This means `access_token` in SharedPreferences is never populated by the current login flow — a significant bug that would prevent authenticated API calls from working after login.

### Session Management

| Key | Preference Key | Set by | Cleared by |
|-----|---------------|--------|------------|
| Access token | `"access_token"` | (should be set by login — currently not wired) | `ProfileScreen.kt` logout |
| Setup done | `"setup_done"` | `LocationScreen.kt`, `SetupContactsScreen.kt` | `ProfileScreen.kt` logout |
| Onboarding seen | `"onboarding_seen"` | `IntroScreen.kt` | Never cleared |
| Active session | `"active_session_id"` | `PreferencesHelper.saveActiveSession()` | `PreferencesHelper.clearActiveSession()` |
| Tracking ID | `"active_tracking_id"` | `PreferencesHelper.saveActiveSession()` | `PreferencesHelper.clearActiveSession()` |

In-memory session state is managed by `ThreatLevelManager` using `MutableStateFlow<String?>`.

### User Profile Storage

User profile fields are stored in `SharedPreferences` under `"user_preferences"`. They are never re-fetched from the backend API during normal operation (`GET /profile` and `GET /auth/me` are defined in `ApiService.kt` but not called from any screen).

---

## 8. API & Backend Analysis

### Base URL

```
https://safesteps-backend-douj.onrender.com/
```

Defined in `data/RetrofitClient.kt:10–11`. The backend is hosted on **Render.com** (a cloud platform-as-a-service).

### WebSocket URL

```
ws://safesteps-backend-douj.onrender.com/ws/audio/{sessionId}
```

Used in `AudioStreamingService.kt:63`. Note: uses **unencrypted `ws://`** (not `wss://`).

### Complete API Endpoint Inventory

All endpoints are defined in `data/ApiService.kt`.

#### Authentication

| Method | Endpoint | Request | Response | Used By |
|--------|----------|---------|----------|---------|
| POST | `/auth/register` | `RegisterRequest` | `RegisterResponse` | `AuthRepository.register()` |
| POST | `/auth/login` | `LoginRequest` | `LoginResponse` | `AuthRepository.login()` |
| POST | `/auth/logout` | — | `String` | `AuthRepository.logout()` |
| GET | `/auth/me` | — | `UserX` | `AuthRepository.getMe()` |

#### Profile

| Method | Endpoint | Request | Response | Used By |
|--------|----------|---------|----------|---------|
| GET | `/profile` | — | `UserX` | `ProfileRepository` |
| PUT | `/profile` | `ProfileUpdateRequest` | `UserX` | `ProfileRepository` |
| PUT | `/profile/language` | `LanguageUpdateRequest` | `UserX` | `ProfileRepository` |
| PUT | `/profile/sim` | `SimUpdateRequest` | `UserX` | `ProfileRepository` |
| GET | `/profile/settings` | — | `SettingsResponse` | `ProfileRepository` |
| PUT | `/profile/settings` | `SettingsUpdateRequest` | `SettingsResponse` | `ProfileRepository` |

#### Contacts

| Method | Endpoint | Request | Response | Used By |
|--------|----------|---------|----------|---------|
| GET | `/contacts` | — | `List<Contact>` | `ContactRepository` |
| POST | `/contacts` | `AddContactRequest` | `Contact` | `SetupContactsScreen.completeSetup()` |
| PUT | `/contacts/{contact_id}` | `AddContactRequest` | `Contact` | `ContactRepository` (no UI) |
| DELETE | `/contacts/{contact_id}` | — | `String` | `ContactRepository` (no UI) |

#### Emergency Session

| Method | Endpoint | Request | Response | Used By |
|--------|----------|---------|----------|---------|
| POST | `/emergency/start` | — | `SessionStartResponse` | `EmergencyHelper`, `NeeScreen` |
| POST | `/emergency/end` | — | `EmergencyIncident` | `NeeScreen` |
| GET | `/emergency/current` | — | `SessionStartResponse` | `EmergencyRepository` (no UI) |
| GET | `/emergency/incidents` | — | `List<EmergencyIncident>` | `EmergencyRepository` (no UI) |
| GET | `/emergency/history` | — | `List<EmergencyIncident>` | `HistoryScreen` |
| POST | `/emergency/trigger` | — | `EmergencyIncident` | `EmergencyRepository` (no UI) |
| GET | `/emergency/{session_id}` | — | `EmergencyIncident` | `EmergencyRepository` (no UI) |

#### Location

| Method | Endpoint | Request | Response | Used By |
|--------|----------|---------|----------|---------|
| POST | `/location/update` | `LocationUpdateRequest` | `LocationUpdateResponse` | `LocationService`, `EmergencyHelper` |
| GET | `/location/{session_id}` | — | `LocationResponse` | `LocationRepository` (no UI) |
| GET | `/location/history/{session_id}` | — | `List<LocationResponse>` | `LocationRepository` (no UI) |

#### Report

| Method | Endpoint | Request | Response | Used By |
|--------|----------|---------|----------|---------|
| POST | `/report/generate` | `?session_id=...` | `ReportResponse` | `ReportRepository` (no UI) |
| GET | `/report/{session_id}` | — | `ReportResponse` | `IncidentReportScreen` |

#### Timeline

| Method | Endpoint | Request | Response | Used By |
|--------|----------|---------|----------|---------|
| POST | `/timeline/event` | `CreateTimelineEventRequest` | `TimelineEvent` | No UI |
| GET | `/timeline/{session_id}` | — | `List<TimelineEvent>` | No UI |

#### Call Status

| Method | Endpoint | Request | Response | Used By |
|--------|----------|---------|----------|---------|
| POST | `/call/status` | `CallStatusRequest` | `String` | No UI |
| GET | `/call/{session_id}` | — | `String` | No UI |

#### Conversation (AI Chat)

| Method | Endpoint | Request | Response | Used By |
|--------|----------|---------|----------|---------|
| POST | `/conversation/message` | `ConversationRequest` | `ConversationResponse` | `ChatScreen` |

#### Notifications

| Method | Endpoint | Request | Response | Used By |
|--------|----------|---------|----------|---------|
| POST | `/notifications/register` | `RegisterDeviceRequest` | `String` | No UI |
| POST | `/notifications/send` | `SendNotificationRequest` | `String` | No UI |
| GET | `/notifications` | — | `List<NotificationItem>` | No UI |

#### Analytics

| Method | Endpoint | Request | Response | Used By |
|--------|----------|---------|----------|---------|
| GET | `/analytics/overview` | — | `AnalyticsResponse` | No UI |
| GET | `/analytics` | — | `AnalyticsResponse` | `AnalyticsScreen` |
| GET | `/analytics/incidents` | — | `String` | No UI |
| GET | `/analytics/severity` | — | `String` | No UI |
| GET | `/analytics/monthly` | — | `String` | No UI |
| GET | `/analytics/trends` | — | `String` | No UI |

#### Text-to-Speech

| Method | Endpoint | Request | Response | Used By |
|--------|----------|---------|----------|---------|
| POST | `/tts/synthesize` | `TtsRequest` | `ResponseBody` (audio bytes) | `ChatScreen` |

#### System / Admin

| Method | Endpoint | Used By |
|--------|----------|---------|
| GET | `/health` | `AdminRepository` |
| HEAD | `/health` | `AdminRepository` |
| GET | `/metrics` | `AdminRepository` |
| GET | `/version` | `AdminRepository` |
| GET | `/` | `AdminRepository` |
| GET | `/permissions` | `PermissionRepository` |

### Request / Response Flow

All HTTP calls go through:
1. `RetrofitClient.okHttpClient` (with `AuthInterceptor` attached)
2. `AuthInterceptor.intercept()` — reads `PreferencesHelper.getAccessToken()` via Koin, adds `Authorization: Bearer {token}` header
3. Retrofit serialises/deserialises via `GsonConverterFactory`

### Third-Party Integrations

| Service | SDK/Library | Usage |
|---------|------------|-------|
| Google Play Services Location | `play-services-location:21.3.0` | FusedLocationProviderClient in `LocationService` |
| Google Play Services Maps | `play-services-maps:19.2.0` | Dependency only; no `MapView` used in screens |
| Google Play Services Auth | `play-services-auth:21.0.0` | `GoogleSignInHelper` in `Auth2.0.kt` (unused in flow) |
| Render.com | Backend host | Custom REST API + WebSocket server |

---

## 9. Location, Safety & Emergency Features

### Location Tracking (`LocationService.kt`)

- **Type**: Android `Service` with `foregroundServiceType="location"` declared in `AndroidManifest.xml`.
- **Client**: `FusedLocationProviderClient` (Google Play Services).
- **Update interval**: Every **10,000 ms** (10 seconds), `Priority.PRIORITY_HIGH_ACCURACY`.
- **Coordinates stored**: In global `object LocationHolder { var latitude, var longitude }`.
- **API sync**: Every update calls `LocationRepository.updateLocation()` → `POST /location/update` with latitude, longitude, accuracy, speed, heading.
- **Address resolution**: `Android Geocoder` reverses lat/lng to a human-readable address string for the foreground notification.
- **Foreground notification**: Ongoing notification (channel `"location_channel"`) shows `"Lat: ..., Lng: ...\n{address}"`.
- **Periodic SOS SMS**: When `ThreatLevelManager.activeSessionId != null` and 20 seconds have elapsed since the last SMS, sends to both contacts via `SmsManager.default.sendTextMessage()`.

### SOS Functionality Implementation

**Trigger points**:
1. Tap the `PulsatingSOSButton` in `NeeScreen.kt:344–363`.
2. Hold Volume-Down for 3 seconds → `VolumeButtonAccessibilityService.triggerEmergencyAction()` → `EmergencyHelper.sendSmsAndCall()`.

**`EmergencyHelper.sendSmsAndCall()` sequence** (`EmergencyHelper.kt:20–101`):
```
1. Start LocationService (foreground)
2. POST /emergency/start  ─── get session_id
3. ThreatLevelManager.setSessionId(session_id)
4. Start AudioStreamingService with SESSION_ID extra
5. waitForLocation(5000ms) — polls LocationHolder every 500ms
6. POST /location/update  ─── get maps_link
7. SmsManager.sendTextMessage(contact1, "EMERGENCY! I am {name}. My location: {url}")
8. SmsManager.sendTextMessage(contact2, same message)
9. Intent(ACTION_CALL, "tel:{contact1}")  ─── auto-dial
```

### Audio Streaming (`AudioStreamingService.kt`)

- WebSocket URL: `ws://safesteps-backend-douj.onrender.com/ws/audio/{sessionId}`
- Sample rate: 16,000 Hz
- Format: PCM 16-bit, Mono
- Buffer/chunk: 4,096 samples = 8,192 bytes ≈ 256ms per frame
- Server sends JSON: `{"threat_level": "LOW"|"MEDIUM"|"HIGH"|"CRITICAL"}`
- Threat level updates flow to `ThreatLevelManager.updateThreatLevel()` → collected by `NeeScreen` via `collectAsState()` → banner colour updates in real time.

### Background Services

| Service | Type | Declared in Manifest | Started By | Stopped By |
|---------|------|----------------------|-----------|-----------|
| `LocationService` | Foreground (`location`) | Yes | `EmergencyHelper`, `LocationScreen` | `onDestroy()` |
| `AudioStreamingService` | Foreground (general) | No (`android:name` present but no `foregroundServiceType`) | `EmergencyHelper`, `NeeScreen` | WebSocket `onClosed`/`onFailure`, `NeeScreen` on End SOS |
| `VolumeButtonAccessibilityService` | Accessibility | Yes | User enables manually in system settings | User disables in settings |

### Notifications

Two notification channels are created:

1. **`"channel_id"` / "Channel Name"** — `BaseApplication.kt`; default importance; used by `BaseApplication`.
2. **`"location_channel"` / "Location Service Channel"** — `LocationService.kt`; no sound, no vibration; used by the persistent location foreground notification.
3. **`"SafeStepsAudioChannel"` / "Emergency Audio Streaming"** — `AudioStreamingService.kt`; HIGH importance; shows "Emergency SOS Active / Streaming audio to emergency contacts...".

---

## 10. Database & Storage Schema

### SharedPreferences Schema (`"user_preferences"`)

| Key | Type | Set By | Read By | Purpose |
|-----|------|--------|---------|---------|
| `name` | String | `PreferencesHelper.saveUserData()` | `getUserData()` | User's display name |
| `age` | String | `PreferencesHelper.saveUserData()` | `getUserData()` | User's age |
| `phone1` | String | `saveUserData()` | `getUserData()`, `EmergencyHelper` | Emergency contact 1 number |
| `phone2` | String | `saveUserData()` | `getUserData()`, `EmergencyHelper` | Emergency contact 2 number |
| `country_code1` | String | `saveUserData()` | `getUserData()`, `EmergencyHelper` | Country code for contact 1 |
| `country_code2` | String | `saveUserData()` | `getUserData()`, `EmergencyHelper` | Country code for contact 2 |
| `own_phone` | String | `saveOwnPhone()` | `getOwnPhone()` | User's own phone number |
| `own_country_code` | String | `saveOwnPhone()` | `getOwnCountryCode()` | User's own country code |
| `access_token` | String | `saveAccessToken()` | `getAccessToken()`, `AuthInterceptor` | JWT Bearer token for API |
| `active_session_id` | String | `saveActiveSession()` | `getActiveSessionId()` | Current emergency session ID |
| `active_tracking_id` | String | `saveActiveSession()` | `getActiveTrackingId()` | Backend tracking ID |
| `setup_done` | Boolean | `setAppSetupDone()` | `isAppSetupDone()` | Has user completed 3-step setup |
| `onboarding_seen` | Boolean | `setOnboardingSeen()` | `isOnboardingSeen()` | Has user seen intro slides |

### Remote Data Models

**SessionStartResponse** (`sessiondataclass/SessionStartResponse.kt`):
```kotlin
data class SessionStartResponse(
    val session_id: String,
    val tracking_id: String,
    val created_at: String
)
```

**EmergencyIncident** (`sessiondataclass/EmergencyIncident.kt`):
```kotlin
data class EmergencyIncident(
    val id: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val duration: String,
    val severity: String,           // "LOW"|"MEDIUM"|"HIGH"|"CRITICAL"
    val incidentType: String,
    val summary: String,
    val actionsPerformed: List<String>,
    val timeline: List<TimelineItem>,
    val transcript: List<TranscriptItem>
)
```

**ReportResponse** (`reportdataclass/ReportResponse.kt`):
```kotlin
data class ReportResponse(
    val threatLevel: String,
    val incidentType: String,
    val summary: String,
    val actionsTaken: List<String>,
    val recommendations: List<String>
)
```

**AnalyticsResponse** (`analyticsdataclass/AnalyticsResponse.kt`):
```kotlin
data class AnalyticsResponse(
    val totalEncounters: Int,
    val avgResponseLimit: String,
    val criticalCount: Int,
    val highCount: Int,
    val mediumCount: Int,
    val lowCount: Int
)
```

**SettingsResponse** (`profilesdataclass/SettingsResponse.kt`):
```kotlin
data class SettingsResponse(
    val notificationEnabled: Boolean,
    val privacyEnabled: Boolean,
    val sosSensitivity: Double,
    val themeDarkMode: Boolean
)
```

**Contact** (`contactdataclass/Contact.kt`):
```kotlin
data class Contact(
    val id: String,
    val name: String,
    val relationship: String,
    val phoneNumber: String,
    val priority: String        // "Primary"|"Secondary"
)
```

---

## 11. Permissions Analysis

### AndroidManifest.xml Declared Permissions

| Permission | Category | Required By | Reason |
|-----------|----------|-------------|--------|
| `SEND_SMS` | Dangerous | `EmergencyHelper`, `LocationService` | Send emergency alert SMS and periodic location updates |
| `CALL_PHONE` | Dangerous | `EmergencyHelper` | Auto-dial emergency contact 1 on SOS trigger |
| `ACCESS_COARSE_LOCATION` | Dangerous | `LocationService`, `LocationScreen` | Approximate location for fallback |
| `ACCESS_FINE_LOCATION` | Dangerous | `LocationService`, `LocationScreen` | Precise GPS location for emergency SMS |
| `ACCESS_BACKGROUND_LOCATION` | Dangerous (special) | `LocationService` | Location while app is in background |
| `POST_NOTIFICATIONS` | Dangerous (Android 13+) | `LocationService`, `AudioStreamingService` | Foreground service notification required |
| `FOREGROUND_SERVICE` | Normal | `LocationService` | Required to run foreground service |
| `FOREGROUND_SERVICE_LOCATION` | Normal | `LocationService` | Required for `foregroundServiceType="location"` |
| `android.hardware.telephony` (feature, not required) | — | `EmergencyHelper` | Declares telephony hardware as optional |

### Runtime-Requested Permissions (Not All in Manifest)

`NeeScreen.kt:77–90` requests the following at runtime:
- `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`, `SEND_SMS`, `CALL_PHONE`, `POST_NOTIFICATIONS` (Android 13+)
- **`RECORD_AUDIO`** — requested at runtime but **NOT declared in `AndroidManifest.xml`**

`PermissionScreen.kt:43–50` requests:
- `SEND_SMS`, `CALL_PHONE`, `POST_NOTIFICATIONS` (Android 13+)

---

## 12. Security Analysis

### 1. Missing `RECORD_AUDIO` in Manifest (Critical Bug)

**File**: `NeeScreen.kt:83`, `AudioStreamingService.kt:98`

`RECORD_AUDIO` is requested at runtime and checked before use, but it is **not declared in `AndroidManifest.xml`**. On Android 6+ (API 23+), the system will silently deny any runtime permission not declared in the manifest, and on newer versions may throw a `SecurityException`. The `AudioStreamingService` will fail to start recording without this permission.

### 2. Unencrypted WebSocket (Security Risk)

**File**: `AudioStreamingService.kt:63`

```
ws://safesteps-backend-douj.onrender.com/ws/audio/{sessionId}
```

Audio data — which may contain sensitive speech captured during an emergency — is transmitted over an **unencrypted WebSocket connection (`ws://`)** rather than `wss://`. Any network intermediary (ISP, public Wi-Fi router, etc.) can intercept and listen to the raw audio stream.

### 3. Access Token Never Saved After Login

**File**: `viewmodel/AuthViewModel.kt:15–27`

`AuthViewModel.login()` calls `repository.login()` but ignores the returned `LoginResponse` containing the JWT token. `PreferencesHelper.saveAccessToken()` is never called in the current login flow. As a result, `AuthInterceptor` will always send requests without a `Bearer` token after login, causing all authenticated API calls to fail with 401 Unauthorized.

### 4. Hardcoded User Registration Fields

**File**: `screens/RegisterScreen.kt:142–151`

Age, blood group, date of birth, gender, and medical notes are hardcoded:
```kotlin
age = "20",
blood_group = "O+",
date_of_birth = "01-01-2000",
gender = "Not Specified",
medical_notes = "None"
```

Medical information is inaccurate for most users and cannot be changed from the UI, which could be problematic in a real emergency where responders rely on this data.

### 5. Access Token Stored in Plain Text

**File**: `PreferenceHelper.kt:128`

The JWT access token is stored in `SharedPreferences` as a plain string. On non-rooted devices this is acceptable (the file is only readable by the app), but on rooted devices or via ADB backup it can be extracted. Using Android `EncryptedSharedPreferences` (from Jetpack Security) would be more robust.

### 6. `AudioStreamingService` Not Declared with `foregroundServiceType`

**File**: `AndroidManifest.xml`, `AudioStreamingService.kt`

`AudioStreamingService` calls `startForeground()` but is declared in the manifest without a `foregroundServiceType`. On Android 14 (API 34), this can cause a `ForegroundServiceStartNotAllowedException` because the service uses the microphone.

### 7. Contact Phones Stored as Plain Text in SharedPreferences

Emergency contact phone numbers are stored without any encryption. While the threat model for SharedPreferences is limited to rooted devices, given that this app is specifically for vulnerable users in emergency scenarios, encrypting sensitive contact data would be advisable.

### 8. Navigation After Auth Without Awaiting Response

**Files**: `LoginScreen.kt:124–127`, `RegisterScreen.kt:141–154`

Both screens navigate to `PermissionScreen` immediately after calling the ViewModel method, without awaiting the API response or checking for errors. A user who enters incorrect credentials is still navigated forward, and authentication failures are silently swallowed.

### 9. Google Sign-In Dead Code

**File**: `Auth2.0.kt`

`GoogleSignInHelper` requests only email via `GoogleSignInOptions.DEFAULT_SIGN_IN`. It is not used in any flow but its presence in the codebase may cause confusion or be accidentally activated.

---

## 13. Dependencies & Libraries

All versions defined in `gradle/libs.versions.toml` and `app/build.gradle.kts`.

| Library | Version | Purpose | Used In |
|---------|---------|---------|---------|
| **Jetpack Compose BOM** | `2024.09.00` | Compose UI framework | All screens |
| **Compose Material3** | `1.4.0` | Material Design 3 UI components | All screens |
| **Compose Navigation** | `2.7.7` | In-app navigation | `NavGraph.kt` |
| **Activity Compose** | `1.10.1` | `setContent {}`, `ComponentActivity` | `MainActivity.kt` |
| **Lifecycle Runtime KTX** | `2.8.7` | `viewModelScope`, lifecycle awareness | ViewModels |
| **Core KTX** | `1.12.0` | Kotlin extensions for Android | General |
| **Core Splashscreen** | `1.0.1` | Android 12+ splash screen API | `MainActivity.kt` |
| **Material Icons Extended** | — | Extended icon set | All screens |
| **Retrofit2** | (transitive) | HTTP client for REST API calls | `RetrofitClient.kt`, `ApiService.kt` |
| **OkHttp3** | (transitive) | HTTP/WebSocket client | `RetrofitClient.kt`, `AudioStreamingService.kt` |
| **Gson Converter** | (transitive) | JSON serialisation for Retrofit | `RetrofitClient.kt` |
| **Koin Android** | (transitive) | Dependency injection framework | `BaseApplication.kt`, `di/AppModule.kt`, all ViewModels |
| **Koin Compose** | (transitive) | Koin integration for Compose ViewModels | `LoginScreen.kt`, `RegisterScreen.kt` |
| **Accompanist Permissions** | `0.34.0` | Compose permission request helpers | `PermissionScreen.kt`, `LocationScreen.kt`, `NeeScreen.kt` |
| **Play Services Location** | `21.3.0` | `FusedLocationProviderClient` | `LocationService.kt` |
| **Play Services Maps** | `19.2.0` | Maps SDK (declared, not actively used in UI) | Dependency |
| **Play Services Auth** | `21.0.0` | Google Sign-In client | `Auth2.0.kt` (unused) |
| **Lottie Compose** | `6.1.0` | Lottie animation player | `NeeScreen.kt` (animation assets in `res/raw/`) |
| **Coil Compose** | `2.6.0` | Async image loading | Declared; no `AsyncImage` calls found in reviewed screens |
| **Room Runtime** | `2.6.1` | SQLite ORM | Declared; no `@Entity`/`@Dao` found — unused |
| **Room KTX** | `2.6.1` | Coroutine support for Room | Declared; unused |
| **Room Compiler (kapt)** | `2.6.1` | Annotation processor for Room | Declared; unused |
| **UI Graphics** | `1.9.0` | Compose graphics | UI layer |
| **Kotlin** | `2.0.21` | Programming language | Entire codebase |
| **AGP** | `8.13.0` | Android Gradle Plugin | Build system |

---

## 14. Application Flow

### First-Time User Journey

```
App Install & Launch
    │
    ▼
CustomSplashScreen (2.5s animated logo)
    │ isOnboardingSeen() == false
    ▼
IntroScreen (3-slide horizontal pager)
    │ "Get Started" → setOnboardingSeen(true)
    ▼
LoginScreen
    │ "Create one" link
    ▼
RegisterScreen (Name, Phone, Password)
    │ "Sign Up" → AuthViewModel.register() → navigate
    ▼
PermissionScreen [Step 1/3]
    │ Grant SMS + CALL + NOTIFICATIONS + Accessibility
    ▼
LocationPermission [Step 2/3]
    │ Grant FINE_LOCATION + COARSE_LOCATION → start LocationService
    ▼
SetupContactsScreen [Step 3/3]
    │ Enter Contact 1 (required) + Contact 2 (optional)
    │ → saveUserData() + addContact() API calls + setAppSetupDone(true)
    ▼
NeeScreen (Main Dashboard)
```

### Returning User Journey

```
App Launch
    │
    ▼
CustomSplashScreen (2.5s)
    │ isOnboardingSeen() == true
    │ getAccessToken() != null
    │ isAppSetupDone() == true
    │ getUserData() complete
    ▼
NeeScreen (directly)
```

### SOS Emergency Flow (Full Sequence)

```
User triggers SOS
(tap button OR hold Volume-Down 3s)
    │
    ▼
EmergencyHelper.sendSmsAndCall()
    ├─► Start LocationService (foreground)
    ├─► POST /emergency/start
    │       └─ session_id returned
    ├─► ThreatLevelManager.setSessionId(session_id)
    ├─► Start AudioStreamingService (SESSION_ID extra)
    │       └─ Opens WebSocket ws://.../ws/audio/{session_id}
    │       └─ Begins streaming PCM audio
    │       └─ Server sends {"threat_level": "..."} every N seconds
    │               └─ ThreatLevelManager.updateThreatLevel(level)
    │                       └─ NeeScreen SOS banner updates colour
    ├─► waitForLocation (≤5s polling LocationHolder)
    ├─► POST /location/update → returns maps_link
    ├─► SmsManager.sendTextMessage(contact1, "EMERGENCY! ... {maps_link}")
    ├─► SmsManager.sendTextMessage(contact2, same)
    └─► Intent(ACTION_CALL, "tel:{contact1}")

─── Active Session Running ───────────────────────────────────────────────
LocationService (every 10s GPS poll):
    ├─► POST /location/update
    ├─► Update foreground notification (address)
    └─► Every 20s (if session active):
            ├─► SmsManager.sendTextMessage(contact1, "UPDATE: ... {lat,lng}")
            └─► SmsManager.sendTextMessage(contact2, same)

Optional during session:
    └─► Tap "AI Safety Chat" → navigate ChatScreen/{sessionId}
            ├─► User types message
            ├─► POST /conversation/message → guidance text displayed
            └─► POST /tts/synthesize → audio bytes → MediaPlayer plays

─── End SOS ──────────────────────────────────────────────────────────────
User taps "End SOS"
    ├─► POST /emergency/end
    ├─► stopService(AudioStreamingService)
    ├─► ThreatLevelManager.clearSession()
    └─► navigate ReportScreen/{sessionId}
            └─► GET /report/{sessionId}
                    └─ Display: threat level, incident type, summary,
                                actions taken, recommendations
```

---

## 15. Detailed Technical Findings

### 15.1 Two Parallel Service Architectures

The app has two foreground services started simultaneously during an active SOS session:
- `LocationService` — GPS + SMS daemon
- `AudioStreamingService` — microphone + WebSocket daemon

Both use `START_STICKY` (or similar) return codes and must be explicitly stopped. `AudioStreamingService` stops when the WebSocket closes or fails, or when the user ends the SOS session in the UI. `LocationService` continues running even after the SOS session ends — it is started once in `LocationScreen` and never stopped from the app side.

### 15.2 Lottie Animations (Partially Used)

Two Lottie JSON files exist in `app/src/main/res/raw/`:
- `sos.json`
- `safety.json`

`NeeScreen.kt` imports `LottieComposable` (via `import com.airbnb.lottie.compose.*`) but the `PulsatingSOSButton` composable uses a custom CSS-like pulsating `Box` animation instead of Lottie. The Lottie animations may be legacy or reserved for future use.

### 15.3 `ThreatLevelManager` as Application-Wide State Bus

`ThreatLevelManager` is a Kotlin `object` (process-wide singleton) holding two `MutableStateFlow` fields:
- `_threatLevel: MutableStateFlow<String?>`
- `_activeSessionId: MutableStateFlow<String?>`

These are collected in `NeeScreen.kt` via `collectAsState()` and drive the SOS banner UI. The `AudioStreamingService` writes to `ThreatLevelManager.updateThreatLevel()` from a WebSocket background thread — a cross-thread write to a `MutableStateFlow`, which is thread-safe by design.

### 15.4 `LocationHolder` Concurrency

`LocationHolder` is a plain Kotlin `object` with mutable vars:
```kotlin
object LocationHolder {
    var latitude: Double? = null
    var longitude: Double? = null
}
```

`LocationService` writes to it from `Looper.getMainLooper()` (location callback). `EmergencyHelper` reads it from `Dispatchers.IO`. This constitutes an unsynchronised cross-thread read/write. On JVM/Android this is generally safe for `Double?` (due to how JVM handles object references), but is technically not guaranteed by the memory model.

### 15.5 `AuthInterceptor` Koin Injection via `KoinComponent`

`AuthInterceptor` implements `KoinComponent` and injects `PreferencesHelper` lazily:
```kotlin
private val preferencesHelper: PreferencesHelper by inject()
```

This is unusual — OkHttp interceptors are typically created before the Koin context. Since `RetrofitClient` is a lazy singleton and Koin is started in `BaseApplication.onCreate()` before any network call is made, this works correctly in practice.

### 15.6 Unused but Declared Repository Methods

Many repository and ViewModel methods reference API endpoints that are defined but never called from any Compose screen:
- `GET /emergency/current`, `GET /emergency/incidents`, `GET /emergency/{id}`
- `GET /location/{session_id}`, `GET /location/history/{session_id}`
- `POST /report/generate`
- All timeline and call-status endpoints
- All notification endpoints
- `GET /profile` (user data only ever read from SharedPreferences)

These represent unimplemented features or planned functionality.

### 15.7 `google-services (2).json` File

A Firebase configuration file named `google-services (2).json` exists in the project root. However:
- No Firebase SDK (`firebase-auth` is present in `libs.versions.toml` but **not used** in `app/build.gradle.kts` dependencies list).
- The `google-gms-google-services` plugin is declared in the root `build.gradle.kts` but is not applied in the app-level `build.gradle.kts`.
- FirebaseAuth is never imported in any source file.

Firebase appears to have been planned and removed, or is included for a future feature.

### 15.8 `PhoneScreen.kt` and `OTPScreen.kt` — Commented Out

The source tree contains `PhoneScreen.kt` (in the `com.Siddharth.SafeSteps` package root) and references to an `OTPScreen.kt` in older code. These are remnants of a previous phone-OTP authentication flow that was replaced by the current phone+password system.

### 15.9 Step Progress Indicator Reuse

`SetupUIComponents.kt` contains shared composables:
- `StepProgressIndicator(currentStep, totalSteps)` — renders filled/unfilled dots for onboarding steps
- `getPremiumBackgroundBrush()` — returns a gradient brush used across setup screens and ProfileScreen
- `PremiumCardModifier()` — returns a Modifier with card styling (shadow, rounded corners, white background) used in permission cards and feature cards

### 15.10 App Version

From `app/build.gradle.kts`:
```
versionCode = 2
versionName = "1.0"
applicationId = "com.Siddharth.SafeSteps"
minSdk = 24  (Android 7.0)
targetSdk = 35
compileSdk = 35
```

A release AAB (`app/release/app-release.aab`) is present in the repository, suggesting the app has been submitted or prepared for the Play Store.

---

## 16. Conclusion

### Complete Summary

SafeSteps is a personal safety Android application that solves the critical problem of quickly alerting emergency contacts when a user is in danger. It provides a multilayered emergency triggering system (on-screen SOS button + covert hardware shortcut), combined with real-time location broadcasting, AI-powered threat assessment via audio streaming, and an AI safety chat assistant.

### Main Functionalities

| Feature | Status |
|---------|--------|
| One-tap SOS trigger | ✅ Fully implemented |
| Volume-down 3s hold trigger | ✅ Fully implemented |
| Emergency SMS to 2 contacts | ✅ Fully implemented |
| Auto-dial emergency contact 1 | ✅ Fully implemented |
| GPS location tracking (foreground) | ✅ Fully implemented |
| Periodic location SMS (every 20s) | ✅ Fully implemented |
| Audio streaming via WebSocket | ✅ Fully implemented |
| Real-time threat level from AI | ✅ Fully implemented |
| AI safety chat | ✅ Fully implemented |
| Text-to-Speech for AI responses | ✅ Fully implemented |
| Incident history | ✅ Fully implemented |
| Incident report | ✅ Fully implemented |
| Analytics dashboard | ✅ Fully implemented |
| User profile view | ✅ Fully implemented |
| 3-step onboarding setup | ✅ Fully implemented |
| Edit contacts (post-setup) | ❌ Not implemented (API exists) |
| Push notifications | ❌ Not implemented (API + models exist) |
| Firebase integration | ❌ Removed / unused |
| Room local database | ❌ Declared but unused |
| Token save after login | ❌ Bug — token not persisted |

### Technical Architecture Summary

SafeSteps is a **single-Activity Jetpack Compose app** following **MVVM + Repository** pattern with **Koin** for dependency injection. The UI layer is 100% Jetpack Compose with Material3. Networking is handled by **Retrofit2 + OkHttp3** with a custom `AuthInterceptor`. Two foreground services handle continuous background work (GPS + audio). A system-level `AccessibilityService` provides the covert SOS trigger. All user data and tokens are stored in a single SharedPreferences file.

### Key Observations and Recommendations

1. **Fix RECORD_AUDIO manifest declaration** — Add `<uses-permission android:name="android.permission.RECORD_AUDIO" />` to `AndroidManifest.xml` immediately; audio streaming will silently fail without it.

2. **Fix token save after login** — `AuthViewModel.login()` must save the token from `LoginResponse` to `PreferencesHelper` before navigating; currently all authenticated API calls will return 401.

3. **Upgrade WebSocket to `wss://`** — Raw audio of users in distress must not be transmitted in plaintext over `ws://`.

4. **Declare `foregroundServiceType` for `AudioStreamingService`** — Required on Android 14+ for microphone-using foreground services.

5. **Implement `EncryptedSharedPreferences`** — Access tokens and contact phone numbers of a vulnerable user base deserve encryption at rest.

6. **Add login error feedback** — Navigation should not proceed before the auth response is received; implement loading states and error handling.

7. **Remove hardcoded medical data** — Allow users to enter their actual age, blood group, and medical notes at registration; this data may be critical during a real emergency.

8. **Implement contact editing** — The API supports `PUT /contacts/{id}` and `DELETE /contacts/{id}` but no UI screen provides this functionality.

9. **Stop `LocationService` when no longer needed** — The service runs indefinitely after setup; it should be tied to the session lifecycle.

10. **Synchronise `LocationHolder`** — Use `@Volatile` or `AtomicReference` to safely share coordinates between the location callback thread and `EmergencyHelper`'s IO coroutine.

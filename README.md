# SafeSteps – Women Safety Application

SafeSteps is an Android application designed to help users quickly share their live location and alert trusted contacts during emergencies using system-level triggers.

## 🚨 Problem Statement
In emergency situations, accessing the phone and manually contacting someone may not be possible. SafeSteps provides a fast and reliable way to alert emergency contacts using hardware-based actions.

## ✨ Key Features
- Emergency trigger using a hardware button gesture
- Live location tracking using foreground services
- Share live location via SMS to saved contacts
- Automatic emergency call initiation
- Runs reliably in the background using Accessibility Services

## ⚙️ How It Works
- User registers emergency contacts in the app
- A predefined hardware gesture triggers emergency mode
- App starts foreground location tracking
- Live location is shared via SMS and an emergency call is initiated

## 🔄 App Flow Diagram
```mermaid
flowchart TD
    A[App Launched] --> B[User Registers Emergency Contacts]
    B --> C[App Runs in Background]
    C --> D[Hardware Button Gesture Detected]
    D --> E[Emergency Mode Activated]
    E --> F[Foreground Location Service Started]
    F --> G[Live Location Fetched]
    G --> H[SMS Sent to Emergency Contacts]
    H --> I[Emergency Call Initiated]


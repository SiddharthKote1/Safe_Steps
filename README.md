# SafeSteps – Women Safety Application

SafeSteps is an Android application designed to help users quickly share their live location and alert trusted contacts during emergencies using system-level Android features.

---

## 🚨 Problem Statement
In emergency situations, users may not have enough time to unlock their phone and manually call or message someone. SafeSteps provides a fast and reliable way to alert emergency contacts using hardware-based triggers and background services.

---

## ✨ Key Features
- Emergency trigger using a hardware button gesture  
- Live location tracking using foreground services  
- Share live location via SMS to saved emergency contacts  
- Automatic emergency call initiation  
- Reliable background execution using Accessibility Services  

---

## ⚙️ How It Works
- The user saves emergency contacts inside the app  
- The app runs silently in the background  
- A predefined hardware button gesture activates emergency mode  
- Foreground location tracking starts immediately  
- Live location is sent via SMS to emergency contacts  
- An emergency call is automatically initiated  

---

## 🔄 App Flow Diagram

```mermaid
flowchart TD
    A[App Launched]
    B[User Saves Emergency Contacts]
    C[App Runs in Background]
    D[Hardware Button Gesture Detected]
    E[Emergency Mode Activated]
    F[Foreground Location Service Started]
    G[Live Location Retrieved]
    H[SMS Sent to Emergency Contacts]
    I[Emergency Call Initiated]

    A --> B
    B --> C
    C --> D
    D --> E
    E --> F
    F --> G
    G --> H
    H --> I




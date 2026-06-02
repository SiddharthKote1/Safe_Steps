import os
import re

base_dir = r"C:\Users\asus\Safestep\app\src\main\java\com\Siddharth\SafeSteps"

# Map old root packages to new package names
replacements = {
    "AnalyticsDataClass": "com.Siddharth.SafeSteps.analyticsdataclass",
    "AuthDataClass": "com.Siddharth.SafeSteps.authdataclass",
    "ContactDataClass": "com.Siddharth.SafeSteps.contactdataclass",
    "ConversationDataClass": "com.Siddharth.SafeSteps.conversationdataclass",
    "ConversionDataClass": "com.Siddharth.SafeSteps.conversiondataclass",
    "LocationDataClass": "com.Siddharth.SafeSteps.locationdataclass",
    "NotificationDataClass": "com.Siddharth.SafeSteps.notificationdataclass",
    "PermissionsDataClass": "com.Siddharth.SafeSteps.permissionsdataclass",
    "ProfilesDataclass": "com.Siddharth.SafeSteps.profilesdataclass",
    "ReportDataClass": "com.Siddharth.SafeSteps.reportdataclass",
    "Screens": "com.Siddharth.SafeSteps.screens",
    "SessionDataclass": "com.Siddharth.SafeSteps.sessiondataclass",
    "TimelineDataClass": "com.Siddharth.SafeSteps.timelinedataclass",
    "TtsDataClass": "com.Siddharth.SafeSteps.ttsdataclass",
    "repository": "com.Siddharth.SafeSteps.repository",
    "viewmodel": "com.Siddharth.SafeSteps.viewmodel",
    "com.Siddharth.chat": "com.Siddharth.SafeSteps.screens" # Catching the wrong package in NavGraph
}

for root, dirs, files in os.walk(base_dir):
    for file in files:
        if file.endswith(".kt"):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()

            changed = False
            # Fix imports
            for old_pkg, new_pkg in replacements.items():
                pattern = r"^import\s+" + re.escape(old_pkg) + r"\b"
                replacement = f"import {new_pkg}"
                new_content = re.sub(pattern, replacement, content, flags=re.MULTILINE)
                if new_content != content:
                    content = new_content
                    changed = True
            
            # Also fix internal references to package com.example.SafeSteps inside code
            # Some files like PhoneScreen might have had wrong packages
            if "com.example.SafeSteps" in content:
                content = content.replace("com.example.SafeSteps", "com.Siddharth.SafeSteps")
                changed = True

            if changed:
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"Fixed imports in {file}")

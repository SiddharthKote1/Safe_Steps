import os
import shutil

base_dir = r"C:\Users\asus\Safestep\app\src\main\java"

folders_to_delete = [
    "AnalyticsDataClass", "AuthDataClass", "ContactDataClass", 
    "ConversationDataClass", "ConversionDataClass", "LocationDataClass", 
    "NotificationDataClass", "PermissionsDataClass", "ProfilesDataclass", 
    "ReportDataClass", "Screens", "SessionDataclass", "TimelineDataClass", 
    "TtsDataClass", "repository", "viewmodel", "com.Siddharth.chat", "com"
]

# We don't delete com directly, we only delete com/Siddharth if there's stuff outside com/Siddharth/SafeSteps, but wait!
# The new files are in com\Siddharth\SafeSteps. We shouldn't delete `com`.
folders_to_delete.remove("com")

for f in folders_to_delete:
    path = os.path.join(base_dir, f)
    if os.path.exists(path):
        shutil.rmtree(path)
        print(f"Deleted folder {path}")

# Delete root .kt files
for file in os.listdir(base_dir):
    path = os.path.join(base_dir, file)
    if os.path.isfile(path) and file.endswith(".kt"):
        os.remove(path)
        print(f"Deleted file {path}")

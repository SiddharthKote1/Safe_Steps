import os
import shutil
import re

base_dir = r"C:\Users\asus\Safestep\app\src\main\java"
target_dir = r"C:\Users\asus\Safestep\app\src\main\java\com\Siddharth\SafeSteps"

def process_file(filepath, relative_path):
    # Determine new package name based on relative path
    # relative_path e.g., "AnalyticsDataClass/AnalyticsResponse.kt"
    # or "Auth2.0.kt"
    parts = os.path.split(relative_path)
    
    if len(parts) > 1 and parts[0]:
        subpkg = parts[0].replace('\\', '.').replace('/', '.').lower()
        new_pkg = f"com.Siddharth.SafeSteps.{subpkg}"
        new_dir = os.path.join(target_dir, parts[0].lower())
    else:
        new_pkg = "com.Siddharth.SafeSteps"
        new_dir = target_dir

    os.makedirs(new_dir, exist_ok=True)
    new_filepath = os.path.join(new_dir, parts[-1])

    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # Update package declaration
    content = re.sub(r'^package\s+.*$', f'package {new_pkg}', content, flags=re.MULTILINE)

    # Some files like NavGraph have wrong package "com.Siddharth.chat"
    # Some have "com.example.SafeSteps"
    # The regex above catches all starting with package.

    with open(new_filepath, 'w', encoding='utf-8') as f:
        f.write(content)

    print(f"Moved {relative_path} -> {new_filepath} (package {new_pkg})")

for root, dirs, files in os.walk(base_dir):
    # Skip target_dir itself to avoid infinite loop or processing already correct files
    if "com\\Siddharth" in root or "com/Siddharth" in root:
        continue
    
    for file in files:
        if file.endswith(".kt"):
            filepath = os.path.join(root, file)
            relative_path = os.path.relpath(filepath, base_dir)
            process_file(filepath, relative_path)

# Cleanup old empty directories
for root, dirs, files in os.walk(base_dir, topdown=False):
    if "com" in root:
        continue
    for d in dirs:
        dir_path = os.path.join(root, d)
        try:
            os.rmdir(dir_path)
        except OSError:
            pass

import os
import re

base_dir = r"C:\Users\asus\Safestep\app\src\main\java\com\Siddharth\SafeSteps"

for root, dirs, files in os.walk(base_dir):
    for file in files:
        if file.endswith(".kt"):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            original_content = content
            
            # Replace import data.X with import com.Siddharth.SafeSteps.data.X
            content = re.sub(r"^import\s+data\.", "import com.Siddharth.SafeSteps.data.", content, flags=re.MULTILINE)
            
            # Replace AuthDataClass. with com.Siddharth.SafeSteps.authdataclass.
            content = content.replace("AuthDataClass.", "com.Siddharth.SafeSteps.authdataclass.")
            
            # Replace LocationDataClass. with com.Siddharth.SafeSteps.locationdataclass.
            content = content.replace("LocationDataClass.", "com.Siddharth.SafeSteps.locationdataclass.")
            
            # NavGraph.kt issues: LocationPermission and NeeScreen are now in com.Siddharth.SafeSteps
            if file == "NavGraph.kt":
                content = content.replace("import Screens.LocationPermission", "import com.Siddharth.SafeSteps.LocationScreen")
                content = content.replace("import Screens.NeeScreen", "import com.Siddharth.SafeSteps.NeeScreen")
                content = content.replace("LocationPermission(", "LocationScreen(")
            
            if content != original_content:
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"Fixed imports in {file}")

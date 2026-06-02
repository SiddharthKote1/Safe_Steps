import os

base_dir = r"C:\Users\asus\Safestep\app\src\main\java\com\Siddharth\SafeSteps"

for root, dirs, files in os.walk(base_dir):
    for file in files:
        if file.endswith(".kt"):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            original_content = content
            
            # Fix inline data.RetrofitClient usages
            content = content.replace("data.RetrofitClient", "com.Siddharth.SafeSteps.data.RetrofitClient")
            
            # NavGraph.kt specific fixes since LocationScreen and NeeScreen might still be broken
            if file == "NavGraph.kt":
                content = content.replace("LocationPermission", "LocationScreen")
                content = content.replace("import Screens.LocationScreen", "import com.Siddharth.SafeSteps.LocationScreen")
                content = content.replace("import Screens.NeeScreen", "import com.Siddharth.SafeSteps.NeeScreen")

            if content != original_content:
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"Fixed inline usages in {file}")

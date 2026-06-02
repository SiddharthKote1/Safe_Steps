import os

base_dir = r"C:\Users\asus\Safestep\app\src\main\java\com\Siddharth\SafeSteps"

for root, dirs, files in os.walk(base_dir):
    for file in files:
        if file.endswith(".kt"):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            original_content = content
            
            # Fix double package prefixes
            content = content.replace("com.Siddharth.SafeSteps.com.Siddharth.SafeSteps.", "com.Siddharth.SafeSteps.")
            
            # Instead of using fully qualified com.Siddharth.SafeSteps.data.RetrofitClient in code,
            # let's just make sure it's RetrofitClient and we have the import.
            # But wait, if they didn't have the import before, they used data.RetrofitClient.
            # I replaced it with com.Siddharth.SafeSteps.data.RetrofitClient.
            # That is fully qualified. Kotlin should accept fully qualified names unless it conflicts with a local variable.
            # Let's just fix the double prefix first.
            
            # NavGraph LocationScreen and NeeScreen
            # NavGraph: Unresolved reference 'LocationScreen'
            # Let's make sure they are imported correctly.
            if file == "NavGraph.kt":
                if "import com.Siddharth.SafeSteps.screens.LocationScreen" in content:
                     content = content.replace("import com.Siddharth.SafeSteps.screens.LocationScreen", "import com.Siddharth.SafeSteps.screens.LocationScreen\nimport com.Siddharth.SafeSteps.LocationScreen")
                elif "import com.Siddharth.SafeSteps.LocationScreen" not in content:
                     content = "import com.Siddharth.SafeSteps.LocationScreen\n" + content
                
                if "import com.Siddharth.SafeSteps.screens.NeeScreen" in content:
                     content = content.replace("import com.Siddharth.SafeSteps.screens.NeeScreen", "import com.Siddharth.SafeSteps.screens.NeeScreen\nimport com.Siddharth.SafeSteps.NeeScreen")
                elif "import com.Siddharth.SafeSteps.NeeScreen" not in content:
                     content = "import com.Siddharth.SafeSteps.NeeScreen\n" + content
                     
            if content != original_content:
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"Fixed double prefix in {file}")

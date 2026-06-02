import os
import re

base_dir = r"C:\Users\asus\Safestep\app\src\main\java\com\Siddharth\SafeSteps"
repo_dir = os.path.join(base_dir, "repository")
vm_dir = os.path.join(base_dir, "viewmodel")

# 1. Refactor Repositories
for root, dirs, files in os.walk(repo_dir):
    for file in files:
        if file.endswith(".kt"):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            repo_name = file.replace(".kt", "")
            
            # import com.Siddharth.SafeSteps.data.RetrofitClient -> import com.Siddharth.SafeSteps.data.ApiService
            content = content.replace("import com.Siddharth.SafeSteps.data.RetrofitClient", "import com.Siddharth.SafeSteps.data.ApiService")
            
            # class NameRepository { private val api = RetrofitClient.apiService
            # -> class NameRepository(private val api: ApiService) {
            pattern = r"class\s+" + repo_name + r"\s*\{\s*private\s+val\s+api\s*=\s*RetrofitClient\.apiService"
            replacement = f"class {repo_name}(private val api: ApiService) {{"
            content = re.sub(pattern, replacement, content)
            
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)

# 2. Refactor ViewModels
for root, dirs, files in os.walk(vm_dir):
    for file in files:
        if file.endswith(".kt"):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            vm_name = file.replace(".kt", "")
            repo_name = vm_name.replace("ViewModel", "Repository")
            
            # class NameViewModel : ViewModel() { private val repository = NameRepository()
            # -> class NameViewModel(private val repository: NameRepository) : ViewModel() {
            pattern = r"class\s+" + vm_name + r"\s*:\s*ViewModel\(\)\s*\{\s*private\s+val\s+repository\s*=\s*" + repo_name + r"\(\)"
            replacement = f"class {vm_name}(private val repository: {repo_name}) : ViewModel() {{"
            content = re.sub(pattern, replacement, content)
            
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)

print("DI refactoring complete")

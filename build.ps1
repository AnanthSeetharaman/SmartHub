# SmartHub.apk Build Pipeline
$ErrorActionPreference = "Stop"

$base = "C:\Users\anant\Downloads\SmartHub"
$sdk = "C:\Users\anant\AppData\Local\Android\Sdk"
$bt = "$sdk\build-tools\34.0.0"
$jar = "$sdk\platforms\android-34\android.jar"
$jdkBin = "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot\bin"

$buildDir = "$base\build"
$distDir = "$base\dist"
$keystore = "$base\debug.keystore"

Write-Host ">>> Step 0: Cleaning build directories..."
Remove-Item -Recurse -Force $buildDir -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force -Path "$buildDir\res_compiled", "$buildDir\classes", "$buildDir\gen", $distDir | Out-Null

Write-Host ">>> Step 1: Compiling resources with aapt2..."
& "$bt\aapt2.exe" compile "$base\app\res\values\strings.xml" -o "$buildDir\res_compiled\"
& "$bt\aapt2.exe" compile "$base\app\res\drawable\ic_launcher.png" -o "$buildDir\res_compiled\"

$compiledRes = Get-ChildItem "$buildDir\res_compiled\*.flat" | ForEach-Object { $_.FullName }

Write-Host ">>> Step 2: Linking APK with aapt2..."
& "$bt\aapt2.exe" link -I $jar `
    --manifest "$base\app\AndroidManifest.xml" `
    -A "$base\app\assets" `
    -o "$buildDir\unaligned.apk" `
    --java "$buildDir\gen" `
    $compiledRes `
    --auto-add-overlay

Write-Host ">>> Step 3: Compiling Java source with javac..."
$javaFiles = Get-ChildItem "$base\app\src\*.java" -Recurse | ForEach-Object { $_.FullName }
$genFiles = Get-ChildItem "$buildDir\gen\*.java" -Recurse -ErrorAction SilentlyContinue | ForEach-Object { $_.FullName }
$allJava = @($javaFiles) + @($genFiles)

& "$jdkBin\javac.exe" -cp $jar -d "$buildDir\classes" -source 17 -target 17 $allJava

Write-Host ">>> Step 4: Dexing class files with d8..."
$classFiles = Get-ChildItem "$buildDir\classes\*.class" -Recurse | ForEach-Object { $_.FullName }
& "$bt\d8.bat" --output "$buildDir" --lib $jar $classFiles

Write-Host ">>> Step 5: Adding classes.dex to APK..."
Set-Location $buildDir
& "$jdkBin\jar.exe" -uf "$buildDir\unaligned.apk" "classes.dex"
Set-Location $base

Write-Host ">>> Step 6: Aligning APK with zipalign..."
& "$bt\zipalign.exe" -f -p 4 "$buildDir\unaligned.apk" "$buildDir\aligned.apk"

Write-Host ">>> Step 7: Checking Keystore & Signing APK..."
if (-not (Test-Path $keystore)) {
    & "$jdkBin\keytool.exe" -genkey -v -keystore $keystore -alias androiddebugkey -storepass android -keypass android -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=SmartHub, OU=Development, O=OpenSource, L=Local, S=State, C=US"
}

$finalApk = "$distDir\SmartHub.apk"
& "$bt\apksigner.bat" sign --ks $keystore --ks-pass pass:android --key-pass pass:android --ks-key-alias androiddebugkey --out $finalApk "$buildDir\aligned.apk"

Write-Host ">>> SUCCESS! SmartHub.apk compiled: $finalApk"
Get-Item $finalApk | Select-Object Name, Length, LastWriteTime

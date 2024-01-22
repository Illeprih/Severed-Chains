@echo off

if exist ".\jdk21\bin\java.exe" (
  goto LAUNCH
)

: DOWNLOAD_JAVA
if exist ".\jdk21\" (
  @rmdir /S /Q ".\jdk21"
)

echo Downloading java...
powershell -Command "$ProgressPreference = 'SilentlyContinue'; Invoke-WebRequest -Uri 'https://corretto.aws/downloads/resources/21.0.2.13.1/amazon-corretto-21.0.2.13.1-windows-x64-jdk.zip' -OutFile '.\jdk.zip'"

echo Extracting java...
powershell Expand-Archive ".\jdk.zip" -DestinationPath "."

echo Cleaning up...
move ".\jdk21.0.2_13" ".\jdk21"
del ".\jdk.zip"

: LAUNCH
".\jdk21\bin\java" -cp "lod-game-@version@.jar;libs/*" legend.game.MainWindows -Xmx2G -ea -Djoml.fastmath -Djoml.sinLookup -Djoml.useMathFma || pause

@echo off
echo ====================================
echo 打地鼠助手 APK 打包工具
echo ====================================
echo.

REM 检查Java环境
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到Java环境，请先安装JDK
    pause
    exit /b 1
)

echo [1/4] 检查环境...
if not exist "app\src\main\AndroidManifest.xml" (
    echo [错误] 项目文件不完整
    pause
    exit /b 1
)

echo [2/4] 创建必要的目录...
if not exist "app\build\outputs\apk\debug" mkdir "app\build\outputs\apk\debug"

echo [3/4] 开始编译APK...
echo.
echo 提示：由于没有Gradle Wrapper，建议使用以下方式之一：
echo.
echo 方式1：使用Android Studio打开项目并构建
echo 方式2：安装Gradle后运行: gradle assembleDebug
echo 方式3：使用在线APK构建服务
echo.
echo 如果您已安装Gradle，按任意键继续使用Gradle构建...
pause >nul

where gradle >nul 2>&1
if %errorlevel% equ 0 (
    echo 检测到Gradle，开始构建...
    gradle assembleDebug
    if %errorlevel% equ 0 (
        echo.
        echo [4/4] 构建成功！
        echo APK文件位置: app\build\outputs\apk\debug\app-debug.apk
    ) else (
        echo [错误] 构建失败
    )
) else (
    echo [错误] 未检测到Gradle，请先安装Gradle或使用Android Studio
)

echo.
pause


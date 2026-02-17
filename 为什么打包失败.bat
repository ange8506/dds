@echo off
chcp 65001 >nul
cls
echo.
echo ╔════════════════════════════════════════════════════════════╗
echo ║                  打包失败 - 原因分析                       ║
echo ╚════════════════════════════════════════════════════════════╝
echo.
echo [问题] 您的系统安装了 JDK 21，但 Android 构建工具不兼容
echo.
echo ┌────────────────────────────────────────────────────────────┐
echo │ 推荐解决方案（按优先级）：                                 │
echo └────────────────────────────────────────────────────────────┘
echo.
echo [1] 安装 JDK 17（最快，10分钟）
echo     下载地址：https://adoptium.net/temurin/releases/?version=17
echo     安装后设置 JAVA_HOME 环境变量
echo.
echo [2] 使用 Android Studio（最简单）
echo     下载地址：https://developer.android.com/studio
echo     内置正确的JDK，无需额外配置
echo.
echo [3] 使用 GitHub Actions（在线构建）
echo     将项目上传到GitHub，自动构建APK
echo     无需本地环境
echo.
echo ┌────────────────────────────────────────────────────────────┐
echo │ 详细说明请查看：打包失败原因及解决方案.md                  │
echo └────────────────────────────────────────────────────────────┘
echo.
echo 按任意键打开详细说明文档...
pause >nul
start "" "打包失败原因及解决方案.md"


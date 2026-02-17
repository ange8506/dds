@echo off
chcp 65001 >nul
echo.
echo ╔════════════════════════════════════════════════════════════╗
echo ║          打地鼠助手 - APK打包助手                          ║
echo ╚════════════════════════════════════════════════════════════╝
echo.
echo 检测到您想要打包APK，但当前环境缺少必要工具。
echo.
echo ┌────────────────────────────────────────────────────────────┐
echo │ 推荐方案：                                                 │
echo └────────────────────────────────────────────────────────────┘
echo.
echo [1] 使用 Android Studio（最简单，推荐）
echo     - 自动处理所有依赖
echo     - 一键构建APK
echo     - 下载地址：https://developer.android.com/studio
echo.
echo [2] 使用 GitHub Actions（在线构建，无需本地环境）
echo     - 完全免费
echo     - 自动构建
echo     - 将项目上传到GitHub即可
echo.
echo [3] 手动配置环境（需要技术经验）
echo     - 需要下载Gradle和Android SDK
echo     - 总大小约2-3GB
echo.
echo ┌────────────────────────────────────────────────────────────┐
echo │ 详细说明请查看：                                           │
echo │ - 如何打包APK.md                                           │
echo │ - QUICK_BUILD.md                                           │
echo │ - BUILD_GUIDE.md                                           │
echo └────────────────────────────────────────────────────────────┘
echo.
echo 按任意键打开详细说明文档...
pause >nul
start "" "如何打包APK.md"


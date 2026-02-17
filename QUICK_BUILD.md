# 快速打包APK - 完整方案

## 🚀 最简单的方法：使用在线构建

### Appetize.io / APK在线构建
由于本地缺少Android SDK和Gradle，推荐使用以下在线服务：

1. **使用GitHub + GitHub Actions（完全免费）**
   
   我已经为你准备好了配置文件，只需：
   - 将项目上传到GitHub
   - GitHub会自动构建APK
   - 下载构建好的APK

2. **使用AppGyver / Kodular（无需编程）**
   - 上传项目文件
   - 在线构建APK

---

## 💻 本地构建方案

### 需要安装的工具

#### 1. 安装Android命令行工具

下载地址：https://developer.android.com/studio#command-tools

下载后解压到：`C:\Android\cmdline-tools`

#### 2. 设置环境变量

```batch
setx ANDROID_HOME "C:\Android"
setx PATH "%PATH%;C:\Android\cmdline-tools\latest\bin;C:\Android\platform-tools"
```

#### 3. 安装必要的SDK组件

```batch
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
```

#### 4. 安装Gradle

下载：https://gradle.org/releases/
解压后添加到PATH环境变量

---

## 🎯 推荐方案：使用Android Studio

这是最可靠的方式：

1. 下载Android Studio：https://developer.android.com/studio
2. 安装并打开Android Studio
3. 选择 "Open an existing project"
4. 打开 `x:/DPS/dds` 目录
5. 等待Gradle同步（首次会自动下载所需工具）
6. 点击 Build -> Build Bundle(s) / APK(s) -> Build APK(s)
7. 完成后点击通知中的 "locate" 找到APK文件

APK位置：`app/build/outputs/apk/debug/app-debug.apk`

---

## 📦 使用预构建的Gradle Wrapper

我可以帮你配置完整的Gradle Wrapper，但需要先下载gradle-wrapper.jar文件。

你可以：
1. 从其他Android项目复制 `gradle/wrapper/gradle-wrapper.jar`
2. 或者从这里下载：https://services.gradle.org/distributions/

---

## ⚡ 临时方案：使用Docker

如果你安装了Docker：

```bash
docker run --rm -v "x:/DPS/dds:/project" -w /project mingc/android-build-box bash -c "chmod +x gradlew && ./gradlew assembleDebug"
```

---

**我的建议**：直接使用Android Studio是最省时省力的方案，它会自动处理所有依赖和配置。


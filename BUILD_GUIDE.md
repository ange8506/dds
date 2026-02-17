# 打地鼠助手 - 手动打包指南

由于项目需要Android SDK和构建工具，这里提供几种打包APK的方法：

## 方法1：使用Android Studio（推荐）

1. 打开Android Studio
2. 选择 File -> Open，打开 `x:/DPS/dds` 目录
3. 等待Gradle同步完成
4. 选择 Build -> Build Bundle(s) / APK(s) -> Build APK(s)
5. 构建完成后，APK位于 `app/build/outputs/apk/debug/app-debug.apk`

## 方法2：使用命令行（需要Android SDK）

### 前置要求
- 安装 Android SDK
- 设置环境变量 ANDROID_HOME

### 步骤

1. **初始化Gradle Wrapper**
```bash
gradle wrapper
```

2. **构建APK**
```bash
gradlew.bat assembleDebug
```

3. **查找APK**
生成的APK位于：`app/build/outputs/apk/debug/app-debug.apk`

## 方法3：在线构建服务

如果本地环境配置困难，可以使用在线构建服务：

1. **GitHub Actions**（免费）
   - 将代码推送到GitHub
   - 配置GitHub Actions自动构建

2. **AppCenter**（免费）
   - 注册Microsoft AppCenter账号
   - 连接代码仓库自动构建

## 方法4：使用Gradle（如果已安装）

如果系统已安装Gradle：

```bash
cd x:/DPS/dds
gradle assembleDebug
```

## 构建产物

成功构建后，APK文件位置：
```
app/build/outputs/apk/debug/app-debug.apk
```

这是一个未签名的Debug版本APK，可以直接安装到手机上测试使用。

## 发布版本（可选）

如果需要发布到应用市场，需要生成签名的Release版本：

1. 创建密钥库
2. 配置签名信息
3. 运行 `gradlew.bat assembleRelease`

---

**推荐方案**：使用Android Studio是最简单可靠的方式，它会自动下载所需的SDK和构建工具。


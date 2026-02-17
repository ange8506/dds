# 打地鼠游戏助手

一个基于Android无障碍服务的自动化游戏辅助工具，用于自动识别并点击游戏中的彩色兔子目标。

## 功能特点

- ✅ 自动识别游戏中的彩色兔子（紫色、黄色、蓝色、绿色等）
- ✅ 智能避开炸弹目标
- ✅ 悬浮窗控制，方便开启/停止
- ✅ 实时状态显示
- ✅ 高效的点击响应速度

## 使用方法

1. **安装应用**
   - 使用Android Studio打开项目
   - 连接手机或启动模拟器
   - 点击运行按钮安装应用

2. **开启无障碍服务**
   - 打开应用
   - 点击"开启无障碍服务"按钮
   - 在系统设置中找到"打地鼠助手"
   - 开启无障碍服务权限

3. **开始使用**
   - 进入游戏界面
   - 点击悬浮窗的"开始"按钮
   - 工具会自动识别并点击目标
   - 需要停止时点击"停止"按钮

## 技术实现

- **无障碍服务**: 使用AccessibilityService获取屏幕内容
- **手势模拟**: 通过GestureDescription实现自动点击
- **协程**: 使用Kotlin协程实现异步扫描和点击
- **悬浮窗**: TYPE_ACCESSIBILITY_OVERLAY实现游戏内控制

## 系统要求

- Android 7.0 (API 24) 及以上
- 需要开启无障碍服务权限
- 需要开启悬浮窗权限

## 注意事项

⚠️ 本工具仅供学习和研究使用，请勿用于违反游戏规则的行为。

## 项目结构

```
app/
├── src/main/
│   ├── java/com/dds/assistant/
│   │   ├── MainActivity.kt                    # 主界面
│   │   └── WhackMoleAccessibilityService.kt   # 无障碍服务核心
│   ├── res/
│   │   ├── layout/
│   │   │   ├── activity_main.xml              # 主界面布局
│   │   │   └── floating_control.xml           # 悬浮窗布局
│   │   ├── values/
│   │   │   ├── strings.xml
│   │   │   └── themes.xml
│   │   └── xml/
│   │       └── accessibility_service_config.xml
│   └── AndroidManifest.xml
└── build.gradle
```

## 开发环境

- Android Studio Hedgehog | 2023.1.1+
- Kotlin 1.9.0
- Gradle 8.1.0
- Target SDK: 34


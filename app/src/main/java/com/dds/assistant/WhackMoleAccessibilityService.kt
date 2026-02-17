package com.dds.assistant

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.*

class WhackMoleAccessibilityService : AccessibilityService() {

    private var isRunning = false
    private var windowManager: WindowManager? = null
    private var floatingView: android.view.View? = null
    private val handler = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    // 游戏区域配置（根据图片分析，4x4网格）
    private val gridRows = 4
    private val gridCols = 4
    private var gameAreaRect: Rect? = null
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        showFloatingButton()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!isRunning) return
        
        // 检测游戏界面变化时扫描目标
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            scanAndClickTargets()
        }
    }

    override fun onInterrupt() {
        stopService()
    }

    private fun showFloatingButton() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 100
        }

        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_control, null)
        
        val startButton = floatingView?.findViewById<android.widget.Button>(R.id.btnStart)
        val stopButton = floatingView?.findViewById<android.widget.Button>(R.id.btnStop)
        val statusText = floatingView?.findViewById<android.widget.TextView>(R.id.tvStatus)
        
        startButton?.setOnClickListener {
            isRunning = true
            statusText?.text = "运行中"
            startAutoClick()
        }
        
        stopButton?.setOnClickListener {
            isRunning = false
            statusText?.text = "已停止"
        }
        
        windowManager?.addView(floatingView, params)
    }

    private fun startAutoClick() {
        scope.launch {
            while (isRunning) {
                scanAndClickTargets()
                delay(150) // 每150毫秒扫描一次
            }
        }
    }

    private fun scanAndClickTargets() {
        val rootNode = rootInActiveWindow ?: return
        
        // 查找游戏区域
        findGameArea(rootNode)
        
        // 扫描所有可点击的目标
        val targets = findClickableTargets(rootNode)
        
        targets.forEach { target ->
            if (isRunning && !isBomb(target)) {
                performClick(target.bounds)
                Thread.sleep(50) // 点击间隔
            }
        }
        
        rootNode.recycle()
    }

    private fun findGameArea(node: AccessibilityNodeInfo) {
        // 尝试通过特征识别游戏区域
        // 可以根据游戏界面的特定文本或ID来定位
        val rect = Rect()
        node.getBoundsInScreen(rect)
        
        // 假设游戏区域在屏幕中央偏下位置
        if (gameAreaRect == null && rect.width() > 0 && rect.height() > 0) {
            // 根据实际游戏界面调整这些值
            val screenHeight = resources.displayMetrics.heightPixels
            val screenWidth = resources.displayMetrics.widthPixels
            
            gameAreaRect = Rect(
                screenWidth / 8,
                screenHeight / 3,
                screenWidth * 7 / 8,
                screenHeight * 3 / 4
            )
        }
    }

    private fun findClickableTargets(node: AccessibilityNodeInfo): List<TargetInfo> {
        val targets = mutableListOf<TargetInfo>()
        
        fun traverse(n: AccessibilityNodeInfo) {
            val rect = Rect()
            n.getBoundsInScreen(rect)
            
            // 检查是否在游戏区域内
            if (gameAreaRect?.contains(rect.centerX(), rect.centerY()) == true) {
                // 通过节点特征判断是否为目标
                if (isTarget(n)) {
                    targets.add(TargetInfo(rect, n.className?.toString() ?: ""))
                }
            }
            
            for (i in 0 until n.childCount) {
                n.getChild(i)?.let { traverse(it) }
            }
        }
        
        traverse(node)
        return targets
    }

    private fun isTarget(node: AccessibilityNodeInfo): Boolean {
        // 根据节点特征判断是否为可点击目标
        // 可以通过contentDescription、className等属性判断
        val desc = node.contentDescription?.toString() ?: ""
        val className = node.className?.toString() ?: ""
        
        // 排除炸弹和空洞
        return node.isClickable && 
               !desc.contains("炸弹") && 
               !desc.contains("bomb") &&
               !className.contains("Empty")
    }

    private fun isBomb(target: TargetInfo): Boolean {
        // 通过特征判断是否为炸弹
        // 炸弹通常有特定的描述或样式
        return target.className.contains("bomb", ignoreCase = true)
    }

    private fun performClick(rect: Rect) {
        val path = Path()
        val x = rect.centerX().toFloat()
        val y = rect.centerY().toFloat()
        
        path.moveTo(x, y)
        
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 50))
            .build()
        
        dispatchGesture(gesture, null, null)
    }

    private fun stopService() {
        isRunning = false
        scope.cancel()
        floatingView?.let { windowManager?.removeView(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService()
    }

    data class TargetInfo(
        val bounds: Rect,
        val className: String
    )
}


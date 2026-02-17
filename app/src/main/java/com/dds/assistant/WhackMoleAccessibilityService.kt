package com.dds.assistant

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.*
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.*
import kotlin.math.abs

class WhackMoleAccessibilityService : AccessibilityService() {

    private var isRunning = false
    private var windowManager: WindowManager? = null
    private var floatingView: android.view.View? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // 游戏区域配置（4x4网格）
    private val gridRows = 4
    private val gridCols = 4
    private var gameAreaRect: Rect? = null
    private val gridCells = mutableListOf<Rect>()
    
    // 记录每个格子的状态
    private val cellStates = mutableMapOf<Int, CellState>()
    
    // 屏幕尺寸
    private var screenWidth = 0
    private var screenHeight = 0
    
    data class CellState(
        var hasContent: Boolean = false,
        var isBomb: Boolean = false,
        var lastCheckTime: Long = 0
    )
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        initScreenDimensions()
        initGameArea()
        showFloatingButton()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!isRunning) return
        
        // 监听屏幕内容变化
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
            event?.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            // 触发扫描
        }
    }

    override fun onInterrupt() {
        stopService()
    }
    
    private fun initScreenDimensions() {
        val metrics = resources.displayMetrics
        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }
    
    private fun initGameArea() {
        // 根据图片分析，游戏区域大约在屏幕中央偏下
        val areaWidth = (screenWidth * 0.85).toInt()
        val areaHeight = (screenHeight * 0.5).toInt()
        val left = (screenWidth - areaWidth) / 2
        val top = (screenHeight * 0.35).toInt()
        
        gameAreaRect = Rect(left, top, left + areaWidth, top + areaHeight)
        
        // 计算每个格子的位置
        val cellWidth = areaWidth / gridCols
        val cellHeight = areaHeight / gridRows
        
        gridCells.clear()
        for (row in 0 until gridRows) {
            for (col in 0 until gridCols) {
                val cellLeft = left + col * cellWidth
                val cellTop = top + row * cellHeight
                val cellRect = Rect(
                    cellLeft + cellWidth / 4,  // 缩小点击区域，避免边缘
                    cellTop + cellHeight / 4,
                    cellLeft + cellWidth * 3 / 4,
                    cellTop + cellHeight * 3 / 4
                )
                gridCells.add(cellRect)
                cellStates[row * gridCols + col] = CellState()
            }
        }
    }

    private fun showFloatingButton() {
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
                try {
                    scanAndClickTargets()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(150) // 每150毫秒扫描一次
            }
        }
    }

    private suspend fun scanAndClickTargets() = withContext(Dispatchers.Main) {
        val rootNode = rootInActiveWindow ?: return@withContext
        
        // 查找游戏区域内的所有可交互节点
        val clickableNodes = mutableListOf<NodeInfo>()
        findClickableNodes(rootNode, clickableNodes)
        
        // 检查每个格子
        gridCells.forEachIndexed { index, cellRect ->
            if (!isRunning) return@forEachIndexed
            
            val currentTime = System.currentTimeMillis()
            val cellState = cellStates[index] ?: return@forEachIndexed
            
            // 检查该格子区域是否有新内容
            val hasContent = clickableNodes.any { node ->
                cellRect.contains(node.bounds.centerX(), node.bounds.centerY())
            }
            
            // 如果有新内容出现
            if (hasContent && !cellState.hasContent) {
                // 检查是否是炸弹
                val isBomb = checkIfBomb(clickableNodes, cellRect)
                
                if (!isBomb) {
                    // 不是炸弹，立即点击
                    val clickX = cellRect.centerX()
                    val clickY = cellRect.centerY()
                    performClick(clickX, clickY)
                    delay(30)
                }
                
                cellState.hasContent = true
                cellState.isBomb = isBomb
                cellState.lastCheckTime = currentTime
            } else if (!hasContent) {
                // 内容消失，重置状态
                cellState.hasContent = false
                cellState.isBomb = false
            }
        }
        
        rootNode.recycle()
    }
    
    private fun findClickableNodes(node: AccessibilityNodeInfo, result: MutableList<NodeInfo>) {
        val bounds = Rect()
        node.getBoundsInScreen(bounds)
        
        // 只关注游戏区域内的节点
        if (gameAreaRect?.intersect(bounds) == true) {
            // 收集所有节点信息（不仅是可点击的）
            val desc = node.contentDescription?.toString() ?: ""
            val text = node.text?.toString() ?: ""
            val className = node.className?.toString() ?: ""
            
            result.add(NodeInfo(bounds, desc, text, className))
            
            // 递归查找子节点
            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { child ->
                    findClickableNodes(child, result)
                    child.recycle()
                }
            }
        }
    }
    
    private fun checkIfBomb(nodes: List<NodeInfo>, cellRect: Rect): Boolean {
        // 检查该格子内的节点特征
        val cellNodes = nodes.filter { node ->
            cellRect.contains(node.bounds.centerX(), node.bounds.centerY())
        }
        
        // 炸弹特征检测
        for (node in cellNodes) {
            val desc = node.description.lowercase()
            val text = node.text.lowercase()
            val className = node.className.lowercase()
            
            // 关键词检测
            if (desc.contains("炸弹") || desc.contains("bomb") ||
                text.contains("炸弹") || text.contains("bomb") ||
                className.contains("bomb")) {
                return true
            }
            
            // 如果节点描述包含"危险"、"爆炸"等关键词
            if (desc.contains("危险") || desc.contains("爆炸") ||
                desc.contains("danger") || desc.contains("explosive")) {
                return true
            }
        }
        
        return false
    }
    
    private fun performClick(x: Int, y: Int) {
        val path = Path()
        path.moveTo(x.toFloat(), y.toFloat())
        
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 50))
            .build()
        
        dispatchGesture(gesture, null, null)
    }

    private fun stopService() {
        isRunning = false
        scope.cancel()
        floatingView?.let { windowManager?.removeView(it) }
        cellStates.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService()
    }
    
    data class NodeInfo(
        val bounds: Rect,
        val description: String,
        val text: String,
        val className: String
    )
}

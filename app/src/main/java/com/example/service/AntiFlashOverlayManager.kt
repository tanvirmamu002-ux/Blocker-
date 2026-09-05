package com.example.service

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.example.util.FocusPermissionHelper

/**
 * High-performance, anti-content-flash full-screen black overlay manager.
 * Safely displays an opaque backdrop the instant short-video / reels UI is detected,
 * hiding the screen content before launching the home action, then smoothly dismissing
 * after ~600ms.
 */
class AntiFlashOverlayManager(private val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
    private val mainHandler = Handler(Looper.getMainLooper())
    private var overlayView: View? = null
    private var isOverlayShowing = false

    private val dismissRunnable = Runnable {
        removeOverlay()
    }

    /**
     * Attempts to show an opaque black overlay immediately to prevent video content flash.
     * Checks Settings.canDrawOverlays before attempting to add the Window.
     * Auto-dismisses after 600ms.
     */
    fun showTemporaryAntiFlashOverlay(dismissAfterMs: Long = 600L) {
        if (!FocusPermissionHelper.isOverlayPermissionGranted(context)) {
            return
        }

        mainHandler.post {
            try {
                if (windowManager == null) return@post

                if (isOverlayShowing && overlayView != null) {
                    // Reset dismiss timer
                    mainHandler.removeCallbacks(dismissRunnable)
                    mainHandler.postDelayed(dismissRunnable, dismissAfterMs)
                    return@post
                }

                val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    @Suppress("DEPRECATION")
                    WindowManager.LayoutParams.TYPE_PHONE
                }

                val params = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    layoutType,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.OPAQUE
                ).apply {
                    gravity = Gravity.CENTER
                }

                val view = View(context).apply {
                    setBackgroundColor(Color.BLACK)
                }

                windowManager.addView(view, params)
                overlayView = view
                isOverlayShowing = true

                mainHandler.removeCallbacks(dismissRunnable)
                mainHandler.postDelayed(dismissRunnable, dismissAfterMs)
            } catch (e: Exception) {
                // If WindowManager fails due to permissions or race condition, fail silently
                isOverlayShowing = false
                overlayView = null
            }
        }
    }

    private fun removeOverlay() {
        mainHandler.post {
            try {
                if (isOverlayShowing && overlayView != null && windowManager != null) {
                    windowManager.removeView(overlayView)
                }
            } catch (e: Exception) {
                // Ignore removal exception
            } finally {
                overlayView = null
                isOverlayShowing = false
            }
        }
    }
}

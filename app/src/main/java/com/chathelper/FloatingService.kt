package com.chathelper

import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast

class FloatingService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var bubble: TextView

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        bubble = TextView(this)

        bubble.text = "💬"
        bubble.textSize = 26f
        bubble.gravity = Gravity.CENTER
        bubble.setTextColor(Color.WHITE)
        bubble.setBackgroundColor(Color.DKGRAY)
        bubble.setPadding(20, 20, 20, 20)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
        params.x = 20
        params.y = 0

        bubble.setOnClickListener {

            val message = getSharedPreferences(
                "helper",
                MODE_PRIVATE
            ).getString("message", "") ?: ""

            if (message.isNotEmpty()) {

                val clipboard =
                    getSystemService(Context.CLIPBOARD_SERVICE)
                            as ClipboardManager

                val clip =
                    android.content.ClipData.newPlainText(
                        "Message",
                        message
                    )

                clipboard.setPrimaryClip(clip)

                Toast.makeText(
                    this,
                    "Message copied. Chat me paste karke Send karo.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        windowManager.addView(bubble, params)
    }

    override fun onDestroy() {

        if (::bubble.isInitialized) {
            windowManager.removeView(bubble)
        }

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}

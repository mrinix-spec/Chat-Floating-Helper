package com.chathelper

import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast

class FloatingService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var bubble: TextView

    private var currentIndex = 0

    override fun onCreate() {
        super.onCreate()

        windowManager =
            getSystemService(WINDOW_SERVICE) as WindowManager

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

        params.gravity =
            Gravity.CENTER_VERTICAL or Gravity.RIGHT

        params.x = 20

        bubble.setOnClickListener {

            copyNextMessage()
        }

        windowManager.addView(bubble, params)
    }

    private fun copyNextMessage() {

        val data = getSharedPreferences(
            "messages",
            MODE_PRIVATE
        )
            .getString("list", "") ?: ""

        if (data.isEmpty()) {

            Toast.makeText(
                this,
                "Pehle messages add karo",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val messages = data.split("|||")

        if (currentIndex >= messages.size) {
            currentIndex = 0
        }

        val message = messages[currentIndex]

        val clipboard =
            getSystemService(
                Context.CLIPBOARD_SERVICE
            ) as ClipboardManager

        clipboard.setPrimaryClip(
            ClipData.newPlainText(
                "Message",
                message
            )
        )

        Toast.makeText(
            this,
            "Message ${currentIndex + 1} copied",
            Toast.LENGTH_SHORT
        ).show()

        currentIndex++
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

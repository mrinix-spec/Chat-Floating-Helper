package com.chathelper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val messageInput = findViewById<EditText>(R.id.messageInput)
        val startButton = findViewById<Button>(R.id.startButton)
        val stopButton = findViewById<Button>(R.id.stopButton)

        startButton.setOnClickListener {

            val message = messageInput.text.toString()

            if (message.isNotEmpty()) {

                getSharedPreferences("helper", MODE_PRIVATE)
                    .edit()
                    .putString("message", message)
                    .apply()

                if (!Settings.canDrawOverlays(this)) {

                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )

                    startActivity(intent)

                } else {

                    startService(
                        Intent(this, FloatingService::class.java)
                    )
                }
            }
        }

        stopButton.setOnClickListener {

            stopService(
                Intent(this, FloatingService::class.java)
            )
        }
    }
}

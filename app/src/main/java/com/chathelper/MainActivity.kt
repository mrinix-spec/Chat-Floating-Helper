package com.chathelper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val messages = ArrayList<String>()

    private lateinit var messageInput: EditText
    private lateinit var messageList: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        messageInput = findViewById(R.id.messageInput)
        messageList = findViewById(R.id.messageList)

        val addButton = findViewById<Button>(R.id.addMessageButton)
        val startButton = findViewById<Button>(R.id.startButton)
        val stopButton = findViewById<Button>(R.id.stopButton)

        addButton.setOnClickListener {

            val message = messageInput.text.toString().trim()

            if (message.isNotEmpty()) {

                messages.add(message)

                messageInput.text.clear()

                updateMessageList()

                saveMessages()

                Toast.makeText(
                    this,
                    "Message added",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                Toast.makeText(
                    this,
                    "Pehle message likho",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        startButton.setOnClickListener {

            saveMessages()

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

        stopButton.setOnClickListener {

            stopService(
                Intent(this, FloatingService::class.java)
            )
        }

        loadMessages()
    }

    private fun updateMessageList() {

        if (messages.isEmpty()) {

            messageList.text = "Messages yahan dikhenge"

            return
        }

        val text = StringBuilder()

        messages.forEachIndexed { index, message ->

            text.append(index + 1)
            text.append(". ")
            text.append(message)
            text.append("\n\n")
        }

        messageList.text = text.toString()
    }

    private fun saveMessages() {

        val data = messages.joinToString("|||")

        getSharedPreferences(
            "messages",
            MODE_PRIVATE
        )
            .edit()
            .putString("list", data)
            .apply()
    }

    private fun loadMessages() {

        val data = getSharedPreferences(
            "messages",
            MODE_PRIVATE
        )
            .getString("list", "") ?: ""

        if (data.isNotEmpty()) {

            messages.clear()

            messages.addAll(
                data.split("|||")
            )
        }

        updateMessageList()
    }
}

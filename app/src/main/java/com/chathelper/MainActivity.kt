package com.chathelper

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val messages = ArrayList<String>()

    private lateinit var chatIdInput: EditText
    private lateinit var messageInput: EditText
    private lateinit var messageList: TextView
    private lateinit var statusText: TextView

    private val handler = Handler(Looper.getMainLooper())

    private var currentIndex = 0
    private var autoRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        chatIdInput = findViewById(R.id.chatIdInput)
        messageInput = findViewById(R.id.messageInput)
        messageList = findViewById(R.id.messageList)
        statusText = findViewById(R.id.statusText)

        val addMessageButton =
            findViewById<Button>(R.id.addMessageButton)

        val sendTestButton =
            findViewById<Button>(R.id.sendTestButton)

        val startButton =
            findViewById<Button>(R.id.startButton)

        val stopButton =
            findViewById<Button>(R.id.stopButton)

        loadMessages()

        addMessageButton.setOnClickListener {

            val message =
                messageInput.text.toString().trim()

            if (message.isEmpty()) {
                Toast.makeText(
                    this,
                    "Pehle message likho",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            messages.add(message)

            messageInput.text.clear()

            saveMessages()
            updateMessageList()

            Toast.makeText(
                this,
                "Message saved",
                Toast.LENGTH_SHORT
            ).show()
        }

        sendTestButton.setOnClickListener {

            val chatId =
                chatIdInput.text.toString().trim()

            if (chatId.isEmpty()) {
                Toast.makeText(
                    this,
                    "Telegram Chat ID enter karo",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (messages.isEmpty()) {
                Toast.makeText(
                    this,
                    "Pehle message add karo",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            statusText.text =
                "Status: Telegram test ready\nChat ID: $chatId"
        }

        startButton.setOnClickListener {

            if (messages.isEmpty()) {
                Toast.makeText(
                    this,
                    "Pehle message add karo",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (chatIdInput.text.toString().trim().isEmpty()) {
                Toast.makeText(
                    this,
                    "Telegram Chat ID enter karo",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            autoRunning = true
            currentIndex = 0

            statusText.text =
                "Status: Auto Bot ON"
        }

        stopButton.setOnClickListener {

            autoRunning = false

            statusText.text =
                "Status: Stopped"
        }
    }

    private fun updateMessageList() {

        if (messages.isEmpty()) {
            messageList.text = "Saved messages..."
            return
        }

        val text = StringBuilder()

        messages.forEachIndexed { index, message ->

            text.append(index + 1)
            text.append(". ")
            text.append(message)
            text.append("\n")
        }

        messageList.text = text.toString()
    }

    private fun saveMessages() {

        val data =
            messages.joinToString("|||")

        getSharedPreferences(
            "messages",
            MODE_PRIVATE
        )
            .edit()
            .putString("list", data)
            .apply()
    }

    private fun loadMessages() {

        val data =
            getSharedPreferences(
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

    override fun onDestroy() {

        handler.removeCallbacksAndMessages(null)

        super.onDestroy()
    }
}

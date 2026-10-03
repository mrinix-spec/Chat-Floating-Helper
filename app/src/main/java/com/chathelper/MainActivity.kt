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

    private lateinit var messageInput: EditText
    private lateinit var messageList: TextView
    private lateinit var chatBox: TextView

    private val handler = Handler(Looper.getMainLooper())

    private var currentIndex = 0
    private var autoRunning = false

    private val autoSendRunnable = object : Runnable {

        override fun run() {

            if (!autoRunning || messages.isEmpty()) {
                return
            }

            sendNextMessage()

            handler.postDelayed(
                this,
                5000
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        messageInput = findViewById(R.id.messageInput)
        messageList = findViewById(R.id.messageList)
        chatBox = findViewById(R.id.chatBox)

        val addButton =
            findViewById<Button>(R.id.addMessageButton)

        val startButton =
            findViewById<Button>(R.id.startButton)

        val stopButton =
            findViewById<Button>(R.id.stopButton)

        loadMessages()

        addButton.setOnClickListener {

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

        startButton.setOnClickListener {

            if (messages.isEmpty()) {

                Toast.makeText(
                    this,
                    "Pehle kam se kam 1 message add karo",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (autoRunning) {
                return@setOnClickListener
            }

            autoRunning = true
            currentIndex = 0

            Toast.makeText(
                this,
                "Auto Chat Started",
                Toast.LENGTH_SHORT
            ).show()

            handler.post(autoSendRunnable)
        }

        stopButton.setOnClickListener {

            stopAutoChat()
        }
    }

    private fun sendNextMessage() {

        if (messages.isEmpty()) {
            return
        }

        if (currentIndex >= messages.size) {
            currentIndex = 0
        }

        val message =
            messages[currentIndex]

        chatBox.append(
            "You: $message\n\n"
        )

        currentIndex++
    }

    private fun stopAutoChat() {

        autoRunning = false

        handler.removeCallbacks(
            autoSendRunnable
        )

        Toast.makeText(
            this,
            "Auto Chat Stopped",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun updateMessageList() {

        if (messages.isEmpty()) {

            messageList.text =
                "Saved messages..."

            return
        }

        val text =
            StringBuilder()

        messages.forEachIndexed { index, message ->

            text.append(index + 1)
            text.append(". ")
            text.append(message)
            text.append("\n")
        }

        messageList.text =
            text.toString()
    }

    private fun saveMessages() {

        val data =
            messages.joinToString("|||")

        getSharedPreferences(
            "messages",
            MODE_PRIVATE
        )
            .edit()
            .putString(
                "list",
                data
            )
            .apply()
    }

    private fun loadMessages() {

        val data =
            getSharedPreferences(
                "messages",
                MODE_PRIVATE
            )
                .getString(
                    "list",
                    ""
                ) ?: ""

        if (data.isNotEmpty()) {

            messages.clear()

            messages.addAll(
                data.split("|||")
            )
        }

        updateMessageList()
    }

    override fun onDestroy() {

        handler.removeCallbacks(
            autoSendRunnable
        )

        super.onDestroy()
    }
}

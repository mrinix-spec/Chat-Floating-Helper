package com.chathelper

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {

    private lateinit var botTokenInput: EditText
    private lateinit var chatIdInput: EditText
    private lateinit var messageInput: EditText
    private lateinit var messageList: TextView
    private lateinit var statusText: TextView

    private val messages = ArrayList<String>()
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        botTokenInput = findViewById(R.id.botTokenInput)
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

            val token =
                botTokenInput.text.toString().trim()

            val chatId =
                chatIdInput.text.toString().trim()

            if (token.isEmpty()) {
                Toast.makeText(
                    this,
                    "Bot Token enter karo",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (chatId.isEmpty()) {
                Toast.makeText(
                    this,
                    "Chat ID enter karo",
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

            sendTelegramMessage(
                token,
                chatId,
                messages[0]
            )
        }

        startButton.setOnClickListener {

            Toast.makeText(
                this,
                "Auto Bot setup next step me hoga",
                Toast.LENGTH_SHORT
            ).show()
        }

        stopButton.setOnClickListener {

            statusText.text = "Status: Stopped"
        }
    }

    private fun sendTelegramMessage(
        token: String,
        chatId: String,
        message: String
    ) {

        statusText.text = "Status: Sending..."

        val encodedMessage =
            URLEncoder.encode(
                message,
                "UTF-8"
            )

        val url =
            "https://api.telegram.org/bot$token/sendMessage" +
                    "?chat_id=$chatId" +
                    "&text=$encodedMessage"

        val request =
            Request.Builder()
                .url(url)
                .get()
                .build()

        client.newCall(request).enqueue(
            object : Callback {

                override fun onFailure(
                    call: Call,
                    e: IOException
                ) {

                    runOnUiThread {

                        statusText.text =
                            "Status: Failed - ${e.message}"
                    }
                }

                override fun onResponse(
                    call: Call,
                    response: Response
                ) {

                    val success =
                        response.isSuccessful

                    response.close()

                    runOnUiThread {

                        if (success) {

                            statusText.text =
                                "Status: Telegram message sent"

                        } else {

                            statusText.text =
                                "Status: Telegram API error"
                        }
                    }
                }
            }
        )
    }

    private fun updateMessageList() {

        if (messages.isEmpty()) {

            messageList.text =
                "Saved messages..."

            return
        }

        val text =
            StringBuilder()

        messages.forEachIndexed {
                index,
                message ->

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

        client.dispatcher.cancelAll()

        super.onDestroy()
    }
}

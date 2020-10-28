package com.datastax.restaurant_reviews

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val usernameEditText = findViewById<EditText>(R.id.editTextUsername)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)

        val submitButton = findViewById<Button>(R.id.button1).also {
            it.isEnabled = false
            it.setOnClickListener(
                View.OnClickListener {
                    doAuth(
                        "https://9f31a5a3-b963-46d3-a43f-4b261f2bfb0c-us-east1.apps.astra.datastax.com/api/rest/v1/auth",
                        usernameEditText.text.toString(),
                        passwordEditText.text.toString() )
                }
            )
        }

        usernameEditText.doAfterTextChanged { submitButton.isEnabled = true }
        passwordEditText.doAfterTextChanged { submitButton.isEnabled = true }

    }



    private fun doAuth (
                    url: String,
                    username: String,
                    password: String ) {

        Log.wtf("doAuth", "$username, $password")

        val jsonCredentials = """{"username":"$username","password":"$password"}"""

        Log.wtf("doAuth:credentials", jsonCredentials)

        val requestBody = jsonCredentials
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        Log.wtf("MainActivity", "Request built")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.wtf("doAuth", "onFailure" )
            }
            override fun onResponse(call: Call, response: Response) {
                Log.wtf("doAuth", response.body?.string())



            }
        })


    }

}
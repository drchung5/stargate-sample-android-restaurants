package com.datastax.restaurant_reviews

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
        doAuth(
            "https://9f31a5a3-b963-46d3-a43f-4b261f2bfb0c-us-east1.apps.astra.datastax.com/api/rest/v1/auth",
            "",
            "" )

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
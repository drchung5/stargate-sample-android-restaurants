package com.datastax.restaurant_reviews

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.datastax.restaurant_reviews.json_types.AuthResponse
import com.datastax.restaurant_reviews.json_types.RestaurantsWrapper
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.xml.sax.Parser
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    private val baseURL = "https://9f31a5a3-b963-46d3-a43f-4b261f2bfb0c-us-east1.apps.astra.datastax.com/api/rest/v1"

    private val client = OkHttpClient()

    var gson = Gson()

    lateinit var submitButton: Button;

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val usernameEditText = findViewById<EditText>(R.id.editTextUsername).also {
            it.doAfterTextChanged { submitButton.isEnabled = true }
        }

        val passwordEditText = findViewById<EditText>(R.id.editTextPassword).also {
            it.doAfterTextChanged { submitButton.isEnabled = true }
        }


//
//        val foobar = gson.fromJson("""{"foo":"bar"}""",MainActivity.Foobar::class.java)
//        val usernameEditText = findViewById<EditText>(R.id.editTextUsername).also {
//            it.setText( foobar.foo )
//        }
//
//        val jsonString = gson.toJson(Foobar("bar"))
//        val passwordEditText = findViewById<EditText>(R.id.editTextUsername).also {
//            it.setText(jsonString)
//        }


//        val foobar = gson.fromJson(json ,Foobar::class.java)
//
//        val usernameEditText = findViewById<EditText>(R.id.editTextUsername).also {
//            if( foobar == null ) {
//                it.setText("foobar is null")
//            } else {
//                it.setText( foobar.toString() )
//            }
//        }


        submitButton = findViewById<Button>(R.id.button1).also {
            it.isEnabled = false
            it.setOnClickListener(
                View.OnClickListener {
                    doAuth(
                        usernameEditText.text.toString(),
                        passwordEditText.text.toString() )
                }
            )
        }

    }

    private fun doAuth (
                    username: String,
                    password: String ) {

        Log.wtf("doAuth", "$username, $password")

        val jsonCredentials = """{"username":"$username","password":"$password"}"""

        Log.wtf("doAuth:credentials", jsonCredentials)

        val requestBody = jsonCredentials
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("""$baseURL/auth""")
            .post(requestBody)
            .build()

        Log.wtf("MainActivity", "Request built")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.wtf("doAuth", "onFailure" )
            }
            override fun onResponse(call: Call, response: Response) {

                val authResponse = Gson().fromJson(response.body?.string(), AuthResponse::class.java)

                Log.wtf("token", authResponse.authToken )

                getData(authResponse.authToken)

            }
        })

        }

        fun getData(authToken: String?) {
            val uuid = UUID.randomUUID();
            val url = """$baseURL/keyspaces/reviews/tables/restaurants/rows/IA;Cedar Rapids"""

            val request = authToken?.let {
                Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Accept","application/json")
                    .addHeader("X-Cassandra-Request-Id", uuid.toString())
                    .addHeader("X-Cassandra-Token", it)
                    .build()
            }

            Log.d("request", request.toString())

            client.newCall(request!!).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.wtf("getData", "onFailure" )
                }
                override fun onResponse(call: Call, response: Response) {

                    val restaurantsWrapper = Gson().fromJson(response.body?.string(), RestaurantsWrapper::class.java)

                    for( r in restaurantsWrapper.rows ) {
                        Log.wtf("restaurant: ", """${r.name}, ${r.city}, ${r.state}""" )

                    }

                }
            })


        }

    }





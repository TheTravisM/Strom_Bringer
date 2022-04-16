package io.travis.strombringer

import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private val tag: String = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val apiKey: String = "dc0e0b64666b1ca150b5269a0b7c4ed3"
        var latitude: Double = 37.8267
        var longitude: Double = -122.4233
        var forecastURL: String = "https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&appid=$apiKey";


        if (isNetworkAvailable()) {
            val client = OkHttpClient()

            val request = Request
                .Builder()
                .url(forecastURL)
                .build()

            val call = client.newCall(request)

            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {}

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    try {
                        Log.d(tag, response.body!!.string())
                        if (response.isSuccessful) {
                            Log.d(tag, "isSuccessful")
                        } else {
                            alertUserAboutError()
                        }
                    } catch (e: IOException) {
                        Log.d(tag, "IO Exception caught: ", e)
                    }
                }
            })
        }


        Log.d(tag,"END")

    }

    private fun isNetworkAvailable(): Boolean {
        val manager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        var isAvailable = false
        if (networkInfo != null && networkInfo.isConnected) {
            isAvailable = true
        } else {
            Toast.makeText(this, "The Network Is Down!",LENGTH_SHORT).show()
        }
        return isAvailable
    }

    private fun alertUserAboutError() {
        val dialog = AlertDialogFragment()
        dialog.show(supportFragmentManager, "error_dialog")
        //dialog.show(FragmentManager, "error_dialog")
    }
}
package io.travis.stormbringer

import android.net.ConnectivityManager
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val tag: String = MainActivity::class.java.simpleName

    private var currentWeather = CurrentWeather()

    private val latitude: Double = 37.8267
    private val longitude: Double = -122.4233

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val odenForce = findViewById<TextView>(R.id.textView_PoweredBy)
        odenForce.movementMethod = LinkMovementMethod.getInstance()

        val apiKey: String = "dc0e0b64666b1ca150b5269a0b7c4ed3"

        val forecastURL: String = "https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&appid=$apiKey"

        if (isNetworkAvailable()) {
            val client:OkHttpClient = OkHttpClient()

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
                        val jsonData = response.body!!.string()
                        Log.d(tag, "JSON Data: $jsonData")
                        if (response.isSuccessful) {
                            Log.d(tag, "isSuccessful")
                            currentWeather = getCurrentDetails(jsonData)
                            Log.d(tag,"get Current Time 52 " + currentWeather.getFormattedTime().toString())
                        } else {
                            alertUserAboutError()
                        }
                    } catch (e: IOException) {
                        Log.d(tag, "IO Exception caught: ", e)
                    } catch (e: JSONException) {
                        Log.d(tag, "JSON Exception caught: ", e)
                    }
                }
            })
        }
        else {
            Toast.makeText(this, "The Network Is Down!",LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun isNetworkAvailable(): Boolean {
        val manager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        var isAvailable:Boolean = false
        if (networkInfo != null && networkInfo.isConnected) {
            isAvailable = true
        } else {
            Toast.makeText(this, "The Network Is Down!",LENGTH_SHORT).show()
        }
        return isAvailable
    }

    private fun getCurrentDetails(jsonData: String): CurrentWeather {
        val forecast:JSONObject = JSONObject(jsonData)
        val weather = forecast.getJSONArray("weather")
        val main = forecast.getJSONObject("main")
        val name = forecast.getString("name")
        val timezone = forecast.getString("timezone")
        //val dt = forecast.getString("dt")
        Log.d(tag, "From JSON TIMEZONE!: $timezone")


        //val description = weather[0].getString("description")

        val currentWeather = CurrentWeather()

        currentWeather.setName(forecast.getString("name"))
        currentWeather.setTimezone("timezone")
        //currentWeather.setIcon(weather.getString("icon"))
        //currentWeather.setDt(forecast.getLong("dt"))
        currentWeather.setTime(forecast.getLong("dt"))

        currentWeather.setTemp(main.getDouble("temp"))
        currentWeather.setHumidity(main.getInt("humidity"))
        //currentWeather.setDescription(weather.getString("description"))

        Log.d(tag,"From JSON Name: $name")
        //Log.d(tag,"From JSON Description: $description")

        Log.d(tag,"get Current Time 110 " + currentWeather.getFormattedTime().toString())

//        val description = weather.getString("description")
//        val icon = weather.getString("icon")
//        val humidity = main.getString("humidity")
//
//        Log.d(tag,"From JSON Description: $description")
//        Log.d(tag,"From JSON Icon: $icon")
//        Log.d(tag,"From JSON Name: $humidity")

        return currentWeather
    }

    private fun alertUserAboutError() {
        val dialog = AlertDialogFragment()
        dialog.show(supportFragmentManager, "error_dialog")
    }

//    @Throws(JSONException::class)
//    private fun getCurrentDetails(jsonData: String): CurrentWeather? {
//        val forecast = JSONObject(jsonData)
//        val timezone = forecast.getString("timezone")
//        Log.i(MainActivity.TAG, "From JSON: $timezone")
//        val currently = forecast.getJSONObject("currently")
//        val currentWeather = CurrentWeather()
//
//        // Parse weather data from currently object
//        currentWeather.setHumidity(currently.getDouble("humidity").toInt())
//        currentWeather.setTime(currently.getLong("time"))
//        currentWeather.setIcon(currently.getString("icon"))
//        currentWeather.setLocationLabel("Alcatraz Island, CA")
//        currentWeather.setPrecipChance(currently.getDouble("precipProbability"))
//        currentWeather.setSummary(currently.getString("summary"))
//        currentWeather.setTemperature(currently.getDouble("temperature"))
//        currentWeather.setTimeZone(timezone)
//        Log.d(MainActivity.TAG, currentWeather.getFormattedTime()!!)
//        return currentWeather
//    }

}
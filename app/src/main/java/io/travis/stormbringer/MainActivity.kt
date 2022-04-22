package io.travis.stormbringer

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.widget.ImageView

import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import io.travis.stormbringer.databinding.ActivityMainBinding
import okhttp3.*

import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class MainActivity : AppCompatActivity() {

    var currentWeather = CurrentWeather()
    lateinit var binding:ActivityMainBinding

    public final val tag: String = MainActivity::class.java.simpleName

    private final var latitude: Double = 39.071924
    private final var longitude: Double = -84.3423023

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getForecast(latitude,longitude)

        Log.d(tag, "Main UI is running")
    }

    private fun getForecast(latitude:Double, longitude:Double) {
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)

        val odenForce = findViewById<TextView>(R.id.textView_PoweredBy)
        odenForce.movementMethod = LinkMovementMethod.getInstance()

        val iconImageView = findViewById<ImageView>(R.id.imageView_Icon)

        val apiKey: String = "dc0e0b64666b1ca150b5269a0b7c4ed3"

        val forecastURL: String =
            "https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&appid=$apiKey&units=imperial"

        if (isNetworkAvailable()) {
            val client: OkHttpClient = OkHttpClient()

            val request = Request
                .Builder()
                .url(forecastURL)
                .build()

            val call = client.newCall(request)

            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {}

                @SuppressLint("UseCompatLoadingForDrawables")
                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    try {
                        val jsonData = response.body!!.string()
                        Log.d(tag, "JSON Data: $jsonData")

                        if (response.isSuccessful) {

                            currentWeather = getCurrentDetails(jsonData)

                            val displayWeather: CurrentWeather = CurrentWeather(
                                currentWeather.getName(),
                                currentWeather.getIcon(),
                                currentWeather.getTime(),
                                currentWeather.getTemp(),
                                currentWeather.getHumidity(),
                                currentWeather.getDescription(),
                                currentWeather.getTimezone()
                            )

                            binding.weather = displayWeather

                            val drawable = resources.getDrawable(displayWeather.getIconId())

                            iconImageView.setImageDrawable(drawable)

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
        } else {
            Toast.makeText(this, "The Network Is Down!", LENGTH_SHORT).show()
        }
    }

    @Throws(JSONException::class)
    private fun getCurrentDetails(jsonData: String): CurrentWeather {
        val forecast:JSONObject = JSONObject(jsonData)

        val weather = forecast.getJSONArray("weather")
        val main = forecast.getJSONObject("main")
        //val name = forecast.getString("name")
        val timezone = forecast.getString("timezone")
        //val dt = forecast.getString("dt")
        Log.d(tag, "From JSON TIMEZONE!: $timezone")


        val icon = weather.getJSONObject(0).get("icon")
        val description = weather.getJSONObject(0).get("description")
        //val humidity = main.getJSONObject("humidity")
        //val temp = main.getDouble("temp").toString()

        val currentWeather: CurrentWeather = CurrentWeather(
            currentWeather.getName(),
            currentWeather.getIcon(),
            currentWeather.getTime(),
            currentWeather.getTemp(),
            currentWeather.getHumidity(),
            currentWeather.getDescription(),
            currentWeather.getTimezone(),
        )

        currentWeather.setName(forecast.getString("name"))
        currentWeather.setIcon(icon as String)
        currentWeather.setTime(forecast.getLong("dt"))
        currentWeather.setTemp(main.getDouble("temp"))
        currentWeather.setHumidity(main.getInt("humidity"))
        currentWeather.setDescription(description as String)
        currentWeather.setTimezone("timezone")

        return currentWeather
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


    private fun alertUserAboutError() {
        val dialog = AlertDialogFragment()
        dialog.show(supportFragmentManager, "error_dialog")
    }

    fun refreshOnClick(view: View){
        getForecast(latitude,longitude)
        Toast.makeText(this, "Oden As Spoken", LENGTH_SHORT).show()
    }
}
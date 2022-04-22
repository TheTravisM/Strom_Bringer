package io.travis.stormbringer

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class CurrentWeather{
    private var name: String = "" // Name = city name
    private var icon: String = ""// weather/icon
    private var time: Long = 0 // dt
    private var temp: Double = 0.0 // main/temp
    private var humidity: Int = 0
    private var description: String = "" //weather/description
    private var timezone: String = ""


    // private lateinit var humidity: Int // main/humidity
    // private lateinit var temperature: Int // main/temp
    //private lateinit var precip: String

    constructor() { }

    constructor(
        name: String,
        icon: String,
        time: Long,
        temp: Double,
        humidity: Int,
        description: String,
        timezone: String
    ) {
        this.name = name
        this.icon = icon
        this.time = time
        this.temp = temp
        this.humidity = humidity
        this.description = description
        this.timezone = timezone
    }


    fun getName(): String    { return name }
    fun setName(name:String) { this.name = name }

    fun getIcon(): String { return icon  }
    fun setIcon(icon: String) { this.icon = icon }

    fun getIconId(): Int {
        var iconId:Int = R.drawable.clear_day

        when (icon) {
            "clear sky" -> iconId = R.drawable.clear_day
            //"clear sky" -> iconId = R.drawable.clear_night
            "rain" -> iconId = R.drawable.rain
            "snow" -> iconId = R.drawable.snow
            //"sleet" -> iconId = R.drawable.sleet
            //"wind" -> iconId = R.drawable.wind
            "mist" -> iconId = R.drawable.fog
            "scattered clouds" -> iconId = R.drawable.cloudy
            "few clouds" -> iconId = R.drawable.partly_cloudy
            //"partly-cloudy-night" -> iconId = R.drawable.cloudy_night
        }

        return iconId
    }

    fun getTime(): Long { return time }
    fun setTime(time: Long) { this.time = time }

    @SuppressLint("SimpleDateFormat")
    fun getFormattedTime(): String? {
        val formatter = SimpleDateFormat("h:mm a")
        formatter.timeZone = TimeZone.getTimeZone(timezone)
        //val time = dt.toLong();
        val dateTime = Date(time * 1000)
        return formatter.format(dateTime)
    }

    fun getTemp(): Double { return temp }
    fun setTemp(temp: Double) { this.temp = temp }

    fun getTempString(): String {
        return temp.roundToInt().toString()
    }
    //fun setTempString(temp: String) { this.tempString = tempString }

    fun getHumidity(): Int { return humidity }
    fun setHumidity(humidity: Int) { this.humidity = humidity }
    fun getHumidityString(): String {
        return "$humidity %"
    }


    fun getDescription(): String { return description }
    fun setDescription(description: String) { this.description = description.capitalize()
    }

    fun getTimezone():String{ return timezone }
    fun setTimezone(timezone:String){ this.timezone = timezone }

}
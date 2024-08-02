package com.example.implementapi_dezan

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var namaKotaTextView: TextView
    private lateinit var suhuTextView: TextView
    private lateinit var deskripsiSuhuTextView: TextView
    private lateinit var iconSuhuImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        namaKotaTextView = findViewById(R.id.namaKota)
        suhuTextView = findViewById(R.id.suhu)
        deskripsiSuhuTextView = findViewById(R.id.deskripsiSuhu)
        iconSuhuImageView = findViewById(R.id.iconSuhu)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherService::class.java)
        val call = service.getCurrentWeather("Bandung", "b3e6a0454268451c2053c923dbef52ce", "id")

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    weatherResponse?.let {
                        val tempInCelsius = it.main.temp - 273.15
                        val roundedTemp = Math.round(tempInCelsius).toInt()
                        namaKotaTextView.text = it.name
                        suhuTextView.text = "$roundedTemp °C"
                        deskripsiSuhuTextView.text = "${it.weather[0].description.capitalize()}"

                        val iconUrl = "https://openweathermap.org/img/wn/${it.weather[0].icon}.png"
                        Glide.with(this@MainActivity)
                            .load(iconUrl)
                            .into(iconSuhuImageView)
                    }
                } else {
                    Log.e("MainActivity", "Response not successful: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
            }
        })
    }
}

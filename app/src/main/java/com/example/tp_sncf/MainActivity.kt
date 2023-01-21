package com.example.tp_sncf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.io.BufferedReader
import android.view.View
import android.widget.AdapterView

import android.widget.AutoCompleteTextView

import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.get
import okhttp3.*
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.sql.DriverManager


class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val stations = initListStations()
        println("fizejfze")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line, stations
        )
        val autocomplete = findViewById<View>(R.id.ac_ville) as AutoCompleteTextView
        autocomplete.setAdapter(adapter)


        autocomplete.setOnItemClickListener(AdapterView.OnItemClickListener { parent, arg1, pos, id ->
            var selectedStation = parent.getItemAtPosition(pos) as Station
            println(selectedStation.toString())
            buildApiurl(selectedStation.codeUic)
        })



    }

    fun buildApiurl(id:Int){
        var base_url = "https://api.sncf.com/v1/coverage/sncf/stop_areas/stop_area:SNCF:$id/departures/?count=10&key=3e194807-7518-4a97-a37d-c6076e38fce8"
        run(base_url)
    }
    fun run(base_url:String) {
        val request = Request.Builder()
            .url(base_url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    for ((name, value) in response.headers) {
                        println("$name: $value")
                    }
                    val jsonObject = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                    val departures = jsonObject["departures"]

                    for (i in 0 until jsonObject.length()) {
                        val item = jsonObject.getJSONObject("departSures")
                        println(item)
                        // Your code here
                    }

                    println(departures)
                }
            }
        })
    }

    fun initListStations(): List<Station> {
        val inputStream = resources.openRawResource(R.raw.gares)
        val reader = inputStream.bufferedReader()

        val header = reader.readLine()
        return reader.lineSequence()
            .filter { it.isNotBlank() }
            .map {
                val (uic, libelle, long,lat) = it.split(';', ignoreCase = false, limit = 4)
                Station(uic.toInt(),libelle.toString(),long.toFloat(),lat.toFloat())
            }.toList()
    }

}
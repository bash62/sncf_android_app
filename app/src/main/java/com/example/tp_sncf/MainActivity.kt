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

        // Initialisation de toutes les stations disponible dans le csv
        val stations = initListStations()

        // Adapter pour l'AutoCompleteTextView
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line, stations
        )
        val autocomplete = findViewById<View>(R.id.ac_ville) as AutoCompleteTextView
        // Load les données dans l'AutoCompleteTextView
        autocomplete.setAdapter(adapter)

        // Listener a la selection de la ville correspondante
        autocomplete.setOnItemClickListener(AdapterView.OnItemClickListener { parent, arg1, pos, id ->
            var selectedStation = parent.getItemAtPosition(pos) as Station
            println(selectedStation.toString())
            buildApiurl(selectedStation.codeUic)
        })



    }
    // Retourne un lien de l'api correspondant à l'id du train séléctionné
    fun buildApiurl(id:Int){
        var base_url = "https://api.sncf.com/v1/coverage/sncf/stop_areas/stop_area:SNCF:$id/departures/?count=10&key=3e194807-7518-4a97-a37d-c6076e38fce8"
        run(base_url)
    }
    // Fetch l'api SNCF
    // Async
    fun run(base_url:String) {
        val request = Request.Builder()
            .url(base_url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            // Réponse de l'api
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    for ((name, value) in response.headers) {
                        println("$name: $value")
                    }
                    // Cast de la réponse en JSON
                    val jsonObject = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                    val departures = jsonObject["departures"]

                    println(departures)
                }
            }
        })
    }

    fun initListStations(): List<Station> {
        // Loading des resources
        val inputStream = resources.openRawResource(R.raw.gares)
        // Lecture du de l'input stream dans un buffer
        val reader = inputStream.bufferedReader()
        // On enléve l'header
        val header = reader.readLine()
        return reader.lineSequence()
                // Filtre pour verifier que il y a bien des données dans le buffer
            .filter { it.isNotBlank() }
            // Map : itére ttes les lignes
            .map {
                //Parsing des lignes, on récupérer seulement champs que on veut récupérer
                val (uic, libelle, long,lat) = it.split(';', ignoreCase = false, limit = 4)
                // Avec les valeurs parser on créer pour chacune des lignes un objet station
                Station(uic.toInt(),libelle.toString(),long.toFloat(),lat.toFloat())
                // On renvoie la liste des stations
            }.toList()
    }

}
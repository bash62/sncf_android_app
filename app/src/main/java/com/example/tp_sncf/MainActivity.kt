package com.example.tp_sncf

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.ArrayAdapter
import android.widget.ListView
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    var trains = arrayListOf<Train>()
    var adapter_lv: ArrayAdapter<Train>? = null
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

        adapter_lv = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, trains
        )
        var mListView = findViewById<ListView>(R.id.lv_res) as ListView
        mListView.adapter = adapter_lv



        // Listener a la selection de la ville correspondante
        autocomplete.setOnItemClickListener(AdapterView.OnItemClickListener { parent, arg1, pos, id ->

            // Récupération de l'élément cliqué
            var selectedStation = parent.getItemAtPosition(pos) as Station
            println(selectedStation.toString())
            buildApiurl(selectedStation.codeUic)
        })

        //


    }

    //Ferme le clavier après séléction
    fun closeKeyboard(){
        this.currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
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

                // Reset des trains
                trains.clear()
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")


                    //Reponse de la requete

                    // On récupére le JSON et on le cast en JSONObject
                    val j =  JSONObject(response.body!!.string())

                    // Récupération de departures qui est de forme JSON array
                    val jArray = j.getJSONArray("departures")

                    //Pour chaque train on boucle
                    for (i in 0 until jArray.length()){
                        // Cast de chaque train en JSONObject
                        val jsonTrain = JSONObject(jArray[i].toString())
                        // Création d'un ojet JSON pour pouvoir récupérer les informations
                        val jsonInfo = JSONObject(jsonTrain.get("display_informations").toString())
                        val jsonDate = JSONObject(jsonTrain.get("stop_date_time").toString())

                        // Création d'un Train avec les donnèes de l'api
                        val train = Train(jsonInfo.get("headsign").toString(),jsonInfo.get("direction").toString(),
                            jsonDate.get("arrival_date_time").toString()

                        )

                        trains.add(train)

                    }
                    var mListView = findViewById<ListView>(R.id.lv_res) as ListView
                    // Notifier le changement de l'adaptateur en utilisant le thread principal
                    this@MainActivity.runOnUiThread(java.lang.Runnable {
                        // Notifi le Dataset
                        adapter_lv?.notifyDataSetChanged()
                        // Change les données de l'adaptater
                        mListView.adapter = adapter_lv
                        // Réduit le clavier
                        closeKeyboard()
                    })
                    println(trains)

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
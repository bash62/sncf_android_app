package com.example.tp_sncf

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.tp_sncf.entities.Station
import com.example.tp_sncf.entities.Stop
import com.example.tp_sncf.entities.Train
import com.example.tp_sncf.entities.TypeTrain
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    var trains = arrayListOf<Train>()
    var adapter_lv: ArrayAdapter<Train>? = null
    var stations: List<Station> = ArrayList<Station>()
    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialisation de toutes les stations disponible dans le csv
        this.stations = initListStations()

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
            buildApiurl(selectedStation.getCodeUIC().toString())
        })

        //
        mListView.setOnItemClickListener(AdapterView.OnItemClickListener { parent, _, position, _ ->
            println("Clicked item: ${parent.getItemAtPosition(position)}")
            // Get the selected train
            val el = parent.getItemAtPosition(position) as Train
            println(el)

            // Create intent to open the map activity
            val intent = Intent(this, MapsActivity::class.java)
            // Pass the train to the map activity
            intent.putExtra("train", el)
            // Start the map activity
            startActivity(intent)
        });


    }

    //Ferme le clavier après séléction
    fun closeKeyboard(){
        this.currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    // Retourne un lien de l'api correspondant à l'id du train séléctionné
    fun buildApiurl(id:String){

        var base_url = "https://api.sncf.com/v1/coverage/sncf/stop_areas/stop_area:SNCF:$id/departures/?count=10&key=3e194807-7518-4a97-a37d-c6076e38fce8"
        println(base_url)
        fetchApiTrainsrun(base_url)
    }

    fun getStationById(id:Int):Station{
        for (station in stations){
            if (station.getCodeUIC() == id){
                return station
            }
        }
        return Station("Error",0.0,0.0,0)
    }

    // Fetch l'api SNCF
    // Async
    fun fetchApiTrainsrun(base_url:String) {
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
                        val jsonLink = JSONArray(jsonTrain.get("links").toString())
                        val jsonDate = JSONObject(jsonTrain.get("stop_date_time").toString())
                        val jsonStopPoint = jsonTrain.getJSONObject("stop_point").getJSONObject("stop_area").getJSONArray("codes").getJSONObject(1).get("value").toString()

                        val jsonLocalHour = jsonDate.get("arrival_date_time").toString().split('T')[1].substring(0,2);
                        val jsonLocalMinute = jsonDate.get("arrival_date_time").toString().split('T')[1].substring(2,4);


                        // Création de la station to
                        //val stationTo = Station(jsonInfo.get("direction").toString(),jsonInfo.get("direction").toString())
                        val stationFrom = getStationById(jsonStopPoint.toInt())
                        val stopFrom = Stop(jsonLocalHour,jsonLocalMinute,jsonLocalHour,jsonLocalMinute,stationFrom)
                        //val stationTo = Station(jsonInfo.get("direction").toString(),jsonInfo.get("direction").toString())
                        val type = jsonInfo.getString("commercial_mode").toString().split(" ")[0]

                        // Récupération du type de train
                        val t: TypeTrain = try {
                            TypeTrain.valueOf(type)
                        } catch (e: java.lang.IllegalArgumentException) {
                            TypeTrain.TER
                        }


                        val idTrain = jsonLink.getJSONObject(1).get("id").toString()
                        println(idTrain)

                        // Création d'un Train avec les donnèes de l'api
                        val train = Train(jsonInfo.get("trip_short_name").toString(),from=stopFrom,
                             localHour = jsonLocalHour, localMinute = jsonLocalMinute,
                            type = t
                        )
                        val payloadJSON = fetchApiJourney(idTrain)

                        trains.add(parseApiJourney(payloadJSON,train))

                    }

                    changeDataAdapter()
                }
            }
        })
    }

    fun parseApiJourney(payload: JSONObject, train: Train): Train{
        val vehicleJourneyObj = payload.getJSONArray("vehicle_journeys")[0] as JSONObject

        val stops = vehicleJourneyObj.getJSONArray("stop_times")

        for (i in 0 until stops.length()) {
            val stopObj = stops.getJSONObject(i)

            val hArrival = stopObj.getString("arrival_time").toString().chunked(2)[0]
            val mArrival = stopObj.getString("arrival_time").toString().chunked(2)[1]

            val hDeparture = stopObj.getString("departure_time").toString().chunked(2)[0]
            val mDeparture = stopObj.getString("departure_time").toString().chunked(2)[1]

            val stopPoint = stopObj.getJSONObject("stop_point")
            val codeUIC = stopPoint.getString("id").toString().split(':', ignoreCase = false, limit = 4)[2]

            val lat = stopPoint.getJSONObject("coord").getDouble("lat")
            val lon = stopPoint.getJSONObject("coord").getDouble("lon")

            val name = stopPoint.getString("name")

            val station = Station(
                name,
                lon,
                lat,
                codeUIC.toInt()
            )
            val stop = Stop(hArrival, mArrival, hDeparture, mDeparture)
            stop.setStation(station)
            println(stop)
            train.addStop(stop, !stopObj.getBoolean("drop_off_allowed"), !stopObj.getBoolean("pickup_allowed"))
        }
        return train;
    }
    fun fetchApiJourney(vehiculeJourneyId:String): JSONObject{
        var json = JSONObject();
        var base_url = "https://api.sncf.com/v1/coverage/sncf/vehicle_journeys/$vehiculeJourneyId/vehicle_journeys?key=3e194807-7518-4a97-a37d-c6076e38fce8"
        val request = Request.Builder()
            .url(base_url)
            .build()

        val res = client.newCall(request).execute();
        if(res.isSuccessful){
            json = JSONObject(res.body!!.string())
        }
        else{
            println("Error")
        }
        return json;

    }



            fun changeDataAdapter(){
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
    }

    fun initListStations(): List<Station> {
        // Loading des resources
        val inputStream = resources.openRawResource(R.raw.gares)
        // Lecture du de l'input stream dans un buffer
        val reader = inputStream.bufferedReader()
        // On enléve l'header
        val header = reader.readLine()
        return reader.lineSequence()
            .filter { it.isNotBlank()  } // Filtre pour verifier que il y a bien des données dans le buffer
            .map {// Map : itére ttes les lignes

                val (codeUIC, name, lon,lat) = it.split(';', ignoreCase = false, limit = 4) //Parsing des lignes, on récupérer seulement champs que on veut récupérer

                Station(name.toString(),lon.toDouble(),lat.toDouble(),codeUIC.toInt())// Avec les valeurs parser on créer pour chacune des lignes un objet station
            }.toList() // On renvoie la liste des stations
    }

}
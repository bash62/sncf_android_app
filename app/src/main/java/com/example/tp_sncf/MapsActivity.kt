package com.example.tp_sncf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.tp_sncf.databinding.ActivityMapsBinding
import com.example.tp_sncf.entities.Train
import com.google.android.gms.maps.model.PolylineOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val train = intent.extras!!.get("train") as Train // get the train from the intent
        val stops: List<LatLng> = train.stops.map { LatLng(it.getStation()!!.getLat(), it.getStation()!!.getLon()) }

        // Calculate the average of the stops
        val latAverage = train.stops.map { it.getStation()!!.getLat() }.average()
        val longAverage = train.stops.map { it.getStation()!!.getLon() }.average()

        googleMap.addPolyline(
            PolylineOptions()
            .clickable(true)
            .addAll(stops)
        )

        // Add a marker for each stop
        train.stops.forEach {
            val marker = LatLng(it.getStation()!!.getLat(), it.getStation()!!.getLon())
            mMap.addMarker(MarkerOptions().position(marker).title(it.getStation()!!.getName()))
        }

        // Move the camera to the average of the stops
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latAverage, longAverage), 8f))


    }
}
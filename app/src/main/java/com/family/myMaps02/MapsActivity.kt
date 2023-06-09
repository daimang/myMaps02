package com.family.myMaps02

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Looper
import android.os.PersistableBundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.family.myMaps02.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.CameraPosition


import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.Marker


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    val permissions = arrayOf(Manifest.permission. ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
    val PERM_FLAG = 99


    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (isPermitted()) {
            startProcess()
        } else {
            ActivityCompat.requestPermissions( this, permissions,PERM_FLAG)
        }

    }


    fun isPermitted() : Boolean {
        for(perm in permissions){
            if(ContextCompat.checkSelfPermission( this, perm ) != PERMISSION_GRANTED){
                return false
            }
        }
        return  true
    }

    fun startProcess() {
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

        // Add a marker in Sydney and move the camera
        //val sydney = LatLng(-34.0, 151.0)
//        val seoul = LatLng(37.5663, 126.9779)
//
//        mMap.addMarker(MarkerOptions().position(seoul).title("Marker in seoul"))
//        val cameraOption = CameraPosition.Builder()
//            .target(seoul)
//            .zoom(17f)
//            .build()
//        val camera = CameraUpdateFactory.newCameraPosition(cameraOption)
//        // mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul))
//        mMap.moveCamera(camera)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        setUpdateLocationListener()
    }

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback

//    fun setUpdateLocationListener(){
//        val locationRequest = LocationRequest.create()
//        locationRequest.run {
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//            interval = 1000
//        }
//        locationCallback = object :
//    }
@SuppressLint("MissingPermission")
fun setUpdateLocationListener() {
    var locationRequest: LocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000 )
        .setMinUpdateDistanceMeters(0f)
        .build()

    locationCallback = object : LocationCallback() {
        // Your implementation of the location callback goes here
       override fun onLocationResult(locationResult: LocationResult) {
            locationResult.let {
                for((i, location) in it.locations.withIndex()){
                    Log.d( "위치", "$i ${location.latitude},${location.longitude} ")
                    setLastLocation(location)
                }
            }
        }
    }

    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
}

    fun setLastLocation(location : Location){
         val myLocation = LatLng(location.latitude, location.longitude  )
        val marker = MarkerOptions()
            .position(myLocation)
            .title("요기")
        val cameraPosition = CameraPosition.builder()
            .target(myLocation)
            .zoom(17f)
            .build()
        val camera = CameraUpdateFactory.newCameraPosition(cameraPosition)
mMap.clear()
        mMap.addMarker(marker)
        mMap.moveCamera(camera)

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERM_FLAG -> {
                var check = true
                for(grant in grantResults){
                    if(grant != PERMISSION_GRANTED){
                        check = false
                        break
                    }
                }
                if(check) {
                    startProcess()
                }else{
                    Toast.makeText( this, "권한없어요~~", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }
}
package io.monteirodev.pokemon

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.google.android.gms.maps.CameraUpdateFactory.newLatLng
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.monteirodev.pokemon.R.drawable.mario

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    val LOCATION_REQUEST = 1
    var oldLocation:Location?=null
    var location:Location?= null
    var pokemonList = ArrayList<Pokemon>()
    var playerPower = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPermission()
        loadPokemons()
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


    }

    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                    != PERMISSION_GRANTED) {
                // Permission is not granted
                requestPermissions(arrayOf(ACCESS_FINE_LOCATION), LOCATION_REQUEST)
                return
            }
        }

        getUserLocation()
    }

    private fun getUserLocation() {
        Toast.makeText(this, "User locations access on", LENGTH_LONG).show()

        val myLocation = MyLocationListener()

        val locationManager =
                getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // checkPermission()
        try {
            locationManager.requestLocationUpdates(GPS_PROVIDER, 1000, 3f, myLocation)
        } catch (e: SecurityException) {
            Toast.makeText(this, "permission location failed!", LENGTH_LONG).show()
        }

        MyThread().start()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            LOCATION_REQUEST -> {
                if (grantResults[0] == PERMISSION_GRANTED) {
                    getUserLocation()
                } else {
                    Toast.makeText(this, "We cannot access to your location!",
                            LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    inner class MyLocationListener() :LocationListener {

        init {
            location = Location("Start")
            location!!.latitude = 0.0
            location!!.longitude = 0.0
        }

        override fun onLocationChanged(loc: Location?) {
            location = loc
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) { }

        override fun onProviderEnabled(provider: String?) { }

        override fun onProviderDisabled(provider: String?) { }

    }

    inner class MyThread() : Thread() {
        init {
            oldLocation = Location("Start")
            oldLocation!!.latitude = 0.0
            oldLocation!!.longitude = 0.0
        }

        override fun run() {
            while (true) {
// Add a marker in Sydney and move the camera
                try {
                    if (oldLocation!!.distanceTo(location) == 0f) {
                        continue
                    }
                    oldLocation = location

                    runOnUiThread {
                        mMap.clear()
                        showMe()
                        showPokemons()
                    }

                    Thread.sleep(1000)

                } catch (e: Exception) {

                }
            }
        }

        fun showMe() {
            val meLocation = LatLng(location!!.latitude, location!!.longitude)
            mMap.addMarker(MarkerOptions()
                    .position(meLocation)
                    .title("Me")
                    .snippet("Here's me :)")
                    .icon(fromResource(mario)))
            mMap.moveCamera(newLatLng(meLocation))
        }

        fun showPokemons() {
            pokemonList.forEachIndexed { index, pokemon ->
                if (!pokemon.isCaptured) {
                    mMap.addMarker(MarkerOptions()
                            .position(pokemon.latLng)
                            .title(pokemon.name + " ${pokemon.power}")
                            .snippet(pokemon.description)
                            .icon(fromResource(pokemon.image)))
                }
                var pokemonLocation = Location(pokemon.name)
                pokemonLocation.latitude = pokemon.latLng.latitude
                pokemonLocation.longitude = pokemon.latLng.longitude
                if (location!!.distanceTo(pokemonLocation) < 3f) {
                    pokemon.isCaptured = true
                    pokemonList[index] = pokemon
                    playerPower += pokemon.power
                    Toast.makeText(applicationContext,
                            "You got ${pokemon.name} your power is $playerPower",
                            LENGTH_LONG).show()
                }
            }
        }

    }

    fun loadPokemons() {
        pokemonList.add(Pokemon(
                "Charmander", "Charmander living in japan",
                R.drawable.charmander,55.0,
                LatLng(37.7789994893035, -122.401846647263),false))
        pokemonList.add(Pokemon(
                "Bulbasaur", "Bulbasaur living in usa",
                R.drawable.bulbasaur,90.5,
                LatLng(37.7949568502667, -122.410494089127), false))
        pokemonList.add(Pokemon("Squirtle", "Squirtle living in iraq",
                R.drawable.squirtle,33.5,
                LatLng(37.7816621152613, -122.41225361824), false))
    }
}

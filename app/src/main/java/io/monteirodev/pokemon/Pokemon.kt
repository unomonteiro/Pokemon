package io.monteirodev.pokemon

import android.location.Location
import com.google.android.gms.maps.model.LatLng

class Pokemon(var name:String, var description:String, var image:Int, var power:Double,
              var latLng: LatLng, var isCaptured:Boolean)
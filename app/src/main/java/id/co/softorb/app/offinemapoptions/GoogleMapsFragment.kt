package id.co.softorb.app.offinemapoptions

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import id.co.softorb.app.offinemapoptions.MapsForgeFragment.Companion.JAYAPURA
import id.co.softorb.app.offinemapoptions.MapsForgeFragment.Companion.MONAS
import id.co.softorb.app.offinemapoptions.MapsForgeFragment.Companion.TUGU
import id.co.softorb.app.offinemapoptions.MapsForgeFragment.Companion.WAMENA
import id.co.softorb.app.offinemapoptions.databinding.FragmentDemoBinding
import id.co.softorb.app.offinemapoptions.databinding.FragmentGoogleMapsBinding

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject

import org.mapsforge.core.model.LatLong
import java.io.IOException

class GoogleMapsFragment : Fragment() {
    private var _binding: FragmentGoogleMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMap: GoogleMap;
    private val boundsBuilder = LatLngBounds.Builder()
    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        mMap=googleMap
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        mMap.setOnPoiClickListener { pointOfInterest ->
            val poiMarker = mMap.addMarker(
                MarkerOptions()
                    .position(pointOfInterest.latLng)
                    .title(pointOfInterest.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
            )
            poiMarker?.showInfoWindow()
        }

        mMap.setOnMapLongClickListener { latLng ->
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("New Marker")
                    .snippet("Lat: ${latLng.latitude} Long: ${latLng.longitude}")
                    .icon(vectorToBitmap(R.drawable.ic_pin_green, Color.parseColor("#3DDC84")))
            )
        }

        getMyLocation()

        MapsForgeFragment.notablePlaces.forEach{ place ->
            val latlng = place.toLatLng()
            mMap.addMarker(MarkerOptions().position(latlng).title("hihihi"))
            boundsBuilder.include(latlng)
        }
        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
        val origin = TUGU.toLatLng()
        val destination = MONAS.toLatLng()

//        mMap.addMarker(MarkerOptions().position(origin).title("Start"))
//        mMap.addMarker(MarkerOptions().position(destination).title("Destination"))

//        getRoute(origin, destination)
        drawPolyline(origin,destination)
        binding.btnToMap.setOnClickListener {
//        openRoute(origin,destination)
            openNavigation(origin,destination)
        }
    }

    private fun drawPolyline(start: LatLng, end: LatLng) {
        val polylineOptions = PolylineOptions()
            .add(start, end) // Add start and end points
            .width(8f) // Line thickness
            .color(Color.BLUE) // Line color
            .geodesic(true) // Smooth curvature

        mMap.addPolyline(polylineOptions)
    }


    private fun openNavigation(origin: LatLng, destination: LatLng) {
        val uri = Uri.parse("google.navigation:q=${destination.latitude},${destination.longitude}&mode=d")
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(mapIntent)
        } else {
            Toast.makeText(requireContext(), "Google Maps is not installed", Toast.LENGTH_SHORT).show()
        }
    }

    fun openRoute(origin: LatLng, destination: LatLng) {
        val uri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=${origin.latitude},${origin.longitude}&destination=${destination.latitude},${destination.longitude}&travelmode=driving")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        startActivity(intent)
    }

    private fun LatLng.ToString(): String {
        return StringBuilder().apply {
            append(latitude)
            append(",")
            append(longitude)
        }.toString()
    }

    private fun getRoute(origin: LatLng, destination: LatLng) {
        val call = ApiConfig.apiService.getDirections(
            origin.ToString(),
            destination.ToString(),
            "AIzaSyD-8dBfHFBbCXjcznrhprOSdmY8Gl3L-Gk"
        )
        Log.e("xxx", "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.ToString()}&destination=${destination.ToString()}&key=AIzaSyD-8dBfHFBbCXjcznrhprOSdmY8Gl3L-Gk\n")

        call.enqueue(object : retrofit2.Callback<GoogleMapsDirectionsResponse> {
            override fun onResponse(call: retrofit2.Call<GoogleMapsDirectionsResponse>, response: retrofit2.Response<GoogleMapsDirectionsResponse>) {
                if (response.isSuccessful) {
                    val route = response.body()?.routes?.firstOrNull()
                    val polyline = route?.overviewPolyline?.points
                    val decodedPolyline = decodePolyline(polyline!!)
                    if (!polyline.isNullOrEmpty()) {
                        drawPolyline(decodedPolyline)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<GoogleMapsDirectionsResponse>, t: Throwable) {
                Log.e("MAPS", "Failed to fetch route: ${t.message}")
            }
        })
    }

    private fun drawPolyline(route: List<LatLng>) {
        val polylineOptions = PolylineOptions()
            .addAll(route)
            .width(8f)
            .color(Color.BLUE)
            .geodesic(true)

        mMap.addPolyline(polylineOptions)
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            poly.add(LatLng(lat / 1E5, lng / 1E5))
        }
        return poly
    }
    fun LatLong.toLatLng():LatLng{
        return LatLng(this.latitude,this.longitude)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGoogleMapsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.normal_type -> {
                    mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                    true
                }

                R.id.satellite_type -> {
                    mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                    true
                }

                R.id.terrain_type -> {
                    mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                    true
                }

                R.id.hybrid_type -> {
                    mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                    true
                }

                else -> {
                    super.onOptionsItemSelected(item)
                }
            }
        }
    }

    private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }
    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext().applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    companion object{
        val TAG = ""

    }
}
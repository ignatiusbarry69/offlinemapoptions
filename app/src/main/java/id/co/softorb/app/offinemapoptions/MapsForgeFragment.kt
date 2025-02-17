package id.co.softorb.app.offinemapoptions

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import id.co.softorb.app.offinemapoptions.databinding.FragmentMapsForgeBinding
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.layer.overlay.Marker
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.internal.MapsforgeThemes
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class MapsForgeFragment : Fragment() {

    private var _binding: FragmentMapsForgeBinding? = null
    private val binding get() = _binding!!
//    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsForgeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AndroidGraphicFactory.createInstance(requireContext().applicationContext)
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        checkLocationPermission()
        copyMapFileToInternalStorage(requireContext(), "papua.map")
        openMapFromInternalStorage()
    }

    private fun openMapFromInternalStorage() {
        val context = requireContext().applicationContext
        val file = File(context.filesDir, "papua.map")

        if (!file.exists()) {
            Log.e("MapError", "Map file is missing! Path: ${file.absolutePath}")
            return
        }

        // Initialize map
        binding.map.mapScaleBar.isVisible = true
        binding.map.setBuiltInZoomControls(true)

        // Create tile cache
        val cache = AndroidUtil.createTileCache(
            context, "mapcache",
            binding.map.model.displayModel.tileSize, 1f,
            binding.map.model.frameBufferModel.overdrawFactor
        )

        val mapStore = MapFile(file) // Use File instead of FileInputStream
        val renderLayer = TileRendererLayer(
            cache, mapStore,
            binding.map.model.mapViewPosition,
            AndroidGraphicFactory.INSTANCE
        )

        renderLayer.setXmlRenderTheme(MapsforgeThemes.DEFAULT)
        binding.map.layerManager.layers.add(renderLayer)

        binding.map.setCenter(NABIRE)
//        binding.map.setZoomLevelMin(5)
//        binding.map.setZoomLevelMax(18)
        binding.map.setZoomLevel(12)
//        binding.map.model.mapViewPosition.mapLimit = mapStore.boundingBox()

        notablePlaces.forEach { location ->
            addMarker(location,R.drawable.ic_pin_red)
        }
    }
    private fun copyMapFileToInternalStorage(context: Context, fileName: String) {
        val destinationFile = File(context.filesDir, fileName)

        if (destinationFile.exists()) {
            Log.d("MapCopy", "Map file already exists: ${destinationFile.absolutePath}")
            return
        }

        try {
            context.assets.open(fileName).use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            Log.d("MapCopy", "Map file copied successfully: ${destinationFile.absolutePath}")
        } catch (e: IOException) {
            Log.e("MapCopy", "Failed to copy map file: ${e.message}")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        AndroidGraphicFactory.clearResourceMemoryCache()
    }
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        } else {
            getUserLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation()
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val userLatLong = LatLong(location.latitude, location.longitude)
                addMarker(userLatLong,R.drawable.ic_pin_green)
            } else {
                Toast.makeText(requireContext(), "Failed to get location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addMarker(position: LatLong, iconResId: Int) {
        val bitmap = AndroidGraphicFactory.convertToBitmap(
            ResourcesCompat.getDrawable(resources, iconResId, null)
        )

        val marker = Marker(position, bitmap, 0, -bitmap.height / 2)

        binding.map.layerManager.layers.add(marker)
    }

    companion object{
        private const val LOCATION_REQUEST_CODE = 1001
        val TUGU = LatLong(-7.7829, 110.3671)
        val MONAS = LatLong(-6.1754, 106.8272)
        val JAYAPURA = LatLong(-2.5337,140.7181)
        val WAMENA = LatLong(-4.1024, 138.9293)  // Wamena
        val TIMIKA = LatLong(-4.5472, 136.8869)  // Timika
        val BIAK = LatLong(-1.1746, 136.0722)  // Biak
        val MERAUKE = LatLong(-8.4931, 140.4018)  // Merauke
        val NABIRE = LatLong(-3.3638, 135.4963)

        val notablePlaces = listOf(JAYAPURA, WAMENA, TIMIKA, BIAK, MERAUKE, NABIRE)
    }
}
package id.co.softorb.app.offinemapoptions

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.co.softorb.app.offinemapoptions.databinding.FragmentMapsForgeBinding
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
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
        binding.map.setZoomLevelMin(5)
        binding.map.setZoomLevelMax(18)
        binding.map.setZoomLevel(12)
        binding.map.model.mapViewPosition.mapLimit = mapStore.boundingBox()
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

    companion object{
        val JAYAPURA = LatLong(-2.5337,140.7181)
        val NABIRE = LatLong(-3.3638, 135.4963)
    }
}
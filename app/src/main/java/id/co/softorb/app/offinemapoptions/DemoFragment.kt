package id.co.softorb.app.offinemapoptions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import id.co.softorb.app.offinemapoptions.databinding.FragmentDemoBinding

class DemoFragment : Fragment() {

    private var _binding: FragmentDemoBinding? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDemoBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setOnMenuItemClickListener { menu ->
            when(menu.itemId){
                R.id.gmap -> {
                    Snackbar.make(view,"coming soon",Snackbar.LENGTH_SHORT).show()
                    true
                }
                R.id.osm -> {
                    Snackbar.make(view,"coming soon",Snackbar.LENGTH_SHORT).show()
                    true
                }
                R.id.mapsforge -> {
                    findNavController().navigate(R.id.action_demoFragment_to_mapsForgeFragment)
                    true
                }
                R.id.mapbox -> {
                    Snackbar.make(view,"coming soon",Snackbar.LENGTH_SHORT).show()
                    true
                }
                else -> {
                    Snackbar.make(view,"coming soon",Snackbar.LENGTH_SHORT).show()
                    true
                }

            }
        }
    }

}
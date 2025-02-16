package id.co.softorb.app.offinemapoptions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.co.softorb.app.offinemapoptions.databinding.FragmentDemoBinding
import id.co.softorb.app.offinemapoptions.databinding.FragmentMapsForgeBinding

class MapsForgeFragment : Fragment() {

    private var _binding: FragmentMapsForgeBinding? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsForgeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
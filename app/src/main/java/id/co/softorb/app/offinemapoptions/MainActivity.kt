package id.co.softorb.app.offinemapoptions

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//        val contract = registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult()
//        ){result->
//            result?.data?.data?.let{uri->
////                openMap(uri)
//
//            }
//        }
//        contract.launch(
//            Intent(
//                Intent.ACTION_OPEN_DOCUMENT
//            ).apply{
//                type = "*/*"
//                addCategory(Intent.CATEGORY_OPENABLE)
//            }
//        )
    }
}
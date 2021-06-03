package com.example.tracking_app.ui.addEvent

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.tracking_app.R
import com.example.tracking_app.models.DeclareEvent
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import kotlinx.io.InputStream
import kotlinx.serialization.InternalSerializationApi
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import java.io.ByteArrayOutputStream
import java.util.*


class AddEventFragment : Fragment() {

    lateinit var btnTakepictureCamera : Button
    lateinit var btnTakepictureGallery : Button
    lateinit var imageEvent : ImageView
    lateinit var titreEvent : TextInputEditText
    lateinit var descriptionEvent : TextInputEditText
    lateinit var seekbarDegreDanger : DiscreteSeekBar
    var OptionsCategorie  = listOf("Incendie","Contrebande","Immigration illégale","Accident de travail")
    var OptionsZone  = listOf("Zone1","Zone2","Zone3","Zone4")
    var MoncategorieEvent : String = ""
    var MonzoneEvent : String = ""
    var imageBytes : ByteArray? = null
    lateinit var handler : Handler
    companion object {
        fun newInstance() = AddEventFragment()
    }

    private lateinit var viewModel: AddEventViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.add_event_fragment, container, false)
        btnTakepictureCamera = root.findViewById<Button>(R.id.btnTakepictureCamera)
        btnTakepictureGallery = root.findViewById<Button>(R.id.btnTakepictureGallery)
        imageEvent =  root.findViewById(R.id.imageViewEvent)
        btnTakepictureCamera.isEnabled = false
        btnTakepictureGallery.isEnabled = false

        if(ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA),111)
        }else{
            btnTakepictureCamera.isEnabled = true
            btnTakepictureGallery.isEnabled = true
            btnTakepictureGallery.setOnClickListener {
                var action = Intent(Intent.ACTION_PICK)
                action.type = "image/*"
                startActivityForResult(action, 101);
            }
            btnTakepictureCamera.setOnClickListener {
                var action = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(action,102)
            }
        }

        val arrayAdapterCategorie = ArrayAdapter(requireContext(),R.layout.drop_down_roadmap_item,OptionsCategorie)
        root.findViewById<AutoCompleteTextView>(R.id.categorieEvent).setAdapter(arrayAdapterCategorie)

        root.findViewById<AutoCompleteTextView>(R.id.categorieEvent).onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                MoncategorieEvent = OptionsCategorie.get(position)
            }
        }

        val arrayAdapterZone = ArrayAdapter(requireContext(),R.layout.drop_down_roadmap_item,OptionsZone)
        root.findViewById<AutoCompleteTextView>(R.id.zoneEvent).setAdapter(arrayAdapterZone)

        root.findViewById<AutoCompleteTextView>(R.id.zoneEvent).onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                MonzoneEvent = OptionsZone.get(position)
            }
        }

        titreEvent = root.findViewById(R.id.titreEvent)
        descriptionEvent = root.findViewById(R.id.descriptionEvent)
        seekbarDegreDanger = root.findViewById(R.id.degre_dangerEvent)
        handler = Handler()

        root.findViewById<TextView>(R.id.validerEvent).setOnClickListener {

            root.findViewById<ProgressBar>(R.id.progressBarEvent).visibility = View.VISIBLE
            root.findViewById<TextView>(R.id.validerEvent).text = "Attend Svp"
                SendEventToAPI()
            handler.postDelayed({
                var action = AddEventFragmentDirections.actionAddEventFragmentToNavEvents()
                Navigation.findNavController(root).navigate(action)
            },3000)
        }




        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddEventViewModel::class.java)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            btnTakepictureCamera.isEnabled = true
            btnTakepictureGallery.isEnabled = true
        }
    }

    @InternalSerializationApi
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 101){
            var pic = data?.getParcelableExtra<Bitmap>("data")
            println("dddddddddd4444444444444444")
            println(data)
            imageEvent.setImageURI(data?.data)
            val stream = ByteArrayOutputStream()
            pic?.compress(Bitmap.CompressFormat.PNG,100,stream)
            imageBytes = stream.toByteArray()
            println(data?.data)
            println("ibnnnnnnnnnnnnnnnnnnnnn")
            println(imageBytes)


        }else if(requestCode == 102){
            var pic = data?.getParcelableExtra<Bitmap>("data")
            imageEvent.setImageBitmap(pic)
            val stream = ByteArrayOutputStream()
            pic?.compress(Bitmap.CompressFormat.PNG,100,stream)
            imageBytes = stream.toByteArray()
            println("ibnnnnnnnnnnnnnnnnnnnnn1111111111")
            println(imageBytes)
        }
    }

    fun SendEventToAPI(){
        val MonTitreEvent = titreEvent.text.toString()
        val MonDescriptionEvent = descriptionEvent.text.toString()
        val MonDegreDanger : Int = seekbarDegreDanger.progress

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        var longitude : Double = 0.0
        var latitude : Double = 0.0
        LocationServices.getFusedLocationProviderClient(requireContext()).lastLocation
            .addOnSuccessListener { location : Location? ->
                longitude = location!!.longitude
                latitude =  location!!.latitude
                val monEvent : DeclareEvent = DeclareEvent(0,MonTitreEvent,MonDescriptionEvent,
                    MonDegreDanger,longitude,latitude,imageBytes,false,Date().time,MonzoneEvent,MoncategorieEvent)
                viewModel.DeclareSomeEvent(monEvent)
            }
    }

}
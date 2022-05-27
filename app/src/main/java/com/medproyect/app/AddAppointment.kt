package com.medproyect.app

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.medproyect.app.databinding.ActivityAddAppointmentBinding
import java.text.SimpleDateFormat

class AddAppointment : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private var ImageUri : Uri? = null
    private var timeAppointment:String=""
    private  var dateAppointment:String=""
    private lateinit var binding:ActivityAddAppointmentBinding
            override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
                binding = ActivityAddAppointmentBinding.inflate(layoutInflater)
                setContentView(binding.root)
                binding.urlBtnImg.setOnClickListener { requestPermission() }
                //muestra la fecha
                binding.dateEditText.setOnClickListener{showDatePickerDialog()}
                binding.timeEditText.setOnClickListener{showTimePickerDialog()}
                binding.saveButton.setOnClickListener { validateEditText() }
    }

    private fun showTimePickerDialog() {
        val timePicker = TimePickerFragment{ontTimeSelected(it)}
        timePicker.show(supportFragmentManager,"time")
    }
    private fun ontTimeSelected(time:String){
        timeAppointment=time
        binding.timeEditText.setText(time)
    }

    private fun showDatePickerDialog() {
      val datePicker = DatePickerFragment({day,month,year->OnDateSelected(day,month,year)})
        datePicker.show(supportFragmentManager,"datePicker")
    }
    fun OnDateSelected(day:Int,month:Int,year:Int){
        var newMonth= month +1
        dateAppointment="$day/$newMonth/$year"
        binding.dateEditText.setText(dateAppointment)

    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickPhotoFromGallery()
        } else {
            Toast.makeText(this, "habilitar registros", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                pickPhotoFromGallery()
            }
            else -> requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)

        }
    }

    private val startForActivityGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            ImageUri = result.data?.data!!
            binding.ivImgService.setImageURI(ImageUri)
        }
    }

    private fun pickPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startForActivityGallery.launch(intent)
    }

    private fun validateEditText() {
        if (ImageUri==null){
            return Toast.makeText(this, "Ingresa foto de portada", Toast.LENGTH_SHORT).show()
        }
        if (binding.nameEditText.text.toString()==""){
            return Toast.makeText(this, "Ingresa nombre del asesor", Toast.LENGTH_SHORT).show()
        }
        if (binding.priceEditText.text.toString()==""){
            return Toast.makeText(this, "Ingresa el precio", Toast.LENGTH_SHORT).show()
        }
        if (binding.dateEditText.text.toString()==""){
            return Toast.makeText(this, "Seleccione una fecha", Toast.LENGTH_SHORT).show()
        }
        if (binding.timeEditText.text.toString()==""){
            return Toast.makeText(this, "Seleccione una hora", Toast.LENGTH_SHORT).show()
        }
        if (binding.durationEditText.text.toString()==""){
            return Toast.makeText(this, "Agrege la duracion de la cita", Toast.LENGTH_SHORT).show()
        }
        uploadPhotoFromGallery()
    }


    private fun uploadPhotoFromGallery(){
        binding.saveButton.isEnabled=false
        val fileName = binding.nameEditText.text
        val StorageReference = FirebaseStorage.getInstance().getReference().child("Profiles/$fileName")
        StorageReference.putFile(ImageUri!!).addOnSuccessListener {
            binding.ivImgService.setImageURI(null)
            Toast.makeText(this, "subida correcta", Toast.LENGTH_SHORT).show()
            StorageReference.downloadUrl.addOnSuccessListener { uri ->
                val photourl = java.lang.String.valueOf(uri)
                createData(photourl)
                binding.saveButton.isEnabled=true
                finish()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "subida incorrecta", Toast.LENGTH_SHORT).show()
        }

    }

    private fun createData(photourl: String) {
        val idService=intent.getStringExtra("id_service").toString()
        val services = hashMapOf(
            "duration" to binding.durationEditText.text.toString(),
            "isAvailable" to true,
            "itemDate" to SimpleDateFormat("dd/MM/yyyy hh:mm").parse("$dateAppointment $timeAppointment"),
            "itemPrice" to  binding.priceEditText.text.toString(),
            "nameSpecialist" to binding.nameEditText.text.toString(),
            "photoSpecialist" to photourl,
            "serviceID" to idService
        )
        db.collection("appointments")
            .add(services)
            .addOnSuccessListener { UpdateCountCitesService(idService)}
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }


    }
    private fun UpdateCountCitesService(idService: String) {
        val docService = db.collection("service").document(idService)
        docService.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val cites = (document.data?.get("generalRank") as String).toInt()
                    var countCites=cites +1
                    docService.update(
                        "generalRank",countCites.toString()
                    ).addOnSuccessListener {Log.d(TAG, "DocumentSnapshot  service successfully updated!")
                        finish()}
                        .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

    }
}
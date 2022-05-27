package com.medproyect.app
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.medproyect.app.databinding.ActivityEditAppointmentBinding
import java.text.SimpleDateFormat
import java.util.*

class EditAppointmentActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding:ActivityEditAppointmentBinding
    private var ImageUri : Uri? = null
    private var timeAppointment:String=""
    private  var dateAppointment:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val idAppointment = intent.getStringExtra("id_appointment").toString()
        getMyAppointment()
        binding.urlBtnImg.setOnClickListener {requestPermission()}
        binding.dateEditText.setOnClickListener{showDatePickerDialog()}
        binding.timeEditText.setOnClickListener{showTimePickerDialog()}
        binding.saveButton.setOnClickListener {validateEditText(idAppointment)}
    }

    private fun showTimePickerDialog() {
        val timePicker = TimePickerFragment{ontTimeSelected(it)}
        timePicker.show(supportFragmentManager,"timeEdit")
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

    private fun getMyAppointment() {
        val idAppointment = intent.getStringExtra("id_appointment").toString()
        db.collection("appointments").document(idAppointment)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    binding.nameEditText.setText(document.data?.get("nameSpecialist") as String)
                    binding.priceEditText.setText(document.data?.get("itemPrice") as String)
                    binding.durationEditText.setText(document.data?.get("duration") as String)
                    Glide.with(this).load(document.data?.get("photoSpecialist") as String).into(binding.ivImgService)
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun validateEditText(idAppointment:String) {

        if (binding.nameEditText.text.toString()==""){
            return Toast.makeText(this, "Ingresa nombre del asesor", Toast.LENGTH_SHORT).show()
        }
        if (binding.priceEditText.text.toString()==""){
            return Toast.makeText(this, "Ingresa el precio", Toast.LENGTH_SHORT).show()
        }
        if (binding.dateEditText.text.toString()==""){
            return Toast.makeText(this, "Seleccione una nueva fecha", Toast.LENGTH_SHORT).show()
        }
        if (binding.durationEditText.text.toString()==""){
            return Toast.makeText(this, "Seleccione la duracion", Toast.LENGTH_SHORT).show()
        }
        updateMyAppointment(idAppointment)
    }

    private fun updateMyAppointment(idAppointment:String) {
        binding.saveButton.isEnabled=false
        val appointmentRef = db.collection("appointments").document(idAppointment)
        if (ImageUri==null){
            //subir sin actualizar la imagen
            appointmentRef.update(
                "duration",binding.durationEditText.text.toString(),
                "isAvailable",true,
                "itemDate",SimpleDateFormat("dd/MM/yyyy hh:mm").parse("$dateAppointment $timeAppointment"),
                "itemPrice",binding.priceEditText.text.toString(),
                "nameSpecialist", binding.nameEditText.text.toString()
            ).addOnSuccessListener {
                binding.saveButton.isEnabled=true
                Toast.makeText(this, "Datos actualizados", Toast.LENGTH_SHORT).show()
                finish()}
                .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
        }else{

            //subir con cambio de imagen

            val fileName = binding.nameEditText.text
            val StorageReference =
                FirebaseStorage.getInstance().getReference().child("Profiles/$fileName")
            StorageReference.putFile(ImageUri!!).addOnSuccessListener {
                binding.ivImgService.setImageURI(null)
                Toast.makeText(this, "subida correcta", Toast.LENGTH_SHORT).show()
                StorageReference.downloadUrl.addOnSuccessListener { uri ->
                    var photourl = java.lang.String.valueOf(uri)
                    appointmentRef.update(
                        "duration",binding.durationEditText.text.toString(),
                            "isAvailable",true,
                            "itemDate",SimpleDateFormat("dd/MM/yyyy hh:mm").parse("$dateAppointment $timeAppointment"),
                            "itemPrice",binding.priceEditText.text.toString(),
                            "nameSpecialist", binding.nameEditText.text.toString(),
                            "photoSpecialist", photourl
                        ).addOnSuccessListener {
                        binding.saveButton.isEnabled=true
                            Toast.makeText(this, "Datos actualizados", Toast.LENGTH_SHORT).show()
                            finish()}
                        .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "subida incorrecta", Toast.LENGTH_SHORT).show()
            }
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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickPhotoFromGallery()
        } else {
            Toast.makeText(this, "habilitar registros", Toast.LENGTH_SHORT).show()
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
}
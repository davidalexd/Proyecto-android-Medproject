package com.medproyect.app

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.ControlsProviderService
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.medproyect.app.databinding.ActivityAddMyPhotosServicesBinding

class AddMyPhotosServicesActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding:ActivityAddMyPhotosServicesBinding
    private var ImageUri : Uri? = null
    private var UrlNoSearch:String ="https://media.discordapp.net/attachments/839660652006998036/972989492795437116/PerroNoEncontro.gif"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMyPhotosServicesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Glide.with(binding.ivService.context).load(UrlNoSearch).into(binding.ivService)
        val idService = intent.getStringExtra("idService").toString()
        getDataService(idService)
        binding.btnPrevius.setOnClickListener { requestPermission() }
        binding.btnSend.setOnClickListener{validatePhoto()}
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickPhotoFromGallery()
        }
    }
    private fun getDataService(idService:String){

        val docService = db.collection("service").document(idService)
        docService.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    binding.nameTextView.setText(document.data?.get("itemName") as String)
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
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
            binding.ivService.setImageURI(ImageUri)
        }
    }
    private fun pickPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startForActivityGallery.launch(intent)
    }
    private fun validatePhoto() {
        if (ImageUri==null){
            return Toast.makeText(this, "Ingresa foto de portada", Toast.LENGTH_SHORT).show()
        }
        uploadPhotoFromGallery()
    }

    private fun uploadPhotoFromGallery() {
        val fileName =Timestamp.now()
        binding.btnSend.isEnabled=false
        val StorageReference = FirebaseStorage.getInstance().getReference().child("ServiceImages/$fileName")
        StorageReference.putFile(ImageUri!!).addOnSuccessListener {
            binding.ivService.setImageURI(null)
            Toast.makeText(this, "subida correcta", Toast.LENGTH_SHORT).show()
            StorageReference.downloadUrl.addOnSuccessListener { uri ->
                val photourl = java.lang.String.valueOf(uri)
                createData(photourl)
                binding.btnSend.isEnabled=true
                finish()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "subida incorrecta", Toast.LENGTH_SHORT).show()
        }

    }

    private fun createData(photourl: String) {
        val idService = intent.getStringExtra("idService").toString()
        val photo = hashMapOf(
            "itemDate" to Timestamp.now(),
            "photoUrl" to photourl,
            "serviceID" to idService
        )
        db.collection("photos").add(photo)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

    }
}
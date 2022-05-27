package com.medproyect.app

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.medproyect.app.adapter.ServiceAdapter
import com.medproyect.app.databinding.ActivityAddMyServiceBinding
import com.medproyect.app.provider.CategoryProvider
import com.medproyect.app.provider.ServiceProvider
import java.text.SimpleDateFormat
import java.util.*

class AddMyServiceActivity : AppCompatActivity(), AdapterView.OnItemClickListener {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityAddMyServiceBinding
    private var ImageUri : Uri? = null
    private var categoryID = ""
    private val  UserId= Firebase.auth.currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddMyServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.urlBtnImg.setOnClickListener { requestPermission() }

        binding.saveButton.setOnClickListener { validateEditText() }

        //llenado combo de categorias
        val adapter = ArrayAdapter(this,
        R.layout.item_combo_category,getCategryNameList())
        with(binding.comboTextView){
            setAdapter(adapter)
            onItemClickListener =this@AddMyServiceActivity
        }
    }
    fun getCategryNameList(): Array<String?> {
        val categoryNameList= arrayOfNulls<String>(CategoryProvider.categoryList.size)
        for(i in 0 until CategoryProvider.categoryList.size){
            CategoryProvider.categoryList[i].name
            categoryNameList[i] =(CategoryProvider.categoryList[i].name)
        }
        return categoryNameList
    }

    private fun createData(photourl: String) {
        val newService = hashMapOf(
            "date" to Timestamp.now(),
            "description" to binding.descriptionEditText.text.toString(),
            "generalRank" to  "0",
            "idUser" to UserId,
            "imagePath" to photourl,
            "itemCategory" to categoryID,
            "itemName" to binding.nameEditText.text.toString(),
        )
        db.collection("service")
            .add(newService)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
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

    private fun validateEditText(){
        if(categoryID==""){
            return Toast.makeText(this, "Selecciona categoria", Toast.LENGTH_SHORT).show()
        }
        if (ImageUri==null){
            return Toast.makeText(this, "Ingresa foto de portada", Toast.LENGTH_SHORT).show()
        }
        if(binding.nameEditText.text.isEmpty()){
            return Toast.makeText(this, "Ingrese el nombre", Toast.LENGTH_SHORT).show()
        }
        if(binding.descriptionEditText.text.isEmpty()){
            return Toast.makeText(this, "Ingrese la descripcion", Toast.LENGTH_SHORT).show()
        }
        uploadPhotoFromGallery()

    }
    private fun uploadPhotoFromGallery() {
        binding.saveButton.isEnabled=false
        val fileName = binding.nameEditText.text
        val StorageReferenceFirebase = FirebaseStorage.getInstance().getReference().child("Services/$fileName")
        StorageReferenceFirebase.putFile(ImageUri!!).addOnSuccessListener {
                binding.ivImgService.setImageURI(null)
                Toast.makeText(this, "subida correcta", Toast.LENGTH_SHORT).show()
            StorageReferenceFirebase.downloadUrl.addOnSuccessListener { uri ->
                    val photourl = java.lang.String.valueOf(uri)
                    createData(photourl)
                binding.saveButton.isEnabled=true
                    finish()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "subida incorrecta", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val categoryName  = parent?.getItemAtPosition(position).toString()
        val CategoryItem = CategoryProvider.categoryList.filter { category -> category.name==categoryName }
        categoryID=CategoryItem[0].idcategory
    }
}
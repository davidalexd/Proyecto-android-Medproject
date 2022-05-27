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
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.medproyect.app.databinding.ActivityEditMyServiceBinding
import com.medproyect.app.provider.CategoryProvider
import java.text.SimpleDateFormat
import java.util.*

class EditMyServiceActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener,
    AdapterView.OnItemClickListener {
    private val db = FirebaseFirestore.getInstance()
    private var categoryID = ""
    private lateinit var binding: ActivityEditMyServiceBinding
    private var ImageUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditMyServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val IdService = intent.getStringExtra("IdService").toString()

        getMyservices(IdService)

        binding.urlBtnImg.setOnClickListener {requestPermission()}

        binding.saveButton.setOnClickListener {validateEditText(IdService)}

        //llenado combo de categorias
        val adapter = ArrayAdapter(this,
            R.layout.item_combo_category,getCategryNameList())
        with(binding.comboTextView){
            setAdapter(adapter)
            onItemClickListener =this@EditMyServiceActivity
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
    private fun getMyservices(IdService:String){

        db.collection("service").document(IdService).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    setAlltextViewServices(document)
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }
    private fun setAlltextViewServices(document: DocumentSnapshot){

        val CategoryItem = CategoryProvider.categoryList.filter { category -> category.idcategory==document.data?.get("itemCategory") }
        categoryID=CategoryItem[0].idcategory
        binding.tvCategory.text=CategoryItem[0].name
        binding.nameEditText.setText(document.data?.get("itemName") as String)
        binding.descriptionEditText.setText(document.data?.get("description") as String)
        Glide.with(this).load(document.data?.get("imagePath") as String).into(binding.ivImgService)
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

    private fun validateEditText(IdService:String) {

        if(categoryID==""){
            return Toast.makeText(this, "Selecciona categoria", Toast.LENGTH_SHORT).show()
        }
        if(binding.nameEditText.text.isEmpty()){
            return Toast.makeText(this, "Ingrese el nombre", Toast.LENGTH_SHORT).show()
        }
        if(binding.descriptionEditText.text.isEmpty()){
            return Toast.makeText(this, "Ingrese la descripcion", Toast.LENGTH_SHORT).show()
        }
        updateMyservices(IdService)
    }

    private fun updateMyservices(IdService:String){
        val serviceRef = db.collection("service").document(IdService)
        binding.saveButton.isEnabled=false
        if(ImageUri==null){
            //subir sin imagen
            serviceRef.update(
                "itemName",binding.nameEditText.text.toString(),
                "description",binding.descriptionEditText.text.toString(),
                "itemCategory",categoryID,
            ).addOnSuccessListener {Log.d(TAG, "DocumentSnapshot  service successfully updated!")
                binding.saveButton.isEnabled=true
                finish()
               }
                .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }

        }else{
            //subir con cambio de imagen

            val fileName = binding.nameEditText.text
            val StorageReference = FirebaseStorage.getInstance().getReference().child("Services/$fileName")

            StorageReference.putFile(ImageUri!!).addOnSuccessListener {
                binding.ivImgService.setImageURI(null)
                Toast.makeText(this, "Actualizacion correcta", Toast.LENGTH_SHORT).show()
                StorageReference.downloadUrl.addOnSuccessListener { uri ->
                    val photourl = java.lang.String.valueOf(uri)

                    serviceRef.update(
                        "itemName",binding.nameEditText.text.toString(),
                        "description",binding.descriptionEditText.text.toString(),
                        "itemCategory",categoryID,
                        "imagePath",photourl,
                    ).addOnSuccessListener {Log.d(TAG, " DocumentSnapshot successfully updated!")
                        binding.saveButton.isEnabled=true
                        finish()
                    }
                        .addOnFailureListener { e -> Log.w(TAG, "Error updating  imagePath document", e) }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "subida incorrecta", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val categoryName  = parent?.getItemAtPosition(position).toString()
        val CategoryItem = CategoryProvider.categoryList.filter { category -> category.name==categoryName }

        categoryID=CategoryItem[0].idcategory
        binding.tvCategory.text=CategoryItem[0].name
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }


}
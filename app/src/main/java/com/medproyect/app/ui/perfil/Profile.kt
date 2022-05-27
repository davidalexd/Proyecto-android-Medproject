package com.medproyect.app.ui.perfil

import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.service.controls.ControlsProviderService
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.medproyect.app.AddMyServiceActivity
import com.medproyect.app.CheckoutActivity
import com.medproyect.app.GeneralActivity
import com.medproyect.app.LoginActivity
import com.medproyect.app.databinding.FragmentProfileBinding
import de.hdodenhof.circleimageview.CircleImageView
import org.imaginativeworld.whynotimagecarousel.utils.setImage


class Profile : Fragment() {



    private val db= FirebaseFirestore.getInstance()

    private lateinit var binding: FragmentProfileBinding
    private var ImageUri : Uri? = null
    var UidDato: TextView? = null
    var textcorreo: TextView? = null
    var PImage: CircleImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        val user = Firebase.auth.currentUser
        binding = FragmentProfileBinding.inflate(layoutInflater)
        UidDato = binding.UidDato
        textcorreo = binding.textcorreo
        PImage = binding.PImage
        obtenerDatos(user)
        binding.Actualizar.isEnabled=false
        binding.SendProfile.isEnabled=false
        binding.textNameProfile.isEnabled=false
        binding.textape.isEnabled=false
        binding.texttel.isEnabled=false
        binding.textDir.isEnabled=false
        binding.EditProfile.setOnClickListener {
            binding.EditProfile.isEnabled=false
            binding.Actualizar.isEnabled=true
            binding.SendProfile.isEnabled=true
            binding.textNameProfile.isEnabled=true
            binding.textape.isEnabled=true
            binding.texttel.isEnabled=true
            binding.textDir.isEnabled=true
        }

        binding.Actualizar.setOnClickListener { requestPermission() }

        binding.SendProfile.setOnClickListener{validateEditText()}
        return binding.root

    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickPhotoFromGallery()
        } else {
            Toast.makeText(context, "habilitar registros", Toast.LENGTH_SHORT).show()
        }
    }
    private val startForActivityGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            ImageUri = result.data?.data!!
            binding.PImage.setImageURI(ImageUri)
        }
    }


    private fun requestPermission() {
        when {
            context?.let {
                ContextCompat.checkSelfPermission(
                    it, android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            } == PackageManager.PERMISSION_GRANTED -> {
                pickPhotoFromGallery()
            }
            else -> requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)

        }
    }

    private fun pickPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startForActivityGallery.launch(intent)
    }

    private fun validateEditText() {
       // if (ImageUri==null){
         //   return Toast.makeText(context, "Ingresa foto de portada", Toast.LENGTH_SHORT).show()
        //}
        //validaciones aqui
        if (binding.textape.text.isEmpty()){
            return Toast.makeText(context, "El apellido no  puede  estar vacio", Toast.LENGTH_SHORT).show()
        }
        if (binding.texttel.text.isEmpty()){
            return Toast.makeText(context, "El telefono no  puede  estar vacio", Toast.LENGTH_SHORT).show()
        }
        if (binding.textDir.text.isEmpty()){
            return Toast.makeText(context, "La direccion no  puede  estar vacio", Toast.LENGTH_SHORT).show()
        }
        updateData()
    }


    private fun updateData() {
        binding.SendProfile.isEnabled=false
        val user = Firebase.auth.currentUser
        val usuario = user?.uid
        val profileRef = db.collection("profiles").document(usuario.toString())


        if(ImageUri==null){
            //subir sin imagen
            profileRef.update(
                "address",binding.textDir.text.toString(),
                "firtsName",binding.textNameProfile.text.toString(),
                "lastName", binding.textape.text.toString(),
                "phoneNumber", binding.texttel.text.toString(),
            ).addOnSuccessListener {
                binding.UidDato.text=binding.textNameProfile.text.toString()
                binding.Actualizar.isEnabled=false
                binding.EditProfile.isEnabled=true
                binding.textNameProfile.isEnabled=false
                binding.textape.isEnabled=false
                binding.texttel.isEnabled=false
                binding.textDir.isEnabled=false
                Toast.makeText(
                    context,
                    "Datos cargados",
                    Toast.LENGTH_SHORT
                ).show()}
                .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }

        }else {
            //subir con cambio de imagen
            val fileName = binding.UidDato.text
            val StorageReference =
                FirebaseStorage.getInstance().getReference().child("Profiles/$fileName")
            StorageReference.putFile(ImageUri!!).addOnSuccessListener {
                Toast.makeText(context, "Subiendo cambios", Toast.LENGTH_SHORT).show()
                StorageReference.downloadUrl.addOnSuccessListener { uri ->
                    val photourl = java.lang.String.valueOf(uri)
                    profileRef.update(
                        "address", binding.textDir.text.toString(),
                        "firtsName", binding.textNameProfile.text.toString(),
                        "lastName", binding.textape.text.toString(),
                        "phoneNumber", binding.texttel.text.toString(),
                        "imagen", photourl,
                    ).addOnSuccessListener {
                        binding.UidDato.text=binding.textNameProfile.text.toString()
                        binding.Actualizar.isEnabled=false
                        binding.EditProfile.isEnabled=true
                        binding.textNameProfile.isEnabled=false
                        binding.textape.isEnabled=false
                        binding.texttel.isEnabled=false
                        binding.textDir.isEnabled=false
                        Toast.makeText(
                            context,
                            "Datos cargados",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                        .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
                }
            }.addOnFailureListener {
                Toast.makeText(context, "subida incorrecta", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun obtenerDatos(user: FirebaseUser?) {
        val usuario = user?.uid

        val docRef =db.collection("profiles").whereEqualTo("userID", usuario)

        docRef.get()
            .addOnSuccessListener{documents->
                for (document in documents) {

                    Log.d(TAG, "${document.id} => ${document.data}")
                    with(binding) {
                        UidDato.setText(document.data?.get("firtsName") as String)
                        correo.setText(document.data?.get("correo") as String?)
                        textNameProfile.setText(document.data?.get("firtsName") as String)
                        textape.setText(document.data?.get("lastName") as String)
                        texttel.setText(document.data?.get("phoneNumber") as String)
                        textcorreo.setText(document.data?.get("correo") as String?)
                        textDir.setText(document.data?.get("address") as String)
                        if(document.data?.get("imagen").toString()!=""){
                            Glide.with(binding.PImage.context).load(document.data?.get("imagen") as String).into(binding.PImage)
                        }
                    }


                }
            }
            .addOnFailureListener{exception->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

}










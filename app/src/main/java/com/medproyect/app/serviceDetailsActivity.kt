package com.medproyect.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.medproyect.app.databinding.ActivityServiceDetailsBinding
import org.imaginativeworld.whynotimagecarousel.ImageCarousel
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem
import java.text.SimpleDateFormat

class serviceDetailsActivity : AppCompatActivity() {
    private lateinit var binding:ActivityServiceDetailsBinding
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private val db = FirebaseFirestore.getInstance()
    val list = mutableListOf<CarouselItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityServiceDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener{
            finish()
        }
        val idService = intent.getStringExtra("idService")

        //ver fotos del servicio
        binding.btnImages.setOnClickListener{

            val intent = Intent(binding.btnImages.context, MyPhotosServicesActivity::class.java)
            intent.putExtra("idServiceNoUser",idService)
            binding.btnImages.context.startActivity(intent)
        }

        if (idService != null) {
            getPhotosService(idService)
            getDetailService(idService)
            binding.btnAppointment.setOnClickListener{ openAppointment(idService)}
        }
    }


    private fun getPhotosService(id: String){
        val carousel:ImageCarousel=binding.carousel
        db.collection("photos")
            .whereEqualTo("serviceID", id)
            .get()
            .addOnSuccessListener { documents ->
                if(documents.size()>1){
                    binding.PImage.visibility = View.INVISIBLE;
                    for (document in documents) {

                        list.add(CarouselItem(document.data.get("photoUrl") as String))
                    }
                    carousel.addData(list)
                }else{
                    binding.carousel.visibility = View.INVISIBLE;
                }

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

    }
    private fun getDetailService(id: String){
        val docService = db.collection("service").document(id)
        docService.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    binding.txtname.text=document.data?.get("itemName").toString()
                    binding.txtdescription.text=document.data?.get("description").toString()
                    binding.txtDateService.text=simpleDateFormat.format((document.data?.get("date") as Timestamp).seconds * 1000L).toString()
                    Glide.with(binding.PImage.context).load(document.data?.get("imagePath") as String).into(binding.PImage)
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }
    private fun openAppointment(idService : String){
        val intent = Intent(this,ClientAppointments::class.java)
        intent.putExtra("serviceId",idService)
        intent.putExtra("serviceName",binding.txtname.text)
        startActivity(intent)
    }
}
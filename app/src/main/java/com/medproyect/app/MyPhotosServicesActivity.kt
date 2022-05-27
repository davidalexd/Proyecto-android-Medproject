
package com.medproyect.app

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.medproyect.app.adapter.ImagesServiceAdapter
import com.medproyect.app.databinding.ActivityMyPhotosServicesBinding
import com.medproyect.app.models.ImagesService
import com.medproyect.app.provider.ImagesServiceProvider
import java.util.HashMap

class MyPhotosServicesActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMyPhotosServicesBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var idService : String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMyPhotosServicesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener{
            finish()
        }
        if(intent.getStringExtra("idServiceNoUser")!=null){
           idService=intent.getStringExtra("idServiceNoUser").toString()
            binding.btnAddphoto.visibility = View.GONE;
        }
        else{
            idService=intent.getStringExtra("id_service").toString()
            binding.btnAddphoto.visibility = View.VISIBLE;
        }
        getDataService(idService)

        binding.btnAddphoto.setOnClickListener {
            val intent = Intent( binding.btnAddphoto.context, AddMyPhotosServicesActivity::class.java)
            intent.putExtra("idService",idService)
            binding.btnAddphoto.context.startActivity(intent)
        }
        initRecyclerView(idService)
    }



    private fun getDataService(idService:String){
        val docService = db.collection("service").document(idService)
        docService.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    binding.textNameService.setText(document.data?.get("itemName") as String)
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }
    private fun initRecyclerView(idService: String) {
        ImagesServiceProvider.imagesServiceList.clear()
        val manager = GridLayoutManager(this,2)
        val recyclerView =binding.reciclerImagesServices
        recyclerView.layoutManager = manager
        recyclerView.adapter = ImagesServiceAdapter(ImagesServiceProvider.imagesServiceList)
        getPhotodata(recyclerView,idService)
    }

    private fun getPhotodata(recyclerView: RecyclerView, idService: String) {

        db.collection("photos") .whereEqualTo("serviceID", idService)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                ImagesServiceProvider.imagesServiceList.clear()
                for (doc in value!!) {
                    this.runOnUiThread {
                        doc.getString("photoUrl")?.let {

                            ImagesServiceProvider.imagesServiceList.add(
                                ImagesService(it)
                            )
                        }
                        recyclerView.adapter?.notifyDataSetChanged()
                    }

                }
            }
    }
}



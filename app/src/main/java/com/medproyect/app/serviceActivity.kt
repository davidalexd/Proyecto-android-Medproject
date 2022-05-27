package com.medproyect.app

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.ControlsProviderService
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.medproyect.app.adapter.RankServiceAdapter
import com.medproyect.app.adapter.ServiceAdapter
import com.medproyect.app.databinding.ActivityServiceBinding
import com.medproyect.app.models.RankService
import com.medproyect.app.models.Service
import com.medproyect.app.provider.MyserviceProvider
import com.medproyect.app.provider.RankServiceProvider
import com.medproyect.app.provider.ServiceProvider
class serviceActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private var counterUserAppointments:Int =0
    private var Urlsearch:String ="https://media.discordapp.net/attachments/873053673255735346/979008135329095700/giphy.gif"
    private var UrlNoSearch:String ="https://media.discordapp.net/attachments/873053673255735346/979011460426510406/giphy_2.gif"
    private lateinit var binding: ActivityServiceBinding
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                binding = ActivityServiceBinding.inflate(layoutInflater)
                setContentView(binding.root)
                binding.btnBack.setOnClickListener{
                    finish()
                }
                val idcategory = intent.getStringExtra("idcategoria")
                val namecategory = intent.getStringExtra("nombrecategoria")
                binding.tvCategoryNameService.text = "Categoria: "+namecategory
                if (idcategory != null) {
                    getAllServicesPurchases()
                    onChangeServicesBD(idcategory)
                }
                initRecyclerViewServices()
                initRecyclerViewAppointments()


    }

    private fun onChangeServicesBD(idcategory: String) {
        db.collection("service")
            .whereEqualTo("itemCategory", idcategory)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(ControlsProviderService.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                ServiceProvider.serviceList.clear()
                for (doc in value!!) {
                    getAllServices(idcategory)
                }
                //Log.d(TAG, "Current cites in CA: $myservices")
            }

    }

    private fun getAllServicesPurchases(){
        Glide.with(binding.ivnoContentAppointment.context).load(Urlsearch).into(binding.ivnoContentAppointment)
        val idCategory = intent.getStringExtra("idcategoria")
        val recyclerViewPurchases =binding.reciclerRankService
        RankServiceProvider.RankServiceList.clear()
        db.collection("user_appointments")
            .get()
            .addOnSuccessListener { userAppointments ->
                for (userAppointment in userAppointments) {
                    val docAppointment = db.collection("appointments").document(userAppointment.data.get("appointment_id") as String)
                    docAppointment.get()
                        .addOnSuccessListener { appointment ->
                            if (appointment.data != null) {
                                val docService = db.collection("service").document(appointment.data?.get("serviceID") as String)
                                docService.get()
                                    .addOnSuccessListener { service ->
                                        this.runOnUiThread{
                                            if( counterUserAppointments>0){
                                                binding.ivnoContentAppointment.visibility = View.GONE

                                            }else{
                                                Glide.with(binding.ivnoContentAppointment.context).load(UrlNoSearch).into(binding.ivnoContentAppointment)
                                                binding.ivnoContentAppointment.visibility = View.VISIBLE
                                            }
                                            if(service.data?.get("itemCategory") as String ==idCategory){
                                                ++counterUserAppointments
                                                    RankServiceProvider.RankServiceList.add(
                                                        RankService(
                                                            service.id,
                                                            service.data?.get("itemName") as String,
                                                            service.data?.get("description") as String,
                                                            service.data?.get("imagePath") as String,
                                                            (userAppointment.get("payment_date") as Timestamp).toDate(),
                                                            appointment.get("photoSpecialist") as String,
                                                            appointment.get("nameSpecialist") as String,
                                                        )
                                                    )
                                                if( counterUserAppointments>0){
                                                    binding.ivnoContentAppointment.visibility = View.GONE

                                                }
                                                    recyclerViewPurchases.adapter?.notifyDataSetChanged()
                                                }
                                            }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.d(TAG, "get failed with ", exception)
                                    }

                            } else {
                                Log.d(TAG, "No appointment document")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "get failed with ", exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }


    }
    private fun initRecyclerViewAppointments() {
        //recicler Purchases
        val managerPurchases = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        val recyclerViewPurchases =binding.reciclerRankService
        recyclerViewPurchases.layoutManager=managerPurchases
        recyclerViewPurchases.adapter = RankServiceAdapter(RankServiceProvider.RankServiceList)

    }
    private fun initRecyclerViewServices() {
        //recicler Service
        val manager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        val recyclerView =binding.reciclerService
        recyclerView.layoutManager = manager
        recyclerView.adapter = ServiceAdapter(ServiceProvider.serviceList)

    }

    private  fun getAllServices(idcategory:String){
        val recyclerView =binding.reciclerService
        ServiceProvider.serviceList.clear()
        //Glide.with(binding.ivnoContentService.context).load(Urlsearch).into(binding.ivnoContentService)
        db.collection("service")
            .whereEqualTo("itemCategory", idcategory as String)
            .get()
            .addOnSuccessListener { documents ->
                ServiceProvider.serviceList.clear()
                for (document in documents) {
                    this.runOnUiThread{
                        ServiceProvider.serviceList.add(
                            Service(
                                document.id,
                                document.data.get("itemName") as String ,
                                (document.data.get("date") as Timestamp).toDate(),
                                document.data.get("description") as String,
                                (document.data.get("generalRank") as String).toInt(),
                                document.data.get("imagePath") as String,
                            ))
                        if(documents.size()>0){
                            binding.ivnoContentService.visibility = View.GONE
                        }else{
                            Glide.with(binding.ivnoContentService.context).load(UrlNoSearch).into(binding.ivnoContentService)
                            binding.ivnoContentService.visibility = View.VISIBLE
                        }
                        recyclerView.adapter?.notifyDataSetChanged()

                    }


                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }



    }


}




































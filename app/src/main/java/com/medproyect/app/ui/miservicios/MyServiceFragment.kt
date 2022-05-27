package com.medproyect.app.ui.miservicios

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.ktx.Firebase
import com.medproyect.app.AddMyServiceActivity

import com.medproyect.app.adapter.MyServiceAdapter

import com.medproyect.app.databinding.FragmentMyserviceBinding

import com.medproyect.app.models.MyServices

import com.medproyect.app.provider.MyserviceProvider


class MyServiceFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()

    private var _binding: FragmentMyserviceBinding?=null;
    private  val binding get() = _binding!!
    private val  user= Firebase.auth.currentUser
    private var Urlsearch:String ="https://media.discordapp.net/attachments/873053673255735346/979008135329095700/giphy.gif"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        MyserviceProvider.MyServiceList.clear()
        _binding = FragmentMyserviceBinding.inflate(inflater,container,false)
        getMyUser()
        onChangeMyserviceDB()
        initRecyclerView()
        binding.newFloatingActionButton.setOnClickListener{
            val intent = Intent(binding.newFloatingActionButton.context, AddMyServiceActivity::class.java)
            binding.newFloatingActionButton.context.startActivity(intent)
        }
        return binding.root
    }

    private  fun getMyUser(){
        db.collection("profiles").whereEqualTo("userID", user?.uid).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    binding.nameUsertv.text="${(document.data?.get("firtsName") as String)} ${(document.data?.get("lastName") as String)}"

                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }
    private fun onChangeMyserviceDB(){
        db.collection("service")
            .whereEqualTo("idUser", user?.uid)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                MyserviceProvider.MyServiceList.clear()
                for (doc in value!!) {
                    var categoryId=doc.data?.get("itemCategory").toString()
                    getMyCategoriaName(categoryId,doc)
                }
                //Log.d(TAG, "Current cites in CA: $myservices")
               }
    }

    private fun initRecyclerView() {
        val manager = GridLayoutManager(activity,1)
        val recyclerView =binding.myserviceRecyclerView
        recyclerView.layoutManager = manager
        recyclerView.adapter = MyServiceAdapter(MyserviceProvider.MyServiceList)
    }

    private fun getMyCategoriaName(categoryId: String, dataService: QueryDocumentSnapshot) {
        Glide.with(binding.ivnoLoader.context).load(Urlsearch).into(binding.ivnoLoader)
        val recyclerView =binding.myserviceRecyclerView
        MyserviceProvider.MyServiceList.clear()
        db.collection("category").document(categoryId)
        .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    activity?.runOnUiThread {
                        binding.ivnoLoader.visibility = View.GONE
                        MyserviceProvider.MyServiceList.add(
                            MyServices(
                                dataService.data.get("idUser") as String,
                                dataService.data.get("itemName") as String,
                                dataService.data.get("imagePath") as String,
                                document.data?.get("itemName") as String,
                                dataService.data.get("description") as String,
                                (dataService.data.get("generalRank") as String).toInt(),
                                dataService.id
                            ))
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

    }




}
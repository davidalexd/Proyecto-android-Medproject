package com.medproyect.app.ui.dashboard
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.medproyect.app.adapter.ClientsMyServiceAdapter
import com.medproyect.app.databinding.FragmentDashBinding
import com.medproyect.app.models.ClientsMyService
import com.medproyect.app.provider.ClientsMyServiceProvider
import com.medproyect.app.provider.MyserviceProvider

class DashFragment : Fragment() {
        private val  user= Firebase.auth.currentUser
        private val db = FirebaseFirestore.getInstance()
        private var _binding:FragmentDashBinding?=null
        private  val binding get() = _binding!!
    private var  totalProfit: Double = 0.0
    private var counter:Int =0
    private var counterAppointments:Int =0
    private var counterService:Int =0


        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            _binding = FragmentDashBinding.inflate(inflater,container,false)
            getMyProfile()
            getMyServices()
            getMyAppointment()
            getMyClients()
            initRecyclerView()
            return  binding.root
        }
    private fun getMyProfile(){
        db.collection("profiles")
            .whereEqualTo("userID", user?.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                binding.sellerName.text="${document.data?.get("firtsName")} ${document.data?.get("lastName")}"
                    Glide.with(binding.ivsellerPhoto.context).load(document.data.get("imagen")).into(binding.ivsellerPhoto)
            }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

    }

    private fun getMyServices(){
        db.collection("service")
            .whereEqualTo("idUser", user?.uid)
            .get()
            .addOnSuccessListener { documents ->
                counterService=documents.size()
                binding.tvcountMyService.text=counterService.toString()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }
    private fun getMyAppointment(){
        db.collection("appointments")
            .get()
            .addOnSuccessListener { result ->
                for (appointment in result){
                    val docService=db.collection("service").document(appointment.data?.get("serviceID") as String)
                    docService.get().addOnSuccessListener { service ->
                        if(service.data  !=null){
                            if(service.data?.get("idUser")==user?.uid){
                                counterAppointments++
                                binding.tvCountApointments.text=counterAppointments.toString()
                                if(counter==0){
                                    binding.tvAvailableCount.text= (counterAppointments).toString()
                                }
                            }

                        }else{
                            Log.d(TAG, "No such document service")
                        }
                    }.addOnFailureListener { exception ->
                        Log.d(TAG, "get failed with ", exception)}
                }

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

    }

    private fun getMyClients() {
            val recyclerView =binding.myclientsRecyclerView
            ClientsMyServiceProvider.clientsMyServiceList.clear()

            db.collection("user_appointments")
                .get()
                .addOnSuccessListener { result ->
                    for (payment in result) {
                        val docAppointment = db.collection("appointments").document(payment.data.get("appointment_id") as String)
                        docAppointment.get().addOnSuccessListener {appointment ->
                                if (appointment.data != null) {
                                    val docService=db.collection("service").document(appointment.data?.get("serviceID") as String)
                                    docService.get().addOnSuccessListener { service ->
                                            if (service.data != null) {
                                                if(service.data?.get("idUser")==user?.uid){
                                                    db.collection("profiles").whereEqualTo("userID", payment.data.get("user_id") as String)
                                                        .get().addOnSuccessListener { profiles ->
                                                            for (profile in profiles) {
                                                                var ganancia= (appointment.data?.get("itemPrice") as String).toDouble()
                                                                totalProfit += ganancia
                                                                ++counter
                                                                activity?.runOnUiThread {
                                                                ClientsMyServiceProvider.clientsMyServiceList.add(
                                                                ClientsMyService(
                                                                   "${profile.data?.get("firtsName") as String} ${profile.data?.get("lastName") as String}",
                                                                    appointment.data?.get("itemPrice") as String,
                                                                    profile.data?.get("imagen") as String,
                                                                    service.data?.get("itemName") as String,
                                                                    payment.data?.get("payment_date") as Timestamp,
                                                                  )
                                                                )
                                                                    recyclerView.adapter?.notifyDataSetChanged()
                                                                }
                                                            }
                                                            binding.tvtotalProfit.text=totalProfit.toString()
                                                            binding.tvCountClient.text=counter.toString()
                                                            binding.tvAvailableCount.text= (counterAppointments-counter).toString()

                                                        }
                                                        .addOnFailureListener { exception ->
                                                            Log.w(TAG, "Error getting documents: ", exception)
                                                        }
                                                }
                                            } else {
                                                Log.d(TAG, "No such document service")
                                            }
                                    }.addOnFailureListener { exception ->
                                        Log.d(TAG, "get failed with ", exception)
                                    }
                                } else {
                                    Log.d(TAG, "No such document appointment")
                                }
                        }.addOnFailureListener { exception ->
                                Log.d(TAG, "get failed with ", exception)
                            }
                    }
                }.addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
                }
        }

        private fun initRecyclerView() {
            val manager = GridLayoutManager(activity,1)
            val recyclerView =binding.myclientsRecyclerView
            recyclerView.layoutManager = manager
            recyclerView.adapter = ClientsMyServiceAdapter(ClientsMyServiceProvider.clientsMyServiceList)

        }

    }
package com.medproyect.app.adapter

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.medproyect.app.EditMyServiceActivity
import com.medproyect.app.MyPhotosServicesActivity
import com.medproyect.app.ServiceAppointmentsActivity
import com.medproyect.app.databinding.MyservicesContentBinding

import com.medproyect.app.models.MyServices
import com.medproyect.app.provider.MyserviceProvider


class MyServiceViewHolder(view: View): RecyclerView.ViewHolder(view){
    private val db = FirebaseFirestore.getInstance()
    val binding = MyservicesContentBinding.bind(view)
    fun render(myserviceModel: MyServices){


        binding.nameTextView.text = myserviceModel.name
        binding.categoryValue.text = myserviceModel.category
        binding.citesValue.text = myserviceModel.cites.toString()


        Glide.with(binding.posterImgeView.context).load(myserviceModel.photo).into(binding.posterImgeView)

        //editar
        binding.btnedit.setOnClickListener{
            val intent = Intent(binding.nameTextView.context, EditMyServiceActivity::class.java)
            intent.putExtra("IdService",myserviceModel.key)
            binding.nameTextView.context.startActivity(intent)
        }

        //eliminar
        binding.btndelete.setOnClickListener{
            MyserviceProvider.MyServiceList.get(absoluteAdapterPosition).key?.let{
                db.collection("service").document(it).delete()
                MyserviceProvider.MyServiceList.indexOfFirst{ e->e.key==it.toString() }.let {
                    MyserviceProvider.MyServiceList.removeAt(it)
                    bindingAdapter?.notifyDataSetChanged()
                }
            }
        }

        //ver fotos del servicio
        binding.btndetails.setOnClickListener{

            val intent = Intent(binding.btndetails.context, MyPhotosServicesActivity::class.java)
            intent.putExtra("id_service",myserviceModel.key)
            binding.btndetails.context.startActivity(intent)
        }
        //ver citas
        binding.btnAppointment.setOnClickListener{
            val intent = Intent(binding.btnAppointment.context, ServiceAppointmentsActivity::class.java)
            intent.putExtra("name_service", myserviceModel.name)
            intent.putExtra("id_service",myserviceModel.key)
            binding.btnAppointment.context.startActivity(intent)
        }
    }


}
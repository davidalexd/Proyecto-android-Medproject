package com.medproyect.app.adapter

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.medproyect.app.databinding.ItemCategoryBinding
import com.medproyect.app.models.Category
import com.medproyect.app.serviceActivity

class CategoryViewHolder(view: View): RecyclerView.ViewHolder(view){

    val binding = ItemCategoryBinding.bind(view)
    fun render(categoryModel: Category){
        binding.tvCategoryName.text = categoryModel.name
        Glide.with(binding.ivCategory.context).load(categoryModel.photo).into(binding.ivCategory)
        binding.ivCategory.setOnClickListener{
            val intent = Intent(binding.ivCategory.context,serviceActivity::class.java)
            intent.putExtra("idcategoria",categoryModel.idcategory)
            intent.putExtra("nombrecategoria",categoryModel.name)
            binding.ivCategory.context.startActivity(intent)

            }
    }


}
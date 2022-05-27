package com.medproyect.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medproyect.app.R
import com.medproyect.app.databinding.ItemCategoryBinding
import com.medproyect.app.models.Category

class CategoryAdapter(private val categoryList:List<Category>): RecyclerView.Adapter<CategoryViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CategoryViewHolder(layoutInflater.inflate(R.layout.item_category,parent,false))
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = categoryList[position]
        holder.render(item)

    }

    override fun getItemCount(): Int = categoryList.size

}
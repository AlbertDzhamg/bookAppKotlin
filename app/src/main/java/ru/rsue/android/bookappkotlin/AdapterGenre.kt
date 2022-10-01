package ru.rsue.android.bookappkotlin

import android.content.Context
import android.view.View
import android.widget.AbsListView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.rsue.android.bookappkotlin.databinding.RowGenreBinding

class AdapterGenre {


    private lateinit var binding: RowGenreBinding
    private val context: Context
    private val categoryArrayList: ArrayList<ModelCategory>

    constructor(context: Context, categoryArrayList: ArrayList<ModelCategory>){
        this.context = context
        this.categoryArrayList = categoryArrayList
    }

    inner class HolderGenre(itemView: View):RecyclerView.ViewHolder(itemView){
        var genreTv: TextView = binding.genreTv
        var deleteBtn: TextView = binding.deleteBtn
    }
}
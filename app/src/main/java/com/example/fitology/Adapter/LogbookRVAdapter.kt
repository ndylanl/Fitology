package com.example.fitology.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitology.Interface.Cardlistener
import com.example.fitology.Model.Note
import com.example.fitology.R
import com.example.fitology.databinding.CardNoteBinding

class LogbookRVAdapter(val listLogs: ArrayList<Note>, val cardListener: Cardlistener) :
    RecyclerView.Adapter<LogbookRVAdapter.viewHolder>() {

    class viewHolder (val itemview: View, val cardListener1: Cardlistener): RecyclerView.ViewHolder(itemview){

        val binding = CardNoteBinding.bind(itemview)

        fun setData(data: Note){
            binding.noteJudulTV.text = data.title
            binding.noteIsiTV.text = data.notes

            itemview.setOnClickListener{
                cardListener1.onCardClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.card_note, parent, false)
        return viewHolder(view, cardListener)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.setData(listLogs[position])
    }

    override fun getItemCount(): Int {
        return listLogs.size
    }


}
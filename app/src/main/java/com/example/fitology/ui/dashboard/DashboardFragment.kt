package com.example.fitology.ui.dashboard

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.fitology.Adapter.LogbookRVAdapter
import com.example.fitology.Database.GlobalVar
import com.example.fitology.Interface.Cardlistener
import com.example.fitology.Model.Note
import com.example.fitology.databinding.FragmentDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject


class DashboardFragment : Fragment(), Cardlistener {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    val db = FirebaseFirestore.getInstance()
    private val adapter = LogbookRVAdapter(GlobalVar.listLogs, this)
    private var alreadyExecuted = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.addNotesBTN.setOnClickListener(){
            val myIntent = Intent(activity, CreateNoteActivity::class.java)
            requireActivity().startActivity(myIntent)
        }

        Display()
        if(!alreadyExecuted){
            init()
            alreadyExecuted = true
        }

        return root
    }

    private fun init() {
        var testNotes = ArrayList<Note>()
        val query = db.collection("notes").document(FirebaseAuth.getInstance().currentUser!!.uid).collection("myNotes").orderBy("title",Query.Direction.ASCENDING)
        query.get().addOnSuccessListener { result ->
            for (document in result){
                var notes = document.toObject<Note>()
                testNotes.add(notes)
                GlobalVar.listLogs = testNotes
            }
        }
    }

    private fun Display() {
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.notesRV.layoutManager = layoutManager
        binding.notesRV.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    override fun onCardClick(position: Int) {

            val myIntent = Intent(activity, CreateNoteActivity::class.java).apply {
                putExtra("position", position)
            }
            startActivity(myIntent)
    }

    override fun onCardClicked(view: View, position: Int) {

        val myIntent = Intent(activity, CreateNoteActivity::class.java).apply {
            putExtra("position", position)
        }
        startActivity(myIntent)
    }
}
package com.example.fitology.ui.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fitology.Database.GlobalVar
import com.example.fitology.Model.Note
import com.example.fitology.NavActivity
import com.example.fitology.databinding.ActivityCreateNoteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.*


class CreateNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateNoteBinding
    val db = Firebase.firestore
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    private var position = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        @Suppress("DEPRECATION")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()
        binding.progressBarcreatenote.visibility = View.GONE
        binding.deletenoteFAB.visibility = View.GONE
        GetIntent()
        if(position != -1){
            binding.deletenoteFAB.visibility = View.VISIBLE
        }
        Listener()
    }

    private fun Listener() {
        binding.createNoteFAB.setOnClickListener(){
            var title = binding.createnoteTitle.text.toString().trim()
            var noteIsi = binding.createnoteContent.text.toString().trim()
            if(title.isEmpty() || noteIsi.isEmpty()){
                Toast.makeText(this, "Please Fill in both the Title and the Contents.", Toast.LENGTH_SHORT)
            }else{
                binding.progressBarcreatenote.visibility = View.VISIBLE
                if(position != -1){
                    var note = GlobalVar.listLogs[position]
                    note.title = title
                    note.notes = noteIsi
                    db.collection("notes").document(firebaseUser!!.uid).collection("myNotes").document(note.uid).set(note)
                    var testNotes = ArrayList<Note>()
                    val query = db.collection("notes").document(FirebaseAuth.getInstance().currentUser!!.uid).collection("myNotes").orderBy("title",
                        Query.Direction.ASCENDING)
                    query.get().addOnSuccessListener { result ->
                        for (document in result){
                            var notes = document.toObject<Note>()
                            testNotes.add(notes)
                            GlobalVar.listLogs = testNotes
                        }
                    }
                    binding.progressBarcreatenote.visibility = View.GONE
                    startActivity(Intent(this, NavActivity::class.java))
                    finish()
                }else{
                    val note = Note(title, noteIsi, "")
                    var docID = UUID.randomUUID().toString()
                    note.uid = docID
                    GlobalVar.listLogs.add(note)
                    db.collection("notes").document(firebaseUser!!.uid).collection("myNotes").document(note.uid).set(note)
                    var testNotes = ArrayList<Note>()
                    val query = db.collection("notes").document(FirebaseAuth.getInstance().currentUser!!.uid).collection("myNotes").orderBy("title",Query.Direction.ASCENDING)
                    query.get().addOnSuccessListener { result ->
                        for (document in result){
                            var notes = document.toObject<Note>()
                            testNotes.add(notes)
                            GlobalVar.listLogs = testNotes
                        }
                    }
                    binding.progressBarcreatenote.visibility = View.GONE
                    startActivity(Intent(this, NavActivity::class.java))
                    finish()
                }

            }
        }
        binding.deletenoteFAB.setOnClickListener(){
            db.collection("notes").document(firebaseUser!!.uid).collection("myNotes").document(GlobalVar.listLogs[position].uid).delete()
            startActivity(Intent(this, NavActivity::class.java))
            finish()
        }
    }

    private fun GetIntent() {
        position = intent.getIntExtra("position", -1)
        if(position != -1){
            val movie = GlobalVar.listLogs[position]
            Display(movie)
        }
    }

    private fun Display(note: Note) {
        binding.createnoteTitle.setText(note.title)
        binding.createnoteContent.setText(note.notes)


    }

}
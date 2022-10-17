package com.example.fitology

import android.os.Bundle
import android.view.WindowManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.fitology.Database.GlobalVar
import com.example.fitology.Model.Note
import com.example.fitology.Model.User
import com.example.fitology.databinding.ActivityNavBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class NavActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNavBinding
    val db = Firebase.firestore


    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)

        binding = ActivityNavBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
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
        if(GlobalVar.currentUser.isEmpty()){
            GlobalVar.currentUser.clear()
            var query = db.collection("notes").document(FirebaseAuth.getInstance().currentUser!!.uid).collection("userData")
            query.get().addOnSuccessListener { result ->
                for (document in result){
                    var nowUser = document.toObject<User>()
                    GlobalVar.currentUser.add(nowUser)
                }
            }
        }


        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_nav)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}
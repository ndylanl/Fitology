package com.example.fitology

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.core.view.isEmpty
import com.example.fitology.Database.GlobalVar
import com.example.fitology.Model.Note
import com.example.fitology.Model.User
import com.example.fitology.Model.steps
import com.example.fitology.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    val db = Firebase.firestore
    val firebaseUser = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        if(FirebaseAuth.getInstance().currentUser!=null){
            startActivity(Intent(this, NavActivity::class.java))
            finish()
        }
        binding.progressBarlogin.visibility = View.GONE

        GetIntent()
        Listener()

    }

    private fun GetIntent() {
        val emailID = intent.getStringExtra("email_id")
        val passID = intent.getStringExtra("password_id")

        binding.emailTLI.editText?.setText(emailID)
        binding.passwordTLI.editText?.setText(passID)
    }

    private fun Listener() {
        binding.loginBTN.setOnClickListener{
            if(binding.emailTLI.editText?.text.toString().trim().isEmpty() || binding.passwordTLI.editText?.text.toString().trim().isEmpty()){
                Toast.makeText(this, "All fields are required to Log-In.", Toast.LENGTH_SHORT).show()
            }else{
                binding.progressBarlogin.visibility = View.VISIBLE

                val email = binding.emailTLI.editText?.text.toString().trim()
                val password = binding.passwordTLI.editText?.text.toString().trim()

                firebaseUser.signInWithEmailAndPassword(email, password).addOnCompleteListener{ task ->

                    if(task.isSuccessful){
                        Toast.makeText(this, "Logged In.", Toast.LENGTH_SHORT)

                        var stepquery = db.collection("notes").document(firebaseUser.currentUser!!.uid).collection("StepData")
                        stepquery.get().addOnSuccessListener { results ->
                            for (document in results){
                                var nowStep = document.toObject<steps>()
                                GlobalVar.StepCount.add(nowStep)
                            }
                        }

                        var query = db.collection("notes").document(firebaseUser.currentUser!!.uid).collection("userData")
                        query.get().addOnSuccessListener { result ->
                                for (document in result){
                                    var nowUser = document.toObject<User>()
                                    GlobalVar.currentUser.add(nowUser)
                                }
                            }
                            val myIntent = Intent(this, NavActivity::class.java).apply {
                                putExtra("user_id", intent.getStringExtra("user_id")
                                )
                            }
                            binding.progressBarlogin.visibility = View.GONE
                            startActivity(myIntent)
                            finish()

                    }else{
                        Toast.makeText(this, "Account does not exist.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        binding.signupBTN.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }


}
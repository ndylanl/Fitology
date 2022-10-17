package com.example.fitology

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isNotEmpty
import com.example.fitology.Model.User
import com.example.fitology.Model.steps
import com.example.fitology.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarsignup.visibility = View.GONE

        supportActionBar?.hide()

        Listener()

    }

    private fun Listener() {
        binding.backtoLoginBTN.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.registerBTN.setOnClickListener{
            if(binding.usernamesignupTLI.isNotEmpty() || binding.emailsignupTLI.isNotEmpty() || binding.passwordsignupTLI.isNotEmpty()){
                binding.progressBarsignup.visibility = View.VISIBLE
                var email = binding.emailsignupTLI.editText?.text.toString().trim()
                var password = binding.passwordsignupTLI.editText?.text.toString().trim()
                var name = binding.usernamesignupTLI.editText?.text.toString().trim()

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(){ task ->
                        if(task.isSuccessful){
                                    binding.progressBarsignup.visibility = View.VISIBLE
                                    //create user profile
                                    val BabyUser = User(name, "0", "0", 0.0, "lolbaby", "")
                                    val bebiUser: MutableMap<String, Any> = HashMap()
                                    bebiUser["name"] = BabyUser.name
                                    bebiUser["height"] = BabyUser.height
                                    bebiUser["weight"] = BabyUser.weight
                                    bebiUser["bmi"] = BabyUser.bmi
                                    bebiUser["imageUri"] = BabyUser.imageUri
                                    bebiUser["stepgoal"] = BabyUser.stepgoal
                                    var firebaseUser = FirebaseAuth.getInstance()
                                    val documentreference = db.collection("notes").document(firebaseUser.currentUser!!.uid).collection("userData")
                                    var newstep = steps("")
                                    db.collection("notes").document(firebaseUser.currentUser!!.uid).collection("StepData").document("StepCount").set(newstep)
                                        documentreference.document("Profile").set(bebiUser).addOnSuccessListener {
                                        val firebaseUser = FirebaseAuth.getInstance().currentUser
                                        Toast.makeText(this, "Registration successful.", Toast.LENGTH_SHORT).show()
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        intent.putExtra("user_id", firebaseUser?.uid)
                                        intent.putExtra("email_id", firebaseUser?.email)
                                        intent.putExtra("password_id", password)
                                        FirebaseAuth.getInstance().signOut()
                                        startActivity(Intent(this, LoginActivity::class.java))
                                        finish()
                                    }
                        }else{
                            Toast.makeText(this, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

            }else if(binding.passwordsignupTLI.editText?.text.toString().trim().length < 7){
                Toast.makeText(this, "Password must be at least 8 characters long.", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "All fields are required to register.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
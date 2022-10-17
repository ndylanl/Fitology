package com.example.fitology

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.example.fitology.Database.GlobalVar
import com.example.fitology.Model.User
import com.example.fitology.databinding.ActivityEditProfileActivitiyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.math.round
import kotlin.math.roundToInt


class editProfileActivitiy : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileActivitiyBinding
    var setup = false
    var editUser = GlobalVar.currentUser[0]
    var curUserEmail = FirebaseAuth.getInstance().currentUser?.email
    val db = Firebase.firestore
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    lateinit var imagePreview: ImageView
    var newimageuri: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        binding = ActivityEditProfileActivitiyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //fullscreen
        @Suppress("DEPRECATION")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()

        GetIntent()
        Listener()
    }

    private fun Listener() {
        binding.editIV.setOnClickListener(){launchGallery()}
        binding.editimageFAB.setOnClickListener(){launchGallery()}

        binding.saveeditBTN.setOnClickListener(){
            //upload IMG to firebase storage
            if(filePath != null){
                newimageuri = UUID.randomUUID().toString()
                val ref = storageReference?.child("myImages/" + newimageuri + ".jpg")
                val localfile = File.createTempFile("tempImage", "jpg")
                val uploadTask = ref?.putFile(filePath!!)?.addOnCompleteListener(){
                    ref.getFile(localfile).addOnSuccessListener {
                        val bitMap = BitmapFactory.decodeFile(localfile.absolutePath)
                        binding.editIV.setImageBitmap(bitMap)
                    }.addOnFailureListener(){
                        Toast.makeText(this, "Uploaded but can not Display Image", Toast.LENGTH_SHORT)
                    }
                }
            }else if(GlobalVar.currentUser[0].imageUri != ""){
                newimageuri = GlobalVar.currentUser[0].imageUri
            }else{
                Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
            }

            var isCompleted:Boolean = true
            var newname = binding.nameeditTLI.editText?.text.toString().trim()
            var newemail = binding.emaileditTLI.editText?.text.toString().trim()
            var newheight = binding.heighteditTLI.editText?.text.toString()
            var newweight = binding.weighteditTLI.editText?.text.toString()
            var neuUser = User(newname, newheight, newweight, 0.0, newimageuri, GlobalVar.currentUser[0].stepgoal)

            if(neuUser.name.isEmpty()){
                binding.nameeditTLI.error = "Input Name."
                isCompleted = false
            }else{
                binding.nameeditTLI.error = ""
            }
            if(newemail.isEmpty()){
                binding.emaileditTLI.error = "Input Email."
                isCompleted = false
            }else{
                if(!Patterns.EMAIL_ADDRESS.matcher(newemail).matches()){
                    binding.emaileditTLI.error = "Input appropriate Email."
                    isCompleted = false
                }else {
                    binding.emaileditTLI.error = ""
                }
            }
            if(neuUser.height.toString().isEmpty() || neuUser.height == "0"){
                binding.heighteditTLI.error = "Input Height."
                isCompleted = false
            }else{
                if(neuUser.height.toString().isDigitsOnly()){
                    binding.heighteditTLI.error = ""
                }else{
                    binding.heighteditTLI.error = "Input Number Only."
                    isCompleted = false
                }
            }
            if(neuUser.weight.toString().isEmpty() || neuUser.weight == "0"){
                binding.weighteditTLI.error = "Input Weight."
                isCompleted = false
            }else{
                if(neuUser.weight.toString().isDigitsOnly()){
                    binding.weighteditTLI.error = ""
                }else{
                    binding.weighteditTLI.error = "Input Number Only."
                    isCompleted = false
                }
            }

            if (isCompleted){
                GlobalVar.currentUser.clear()
                var bmi: Double = newweight.toDouble() / ((newheight.toDouble()/100) * (newheight.toDouble()/100))
                val roundoff = (bmi * 10.0).roundToInt() / 10.0
                neuUser.bmi = roundoff
                GlobalVar.currentUser.add(neuUser)
                firebaseUser!!.updateEmail(newemail)
                db.collection("notes").document(firebaseUser!!.uid).collection("userData").document("Profile").set(neuUser)
                Toast.makeText(this, "Edit Successful.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, NavActivity::class.java))
                finish()
            }
        }
    }

    private fun Display(editUser: User) {
        binding.nameeditTLI.editText?.setText(editUser.name)
        binding.emaileditTLI.editText?.setText((curUserEmail))
        binding.heighteditTLI.editText?.setText(editUser.height)
        binding.weighteditTLI.editText?.setText(editUser.weight)
        var uuidIMG = GlobalVar.currentUser[0].imageUri
        if(uuidIMG != ""){
            val ref = storageReference?.child("myImages/" + uuidIMG + ".jpg")
            val localfile = File.createTempFile("tempImage", "jpg")
            ref?.getFile(localfile)?.addOnSuccessListener {
                val bitMap = BitmapFactory.decodeFile(localfile.absolutePath)
                binding.editIV.setImageBitmap(bitMap)
            }?.addOnFailureListener(){
                Toast.makeText(this, "Unable to Load Image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun GetIntent() {
        setup = intent.getBooleanExtra("setup", false)
        //if(!setup){
            Display(editUser)
        //}
    }

    @Suppress("DEPRECATION")
    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE_REQUEST )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                binding.editIV.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}
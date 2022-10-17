package com.example.fitology.ui.profile

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.fitology.Database.GlobalVar
import com.example.fitology.LoginActivity
import com.example.fitology.Model.User
import com.example.fitology.databinding.FragmentProfileBinding
import com.example.fitology.editProfileActivitiy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var firebaseStore: FirebaseStorage? = null
    private var storageReference= FirebaseStorage.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        firebaseStore = FirebaseStorage.getInstance()

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.registerfirstCV.visibility = View.GONE

        Listener()
        if(GlobalVar.currentUser.isEmpty()){
            var catchUserError = User( "", "0", "0", 0.0, "", "")
            GlobalVar.currentUser.add(catchUserError)
        }
        Display(GlobalVar.currentUser[0])

        return root
    }

    private fun Display(user: User) {
        binding.WelcomeUserTV.text = """Welcome ${user.name}!"""
        binding.bmiCircle.text = user.bmi.toString()
        binding.heightCircle.text = user.height.toString()
        binding.weightCircle.text = user.weight.toString()
        if(user.bmi.equals(0.0)){
            binding.registerfirstCV.visibility = View.VISIBLE
        }
        var uuidIMG = GlobalVar.currentUser[0].imageUri
        if(uuidIMG != ""){
            val ref = storageReference.child("myImages/" + uuidIMG + ".jpg")
            val localfile = File.createTempFile("tempImage", "jpg")
            ref.getFile(localfile).addOnSuccessListener {
                val bitMap = BitmapFactory.decodeFile(localfile.absolutePath)
                binding.userProfileIV.setImageBitmap(bitMap)
            }.addOnFailureListener(){
                Toast.makeText(activity, "Unable to Load Image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun Listener() {

        binding.LogoutBTN.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            GlobalVar.currentUser.clear()
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        }
        binding.logout2ndBTN.setOnClickListener(){
            FirebaseAuth.getInstance().signOut()
            GlobalVar.currentUser.clear()
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        }
        binding.setupprofileBTN.setOnClickListener(){
            val myIntent = Intent(activity, editProfileActivitiy::class.java).apply {
                putExtra("setup", true)
            }
            startActivity(myIntent)
        }
        binding.editProfileBTN.setOnClickListener(){
            val myIntent = Intent(activity, editProfileActivitiy::class.java).apply{
                putExtra("setup", false)
            }
            startActivity(myIntent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.fitology.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fitology.Database.GlobalVar
import com.example.fitology.Model.User
import com.example.fitology.Model.steps
import com.example.fitology.StepsActivity
import com.example.fitology.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import android.widget.CalendarView.OnDateChangeListener
import android.widget.Toast
import androidx.core.view.get
import com.example.fitology.Model.Note
import com.example.fitology.R
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    val db = Firebase.firestore
    val firebaseUser = FirebaseAuth.getInstance()
    lateinit var calendarView: CalendarView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        val root: View = binding.root

        binding.startpedometerBTN.setOnClickListener {
            startsteppin()
        }
        if(binding.currDateTV.text == "Current Date"){
            val date = Calendar.getInstance()
            val year = date.get(Calendar.YEAR)
            val month = date.get(Calendar.MONTH) + 1
            val day = date.get(Calendar.DAY_OF_MONTH)
            binding.currDateTV.text = day.toString() + "-" + month.toString() + "-" + year.toString()

        }

        var query = db.collection("notes").document(firebaseUser.currentUser!!.uid).collection("userData")
        query.get().addOnSuccessListener { result ->
            for (document in result){
                GlobalVar.currentUser.clear()
                var nowUser = document.toObject<User>()
                GlobalVar.currentUser.add(nowUser)
                binding.stepgoalTV.text = GlobalVar.currentUser[0].stepgoal
            }
        }
        listener()
        Display()

        return root
    }

    private fun Display() {
        if (GlobalVar.currentUser.isNotEmpty()){
            binding.stepgoalTV.text = GlobalVar.currentUser[0].stepgoal
        }
        var query = db.collection("notes").document(firebaseUser.currentUser!!.uid).collection("StepData")
        query.get().addOnSuccessListener { result ->
            for (document in result){
                var stepy = document.toObject<steps>()
                GlobalVar.StepCount[0] = stepy
                binding.curstepsTV.text = GlobalVar.StepCount[0].count
            }
        }
        if(GlobalVar.StepCount.isEmpty()){
            var stepster = steps("0")
            GlobalVar.StepCount.add(stepster)
            binding.curstepsTV.text = GlobalVar.StepCount[0].count
        }else{
            binding.curstepsTV.text = GlobalVar.StepCount[0].count
            binding.stepgoalTV.text = GlobalVar.currentUser[0].stepgoal
        }
        var remquery = db.collection("notes").document(firebaseUser.currentUser!!.uid).collection("reminders")
        remquery.get().addOnSuccessListener { result ->
            for (document in result){
                if(document.id == binding.currDateTV.text.toString().trim()){
                    if(GlobalVar.reminder.isEmpty()){
                        GlobalVar.reminder.add(Note("","",""))
                    }
                    GlobalVar.reminder[0] = document.toObject()
                    binding.reminderTextTV.text = GlobalVar.reminder[0].title
                    return@addOnSuccessListener
                }
            }
            binding.reminderTextTV.text = ""
        }
    }


    private fun listener() {
        binding.startpedometerBTN.setOnClickListener {
            startsteppin()
        }
        calendarView = binding.root.findViewById(R.id.calendarView)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val Date = (dayOfMonth.toString() + "-" + (month + 1) + "-" + year)
            binding.currDateTV.text = Date
            binding.reminderTIL.editText?.setText("")
            var remquery = db.collection("notes").document(firebaseUser.currentUser!!.uid).collection("reminders")
            remquery.get().addOnSuccessListener { result ->
                for (document in result){
                    if(document.id == binding.currDateTV.text.toString().trim()){
                        if(GlobalVar.reminder.isEmpty()){
                            GlobalVar.reminder.add(Note("","",""))
                        }
                        GlobalVar.reminder[0] = document.toObject()
                        binding.reminderTextTV.text = GlobalVar.reminder[0].title
                        return@addOnSuccessListener
                    }
                }
                binding.reminderTextTV.text = ""
            }
        }

        binding.setreminderBTN.setOnClickListener(){
            var text = binding.reminderTIL.editText?.text.toString()
            var reminderNote = Note(text, "", "")
            db.collection("notes").document(firebaseUser.currentUser!!.uid).collection("reminders").document(
                binding.currDateTV.text as String
                ).set(reminderNote)
            var remquery = db.collection("notes").document(firebaseUser.currentUser!!.uid).collection("reminders")
            remquery.get().addOnSuccessListener { result ->
                for (document in result){
                    if(document.id == binding.currDateTV.text.toString().trim()){
                        if(GlobalVar.reminder.isEmpty()){
                            GlobalVar.reminder.add(Note("","",""))
                        }
                        GlobalVar.reminder[0] = document.toObject()
                        binding.reminderTextTV.text = GlobalVar.reminder[0].title
                        binding.reminderTIL.editText?.setText("")
                        Toast.makeText(this.activity, "Reminder Set.", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }
                }
                binding.reminderTextTV.text = ""
            }

        }


        //add more buttons here
    }


    private fun startsteppin() {
        val myIntent = Intent(activity, StepsActivity::class.java)
        requireActivity().startActivity(myIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

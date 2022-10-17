package com.example.fitology.Database

import com.example.fitology.Model.*

class GlobalVar {

    companion object{
        val STORAGE_PERMISSION_CODE: Int = 100
        var listLogs = ArrayList<Note>()
        var currentUser = ArrayList<User>()
        var StepCount = ArrayList<steps>()
        var reminder = ArrayList<Note>()
    }

}
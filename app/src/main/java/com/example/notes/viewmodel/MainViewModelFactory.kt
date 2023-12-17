package com.example.notes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notes.model.DBHelper

class MainViewModelFactory(private val dbhelper: DBHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(dbhelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
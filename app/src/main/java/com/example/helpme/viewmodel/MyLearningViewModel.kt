package com.example.helpme.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.helpme.network.ApiService
import com.example.helpme.network.Project
import com.example.helpme.network.RetrofitClient
import com.example.helpme.network.User
import kotlinx.coroutines.launch

class MyLearningViewModel : ViewModel() {
    private val apiService = RetrofitClient.instance.create(ApiService::class.java)

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _projects = MutableLiveData<List<Project>>()
    val projects: LiveData<List<Project>> = _projects

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun saveUserAndGetProjects(id: String, email: String, nickname: String) {
        viewModelScope.launch {
            try {
                val user = apiService.saveUser(User(id, email, nickname))
                _user.value = user
                val projects = apiService.getOngoingProjects(user.id)
                _projects.value = projects
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            }
        }
    }
}

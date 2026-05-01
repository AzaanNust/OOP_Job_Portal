package oop.project.androidoopproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import oop.project.androidoopproject.api.RetrofitClient
import oop.project.androidoopproject.model.*
import oop.project.androidoopproject.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _seekerProfile   = MutableStateFlow<UiState<SeekerProfile>>(UiState.Idle)
    val seekerProfile: StateFlow<UiState<SeekerProfile>> = _seekerProfile

    private val _employerProfile = MutableStateFlow<UiState<EmployerProfile>>(UiState.Idle)
    val employerProfile: StateFlow<UiState<EmployerProfile>> = _employerProfile

    private val _updateState     = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val updateState: StateFlow<UiState<Unit>> = _updateState

    fun loadSeekerProfile(token: String) {
        viewModelScope.launch {
            _seekerProfile.value = UiState.Loading
            try {
                val res = RetrofitClient.apiWithAuth(token).getSeekerProfile()
                if (res.isSuccessful) _seekerProfile.value = UiState.Success(res.body()!!)
                else _seekerProfile.value = UiState.Error("Failed to load profile.")
            } catch (e: Exception) { _seekerProfile.value = UiState.Error("Cannot connect to server.") }
        }
    }

    fun updateSeekerProfile(token: String, req: UpdateSeekerRequest) {
        viewModelScope.launch {
            _updateState.value = UiState.Loading
            try {
                val res = RetrofitClient.apiWithAuth(token).updateSeekerProfile(req)
                if (res.isSuccessful) { _seekerProfile.value = UiState.Success(res.body()!!); _updateState.value = UiState.Success(Unit) }
                else _updateState.value = UiState.Error("Update failed.")
            } catch (e: Exception) { _updateState.value = UiState.Error("Cannot connect to server.") }
        }
    }

    fun loadEmployerProfile(token: String) {
        viewModelScope.launch {
            _employerProfile.value = UiState.Loading
            try {
                val res = RetrofitClient.apiWithAuth(token).getEmployerProfile()
                if (res.isSuccessful) _employerProfile.value = UiState.Success(res.body()!!)
                else _employerProfile.value = UiState.Error("Failed to load profile.")
            } catch (e: Exception) { _employerProfile.value = UiState.Error("Cannot connect to server.") }
        }
    }

    fun updateEmployerProfile(token: String, req: UpdateEmployerRequest) {
        viewModelScope.launch {
            _updateState.value = UiState.Loading
            try {
                val res = RetrofitClient.apiWithAuth(token).updateEmployerProfile(req)
                if (res.isSuccessful) { _employerProfile.value = UiState.Success(res.body()!!); _updateState.value = UiState.Success(Unit) }
                else _updateState.value = UiState.Error("Update failed.")
            } catch (e: Exception) { _updateState.value = UiState.Error("Cannot connect to server.") }
        }
    }

    fun resetUpdateState() { _updateState.value = UiState.Idle }
}
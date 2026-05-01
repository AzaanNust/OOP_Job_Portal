package oop.project.androidoopproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import oop.project.androidoopproject.api.RetrofitClient
import oop.project.androidoopproject.model.*
import oop.project.androidoopproject.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ApplicationViewModel : ViewModel() {

    private val _myAppsState = MutableStateFlow<UiState<List<ApplicationResponse>>>(UiState.Idle)
    val myAppsState: StateFlow<UiState<List<ApplicationResponse>>> = _myAppsState

    private val _applyState = MutableStateFlow<UiState<ApplicationResponse>>(UiState.Idle)
    val applyState: StateFlow<UiState<ApplicationResponse>> = _applyState

    private val _applicantsState = MutableStateFlow<UiState<List<ApplicationResponse>>>(UiState.Idle)
    val applicantsState: StateFlow<UiState<List<ApplicationResponse>>> = _applicantsState

    fun loadMyApplications(token: String) {
        viewModelScope.launch {
            _myAppsState.value = UiState.Loading
            try {
                val res = RetrofitClient.apiWithAuth(token).getMyApplications()
                if (res.isSuccessful) _myAppsState.value = UiState.Success(res.body() ?: emptyList())
                else _myAppsState.value = UiState.Error("Failed to load applications.")
            } catch (e: Exception) {
                _myAppsState.value = UiState.Error("Cannot connect to server.")
            }
        }
    }

    fun applyForJob(token: String, jobId: Long, coverLetter: String = "") {
        viewModelScope.launch {
            _applyState.value = UiState.Loading
            try {
                val res = RetrofitClient.apiWithAuth(token).applyForJob(ApplyRequest(jobId, coverLetter))
                if (res.isSuccessful) _applyState.value = UiState.Success(res.body()!!)
                else _applyState.value = UiState.Error("Application failed. You may have already applied.")
            } catch (e: Exception) {
                _applyState.value = UiState.Error("Cannot connect to server.")
            }
        }
    }

    fun loadApplicants(token: String, jobId: Long) {
        viewModelScope.launch {
            _applicantsState.value = UiState.Loading
            try {
                val res = RetrofitClient.apiWithAuth(token).getJobApplicants(jobId)
                if (res.isSuccessful) _applicantsState.value = UiState.Success(res.body() ?: emptyList())
                else _applicantsState.value = UiState.Error("Failed to load applicants.")
            } catch (e: Exception) {
                _applicantsState.value = UiState.Error("Cannot connect to server.")
            }
        }
    }

    fun advanceApplication(token: String, appId: Long, onDone: () -> Unit) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiWithAuth(token).advanceApplication(appId)
                onDone()
            } catch (_: Exception) {}
        }
    }

    fun rejectApplication(token: String, appId: Long, onDone: () -> Unit) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiWithAuth(token).rejectApplication(appId)
                onDone()
            } catch (_: Exception) {}
        }
    }

    fun resetApplyState() { _applyState.value = UiState.Idle }
}

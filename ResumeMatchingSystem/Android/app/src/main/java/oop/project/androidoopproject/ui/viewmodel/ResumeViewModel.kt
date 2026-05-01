package oop.project.androidoopproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import oop.project.androidoopproject.api.RetrofitClient
import oop.project.androidoopproject.model.*
import oop.project.androidoopproject.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ResumeViewModel : ViewModel() {

    private val _resumeState = MutableStateFlow<UiState<ResumeResponse>>(UiState.Idle)
    val resumeState: StateFlow<UiState<ResumeResponse>> = _resumeState

    private val _saveState = MutableStateFlow<UiState<ResumeResponse>>(UiState.Idle)
    val saveState: StateFlow<UiState<ResumeResponse>> = _saveState

    fun loadResume(token: String) {
        viewModelScope.launch {
            _resumeState.value = UiState.Loading
            try {
                val res = RetrofitClient.apiWithAuth(token).getMyResume()
                when {
                    res.isSuccessful -> {
                        // Resume exists — pre-fill the form
                        _resumeState.value = UiState.Success(res.body()!!)
                    }
                    res.code() == 404 -> {
                        // New user — no resume yet. Show empty form (Success with empty object)
                        _resumeState.value = UiState.Success(ResumeResponse())
                    }
                    else -> {
                        // Other server error — still show empty form, just log the issue
                        _resumeState.value = UiState.Success(ResumeResponse())
                    }
                }
            } catch (e: Exception) {
                // Network error — still show the form so user isn't stuck
                _resumeState.value = UiState.Success(ResumeResponse())
            }
        }
    }

    fun saveResume(token: String, request: SaveResumeRequest) {
        viewModelScope.launch {
            _saveState.value = UiState.Loading
            try {
                val res = RetrofitClient.apiWithAuth(token).saveResume(request)
                if (res.isSuccessful) {
                    _saveState.value   = UiState.Success(res.body()!!)
                    _resumeState.value = UiState.Success(res.body()!!)
                } else {
                    _saveState.value = UiState.Error("Save failed (${res.code()}). Check backend is running.")
                }
            } catch (e: Exception) {
                _saveState.value = UiState.Error("Cannot connect to server: ${e.message}")
            }
        }
    }

    fun resetSaveState() { _saveState.value = UiState.Idle }
}
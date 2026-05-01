package oop.project.androidoopproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import oop.project.androidoopproject.api.RetrofitClient
import oop.project.androidoopproject.model.*
import oop.project.androidoopproject.util.SessionManager
import oop.project.androidoopproject.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<AuthResponse>>(UiState.Idle)
    val loginState: StateFlow<UiState<AuthResponse>> = _loginState

    private val _registerState = MutableStateFlow<UiState<AuthResponse>>(UiState.Idle)
    val registerState: StateFlow<UiState<AuthResponse>> = _registerState

    fun login(email: String, password: String, session: SessionManager) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                val res = RetrofitClient.api.login(LoginRequest(email.trim(), password))
                if (res.isSuccessful) {
                    val body = res.body()!!
                    session.saveAuth(body.token, body.userId, body.fullName, body.email, body.role)
                    _loginState.value = UiState.Success(body)
                } else {
                    _loginState.value = UiState.Error("Invalid email or password.")
                }
            } catch (e: Exception) {
                _loginState.value = UiState.Error("Cannot connect to server. Is the backend running?")
            }
        }
    }

    fun registerSeeker(req: RegisterSeekerRequest, session: SessionManager) {
        viewModelScope.launch {
            _registerState.value = UiState.Loading
            try {
                val res = RetrofitClient.api.registerSeeker(req)
                if (res.isSuccessful) {
                    val body = res.body()!!
                    session.saveAuth(body.token, body.userId, body.fullName, body.email, body.role)
                    _registerState.value = UiState.Success(body)
                } else {
                    _registerState.value = UiState.Error("Registration failed. Email may already be in use.")
                }
            } catch (e: Exception) {
                _registerState.value = UiState.Error("Cannot connect to server.")
            }
        }
    }

    fun registerEmployer(req: RegisterEmployerRequest, session: SessionManager) {
        viewModelScope.launch {
            _registerState.value = UiState.Loading
            try {
                val res = RetrofitClient.api.registerEmployer(req)
                if (res.isSuccessful) {
                    val body = res.body()!!
                    session.saveAuth(body.token, body.userId, body.fullName, body.email, body.role)
                    _registerState.value = UiState.Success(body)
                } else {
                    _registerState.value = UiState.Error("Registration failed. Email may already be in use.")
                }
            } catch (e: Exception) {
                _registerState.value = UiState.Error("Cannot connect to server.")
            }
        }
    }

    fun resetStates() {
        _loginState.value    = UiState.Idle
        _registerState.value = UiState.Idle
    }
}

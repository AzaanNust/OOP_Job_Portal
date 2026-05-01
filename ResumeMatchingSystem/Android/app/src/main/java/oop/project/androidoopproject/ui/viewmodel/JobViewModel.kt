package oop.project.androidoopproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import oop.project.androidoopproject.api.RetrofitClient
import oop.project.androidoopproject.model.*
import oop.project.androidoopproject.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JobViewModel : ViewModel() {

    private val _jobsState       = MutableStateFlow<UiState<List<JobListingResponse>>>(UiState.Idle)
    val jobsState: StateFlow<UiState<List<JobListingResponse>>> = _jobsState

    private val _myJobsState     = MutableStateFlow<UiState<List<JobListingResponse>>>(UiState.Idle)
    val myJobsState: StateFlow<UiState<List<JobListingResponse>>> = _myJobsState

    private val _jobDetailState  = MutableStateFlow<UiState<JobListingResponse>>(UiState.Idle)
    val jobDetailState: StateFlow<UiState<JobListingResponse>> = _jobDetailState

    private val _postJobState    = MutableStateFlow<UiState<JobListingResponse>>(UiState.Idle)
    val postJobState: StateFlow<UiState<JobListingResponse>> = _postJobState

    private val _updateJobState  = MutableStateFlow<UiState<JobListingResponse>>(UiState.Idle)
    val updateJobState: StateFlow<UiState<JobListingResponse>> = _updateJobState

    /** Loads all open jobs. Passing null/blank params returns everything. */
    fun searchJobs(title: String? = null, location: String? = null, shift: String? = null) {
        viewModelScope.launch {
            _jobsState.value = UiState.Loading
            try {
                val res = RetrofitClient.api.searchJobs(
                    title    = title?.ifBlank { null },
                    location = location?.ifBlank { null },
                    shift    = shift?.ifBlank { null }
                )
                if (res.isSuccessful) _jobsState.value = UiState.Success(res.body()?.content ?: emptyList())
                else _jobsState.value = UiState.Error("Failed to load jobs.")
            } catch (e: Exception) {
                _jobsState.value = UiState.Error("Cannot connect to server.")
            }
        }
    }

    fun getJobById(jobId: Long) {
        viewModelScope.launch {
            _jobDetailState.value = UiState.Loading
            try {
                val res = RetrofitClient.api.getJobById(jobId)
                if (res.isSuccessful) _jobDetailState.value = UiState.Success(res.body()!!)
                else _jobDetailState.value = UiState.Error("Job not found.")
            } catch (e: Exception) {
                _jobDetailState.value = UiState.Error("Cannot connect to server.")
            }
        }
    }

    fun loadMyJobs(token: String) {
        viewModelScope.launch {
            _myJobsState.value = UiState.Loading
            try {
                val res = RetrofitClient.apiWithAuth(token).getMyJobs()
                if (res.isSuccessful) _myJobsState.value = UiState.Success(res.body() ?: emptyList())
                else _myJobsState.value = UiState.Error("Failed to load your jobs.")
            } catch (e: Exception) {
                _myJobsState.value = UiState.Error("Cannot connect to server.")
            }
        }
    }

    fun postJob(token: String, request: PostJobRequest) {
        viewModelScope.launch {
            _postJobState.value = UiState.Loading
            try {
                val res = RetrofitClient.apiWithAuth(token).postJob(request)
                if (res.isSuccessful) _postJobState.value = UiState.Success(res.body()!!)
                else _postJobState.value = UiState.Error("Failed to post job.")
            } catch (e: Exception) {
                _postJobState.value = UiState.Error("Cannot connect to server.")
            }
        }
    }

    fun updateJob(token: String, jobId: Long, request: PostJobRequest) {
        viewModelScope.launch {
            _updateJobState.value = UiState.Loading
            try {
                val res = RetrofitClient.apiWithAuth(token).updateJob(jobId, request)
                if (res.isSuccessful) _updateJobState.value = UiState.Success(res.body()!!)
                else _updateJobState.value = UiState.Error("Failed to update job.")
            } catch (e: Exception) {
                _updateJobState.value = UiState.Error("Cannot connect to server.")
            }
        }
    }

    fun closeJob(token: String, jobId: Long, onDone: () -> Unit) {
        viewModelScope.launch {
            try { RetrofitClient.apiWithAuth(token).closeJob(jobId); onDone() }
            catch (_: Exception) {}
        }
    }

    fun reactivateJob(token: String, jobId: Long, onDone: () -> Unit) {
        viewModelScope.launch {
            try { RetrofitClient.apiWithAuth(token).reactivateJob(jobId); onDone() }
            catch (_: Exception) {}
        }
    }

    fun deleteJob(token: String, jobId: Long, onDone: () -> Unit) {
        viewModelScope.launch {
            try { RetrofitClient.apiWithAuth(token).deleteJob(jobId); onDone() }
            catch (_: Exception) {}
        }
    }

    fun resetPostJobState()   { _postJobState.value   = UiState.Idle }
    fun resetUpdateJobState() { _updateJobState.value = UiState.Idle }
}
package oop.project.androidoopproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import oop.project.androidoopproject.api.RetrofitClient
import oop.project.androidoopproject.model.NotificationItem
import oop.project.androidoopproject.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {

    private val _notificationsState = MutableStateFlow<UiState<List<NotificationItem>>>(UiState.Idle)
    val notificationsState: StateFlow<UiState<List<NotificationItem>>> = _notificationsState

    private val _unreadCount = MutableStateFlow(0L)
    val unreadCount: StateFlow<Long> = _unreadCount

    fun loadNotifications(token: String) {
        viewModelScope.launch {
            _notificationsState.value = UiState.Loading

            try {
                val res = RetrofitClient.apiWithAuth(token).getNotifications()
                if (res.isSuccessful) {
                    val list = res.body() ?: emptyList()
                    // 3. Data Processing: Custom Sorting
                    // First: Group by 'isRead' (false/unread comes before true/read)
                    // Second: Within those groups, sort by 'createdAt' (newest date first)
                    val sorted = list.sortedWith( //In Kotlin/Java, a Boolean is sorted as false first, then true.
                        compareBy<NotificationItem> { it.isRead }
                            .thenByDescending { it.createdAt }
                    )
                    _notificationsState.value = UiState.Success(sorted)
                    // Update the unread badge count specifically for the UI bell icon
                    _unreadCount.value = list.count { !it.isRead }.toLong()
                } else {
                    _notificationsState.value = UiState.Error("Failed to load notifications.")
                }
            } catch (e: Exception) {
                _notificationsState.value = UiState.Error("Cannot connect to server.")
            }
        }
    }

    fun loadUnreadCount(token: String) {
        viewModelScope.launch {
            try {
                val res = RetrofitClient.apiWithAuth(token).getNotificationCount()
                if (res.isSuccessful) _unreadCount.value = res.body()?.unreadCount ?: 0L
            } catch (_: Exception) {}
        }
    }

    /**
     * Marks a single notification as read.
     * Updates state locally IMMEDIATELY so the UI changes without waiting
     * for a server round-trip, then fires the API call in background.
     */
    fun markRead(token: String, id: Long) {

        // Get the current UI state from StateFlow (latest snapshot)
        val current = _notificationsState.value

        // Only proceed if we actually have data loaded successfully
        if (current is UiState.Success) {

            // Create a NEW list by mapping over existing notifications
            // If a notification matches the given id → mark it as read
            // Otherwise → keep it unchanged
            val updated = current.data.map { n ->
                if (n.id == id) n.copy(isRead = true) else n
            }

            // Sort the updated list:
            // 1. Unread items first (isRead = false comes before true)
            // 2. Within each group, sort by latest createdAt (descending)
            val sorted = updated.sortedWith(
                compareBy<NotificationItem> { it.isRead }
                    .thenByDescending { it.createdAt }
            )

            // Update StateFlow with new sorted list
            // This triggers UI recomposition instantly (optimistic update)
            _notificationsState.value = UiState.Success(sorted)

            // Recalculate unread count based on updated list
            _unreadCount.value = sorted.count { !it.isRead }.toLong()
        }

        // Call backend API in background (non-blocking)
        viewModelScope.launch {
            try {
                // Send request to mark notification as read on server
                RetrofitClient.apiWithAuth(token).markRead(id)
            } catch (_: Exception) {
                // Ignore errors (UI already updated optimistically)
                // In production, you might want to handle rollback or show error
            }
        }
    }

    fun markAllRead(token: String) {
        // Instant local update
        val current = _notificationsState.value
        if (current is UiState.Success) {
            val updated = current.data.map { it.copy(isRead = true) }
            _notificationsState.value = UiState.Success(updated)
            _unreadCount.value = 0L
        }
        viewModelScope.launch {
            try { RetrofitClient.apiWithAuth(token).markAllRead() }
            catch (_: Exception) {}
        }
    }
}
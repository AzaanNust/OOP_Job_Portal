package oop.project.androidoopproject.util

import android.content.Context
import android.content.SharedPreferences
import oop.project.androidoopproject.model.UserRole

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("JobPortalPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_ID    = "user_id"
        private const val KEY_NAME  = "user_name"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_ROLE  = "user_role"
    }

    fun saveAuth(token: String, userId: Long, name: String, email: String, role: UserRole) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putLong(KEY_ID, userId)
            .putString(KEY_NAME, name)
            .putString(KEY_EMAIL, email)
            .putString(KEY_ROLE, role.name)
            .apply()
    }

    fun getToken(): String?    = prefs.getString(KEY_TOKEN, null)
    fun getUserId(): Long      = prefs.getLong(KEY_ID, 0L)
    fun getUserName(): String  = prefs.getString(KEY_NAME, "") ?: ""
    fun getUserEmail(): String = prefs.getString(KEY_EMAIL, "") ?: ""

    fun getUserRole(): UserRole {
        val roleName = prefs.getString(KEY_ROLE, null) ?: return UserRole.JOB_SEEKER
        return try { UserRole.valueOf(roleName) } catch (e: Exception) { UserRole.JOB_SEEKER }
    }

    fun isLoggedIn(): Boolean = !getToken().isNullOrEmpty()

    fun clearSession() = prefs.edit().clear().apply()
}

package com.cyna.app.data.local

import android.content.Context
import com.cyna.app.data.dto.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("cyna_prefs", Context.MODE_PRIVATE)

    private val _user = MutableStateFlow<UserDto?>(null)
    val user: StateFlow<UserDto?> = _user.asStateFlow()

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    init {
        val savedUser = prefs.getString("user", null)
        val savedToken = prefs.getString("token", null)

        if (savedUser != null && savedToken != null) {
            try {
                _user.value = Json.decodeFromString<UserDto>(savedUser)
                _token.value = savedToken
            } catch (e: Exception) {
                clearSession()
            }
        }
    }

    fun saveSession(user: UserDto, token: String) {
        _user.value = user
        _token.value = token
        prefs.edit().apply {
            putString("user", Json.encodeToString(user))
            putString("token", token)
            apply()
        }
    }

    fun clearSession() {
        _user.value = null
        _token.value = null
        prefs.edit().apply {
            remove("user")
            remove("token")
            apply()
        }
    }

    fun isAuthenticated(): Boolean = _token.value != null
}

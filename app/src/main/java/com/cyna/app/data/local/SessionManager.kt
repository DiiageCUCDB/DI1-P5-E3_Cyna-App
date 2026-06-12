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

    private val _refreshToken = MutableStateFlow<String?>(null)
    val refreshToken: StateFlow<String?> = _refreshToken.asStateFlow()

    init {
        val savedUser = prefs.getString("user", null)
        val savedToken = prefs.getString("token", null)
        val savedRefresh = prefs.getString("refreshToken", null)

        if (savedToken != null && savedRefresh != null) {
            _token.value = savedToken
            _refreshToken.value = savedRefresh
            if (savedUser != null) {
                runCatching { _user.value = Json.decodeFromString<UserDto>(savedUser) }
                    .onFailure { clearSession() }
            }
        }
    }

    fun saveTokens(token: String, refreshToken: String) {
        _token.value = token
        _refreshToken.value = refreshToken
        prefs.edit().apply {
            putString("token", token)
            putString("refreshToken", refreshToken)
            apply()
        }
    }

    fun saveUser(user: UserDto) {
        _user.value = user
        prefs.edit().putString("user", Json.encodeToString(user)).apply()
    }

    fun saveSession(user: UserDto, token: String, refreshToken: String) {
        saveTokens(token, refreshToken)
        saveUser(user)
    }

    fun clearSession() {
        _user.value = null
        _token.value = null
        _refreshToken.value = null
        prefs.edit().apply {
            remove("user")
            remove("token")
            remove("refreshToken")
            apply()
        }
    }

    fun isAuthenticated(): Boolean = _token.value != null
}

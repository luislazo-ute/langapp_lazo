package com.langapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences>
        by preferencesDataStore(name = "lang_session")

@Singleton
class TokenDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        val KEY_ACCESS   = stringPreferencesKey("access_token")
        val KEY_REFRESH  = stringPreferencesKey("refresh_token")
        val KEY_USER_ID  = intPreferencesKey("user_id")
        val KEY_USERNAME = stringPreferencesKey("username")
        val KEY_EMAIL    = stringPreferencesKey("email")
        val KEY_IS_STAFF = booleanPreferencesKey("is_staff")
    }

    val accessToken: Flow<String?>  = context.dataStore.data.map { it[KEY_ACCESS] }
    val refreshToken: Flow<String?> = context.dataStore.data.map { it[KEY_REFRESH] }

    suspend fun getAccessToken(): String?  = accessToken.first()
    suspend fun getRefreshToken(): String? = refreshToken.first()

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map {
        !it[KEY_ACCESS].isNullOrBlank()
    }
    val isStaff: Flow<Boolean> = context.dataStore.data.map { it[KEY_IS_STAFF] == true }

    suspend fun saveTokens(access: String, refresh: String) {
        context.dataStore.edit { it[KEY_ACCESS] = access; it[KEY_REFRESH] = refresh }
    }
    suspend fun saveAccessToken(access: String) {
        context.dataStore.edit { it[KEY_ACCESS] = access }
    }
    suspend fun saveUser(id: Int, username: String, email: String, isStaff: Boolean) {
        context.dataStore.edit { p ->
            p[KEY_USER_ID] = id; p[KEY_USERNAME] = username
            p[KEY_EMAIL] = email; p[KEY_IS_STAFF] = isStaff
        }
    }
    suspend fun clearSession() { context.dataStore.edit { it.clear() } }

    data class UserSnapshot(val id: Int, val username: String, val email: String, val isStaff: Boolean)

    val userSnapshot: Flow<UserSnapshot?> = context.dataStore.data.map { p ->
        val id = p[KEY_USER_ID] ?: return@map null
        UserSnapshot(id, p[KEY_USERNAME] ?: "", p[KEY_EMAIL] ?: "", p[KEY_IS_STAFF] == true)
    }
}
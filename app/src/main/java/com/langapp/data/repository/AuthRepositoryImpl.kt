package com.langapp.data.repository

import com.langapp.data.local.TokenDataStore
import com.langapp.data.remote.api.AuthApi
import com.langapp.data.remote.dto.*
import com.langapp.domain.model.LoggedUser
import com.langapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import com.langapp.domain.repository.ProfileRepository

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenDataStore: TokenDataStore,
    private val profileRepository: ProfileRepository,
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<LoggedUser> =
        runCatching {
            val r = api.login(LoginRequest(username, password))
            if (!r.isSuccessful) error(parseError(r.errorBody()?.string(), r.code()))
            val b = r.body()!!

            // 1. Guardar tokens PRIMERO
            tokenDataStore.saveTokens(b.access, b.refresh)
            tokenDataStore.saveUser(b.userId ?: 0, b.username ?: username, b.email ?: "", false)

            // 2. Confirmar que el token ya quedó guardado (evita el 401 por timing)
            val token = tokenDataStore.getAccessToken()

            // 3. Ahora sí pedir el perfil para leer is_staff
            val profile = if (!token.isNullOrBlank()) {
                profileRepository.getMyProfile().getOrNull()
            } else null

            val finalUsername = profile?.username ?: b.username ?: username
            val finalUserId   = profile?.userId ?: b.userId ?: 0
            val finalEmail    = profile?.email ?: b.email ?: ""
            val finalStaff    = profile?.isStaff ?: false

            tokenDataStore.saveUser(finalUserId, finalUsername, finalEmail, finalStaff)
            LoggedUser(finalUserId, finalUsername, finalEmail, finalStaff)
        }

    override suspend fun register(username: String, email: String, password: String, password2: String): Result<LoggedUser> =
        runCatching {
            val r = api.register(RegisterRequest(username, email, password, password2))
            if (!r.isSuccessful) error(parseError(r.errorBody()?.string(), r.code()))
            val b = r.body()!!
            val finalUsername = b.username ?: username
            val finalUserId   = b.userId ?: 0
            val finalEmail    = b.email ?: email
            val finalStaff    = b.isStaff ?: false
            tokenDataStore.saveTokens(b.access, b.refresh)
            tokenDataStore.saveUser(finalUserId, finalUsername, finalEmail, finalStaff)
            LoggedUser(finalUserId, finalUsername, finalEmail, finalStaff)
        }

    override suspend fun logout(): Result<Unit> = runCatching {
        val refresh = tokenDataStore.getRefreshToken()
        if (refresh != null) runCatching { api.logout(LogoutRequest(refresh)) }
        tokenDataStore.clearSession()
    }

    override suspend fun getStoredUser(): TokenDataStore.UserSnapshot? =
        tokenDataStore.userSnapshot.first()

    override suspend fun isLoggedIn(): Boolean =
        !tokenDataStore.getAccessToken().isNullOrBlank()

    private fun parseError(body: String?, code: Int): String {
        val base = when (code) {
            400 -> "Datos inválidos. Revisa los campos."
            401 -> "Usuario o contraseña incorrectos."
            403 -> "No tienes permiso para esta acción."
            404 -> "No encontrado."
            500, 502, 503 -> "Error del servidor. Intenta más tarde."
            else -> "Error $code"
        }
        return try {
            val map = com.google.gson.Gson().fromJson(body, Map::class.java)
            map["detail"]?.toString() ?: map.values.firstOrNull()?.toString() ?: base
        } catch (e: Exception) { base }
    }
}
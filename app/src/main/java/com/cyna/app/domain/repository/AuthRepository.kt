package com.cyna.app.domain.repository

import com.cyna.app.data.dto.LoginRequest
import com.cyna.app.data.dto.MessageResponse
import com.cyna.app.data.dto.RegisterRequest

/**
 * Contrat d'authentification exposé à la couche domaine/ViewModel.
 *
 * Les opérations login/register déclenchent le stockage automatique des cookies de session
 * via [SessionManagerCookieStorage]. La session est représentée par la présence d'un token
 * non-nul dans [com.cyna.app.data.local.SessionManager].
 */
interface AuthRepository {
    /** Connecte l'utilisateur et persiste le cookie de session. */
    suspend fun login(request: LoginRequest): MessageResponse

    /** Inscrit un nouvel utilisateur. Ne crée pas de session — l'utilisateur doit ensuite se connecter. */
    suspend fun register(request: RegisterRequest): MessageResponse

    /** Invalide la session côté serveur et efface les tokens locaux. */
    suspend fun logout()
}

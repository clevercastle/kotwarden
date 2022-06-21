package com.ivyxjc.kotwarden.web.service

import com.ivyxjc.kotwarden.util.verifyPassword
import com.ivyxjc.kotwarden.web.model.IdentityConnectData
import com.ivyxjc.kotwarden.web.model.LoginResponse

interface IIdentityService {
    fun refreshToken(connectData: IdentityConnectData): LoginResponse
    fun passwordLogin(connectData: IdentityConnectData): LoginResponse
    fun apiKeyLogin(connectData: IdentityConnectData): LoginResponse
}

class IdentityService(private val userRepository: UserRepository) : IIdentityService {
    override fun refreshToken(connectData: IdentityConnectData): LoginResponse {
        if (connectData.scope != "api offline_access") {
            error("Scope not supported")
        }

        // TODO: 2022/6/20  RateLimit the login
        val username = connectData.username!!.trim()
        val user = userRepository.findByEmail(username)

        TODO()
    }

    override fun passwordLogin(connectData: IdentityConnectData): LoginResponse {
        val scope = connectData.scope
        if (scope != "api offline_access") {
            error("Scope not supported")
        }
        // TODO: 2022/6/20 RateLimit the login
        val username = connectData.username!!.trim()
        val user = userRepository.findByEmail(username) ?: error("Username or password is incorrect")

        // Check password
        val check = verifyPassword(connectData.password!!, user.salt, user.masterPasswordHash!!, user.kdfIterations)
        if (!check) {
            error("")
        }

        // Check whether the user is disabled or not
        if (!user.enabled) {
            error("")
        }


        // TODO: 2022/6/21 Send verify email if user is not verified

        return LoginResponse(
            accessToken = "",
            expiresIn = 0L,
            tokenType = "Bearer",
            refreshToken = "",
            key = user.key,
            privateKey = user.encryptedPrivateKey,
            kdf = user.kdf,
            kdfIterations = user.kdfIterations,
            resetMasterPassword = false,
            scope = scope,
            unofficialServer = true
        )
    }

    override fun apiKeyLogin(connectData: IdentityConnectData): LoginResponse {
        TODO("Not yet implemented")
    }
}

package org.clevercastle.kotwarden.web.service

import io.ktor.http.HttpStatusCode
import org.clevercastle.kotwarden.model.Device
import org.clevercastle.kotwarden.model.DeviceType
import org.clevercastle.kotwarden.model.User
import org.clevercastle.kotwarden.util.verifyPassword
import org.clevercastle.kotwarden.web.kError
import org.clevercastle.kotwarden.web.model.IdentityConnectData
import org.clevercastle.kotwarden.web.model.LoginResponse
import org.clevercastle.kotwarden.web.notAuthorized

interface IIdentityService {
    fun refreshToken(connectData: IdentityConnectData): LoginResponse
    fun passwordLogin(connectData: IdentityConnectData): LoginResponse
    fun apiKeyLogin(connectData: IdentityConnectData): LoginResponse
}

class IdentityService(private val userRepository: UserRepository, private val deviceService: DeviceService) :
    IIdentityService {
    override fun refreshToken(connectData: IdentityConnectData): LoginResponse {
        val scope = "api offline_access"
        val scopeList = listOf("api", "offline_access")

        // TODO: 2022/6/20  RateLimit the refresh token
        val device: Device = deviceService.findByRefreshToken(connectData.refreshToken!!) ?: kError(
            HttpStatusCode.Unauthorized, "Invalid refresh token"
        )
        val user = userRepository.findById(device.userId) ?: TODO("500, fail to get user")
        val tokenPair = deviceService.refreshToken(device, user, scopeList)
        deviceService.save(device)
        return LoginResponse(
            accessToken = tokenPair.first,
            expiresIn = tokenPair.second,
            tokenType = "Bearer",
            refreshToken = device.refreshToken,
            key = user.key,
            privateKey = user.encryptedPrivateKey,
            kdf = user.kdf,
            kdfIterations = user.kdfIterations,
            resetMasterPassword = false,
            scope = scope,
            unofficialServer = true
        )
    }

    override fun passwordLogin(connectData: IdentityConnectData): LoginResponse {
        val scope = connectData.scope
        if (scope != "api offline_access") {
            kError(HttpStatusCode.BadRequest, "Scope not supported")
        }
        val scopeVec = listOf("api", "offline_access")
        // TODO: 2022/6/20 RateLimit the login
        val username = connectData.username!!.trim()
        val user = userRepository.findByEmail(username) ?: error("Username or password is incorrect")
        // Check password
        val check = verifyPassword(connectData.password!!, user.salt, user.masterPasswordHash!!, user.kdfIterations)
        if (!check) {
            notAuthorized(HttpStatusCode.Unauthorized, "password is incorrect")
        }
        // Check whether the user is disabled or not
        if (!user.enabled) {
            notAuthorized(HttpStatusCode.Forbidden, "The account is disabled.")
        }

        // TODO: 2022/6/21 check whether user is verified or not, send verify email if user is not verified

        val devicePair = getDevice(connectData, user)
        val device = devicePair.first
        if (devicePair.second) {
            // TODO: 2022/6/21 new device login (send new device email etc.)
        }
        val tokenPair = deviceService.refreshToken(device, user, listOf())
        deviceService.save(device)

        return LoginResponse(
            accessToken = tokenPair.first,
            expiresIn = tokenPair.second,
            tokenType = "Bearer",
            refreshToken = device.refreshToken,
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

    private fun getDevice(connectData: IdentityConnectData, user: User): Pair<Device, Boolean> {
        val deviceType = DeviceType.parse(connectData.deviceType)
        val deviceId = connectData.deviceIdentifier ?: error("No device id provided")
        val deviceName = connectData.deviceName ?: error("No device name provided")
        var device = deviceService.findByIdAndUser(deviceId, user.id)
        if (device == null) {
            device = Device(deviceId, user.id, deviceName, deviceType)
            return device to true
        }
        return device to false
    }
}
